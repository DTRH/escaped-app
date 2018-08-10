package com.pedersen.escaped.master.controls.games

import android.annotation.SuppressLint
import android.databinding.Bindable
import android.os.CountDownTimer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pedersen.escaped.BR
import com.pedersen.escaped.extensions.bind
import com.pedersen.escaped.master.controls.games.GameControlsActivityViewModel.GameState.*
import com.pedersen.escaped.utils.AppUtils
import io.greenerpastures.mvvm.BaseViewModel
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import timber.log.Timber

class GameControlsActivityViewModel : BaseViewModel<GameControlsActivityViewModel.Commands>() {

    // Handles which game we are dealing with
    var gameId: Int = 0

    private var lastPause: Instant = Instant.now()

    @get:Bindable
    var progress by bind(0, BR.progress)

    // Local copy of the deadline
    private var deadline: Instant = Instant.now()
        set(value) {
            field = value
            if (counter != null) {
                if (gameState == PLAYING)
                    resumeTimer(Duration.between(field, Instant.now()))
            }
        }

    // Counter used for showing remaining time
    private var counter: CountDownTimer? = null

    // Firebase
    private val firebaseInstance = FirebaseDatabase.getInstance()
    private var databaseReference = firebaseInstance.getReference("games")

    @get:Bindable
    var timerTxt by bind("", BR.timerTxt)

    @get:Bindable
    var stateTxt: String = ""
        get() = gameState.toString()

    @get:Bindable
    var idTxt: String = ""
        get() = gameId.toString()

    @get:Bindable
    var seekValueTxt by bind("", BR.seekValueTxt)

    @get:Bindable
    var playable: Boolean = false
        get() = gameState == READY || gameState == PAUSED

    @get:Bindable
    var pausable: Boolean = false
        get() = gameState == PLAYING

    @get:Bindable
    private var gameState by bind(UNKNOWN, BR.gameState, BR.playable, BR.pausable, BR.stateTxt,
            BR.idTxt, BR.timerTxt)

    @SuppressLint("MissingSuperCall")
    override fun onActive() {
        super.onActive()

        // Setup statelistener. GameState will now update automatically
        databaseReference.child(gameId.toString()).child("state")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.value != null && dataSnapshot.value != "") {
                            val data = dataSnapshot.value as String
                            setState(data.toLowerCase())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        val e = error.toException().toString()
                        Timber.w("Debug: Failed to read value: $e")
                    }
                })

        // Setup timer listener that updates
        databaseReference.child(gameId.toString()).child("deadline")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.value != "" && dataSnapshot.value != null)
                            deadline = Instant.parse(dataSnapshot.value as CharSequence?)
                        if (deadline.isBefore(Instant.now())) {
                            endGame()
                            return
                        }

                        Timber.i("Debug: Updated game deadline to: $deadline")
                        if (gameState != PAUSED && gameState != READY)
                            resumeTimer(Duration.between(Instant.now(), deadline))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        val e = error.toException().toString()
                        Timber.w("Debug: Failed to read value: $e")
                    }
                })

        // Setup up pause listener
        databaseReference.child(gameId.toString()).child("pausedAt")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.value is String && dataSnapshot.value != null)
                            lastPause = Instant.parse(dataSnapshot.value as CharSequence?)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        val e = error.toException().toString()
                        Timber.w("Debug: Failed to read value: $e")
                    }
                })

        // Setup progress listener
        databaseReference.child(gameId.toString()).child("progress")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.value is String && dataSnapshot.value != null)
                            if (gameState != ENDED)
                                progress = (dataSnapshot.value as String).toInt()

                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        val e = error.toException().toString()
                        Timber.w("Debug: Failed to read value: $e")
                    }
                })
    }

    private fun setState(data: String) {
        when (data) {
            "unknown" -> gameState = UNKNOWN
            "ready" -> {
                gameState = READY
                counter?.cancel()
                timerTxt = "---"
            }
            "playing" -> {
                val update = HashMap<String, Any>()
                if (gameState == UNKNOWN) {
                    gameState = PLAYING
                    return
                }
                if (gameState == PAUSED) {
                    try {
                        val updatedDeadline: Instant =
                                deadline.plusMillis(
                                        Duration.between(
                                                lastPause,
                                                Instant.now())
                                                .abs().toMillis())
                        update["deadline"] = updatedDeadline.toString()
                        databaseReference.child(gameId.toString()).updateChildren(update)
                    } catch (e: Exception) {
                        if (e is NullPointerException) {
                            commandHandler?.showErrorSnack("Loading save time failed. Resuming timer with current endtime.")
                            gameState = PLAYING
                        }
                    }
                }
                if (gameState == READY) {
                    update["deadline"] = Instant.now().plusSeconds(3600).toString()
                    databaseReference.child(gameId.toString()).updateChildren(update)
                }

                gameState = PLAYING
            }
            "paused" -> {
                counter?.cancel()
                gameState = PAUSED
            }
            "ended" -> {
                counter?.cancel()
                gameState = ENDED
                commandHandler?.resetProgress()
            }
        }
        Timber.i("Debug: Updated game state to: $gameState")
    }

    fun pause() {
        Timber.i("Debug: Pause clicked!")
        val update = HashMap<String, Any>()
        update["state"] = "paused"
        update["pausedAt"] = Instant.now().toString()
        Timber.i("Debug: Sending state paused")
        databaseReference.child(gameId.toString()).updateChildren(update)
        counter?.cancel()
    }

    fun play() {
        Timber.i("Debug: Play clicked!")
        val stateUpdate = HashMap<String, Any>()
        stateUpdate["state"] = "playing"
        databaseReference.child(gameId.toString()).updateChildren(stateUpdate)
    }

    private fun resumeTimer(between: Duration) {
        counter?.cancel()
        counter = object : CountDownTimer(between.abs().toMillis(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timerTxt = AppUtils.getDurationBreakdown(millisUntilFinished)
            }

            override fun onFinish() {
                timerTxt = "The Game has Ended!"
            }
        }.start()
    }

    fun updateDeadline(seconds: Long) {
        val deadlineUpdate = HashMap<String, Any>()
        val updatedDeadline: Instant =
                deadline.plusSeconds(seconds)
        deadlineUpdate["deadline"] = updatedDeadline.toString()
        databaseReference.child(gameId.toString()).updateChildren(deadlineUpdate)
    }

    fun initRestartGame() {
        Timber.i("Game restart prompted")
        commandHandler?.showRestartDialog()
    }

    fun startNewGame() {
        counter?.cancel()
        Timber.i("Game restarting")
        // Clear UI
        commandHandler?.resetProgress()
        timerTxt = "---"

        val stateUpdate = HashMap<String, Any>()
        stateUpdate["state"] = READY
        stateUpdate["progress"] = 0.toString()
        stateUpdate["requestHint"] = false
        databaseReference.child(gameId.toString()).updateChildren(stateUpdate)
        databaseReference.child(gameId.toString()).child("hints").removeValue()
    }

    private fun endGame() {
        val stateUpdate = HashMap<String, Any>()
        stateUpdate["state"] = ENDED
        Timber.i("Debug: Sending state paused")
        databaseReference.child(gameId.toString()).updateChildren(stateUpdate)
        commandHandler?.resetProgress()
    }


    fun updateProgress() {
        Timber.i("Updating Progress in Firebase")
        val progressUpdate = HashMap<String, Any>()
        progressUpdate["progress"] = seekValueTxt
        Timber.i("Sending new Progress: $seekValueTxt")
        databaseReference.child(gameId.toString()).updateChildren(progressUpdate)
    }

    enum class GameState {
        UNKNOWN, READY, PLAYING, PAUSED, ENDED
    }

    interface Commands {

        fun showRestartDialog()

        fun resetProgress()

        fun showErrorSnack(s: String)
    }
}
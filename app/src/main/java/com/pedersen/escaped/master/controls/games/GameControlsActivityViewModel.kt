package com.pedersen.escaped.master.controls.games

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

    // Local copy of the deadline
    private var deadline: Instant = Instant.now()
        set(value) {
            field = value
            if (counter != null) {
                this.resumeTimer(Duration.between(field, Instant.now()))
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
    var playable: Boolean = false
        get() = gameState == READY || gameState == PAUSED || gameState == ENDED

    @get:Bindable
    var pausable: Boolean = false
        get() = gameState == PLAYING

    @get:Bindable
    var gameState by bind(UNKNOWN, BR.gameState, BR.playable, BR.pausable)

    override fun onActive() {
        super.onActive()

        // Setup statelistener. GameState will now update automatically
        databaseReference.child(gameId.toString()).child(
                "state").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.value as String
                setState(data.toLowerCase())
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Debug: Failed to read value: $e")
            }
        })

        // Setup timer listener that updates
        databaseReference.child(gameId.toString()).child(
                "deadline").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                deadline = Instant.parse(dataSnapshot.value as CharSequence?)
                Timber.i("Debug: Updated game deadline to: $deadline")
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
            "ready" -> gameState = READY
            "playing" -> {
                if (gameState == PAUSED) {
                    resumeTimer(Duration.between(Instant.parse(commandHandler?.getPausedTimer()),
                                                 deadline))
                } else
                    resumeTimer(Duration.between(Instant.now(), deadline))
                gameState = PLAYING
            }
            "paused" -> gameState = PAUSED
            "ended" -> gameState = ENDED
        }
        Timber.i("Debug: Updated game state to: $gameState")
    }

    fun pause() {
        Timber.i("Debug: Pause clicked!")
        commandHandler?.setPausedTimer()
        val update = HashMap<String, Any>()
        update["state"] = "paused"
        Timber.i("Debug: Sending state paused")
        databaseReference.child(gameId.toString()).updateChildren(update)
        counter?.cancel()
    }

    fun play() {
        Timber.i("Debug: Play clicked!")
        val update = HashMap<String, Any>()
        update["state"] = "playing"
        if (gameState == PAUSED) {
            val newDeadline = deadline.plusMillis(Duration.between(Instant.parse(
                    commandHandler?.getPausedTimer()), Instant.now()).abs().toMillis())
            update["deadline"] = newDeadline.toString()
        }
        databaseReference.child(gameId.toString()).updateChildren(update)
    }

    private fun resumeTimer(between: Duration) {
        killTimer()
        counter = object : CountDownTimer(between.abs().toMillis(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timerTxt = AppUtils.getDurationBreakdown(millisUntilFinished)
            }

            override fun onFinish() {
                timerTxt = "Debug: done!"
            }
        }.start()
    }

    //
    private fun killTimer() {
        if (counter != null) {
            counter!!.cancel()
            counter = null
        }
    }

    fun initRestartGame() {
        Timber.i("Game restart prompted")
        commandHandler?.showRestartDialog()
    }

    fun startNewGame() {
        killTimer()
        Timber.i("Game restarting")
        val stateUpdate = HashMap<String, Any>()
        stateUpdate.put("state", "playing")
        val deadline: Instant = Instant.now().plusSeconds(3600)
        stateUpdate.put("deadline", deadline.toString())
        Timber.i("Debug: Sending state playing, deadline $deadline")
        databaseReference.child(gameId.toString()).updateChildren(stateUpdate)
    }

    enum class GameState {
        UNKNOWN, READY, PLAYING, PAUSED, ENDED
    }

    interface Commands {

        fun showRestartDialog()

        fun setPausedTimer()

        fun getPausedTimer(): String

    }
}
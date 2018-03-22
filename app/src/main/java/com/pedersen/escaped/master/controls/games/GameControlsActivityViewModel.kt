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
import io.greenerpastures.mvvm.BaseViewModel
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import timber.log.Timber


class GameControlsActivityViewModel : BaseViewModel<GameControlsActivityViewModel.Commands>() {

    var gameId: Int = 0
    private var deadline: Instant = Instant.now()
    private var counter: CountDownTimer? = null

    @get:Bindable
    var timerTxt by bind("", BR.timerTxt)

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private var databaseReference = firebaseInstance.getReference("games")

    @get:Bindable
    var playable: Boolean = false
        get() = gameState == READY || gameState == PAUSED

    @get:Bindable
    var pausable: Boolean = false
        get() = gameState == PLAYING

    @get:Bindable
    var gameState by bind(UNKNOWN, BR.gameState, BR.playable, BR.pausable)

    override fun onActive() {
        super.onActive()

        val stateListener = databaseReference.child(gameId.toString()).child("state")
        stateListener.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.value as String
                when (data.toLowerCase()) {
                    "unknown" -> gameState = UNKNOWN
                    "ready" -> gameState = READY
                    "playing" -> {
                        gameState = PLAYING
                        resumeTimer(Duration.between(Instant.now(), this@GameControlsActivityViewModel.deadline))

                    }
                    "paused" -> gameState = PAUSED
                    "ended" -> gameState = ENDED
                }
                Timber.i("Debug: Updated game state to: $gameState")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Debug: Failed to read value: $e")
            }
        })

        val timerListener = databaseReference.child(gameId.toString()).child("deadline")
        timerListener.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                deadline = Instant.parse(dataSnapshot.value as CharSequence?)
                this@GameControlsActivityViewModel.resumeTimer(Duration.between(deadline, Instant.now()))
                Timber.i("Debug: Updated game deadline to: $deadline")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Debug: Failed to read value: $e")
            }
        })
    }

    fun pause() {
        Timber.i("Debug: Pause clicked!")
        setPausedTime()
        val stateUpdate = HashMap<String, Any>()
        stateUpdate.put("state", "paused")
        Timber.i("Debug: Sending state paused")
        databaseReference.child(gameId.toString()).updateChildren(stateUpdate)
        killTimer()
    }

    private fun setPausedTime() {
        Timber.i("Debug: Pause clicked!")
        commandHandler?.setPausedTimer()
    }

    fun play() {
        Timber.i("Debug: Play clicked!")
        if (gameState == PAUSED) {
            Timber.i("Debug: Game is currently paused, prepare to set it to playing and update deadline based on pause duration!")
            val stateAndDeadlineUpdate = HashMap<String, Any>()
            stateAndDeadlineUpdate.put("state", "playing")
            val newDeadline = deadline.plusMillis(Duration.between(Instant.parse(
                    commandHandler?.getPausedTimer()), Instant.now()).abs().toMillis())
            stateAndDeadlineUpdate.put("deadline", newDeadline.toString())
            databaseReference.child(gameId.toString()).updateChildren(stateAndDeadlineUpdate)
            Timber.i("Debug: Sending state change to Firebase: playing, and new Deadline: $newDeadline")
        } else
            Timber.d("Debug: Game is not paused -> doRestartGame()")
            doRestartGame()
    }

    private fun resumeTimer(between: Duration) {
        counter = object : CountDownTimer(between.abs().toMillis(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timerTxt = "Debug: seconds remaining: " + (millisUntilFinished / 1000)
            }

            override fun onFinish() {
                timerTxt = "Debug: done!"
            }
        }.start()
    }

    private fun killTimer() {
        if (counter != null) {
            counter!!.cancel()
            counter = null
        }
    }

    fun initRestartGame() {
        Timber.i("Game restarted")
        commandHandler?.showRestartDialog()
    }

    fun doRestartGame() {
        val stateUpdate = HashMap<String, Any>()
        stateUpdate.put("state", "playing")
        val deadline: Instant = Instant.now().plusSeconds(3600)
        stateUpdate.put("deadline", deadline.toString())
        Timber.i("Debug: Seginding state playing, deadline $deadline")
        databaseReference.child(gameId.toString()).updateChildren(stateUpdate)
    }

    enum class GameState {
        UNKNOWN, READY, PLAYING, PAUSED, ENDED
    }

    interface Commands {

        fun showRestartDialog()

        fun setPausedTimer()

        fun getPausedTimer() : String

    }
}
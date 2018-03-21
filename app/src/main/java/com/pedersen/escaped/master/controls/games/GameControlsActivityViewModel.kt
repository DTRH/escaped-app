package com.pedersen.escaped.master.controls.games

import android.databinding.Bindable
import com.pedersen.escaped.master.controls.games.GameControlsActivityViewModel.GameState.*
import io.greenerpastures.mvvm.BaseViewModel
import org.threeten.bp.Instant
import timber.log.Timber
import java.util.*
import android.os.CountDownTimer
import com.google.firebase.database.*
import com.pedersen.escaped.BR
import com.pedersen.escaped.extensions.bind
import org.threeten.bp.Duration


class GameControlsActivityViewModel : BaseViewModel<GameControlsActivityViewModel.Commands>() {

    var gameId: Int = 0
    var deadline: Instant = Instant.now()

    @get:Bindable
    var timer by bind("", BR.timer)

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
                        this@GameControlsActivityViewModel.resumeTimer(Duration.between(deadline, Instant.now()))
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

    }

    private fun setPausedTime() {
        Timber.i("Debug: Pause clicked!")

    }

    fun play() {
        Timber.i("Debug: Play clicked!")
        if (gameState == PAUSED) {
            Timber.i("Debug: Game was paused, prepare to set it to playing!")
            val stateUpdate = HashMap<String, Any>()
            stateUpdate.put("state", "playing")
            databaseReference.child(gameId.toString()).updateChildren(stateUpdate)
            Timber.i("Debug: Attempted to set state: playing")
        } else
            Timber.d("Debug: Not paused, doRestartGame()")
            doRestartGame()
    }

    private fun resumeTimer(between: Duration) {
        object : CountDownTimer(between.abs().toMillis(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timer = "Debug: seconds remaining: " + (millisUntilFinished / 1000)
            }

            override fun onFinish() {
                timer = "Debug: done!"
            }
        }.start()
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
        databaseReference.child(gameId.toString()).updateChildren(stateUpdate, { databaseError, databaseReference ->
            Timber.i("Done!")
        })
    }

    enum class GameState {
        UNKNOWN, READY, PLAYING, PAUSED, ENDED
    }

    interface Commands {

        fun showRestartDialog()

    }
}
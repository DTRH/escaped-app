package com.pedersen.escaped.master.controls.games

import android.databinding.Bindable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pedersen.escaped.master.controls.games.GameControlsActivityViewModel.GameState.*
import io.greenerpastures.mvvm.BaseViewModel
import org.threeten.bp.Instant
import timber.log.Timber
import java.util.*
import android.os.CountDownTimer
import org.threeten.bp.Duration


class GameControlsActivityViewModel : BaseViewModel<GameControlsActivityViewModel.Commands>() {

    var gameId: Int = 0
    lateinit var deadline: Instant
    lateinit var timerText: String

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private var databaseReference = firebaseInstance.getReference("games")

    @get:Bindable
    var playable: Boolean = false
        get() = gameState == READY || gameState == PAUSED

    @get:Bindable
    var pausable: Boolean = false
        get() = gameState == PLAYING

    @get:Bindable
    var gameState: GameState = UNKNOWN
        set(value) {
            when (value) {
                UNKNOWN -> {
                    Timber.i("Game mode changed to: UNKNOWN")
                }
                READY -> {
                    Timber.i("Game mode changed to: READY")
                }
                PLAYING -> {
                    Timber.i("Game mode changed to: PLAYING")
                }
                PAUSED -> {
                    Timber.i("Game mode changed to: PAUSED")
                }
                ENDED -> {
                    Timber.i("Game mode changed to: ENDED")
                }
            }
            notifyChange()
        }

    override fun onActive() {
        super.onActive()



        val stateListener = databaseReference.child(gameId.toString()).child("state")
        stateListener.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val gameState = dataSnapshot.value as String
                when (gameState.toLowerCase()) {
                    "unknown" -> this@GameControlsActivityViewModel.gameState = UNKNOWN
                    "ready" -> this@GameControlsActivityViewModel.gameState = READY
                    "playing" -> this@GameControlsActivityViewModel.gameState = PLAYING
                    "paused" -> this@GameControlsActivityViewModel.gameState = PAUSED
                    "ended" -> this@GameControlsActivityViewModel.gameState = ENDED
                }
                Timber.i("Updated game state to: $gameState")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Failed to read value: $e")
            }
        })

        val timerListener = databaseReference.child(gameId.toString()).child("deadline")
        timerListener.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                deadline = dataSnapshot.value as Instant
                Timber.i("Updated game state to: $gameState")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Failed to read value: $e")
            }
        })
    }

    fun pause() {

    }

    fun play() {
        if (gameState == PAUSED) {
            gameState = PLAYING
            resumeTimer(Duration.between(deadline, Instant.now()))
        } else
            doRestartGame()
    }

    private fun resumeTimer(between: Duration) {
        object : CountDownTimer(between.toMillis(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timerText = "seconds remaining: " + (millisUntilFinished / 1000)
            }

            override fun onFinish() {
                timerText = "done!"
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
        databaseReference.child(gameId.toString()).updateChildren(stateUpdate)
    }

    enum class GameState {
        UNKNOWN, READY, PLAYING, PAUSED, ENDED
    }

    interface Commands {

        fun showRestartDialog()

    }
}
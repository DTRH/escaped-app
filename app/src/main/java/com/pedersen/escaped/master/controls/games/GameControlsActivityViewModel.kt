package com.pedersen.escaped.master.controls.games

import android.databinding.Bindable
import com.google.firebase.database.*
import com.pedersen.escaped.master.controls.games.GameControlsActivityViewModel.GameState.*
import io.greenerpastures.mvvm.BaseViewModel
import timber.log.Timber

class GameControlsActivityViewModel : BaseViewModel<GameControlsActivityViewModel.Commands>() {

    var gameId: Int = 0

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private lateinit var stateListener: DatabaseReference

    @get:Bindable
    var gameState: GameState = UNKNOWN
    set(value) {
        when (value) {
            UNKNOWN -> TODO()
            READY -> TODO()
            PLAYING -> TODO()
            PAUSED -> TODO()
            ENDED -> TODO()
        }
    }

    override fun onActive() {
        super.onActive()

        stateListener = firebaseInstance.getReference("games").child(gameId.toString()).child("state")
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
    }

    fun restartGame() {
        Timber.i("Game restarted")
        commandHandler?.showRestartDialog()
    }

    enum class GameState {
        UNKNOWN, READY, PLAYING, PAUSED, ENDED
    }

    interface Commands {

        fun showRestartDialog()

    }
}
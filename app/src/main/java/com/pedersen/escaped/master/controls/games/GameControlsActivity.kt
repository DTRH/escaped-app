package com.pedersen.escaped.master.controls.games

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityGameControlsBinding
import io.greenerpastures.mvvm.ViewModelActivity
import timber.log.Timber

class GameControlsActivity : ViewModelActivity<GameControlsActivityViewModel, ActivityGameControlsBinding>(), GameControlsActivityViewModel.Commands {

    private var gameId: Int = 0

    private var gameState: String = ""

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private lateinit var stateListener: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_game_controls, BR.viewModel, ({ GameControlsActivityViewModel() }))
        super.onCreate(savedInstanceState)

        gameId = intent.extras.get(GameControlsActivity.GAME_ID) as Int

        stateListener = firebaseInstance.getReference("games").child(gameId.toString()).child("state")
        stateListener.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                gameState = dataSnapshot.value as String
                Timber.i("Updated game state to: $gameState")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Failed to read value: $e")
            }
        })

    }

    companion object {

        private const val GAME_ID = "game_id"

        fun newIntent(context: Context, gameId: Int) : Intent {
            val intent = Intent(context, GameControlsActivity::class.java)
            intent.putExtra(GAME_ID, gameId)
            return intent
        }
    }
}

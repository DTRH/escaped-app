package com.pedersen.escaped.master.controls.games

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityGameControlsBinding
import io.greenerpastures.mvvm.ViewModelActivity

class GameControlsActivity : ViewModelActivity<GameControlsActivityViewModel, ActivityGameControlsBinding>(), GameControlsActivityViewModel.Commands {

    private var gameId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_game_controls, BR.viewModel, ({ GameControlsActivityViewModel() }))
        super.onCreate(savedInstanceState)

        gameId = intent.extras.get(GameControlsActivity.GAME_ID) as Int
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

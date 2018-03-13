package com.pedersen.escaped.master

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityGameMasterBinding
import com.pedersen.escaped.master.games.dinner.DinnerRoomActivity
import com.pedersen.escaped.master.games.one.GameOneActivity
import com.pedersen.escaped.master.games.two.GameTwoActivity
import io.greenerpastures.mvvm.ViewModelActivity

class GameMasterActivity : ViewModelActivity<GameMasterActivityViewModel, ActivityGameMasterBinding>(), GameMasterActivityViewModel.Commands {

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_game_master, BR.viewModel, ({ GameMasterActivityViewModel() }))
        super.onCreate(savedInstanceState)
    }

    override fun launchGameActivity(id: Int) {
        when (id) {
            1 -> startActivity(GameOneActivity.newIntent(this))
            2 -> startActivity(GameTwoActivity.newIntent(this))
            3 -> startActivity(DinnerRoomActivity.newIntent(this))
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, GameMasterActivity::class.java)
        }
    }
}

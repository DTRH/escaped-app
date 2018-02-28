package com.pedersen.escaped.master

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityGameMasterBinding
import io.greenerpastures.mvvm.ViewModelActivity

class GameMasterActivity : ViewModelActivity<GameMasterActivityViewModel, ActivityGameMasterBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_game_master, BR.viewModel, ({ GameMasterActivityViewModel() }))
        super.onCreate(savedInstanceState)

    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, GameMasterActivity::class.java)
        }
    }
}

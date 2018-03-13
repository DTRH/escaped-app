package com.pedersen.escaped.master.games.two

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityGameTwoBinding
import io.greenerpastures.mvvm.ViewModelActivity

class GameTwoActivity : ViewModelActivity<GameTwoActivityViewModel, ActivityGameTwoBinding>(), GameTwoActivityViewModel.Commands {

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_game_two, BR.viewModel, ({ GameTwoActivityViewModel() }))
        super.onCreate(savedInstanceState)
    }


    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, GameTwoActivity::class.java)
        }
    }
}

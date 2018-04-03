package com.pedersen.escaped.master.games.one

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.pedersen.escaped.BR
import com.pedersen.escaped.BuildConfig
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityGameOneBinding
import com.pedersen.escaped.master.controls.games.GameControlsActivity
import com.pedersen.escaped.master.controls.hints.HintControlsActivity
import com.pedersen.escaped.utils.AppUtils
import io.greenerpastures.mvvm.ViewModelActivity

class GameOneActivity : ViewModelActivity<GameOneActivityViewModel, ActivityGameOneBinding>(), GameOneActivityViewModel.Commands {

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_game_one, BR.viewModel, ({ GameOneActivityViewModel() }))
        super.onCreate(savedInstanceState)
    }

    override fun launchHintControls() {
        startActivity(HintControlsActivity.newIntent(this, 1))
    }

    override fun launchVideoControls() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun launchGameControls() {
        startActivity(GameControlsActivity.newIntent(this, 1))
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, GameOneActivity::class.java)
        }
    }
}

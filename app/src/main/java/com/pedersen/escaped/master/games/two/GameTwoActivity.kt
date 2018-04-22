package com.pedersen.escaped.master.games.two

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.pedersen.escaped.BR
import com.pedersen.escaped.BuildConfig
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityGameTwoBinding
import com.pedersen.escaped.master.controls.games.GameControlsActivity
import com.pedersen.escaped.master.controls.hints.HintControlsActivity
import com.pedersen.escaped.master.controls.videos.VideoControlsActivity
import com.pedersen.escaped.utils.AppUtils
import io.greenerpastures.mvvm.ViewModelActivity

class GameTwoActivity : ViewModelActivity<GameTwoActivityViewModel, ActivityGameTwoBinding>(), GameTwoActivityViewModel.Commands {

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_game_two, BR.viewModel, ({ GameTwoActivityViewModel() }))
        super.onCreate(savedInstanceState)
    }

    override fun launchHintControls() {
        startActivity(HintControlsActivity.newIntent(this, 2))
    }

    override fun launchVideoControls() {
        startActivity(VideoControlsActivity.newIntent(this, 2))
    }

    override fun launchGameControls() {
        startActivity(GameControlsActivity.newIntent(this, 2))
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, GameTwoActivity::class.java)
        }
    }
}

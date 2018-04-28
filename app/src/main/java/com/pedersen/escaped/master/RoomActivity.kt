package com.pedersen.escaped.master

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.BuildConfig
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityRoomBinding
import com.pedersen.escaped.master.controls.games.GameControlsActivity
import com.pedersen.escaped.master.controls.hints.HintControlsActivity
import com.pedersen.escaped.master.controls.videos.VideoControlsActivity

import io.greenerpastures.mvvm.ViewModelActivity

class RoomActivity : ViewModelActivity<RoomActivityViewModel, ActivityRoomBinding>(), RoomActivityViewModel.Commands {

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_room, BR.viewModel, ({ RoomActivityViewModel() }))
        super.onCreate(savedInstanceState)
    }

    override fun launchHintControls() {
        startActivity(HintControlsActivity.newIntent(this, BuildConfig.gameId))
    }

    override fun launchVideoControls() {
        startActivity(VideoControlsActivity.newIntent(this, BuildConfig.gameId))
    }

    override fun launchGameControls() {
        startActivity(GameControlsActivity.newIntent(this, BuildConfig.gameId))
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, RoomActivity::class.java)
        }
    }
}

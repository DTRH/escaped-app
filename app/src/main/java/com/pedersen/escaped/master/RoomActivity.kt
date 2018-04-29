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

    private lateinit var id: Number

    override fun onCreate(savedInstanceState: Bundle?) {
        id = intent.extras.get(GAME_ID) as Int
        initialize(R.layout.activity_room, BR.viewModel, ({ RoomActivityViewModel() }))
        super.onCreate(savedInstanceState)
    }

    override fun launchHintControls() {
        startActivity(HintControlsActivity.newIntent(this, id.toInt()))
    }

    override fun launchVideoControls() {
        startActivity(VideoControlsActivity.newIntent(this, id.toInt()))
    }

    override fun launchGameControls() {
        startActivity(GameControlsActivity.newIntent(this, id.toInt()))
    }

    companion object {

        const val GAME_ID = "selected_game_id"

        fun newIntent(context: Context, id: Int): Intent {
            val args = Bundle(1)
            args.putInt(GAME_ID, id)
            val intent = Intent(context, RoomActivity::class.java)
            intent.putExtras(args)
            return intent
        }
    }
}

package com.pedersen.escaped.master.controls.videos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityVideoControlsBinding
import io.greenerpastures.mvvm.ViewModelActivity

class VideoControlsActivity : ViewModelActivity<VideoControlsViewModel, ActivityVideoControlsBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_video_controls, BR.viewModel, ({ VideoControlsViewModel() }))
        super.onCreate(savedInstanceState)
    }

    companion object {

        private const val GAME_ID = "game_id"

        fun newIntent(context: Context, gameId: Int): Intent {
            val intent = Intent(context, VideoControlsActivity::class.java)
            intent.putExtra(GAME_ID, gameId)
            return intent
        }
    }
}

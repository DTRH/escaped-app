package com.pedersen.escaped.master.games.one

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.pedersen.escaped.BR
import com.pedersen.escaped.BuildConfig
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityGameOneBinding
import com.pedersen.escaped.master.controls.hints.HintControlsActivity
import io.greenerpastures.mvvm.ViewModelActivity

class GameOneActivity : ViewModelActivity<GameOneActivityViewModel, ActivityGameOneBinding>(), GameOneActivityViewModel.Commands {

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_game_one, BR.viewModel, ({ GameOneActivityViewModel() }))
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        // Remove all system UI
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    override fun onBackPressed() {
        val count = fragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            fragmentManager.popBackStack()
        }
    }

    override fun launchHintControls() {
        startActivity(HintControlsActivity.newIntent(this, 1))
    }

    override fun launchVideoControls() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun launchRoomControls() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, GameOneActivity::class.java)
        }
    }
}

package com.pedersen.escaped.master.controls.games

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityGameControlsBinding
import io.greenerpastures.mvvm.ViewModelActivity
import org.threeten.bp.Instant
import com.pedersen.escaped.BuildConfig


class GameControlsActivity : ViewModelActivity<GameControlsActivityViewModel, ActivityGameControlsBinding>(), GameControlsActivityViewModel.Commands {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        val int = intent.extras.get(GameControlsActivity.GAME_ID) as Int
        initialize(R.layout.activity_game_controls, BR.viewModel,
                   ({ GameControlsActivityViewModel().apply { gameId = int } }))
        super.onCreate(savedInstanceState)

        sharedPref = getPreferences(Context.MODE_PRIVATE)
    }

    override fun showRestartDialog() {
        val restartDialog = AlertDialog.Builder(this@GameControlsActivity).create()
        restartDialog.setTitle("Alert")
        restartDialog.setMessage("This will delete any current game content!")

        restartDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", { dialogInterface, i ->
            viewModel.doRestartGame()
        })

        restartDialog.show()
    }

    override fun setPausedTimer() {
        val editor = sharedPref.edit()
        if (BuildConfig.gameId == 1)
            editor.putString(PAUSED_TIME_GAME_ONE, Instant.now().toString())
        else
            editor.putString(PAUSED_TIME_GAME_TWO, Instant.now().toString())
        editor.apply()
    }

    override fun getPausedTimer(): String {
        return if (BuildConfig.gameId == 1)
            sharedPref.getString(PAUSED_TIME_GAME_ONE, "")
        else
            sharedPref.getString(PAUSED_TIME_GAME_TWO, "")
    }

    companion object {

        private const val GAME_ID = "game_id"
        private const val PAUSED_TIME_GAME_ONE = "paused_time_one"
        private const val PAUSED_TIME_GAME_TWO = "paused_time_two"

        fun newIntent(context: Context, gameId: Int): Intent {
            val intent = Intent(context, GameControlsActivity::class.java)
            intent.putExtra(GAME_ID, gameId)
            return intent
        }
    }
}

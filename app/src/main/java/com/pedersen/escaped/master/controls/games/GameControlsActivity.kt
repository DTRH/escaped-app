package com.pedersen.escaped.master.controls.games

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.SeekBar
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityGameControlsBinding
import io.greenerpastures.mvvm.ViewModelActivity
import org.threeten.bp.Instant

class GameControlsActivity : ViewModelActivity<GameControlsActivityViewModel, ActivityGameControlsBinding>(), GameControlsActivityViewModel.Commands {

    private var gameId: Int = 0
    private lateinit var sharedPref: SharedPreferences
    private lateinit var seekbar: SeekBar

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        gameId = intent.extras.get(GameControlsActivity.GAME_ID) as Int
        initialize(R.layout.activity_game_controls, BR.viewModel,
                   ({ GameControlsActivityViewModel().apply { gameId = this@GameControlsActivity.gameId } }))
        super.onCreate(savedInstanceState)

        sharedPref = getSharedPreferences("gamePrefs", Context.MODE_PRIVATE)

        seekbar = binding.seekBar
        seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.updateProgress()
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.seekValueTxt = progress.toString()
            }

        })
    }

    override fun showRestartDialog() {
        val restartDialog = AlertDialog.Builder(this@GameControlsActivity).create()
        restartDialog.setTitle("Alert")
        restartDialog.setMessage("This will delete any current game content, and begin a new game right away!")

        restartDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", { _, _ ->
            viewModel.startNewGame()
        })

        restartDialog.show()
    }

    @SuppressLint("ApplySharedPref")
    override fun setPausedTimer(id: Int) {
        val editor = sharedPref.edit()
        when (id) {
            1 -> editor.putString(PAUSED_TIME_GAME_ONE, Instant.now().toString()).commit()
            2 -> editor.putString(PAUSED_TIME_GAME_TWO, Instant.now().toString()).commit()
        }
    }

    override fun getPausedTimer(id: Int): String {
        return when (gameId) {
            1 -> sharedPref.getString("paused_time_one", "")
            2 -> sharedPref.getString("paused_time_two", "")
            else -> "No paused time found for gameId: $gameId"
        }
    }

    override fun resetProgress() {
        seekbar.progress = 0
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

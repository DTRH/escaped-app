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
import com.pedersen.escaped.utils.AppUtils
import io.greenerpastures.mvvm.ViewModelActivity
import org.threeten.bp.Instant

class GameControlsActivity : ViewModelActivity<GameControlsActivityViewModel, ActivityGameControlsBinding>(), GameControlsActivityViewModel.Commands {

    private var gameId: Int = 0
    private lateinit var sharedPref: SharedPreferences
    private lateinit var seekBar: SeekBar

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        gameId = intent.extras.get(GameControlsActivity.GAME_ID) as Int
        initialize(R.layout.activity_game_controls, BR.viewModel,
                   ({ GameControlsActivityViewModel().apply { gameId = this@GameControlsActivity.gameId } }))
        super.onCreate(savedInstanceState)

        sharedPref = getSharedPreferences(packageName, Context.MODE_PRIVATE)

        seekBar = binding.seekBar
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.updateProgress()
            }
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.seekValueTxt = progress.toString()
                val editor = sharedPref.edit()
                when (viewModel.gameId) {
                    1 -> editor.putInt(PROGRESS_GAME_ONE, progress)
                    2 -> editor.putInt(PROGRESS_GAME_TWO, progress)
                }
                editor.commit()
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
  //  override fun setPausedTimer(id: Int) {
  //      val editor = sharedPref.edit()
  //      when (id) {
  //          1 -> {
  //              val instant = Instant.now().toString()
  //              editor.putString(PAUSED_TIME_GAME_ONE, instant)
  //              editor.commit()
  //          }
  //          2 -> editor.putString(PAUSED_TIME_GAME_TWO, Instant.now().toString()).commit()
  //      }
  //  }

  //  override fun getPausedTimer(id: Int): String {
  //      return when (gameId) {
  //          1 -> sharedPref.getString(PAUSED_TIME_GAME_ONE, "")
  //          2 -> sharedPref.getString(PAUSED_TIME_GAME_TWO, "")
  //          else -> "No paused time found for gameId: $gameId"
  //      }
  //  }

    override fun resetProgress() {
        seekBar.progress = 0
    }

   // override fun setProgressBar(progress: Int) {
   //     seekBar.progress = progress
   // }

    override fun showErrorSnack(s: String) {
        AppUtils.showSnack(s, window.decorView.rootView)
    }

    companion object {

        private const val GAME_ID = "game_id"

        private const val PAUSED_TIME_GAME_ONE : String = "paused_time_one"
        private const val PAUSED_TIME_GAME_TWO : String = "paused_time_two"

        private const val PROGRESS_GAME_ONE : String = "progress_game_one"
        private const val PROGRESS_GAME_TWO : String = "progress_game_two"

        fun newIntent(context: Context, gameId: Int): Intent {
            val intent = Intent(context, GameControlsActivity::class.java)
            intent.putExtra(GAME_ID, gameId)
            return intent
        }
    }
}

package com.pedersen.escaped.master.controls.games

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityGameControlsBinding
import com.pedersen.escaped.utils.AppUtils
import io.greenerpastures.mvvm.ViewModelActivity
import kotlinx.android.synthetic.main.activity_game_controls.*


class GameControlsActivity : ViewModelActivity<GameControlsActivityViewModel, ActivityGameControlsBinding>(), GameControlsActivityViewModel.Commands {

    private var gameId: Int = 0
    private lateinit var seekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        gameId = intent.extras.get(GameControlsActivity.GAME_ID) as Int
        initialize(R.layout.activity_game_controls, BR.viewModel, ({ GameControlsActivityViewModel()
                .apply { gameId = this@GameControlsActivity.gameId } }))
        super.onCreate(savedInstanceState)

        seekBar = binding.seekBar
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.updateProgress()
            }
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.seekValueTxt = progress.toString()
            }
        })

        // Setup keyboard behavior
        binding.deadlineUpdateInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // hide virtual keyboard
                val imm = this.getSystemService(
                        Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)

                try {
                    showAddTimeDialog(binding.deadlineUpdateInput.text.toString().toLong())
                } catch (e: Exception) {
                    AppUtils.showSnack("Something went wrong!", root)
                }

                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun showAddTimeDialog(seconds: Long) {
        val newDeadlineDialog = AlertDialog.Builder(this@GameControlsActivity).create()
        newDeadlineDialog.setTitle("Alert")
        newDeadlineDialog.setMessage("This will add $seconds seconds to the current timer!")
        newDeadlineDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            viewModel.updateDeadline(seconds)
            binding.deadlineUpdateInput.setText("")
            binding.deadlineUpdateInput.clearFocus()
        }
        newDeadlineDialog.show()
    }

    override fun showRestartDialog() {
        val restartDialog = AlertDialog.Builder(this@GameControlsActivity).create()
        restartDialog.setTitle("Alert")
        restartDialog.setMessage("This will delete any current game content!")
        restartDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            val builder = AlertDialog.Builder(this)
            builder.setMessage("OBS: Select language for the players!")
                    .setCancelable(false)
                    .setPositiveButton("English") { dialog, id -> viewModel.startNewGame(GameControlsActivityViewModel.SupportedLanguages.ENGLISH) }
                    .setNegativeButton("Danish") { dialog, id -> viewModel.startNewGame(GameControlsActivityViewModel.SupportedLanguages.DANISH) }
            val alert = builder.create()
            alert.show()
        }
        restartDialog.show()
    }

    override fun resetProgress() {
        seekBar.progress = 0
    }

    override fun showErrorSnack(s: String) {
        AppUtils.showSnack(s, window.decorView.rootView)
    }

    companion object {

        private const val GAME_ID = "game_id"

        fun newIntent(context: Context, gameId: Int): Intent {
            val intent = Intent(context, GameControlsActivity::class.java)
            intent.putExtra(GAME_ID, gameId)
            return intent
        }
    }
}

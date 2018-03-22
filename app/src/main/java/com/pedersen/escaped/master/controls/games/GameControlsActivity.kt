package com.pedersen.escaped.master.controls.games

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityGameControlsBinding
import com.stylingandroid.prefekt.prefekt
import io.greenerpastures.mvvm.ViewModelActivity
import org.threeten.bp.Instant
import timber.log.Timber


class GameControlsActivity : ViewModelActivity<GameControlsActivityViewModel, ActivityGameControlsBinding>(), GameControlsActivityViewModel.Commands {

    // SharedPreferences object via Prefekt
    private var pausedTime = prefekt(PAUSED_TIME, DEFAULT_TIME) {
        Timber.i("New Value: $it stored as PAUSED_TIME")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val int = intent.extras.get(GameControlsActivity.GAME_ID) as Int
        initialize(R.layout.activity_game_controls, BR.viewModel,
                   ({ GameControlsActivityViewModel().apply { gameId = int } }))
        super.onCreate(savedInstanceState)
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
        pausedTime.setValue(Instant.now().toString())
    }

    override suspend fun getPausedTimer(): String {
        return pausedTime.getValue()
    }

    companion object {

        private const val GAME_ID = "game_id"
        private const val PAUSED_TIME = "paused_time"
        private const val DEFAULT_TIME = "-"

        fun newIntent(context: Context, gameId: Int): Intent {
            val intent = Intent(context, GameControlsActivity::class.java)
            intent.putExtra(GAME_ID, gameId)
            return intent
        }
    }
}

package com.pedersen.escaped.master.controls.games

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityGameControlsBinding
import io.greenerpastures.mvvm.ViewModelActivity

class GameControlsActivity : ViewModelActivity<GameControlsActivityViewModel, ActivityGameControlsBinding>(), GameControlsActivityViewModel.Commands {

    override fun onCreate(savedInstanceState: Bundle?) {
        val int = intent.extras.get(GameControlsActivity.GAME_ID) as Int
        initialize(R.layout.activity_game_controls, BR.viewModel, ({ GameControlsActivityViewModel().apply { gameId = int } }))
        super.onCreate(savedInstanceState)
    }

    override fun showRestartDialog() {
        val simpleAlert = AlertDialog.Builder(this@GameControlsActivity).create()
        simpleAlert.setTitle("Alert")
        simpleAlert.setMessage("This will delete any current game content!")

        simpleAlert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", {
            dialogInterface, i ->
            this.viewModel.doRestartGame()
        })

        simpleAlert.show()
    }


    companion object {

        private const val GAME_ID = "game_id"

        fun newIntent(context: Context, gameId: Int) : Intent {
            val intent = Intent(context, GameControlsActivity::class.java)
            intent.putExtra(GAME_ID, gameId)
            return intent
        }
    }
}

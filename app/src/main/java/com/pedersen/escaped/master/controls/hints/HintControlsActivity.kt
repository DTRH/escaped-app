package com.pedersen.escaped.master.controls.hints

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.HintControlsFragmentBinding
import io.greenerpastures.mvvm.ViewModelActivity

class HintControlsActivity : ViewModelActivity<HintControlsActivityViewModel, HintControlsFragmentBinding>(), HintControlsActivityViewModel.Commands {

    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = intent
        initialize(R.layout.hint_controls_fragment, BR.viewModel, ({ HintControlsActivityViewModel(intent.extras[GAME_ID].toString())}))
        super.onCreate(savedInstanceState)
    }

    override fun updateHintList() {

    }

    companion object {

        private const val GAME_ID = "game_id"

        fun newIntent(context: Context, gameId: Int): Intent {
            val intent = Intent(context, HintControlsActivity::class.java)
            intent.putExtra(GAME_ID, gameId)
            return intent
        }
    }



}

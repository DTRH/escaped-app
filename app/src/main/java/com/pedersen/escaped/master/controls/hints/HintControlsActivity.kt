package com.pedersen.escaped.master.controls.hints

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.BaseAdapter
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.data.models.adapters.HintsAdapter
import com.pedersen.escaped.databinding.HintControlsFragmentBinding
import com.pedersen.escaped.player.HintFragment
import io.greenerpastures.mvvm.ViewModelActivity

class HintControlsActivity : ViewModelActivity<HintControlsActivityViewModel, HintControlsFragmentBinding>(), HintControlsActivityViewModel.Commands {

    private lateinit var hintAdapter: BaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = intent
        initialize(R.layout.hint_controls_fragment, BR.viewModel, ({ HintControlsActivityViewModel().apply { gameId = intent.extras[GAME_ID] as Int }}))
        super.onCreate(savedInstanceState)

        // Setup the adapter and container that will
        hintAdapter = HintsAdapter(this, viewModel.hintList)
        val hintContainer = binding.listContainer
        hintContainer.adapter = hintAdapter
        hintContainer.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val hintFragment = HintFragment.newInstance(viewModel.hintList[position])
            fragmentManager.beginTransaction().replace(R.id.fragment_container, hintFragment).commit()
        }
    }

    override fun updateHintList() {
        hintAdapter.notifyDataSetChanged()
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

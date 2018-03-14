package com.pedersen.escaped.master.controls.hints

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.AdapterView
import android.widget.BaseAdapter
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.data.models.adapters.HintsAdapter
import com.pedersen.escaped.databinding.HintControlsFragmentBinding
import io.greenerpastures.mvvm.ViewModelActivity

class HintControlsActivity : ViewModelActivity<HintControlsActivityViewModel, HintControlsFragmentBinding>(), HintControlsActivityViewModel.Commands {

    private lateinit var hintAdapter: BaseAdapter
    private var selectedId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = intent
        val id = intent.extras.get(GAME_ID)
        initialize(R.layout.hint_controls_fragment, BR.viewModel, ({ HintControlsActivityViewModel().apply {
            gameId = id as Int
        } }))
        super.onCreate(savedInstanceState)

        // Setup the adapter and container that will
        hintAdapter = HintsAdapter(this, viewModel.hintList)
        val hintContainer = binding.listContainer
        hintContainer.adapter = hintAdapter
        hintContainer.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
            selectedId = viewModel.hintList[position].id
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

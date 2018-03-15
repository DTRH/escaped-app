package com.pedersen.escaped.master.controls.hints

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.KeyEvent
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.data.models.adapters.HintsAdapter
import com.pedersen.escaped.databinding.HintControlsFragmentBinding
import io.greenerpastures.mvvm.ViewModelActivity
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.R.drawable.edit_text
import android.app.PendingIntent.getActivity


class HintControlsActivity : ViewModelActivity<HintControlsActivityViewModel, HintControlsFragmentBinding>(), HintControlsActivityViewModel.Commands {

    private lateinit var hintAdapter: BaseAdapter
    private lateinit var hintContainer: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = intent
        val id = intent.extras.get(GAME_ID)
        initialize(R.layout.hint_controls_fragment, BR.viewModel, ({ HintControlsActivityViewModel().apply {
            gameId = id as Int
        } }))
        super.onCreate(savedInstanceState)

        // Setup keyboard behavior
        binding.bodyInput.setOnEditorActionListener(
                { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // hide virtual keyboard
                        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                        return@setOnEditorActionListener true
                    }
                    false
                })

        // Setup the adapter and container that will
        hintAdapter = HintsAdapter(this, viewModel.hintList)
        hintContainer = binding.listContainer
        hintContainer.adapter = hintAdapter
        hintContainer.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            if (viewModel.selectedId.contains(viewModel.hintList[position].id)) {
                viewModel.selectedId.remove(viewModel.hintList[position].id)
                view.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            } else {
                viewModel.selectedId.add(viewModel.hintList[position].id)
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
            }
            viewModel.notifyPropertyChanged(BR.deletable)
            viewModel.notifyPropertyChanged(BR.editable)
        }
    }

    override fun updateHintList() {
        hintAdapter.notifyDataSetChanged()
    }

    override fun checkCreatable(): Boolean {
        return binding.headerInput.length() != 0 && binding.bodyInput.length() != 0
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

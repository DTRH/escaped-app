package com.pedersen.escaped.master.controls.hints

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
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
import com.google.firebase.database.*
import com.pedersen.escaped.data.models.Hint
import timber.log.Timber

class HintControlsActivity : ViewModelActivity<HintControlsActivityViewModel, HintControlsFragmentBinding>(), HintControlsActivityViewModel.Commands {

    var gameId: Int = 0

    private lateinit var hintAdapter: BaseAdapter
    private lateinit var hintContainer: ListView

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private lateinit var hintsDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.hint_controls_fragment, BR.viewModel, ({ HintControlsActivityViewModel() }))
        super.onCreate(savedInstanceState)

        gameId = intent.extras.get(GAME_ID) as Int

        hintsDatabase = firebaseInstance.getReference("games").child(gameId.toString()).child("hints")

        // Read from the firebaseInstance
        hintsDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                viewModel.hintList.clear()
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (hintChild in dataSnapshot.children) {
                    val hint = hintChild.getValue(Hint::class.java)
                    hint.let { hint!!.key = hintChild.key }
                    hint?.let { viewModel.hintList.add(it) }
                }

                hintAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Failed to read value: $e")
            }
        })

        // Setup keyboard behavior
        binding.bodyInput.setOnEditorActionListener(
                { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // hide virtual keyboard
                        val imm = this.getSystemService(
                                Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                        viewModel.notifyPropertyChanged(BR.creatable)
                        return@setOnEditorActionListener true
                    }
                    false
                })

        // Setup the adapter and container that will
        hintAdapter = HintsAdapter(this, viewModel.hintList)
        hintContainer = binding.listContainer
        hintContainer.adapter = hintAdapter
        hintContainer.onItemClickListener = AdapterView.OnItemClickListener { _, view, position, _ ->

            if (viewModel.selectedId.contains(viewModel.hintList[position].id)) {
                viewModel.selectedId.remove(viewModel.hintList[position].id)
                view.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            } else {
                viewModel.selectedId.add(viewModel.hintList[position].id)
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
            }

            viewModel.notifyPropertyChanged(BR.selectedId)
            viewModel.notifyPropertyChanged(BR.deletable)
            viewModel.notifyPropertyChanged(BR.editable)
        }
    }

    override fun onResume() {
        super.onResume()

        // Remove all system UI
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    override fun createHint() {
        val newHint = Hint(System.currentTimeMillis().toString(), binding.headerInput.text.toString(), binding.bodyInput.text.toString(), false)
        hintsDatabase.push().setValue(newHint)
    }

    override fun deleteHint() {
        for (selection in viewModel.selectedId) {
            for (hint in viewModel.hintList) {
                if (hint.id.contentEquals(selection)) {
                    hintsDatabase.child(hint.key).removeValue()
                }
            }
        }
        viewModel.selectedId.clear()
    }

    override fun editHint() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkCreatable(): Boolean = (binding.headerInput.length() != 0 && binding.bodyInput.length() != 0)

    companion object {

        private const val GAME_ID = "game_id"

        fun newIntent(context: Context, gameId: Int): Intent {
            val intent = Intent(context, HintControlsActivity::class.java)
            intent.putExtra(GAME_ID, gameId)
            return intent
        }
    }
}

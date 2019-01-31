package com.pedersen.escaped.master.controls.hints

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import com.google.firebase.database.*
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.data.adapters.HintsAdapter
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.databinding.HintControlsFragmentBinding
import io.greenerpastures.mvvm.ViewModelActivity
import kotlinx.android.synthetic.main.hint_controls_fragment.*
import timber.log.Timber

class HintControlsActivity :
    ViewModelActivity<HintControlsActivityViewModel, HintControlsFragmentBinding>(),
    HintControlsActivityViewModel.Commands {

    private var gameId: Int = 0

    private lateinit var hintAdapter: BaseAdapter
    private lateinit var hintContainer: ListView
    private var hintlist: ArrayList<Hint> = ArrayList()

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private var databaseReference = firebaseInstance.getReference("games")
    private lateinit var hintsDatabase: DatabaseReference
    private lateinit var requestListener: DatabaseReference
    private lateinit var bankDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.hint_controls_fragment,
                   BR.viewModel,
                   ({ HintControlsActivityViewModel() }))
        super.onCreate(savedInstanceState)

        gameId = intent.extras.get(GAME_ID) as Int

        requestListener = databaseReference.child(gameId.toString()).child("requestHint")
        requestListener.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Failed to read value: $e")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {}

        })

        hintsDatabase = databaseReference.child(gameId.toString()).child("hints")
        hintsDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    hintlist.clear()
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    for (hintChild in dataSnapshot.children) {
                        val hint = hintChild.getValue(Hint::class.java)
                        hint?.key = hintChild.key
                        hint?.let { hintlist.add(it) }
                    }

                    hintAdapter.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Failed to read value: $e")
            }
        })

        bankDatabase = databaseReference.child("bankhints")

        // Setup keyboard behavior
        binding.bodyInput.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // hide virtual keyboard
                val imm = this.getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                viewModel.notifyPropertyChanged(BR.creatable)

                return@setOnEditorActionListener true
            }
            false
        }

        // Setup the adapter and container that will
        hintAdapter = HintsAdapter(this, hintlist)
        hintContainer = binding.listContainer
        hintContainer.adapter = hintAdapter
        hintContainer.onItemClickListener =
                AdapterView.OnItemClickListener { _, view, position, _ ->

                    if (viewModel.selectedId.contains(hintlist[position])) {
                        viewModel.selectedId.remove(hintlist[position])
                        view.setBackgroundColor(ContextCompat.getColor(this,
                                                                       android.R.color.transparent))
                    } else {
                        viewModel.selectedId.add(hintlist[position])
                        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
                    }

                    viewModel.notifyPropertyChanged(BR.selectedId)
                    viewModel.notifyPropertyChanged(BR.deletable)
                    viewModel.notifyPropertyChanged(BR.editable)

                }
    }

    override fun createHint() {
        val newHint = Hint(System.currentTimeMillis().toString(),
                           binding.headerInput.text.toString(),
                           binding.bodyInput.text.toString(),
                           false)
        hintsDatabase.push().setValue(newHint)
        binding.headerInput.text.clear()
        binding.bodyInput.text.clear()
        resetHintRequest()
        viewModel.notifyPropertyChanged(BR.creatable)
    }

    private fun resetHintRequest() {
        Timber.i("Debug: Resetting request from player")
        val requestUpdate = HashMap<String, Any>()
        requestUpdate["requestHint"] = false
        databaseReference.child(gameId.toString()).updateChildren(requestUpdate)
    }

    override fun deleteHint() {
        for (selection in viewModel.selectedId) {
            for (hint in hintlist) {
                if (hint.id.contentEquals(selection.id)) {
                    hintsDatabase.child(hint.key).removeValue()
                }
            }
            Timber.i("SelectedIds: ${viewModel.selectedId}")
        }

        for (i in 0 until hintContainer.childCount) {
            val listItem = hintContainer.getChildAt(i)
            listItem.setBackgroundColor(Color.WHITE)
        }
        viewModel.selectedId.clear()
        viewModel.notifyPropertyChanged(BR.creatable)
        viewModel.notifyPropertyChanged(BR.editable)
        viewModel.notifyPropertyChanged(BR.deletable)
    }

    override fun editHint() {
        val snackbar: Snackbar = Snackbar.make(root, "Not implemented yet", Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    override fun checkCreatable(): Boolean =
        (binding.headerInput.length() != 0 && binding.bodyInput.length() != 0)

    override fun addHintToBank() {
        for (selection in viewModel.selectedId) {
            for (hint in hintlist) {
                if (hint.id.contentEquals(selection.id)) {
                    bankDatabase.push().setValue(hint)
                }
            }
            Timber.i("SelectedIds: ${viewModel.selectedId}")
        }

        for (i in 0 until hintContainer.childCount) {
            val listItem = hintContainer.getChildAt(i)
            listItem.setBackgroundColor(Color.WHITE)
        }
        viewModel.selectedId.clear()
        viewModel.notifyPropertyChanged(BR.creatable)
        viewModel.notifyPropertyChanged(BR.editable)
        viewModel.notifyPropertyChanged(BR.deletable)
    }

    override fun openHintBank() {
        val hintBankFragment = HintBankFragment.newInstance()

        try {
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, hintBankFragment)
                .commit()
        } catch (e: Exception) {
            Timber.d("Adding the hintbank fragment threw an exception: $e")
        }
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

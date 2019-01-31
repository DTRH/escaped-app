package com.pedersen.escaped.master.controls.hints

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ListView
import com.google.firebase.database.*
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.data.adapters.HintsAdapter
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.databinding.FragmentHintBankBinding
import io.greenerpastures.mvvm.ViewModelFragment
import timber.log.Timber

class HintBankFragment : ViewModelFragment<HintBankFragmentViewModel, FragmentHintBankBinding>(),
        HintBankFragmentViewModel.Commands {

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private var databaseReference = firebaseInstance.getReference("games")
    private lateinit var hintsDatabase: DatabaseReference

    private lateinit var hintAdapter: HintsAdapter
    private lateinit var hintContainer: ListView
    private var hintlist: ArrayList<Hint> = ArrayList()

    override fun onAttachContext(context: Context) {
        initialize(R.layout.fragment_hint_bank, BR.viewModel) {
            HintBankFragmentViewModel()
        }
        super.onAttachContext(context)

        hintsDatabase = databaseReference.child("bankhints")
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
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup keyboard behavior
        binding.headerInput.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // hide virtual keyboard
                val imm = activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                viewModel.notifyPropertyChanged(BR.creatable)

                return@setOnEditorActionListener true
            }
            false
        }

        // Setup the adapter and container that will
        hintAdapter = HintsAdapter(activity, hintlist)
        hintContainer = binding.listContainer
        hintContainer.adapter = hintAdapter
        hintContainer.onItemClickListener =
                AdapterView.OnItemClickListener { _, view, position, _ ->

                    if (viewModel.selectedId == position) {
                        viewModel.selectedId = -1
                        view.setBackgroundColor(ContextCompat.getColor(view.context,
                                                                       android.R.color.transparent))
                    } else {
                        viewModel.selectedId = position
                        view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.colorAccent))
                    }
                    viewModel.notifyPropertyChanged(BR.selectedId)
                    viewModel.notifyPropertyChanged(BR.deletable)
                }
    }
    override fun sendSelected() {
        // TODO
    }

    override fun closeBank() {
        fragmentManager.beginTransaction()
            .remove(fragmentManager.findFragmentById(R.id.fragment_container)).commit()
    }

    override fun refreshUpdater() {
        hintAdapter.notifyDataSetChanged()
    }

    companion object {

        fun newInstance(): HintBankFragment {
            return HintBankFragment()
        }
    }

    interface Commands
}
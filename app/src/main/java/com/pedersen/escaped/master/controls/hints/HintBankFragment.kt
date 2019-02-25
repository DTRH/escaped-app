package com.pedersen.escaped.master.controls.hints

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import com.google.firebase.database.*
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.data.adapters.HintsAdapter
import com.pedersen.escaped.data.models.Challenge
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.databinding.FragmentHintBankBinding
import io.greenerpastures.mvvm.ViewModelFragment
import timber.log.Timber


class HintBankFragment : ViewModelFragment<HintBankFragmentViewModel, FragmentHintBankBinding>(),
    HintBankFragmentViewModel.Commands, AdapterView.OnItemSelectedListener {

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private var databaseReference = firebaseInstance.getReference("games")
    private lateinit var bankDatabase: DatabaseReference
    private lateinit var hintDatabase: DatabaseReference
    private lateinit var challengeDatabase: DatabaseReference

    private lateinit var challengeAdapter: ArrayAdapter<Challenge>
    private var challengeList: MutableList<Challenge> = mutableListOf()

    private lateinit var hintAdapter: HintsAdapter
    private lateinit var hintContainer: ListView
    private var hintlist: ArrayList<Hint> = ArrayList()

    private lateinit var spinner: Spinner
    private var completeBankList: ArrayList<Hint> = ArrayList()



    override fun onAttachContext(context: Context) {
        initialize(R.layout.fragment_hint_bank, BR.viewModel) {
            HintBankFragmentViewModel()
        }
        super.onAttachContext(context)

        val gameId = arguments.getString(GAME_ID)

        hintDatabase = databaseReference.child(gameId).child("hints")

        challengeDatabase = databaseReference.child("challenges")
        challengeDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                challengeList.clear()
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (hintChild in dataSnapshot.children) {
                    val challenge = hintChild.getValue(Challenge::class.java)
                    challenge?.let { challengeList.add(it) }
                }
                challengeAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Failed to read value: $e")
            }
        })

        bankDatabase = databaseReference.child("bankhints")
        bankDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    completeBankList.clear()
                    hintlist.clear()
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    for (hintChild in dataSnapshot.children) {
                        val bankhint = hintChild.getValue(Hint::class.java)
                        bankhint?.key = hintChild.key
                        bankhint?.let {
                            completeBankList.add(it)
                            if (spinner.selectedItem != null)
                                if (bankhint.challenge == spinner.selectedItem.toString())
                                    hintlist.add(it)
                        }
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

        spinner = binding.challengeSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        challengeAdapter = ArrayAdapter(activity,
                                        R.layout.challenge_spinner_item,
                                        challengeList)
        challengeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = challengeAdapter
        spinner.onItemSelectedListener = this

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
                        view.setBackgroundColor(ContextCompat.getColor(view.context,
                                                                       R.color.colorAccent))
                    }
                    viewModel.notifyPropertyChanged(BR.selectedId)
                    viewModel.notifyPropertyChanged(BR.creatable)
                    viewModel.notifyPropertyChanged(BR.deletable)
                }
    }



    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        hintlist.clear()
        for (hint in completeBankList) {
            if (hint.challenge == spinner.selectedItem.toString()) {
                hintlist.add(hint)
            }
        }
        hintAdapter.notifyDataSetChanged()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.i("TEST", "Something")
    }

    override fun createNewChallenge() {
        val newChallenge = Challenge(System.currentTimeMillis().toString(),
                                     binding.headerInput.text.toString())
        challengeDatabase.push().setValue(newChallenge)
        binding.headerInput.text.clear()
    }

    override fun sendSelected() {
        val newHint = Hint(System.currentTimeMillis().toString(),
                           hintlist[viewModel.selectedId].title,
                           hintlist[viewModel.selectedId].body,
                           false)
        hintDatabase.push().setValue(newHint)

        for (i in 0 until hintContainer.childCount) {
            val listItem = hintContainer.getChildAt(i)
            listItem.setBackgroundColor(Color.WHITE)
        }
        // TODO consider that we are clearing a potential hint request when we send the hint from the bank
    }

    override fun closeBank() {
        fragmentManager.beginTransaction()
            .remove(fragmentManager.findFragmentById(R.id.fragment_container)).commit()
    }

    override fun refreshUpdater() {
        hintAdapter.notifyDataSetChanged()
    }

    companion object {

        const val GAME_ID = "game_id_bank_fragment"

        fun newInstance(gameId: String): HintBankFragment {
            val args = Bundle(1)
            args.putString(HintBankFragment.GAME_ID, gameId)
            val fragment = HintBankFragment()
            fragment.arguments = args
            return fragment

        }
    }
}
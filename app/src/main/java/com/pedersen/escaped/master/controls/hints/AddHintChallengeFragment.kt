package com.pedersen.escaped.master.controls.hints

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.database.*
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.data.models.Challenge
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.databinding.FragmentAddHintChallengeBinding
import io.greenerpastures.mvvm.ViewModelFragment
import timber.log.Timber

class AddHintChallengeFragment :
    ViewModelFragment<AddHintChallengeFragmentViewModel, FragmentAddHintChallengeBinding>(),
    AddHintChallengeFragmentViewModel.Commands {

    private lateinit var hintToBeSent: Hint
    private lateinit var bankDatabase: DatabaseReference
    private lateinit var challengeDatabase: DatabaseReference
    private val firebaseInstance = FirebaseDatabase.getInstance()
    private var databaseReference = firebaseInstance.getReference("games")
    private lateinit var challengeAdapter: ArrayAdapter<Challenge>
    private var challengeList: MutableList<Challenge> = mutableListOf()
    private lateinit var spinner: Spinner

    override fun onAttachContext(context: Context) {
        initialize(R.layout.fragment_add_hint_challenge, BR.viewModel) {
            AddHintChallengeFragmentViewModel()
        }
        super.onAttachContext(context)

        hintToBeSent = arguments[HINT] as Hint

        bankDatabase = databaseReference.child("bankhints")

        challengeDatabase = databaseReference.child("challenges")
        challengeDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
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
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner = binding.spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        challengeAdapter = ArrayAdapter(activity,
                                        R.layout.challenge_spinner_item,
                                        challengeList)
        challengeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = challengeAdapter
    }

    override fun sendHint() {
        if (spinner.selectedItem != null) {
            val newHint = hintToBeSent.apply {
                challenge = spinner.selectedItem.toString()
            }
            bankDatabase.push().setValue(newHint)
            closeChallengeSelector()
        }
    }

    override fun closeChallengeSelector() {
        fragmentManager.beginTransaction()
            .remove(fragmentManager.findFragmentById(R.id.fragment_container)).commit()
    }

    companion object {

        const val HINT = "hint"

        fun newInstance(hint: Hint): AddHintChallengeFragment {
            val args = Bundle(1)
            args.putSerializable(AddHintChallengeFragment.HINT, hint)
            val fragment = AddHintChallengeFragment()
            fragment.arguments = args
            return fragment

        }
    }
}

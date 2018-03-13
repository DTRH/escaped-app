package com.pedersen.escaped.master.rooms.dinner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityDinnerRoomBinding
import io.greenerpastures.mvvm.ViewModelActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pedersen.escaped.master.controls.hints.HintControlsFragment

class DinnerRoomActivity : ViewModelActivity<DinnerRoomActivityViewModel, ActivityDinnerRoomBinding>(), DinnerRoomActivityViewModel.Commands {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_dinner_room, BR.viewModel, ({ DinnerRoomActivityViewModel() }))
        super.onCreate(savedInstanceState)

    }

    override fun launchHintControls() {
        val hintControlsFragment = HintControlsFragment.newInstance()
        fragmentManager.beginTransaction().replace(R.id.fragment_container, hintControlsFragment).commit()
    }

    override fun launchVideoControls() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun launchRoomControls() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, DinnerRoomActivity::class.java)
        }
    }
}
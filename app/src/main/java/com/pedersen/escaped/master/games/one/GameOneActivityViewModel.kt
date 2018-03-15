package com.pedersen.escaped.master.games.one

import android.databinding.Bindable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pedersen.escaped.BuildConfig
import com.pedersen.escaped.data.models.Hint
import io.greenerpastures.mvvm.BaseViewModel
import timber.log.Timber

class GameOneActivityViewModel : BaseViewModel<GameOneActivityViewModel.Commands>() {

    @get:Bindable
    var hintList = ArrayList<Hint>()

    override fun onActive() {
        super.onActive()

//        val firebaseInstance = FirebaseDatabase.getInstance()
//        val hintsDatabase = firebaseInstance.getReference("games").child("1").child("hints")
//        // Read from the firebaseInstance
//        hintsDatabase.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                for (hintChild in dataSnapshot.children) {
//                    val hint = hintChild.getValue(Hint::class.java)
//                    hint?.let { hintList.add(it) }
//                }
//                //commandHandler?.updateHintList()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                val e = error.toException().toString()
//                Timber.w("Failed to read value: $e")
//            }
//        })

    }


    fun launchHintControls() = commandHandler?.launchHintControls()

    fun launchVideoControls() = commandHandler?.launchVideoControls()

    fun launchRoomControls() = commandHandler?.launchRoomControls()

    interface Commands {

        fun launchHintControls()

        fun launchVideoControls()

        fun launchRoomControls()

    }
}
package com.pedersen.escaped.master.controls.hints

import android.databinding.Bindable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pedersen.escaped.data.models.Hint
import io.greenerpastures.mvvm.BaseViewModel
import timber.log.Timber

class HintControlsActivityViewModel(private var gameId: String) : BaseViewModel<HintControlsActivityViewModel.Commands>() {

    init {
        val gameId = this.gameId
        val firebaseInstance = FirebaseDatabase.getInstance()
        val hintsDatabase = firebaseInstance.getReference("games").child(gameId).child("hints")
        // Read from the firebaseInstance
        hintsDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (hintChild in dataSnapshot.children) {
                    val hint = hintChild.getValue(Hint::class.java)
                    hint?.let { hintList.add(it) }
                }
                commandHandler?.updateHintList()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Failed to read value: $e")
            }
        })
    }

    override fun onActive() {
        super.onActive()
    }

    @get:Bindable
    var hintList = ArrayList<Hint>()


    interface Commands {

        fun updateHintList()
    }

}

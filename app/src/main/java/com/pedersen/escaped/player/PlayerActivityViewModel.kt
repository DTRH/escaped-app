package com.pedersen.escaped.player

import android.databinding.Bindable
import android.view.View

import com.pedersen.escaped.BR
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.extensions.bind
import io.greenerpastures.mvvm.BaseViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import com.pedersen.escaped.BuildConfig


class PlayerActivityViewModel : BaseViewModel<PlayerActivityViewModel.Commands>() {

    @get:Bindable
    var hintList = ArrayList<Hint>()

    @get:Bindable
    var progress: Int = 0
        set(value) {
            if (value == field) return
            commandHandler?.animateProgressBar(field, value)
            field = value
            notifyPropertyChanged(BR.progress)
        }

    init {
        val firebaseInstance = FirebaseDatabase.getInstance()
        val hintsDatabase = firebaseInstance.getReference("games").child(BuildConfig.gameId.toString()).child("hints")
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
        progress = 50
    }

    interface Commands {

        fun animateProgressBar(from: Int, to: Int)

        fun updateHintList()
    }
}

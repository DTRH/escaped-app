package com.pedersen.escaped.master

import android.databinding.Bindable
import com.google.firebase.database.*
import com.pedersen.escaped.extensions.bind
import io.greenerpastures.mvvm.BaseViewModel
import com.pedersen.escaped.BR
import timber.log.Timber

class GameMasterActivityViewModel : BaseViewModel<GameMasterActivityViewModel.Commands>() {

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private var databaseReference = firebaseInstance.getReference("games")
    private lateinit var gameOneRequestListener: DatabaseReference
    private lateinit var gameTwoRequestListener: DatabaseReference

    @get:Bindable
    var isGameOneHintRequested by bind(false, BR.gameOneHintRequested)

    @get:Bindable
    var isGameTwoHintRequested by bind(false, BR.gameTwoHintRequested)

    override fun onActive() {
        super.onActive()

        gameOneRequestListener = databaseReference.child("1").child("requestHint")
        gameOneRequestListener.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Debug: Failed to read value: $e")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null)
                    isGameOneHintRequested = dataSnapshot.value as Boolean
            }
        })

        gameTwoRequestListener = databaseReference.child("2").child("requestHint")
        gameTwoRequestListener.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Debug: Failed to read value: $e")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null)
                    isGameTwoHintRequested = dataSnapshot.value as Boolean
            }
        })
    }

    fun launchRoom(id: Int) {
        commandHandler?.launchGameActivity(id)
    }

    interface Commands {

        fun launchGameActivity(id: Int)
    }
}
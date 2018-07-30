package com.pedersen.escaped.master

import android.databinding.Bindable
import com.google.firebase.database.*
import com.pedersen.escaped.BR
import com.pedersen.escaped.extensions.bind
import io.greenerpastures.mvvm.BaseViewModel
import timber.log.Timber

class GameMasterActivityViewModel : BaseViewModel<GameMasterActivityViewModel.Commands>() {

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private var databaseReference = firebaseInstance.getReference("games")

    // Request listeners
    private lateinit var gameOneRequestListener: DatabaseReference
    private lateinit var gameTwoRequestListener: DatabaseReference


    // State listeners
    private lateinit var gameOneStateListener: DatabaseReference
    private lateinit var gameTwoStateListener: DatabaseReference


    // Request bindings
    @get:Bindable
    var isGameOneHintRequested = false
        get() = field && isGameOneActive

    @get:Bindable
    var isGameTwoHintRequested = false
        get() = field && isGameTwoActive

    // State bindings
    @get:Bindable
    var isGameOneActive by bind(false, BR.gameOneActive, BR.gameOneHintRequested)

    @get:Bindable
    var isGameTwoActive by bind(false, BR.gameTwoActive, BR.gameTwoHintRequested)

    override fun onActive() {
        super.onActive()

        gameOneStateListener = databaseReference.child("1").child("state")
        gameOneStateListener.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Debug: Failed to read value: $e")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                isGameOneActive = dataSnapshot.value.toString().toLowerCase() != "ended"
            }
        })

        gameTwoStateListener = databaseReference.child("2").child("state")
        gameTwoStateListener.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Debug: Failed to read value: $e")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                isGameTwoActive = dataSnapshot.value.toString().toLowerCase() != "ended"
            }
        })

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
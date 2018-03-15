package com.pedersen.escaped.master.controls.hints

import android.databinding.Bindable
import com.google.firebase.database.*
import com.pedersen.escaped.BR
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.extensions.bind
import io.greenerpastures.mvvm.BaseViewModel
import timber.log.Timber

class HintControlsActivityViewModel : BaseViewModel<HintControlsActivityViewModel.Commands>() {

    var gameId: Int = 0

    @get:Bindable
    var selectedId: ArrayList<String> = ArrayList()

    @get:Bindable
    var hintList = ArrayList<Hint>()

    @get:Bindable
    var isDeletable: Boolean = false
        get() = !selectedId.isEmpty()

    @get:Bindable
    var isEditable: Boolean = false
        get() = selectedId.size == 1

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private lateinit var hintsDatabase: DatabaseReference

    override fun onActive() {
        super.onActive()

        hintsDatabase = firebaseInstance.getReference("games").child(gameId.toString()).child(
                "hints")

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

    fun createHint() {}

    fun editHint() {}

    fun deleteHint() {
        for (selection in selectedId) {
            for (hint in hintList) {
                if (hint.id.contentEquals(selection))
                    hintList.clear()
                    hintsDatabase.child(hint.id).removeValue()
            }
        }
        selectedId.clear()
        //commandHandler?.updateHintList()
    }

    interface Commands {

       // fun unselectAll()

        fun updateHintList()
    }

}

package com.pedersen.escaped.player

import android.annotation.SuppressLint
import android.databinding.Bindable
import android.net.Uri
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import com.pedersen.escaped.data.models.Hint
import io.greenerpastures.mvvm.BaseViewModel
import timber.log.Timber
import com.pedersen.escaped.BuildConfig
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class PlayerActivityViewModel : BaseViewModel<PlayerActivityViewModel.Commands>() {

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private var databaseReference = firebaseInstance.getReference("games")

    private lateinit var storageRef: StorageReference

    private lateinit var hintsDatabase: DatabaseReference
    private lateinit var progressListener: DatabaseReference

    @get:Bindable
    var hintList = ArrayList<Hint>()



    @get:Bindable
    var progress: Int = 0
        set(value) {
            if (value == field) return
            commandHandler?.animateProgressBar(field, value)
            field = value
        }

    @SuppressLint("MissingSuperCall")
    override fun onActive() {
        super.onActive()

        storageRef = FirebaseStorage.getInstance().reference

        storageRef.child("video_intro.mp4").downloadUrl
                .addOnSuccessListener {
                    taskSnapshot -> Observable.timer(2000, TimeUnit.MILLISECONDS).subscribe { commandHandler?.playVideo(taskSnapshot) }
                }.addOnFailureListener {
                    exception -> Timber.i("Logging exception: $exception")
                }

        hintsDatabase = databaseReference.child(BuildConfig.gameId.toString()).child("hints")
        // Read from the firebaseInstance
        hintsDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                hintList.clear()
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (hintChild in dataSnapshot.children) {
                    val hint = hintChild.getValue(Hint::class.java)
                    hint?.let { hintList.add(it) }
                }
                commandHandler?.refreshAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Failed to read value: $e")
            }
        })

        progressListener = databaseReference.child(BuildConfig.gameId.toString()).child("progress")

        progressListener.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value is String && dataSnapshot.value != null)
                    progress = (dataSnapshot.value as String).toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val e = error.toException().toString()
                Timber.w("Debug: Failed to read value: $e")
            }
        })
    }

    fun requestHint() {
        Timber.i("Debug: Game ${BuildConfig.gameId} requests a hint!")
        val update = HashMap<String, Any>()
        update["requestHint"] = true
        Timber.i("Debug: Sending hint request")
        databaseReference.child(BuildConfig.gameId.toString()).updateChildren(update)
    }

    interface Commands {

        fun animateProgressBar(from: Int, to: Int)

        fun refreshAdapter()

        fun playVideo(taskSnapshot: Uri)

    }
}

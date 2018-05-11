package com.pedersen.escaped.player

import android.databinding.Bindable
import android.net.Uri
import android.os.CountDownTimer
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pedersen.escaped.BR

import com.pedersen.escaped.data.models.Hint
import io.greenerpastures.mvvm.BaseViewModel
import timber.log.Timber
import com.pedersen.escaped.BuildConfig
import com.pedersen.escaped.extensions.bind
import io.reactivex.Observable
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import java.util.concurrent.TimeUnit

class PlayerActivityViewModel : BaseViewModel<PlayerActivityViewModel.Commands>() {

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private var databaseReference = firebaseInstance.getReference("games")

    private lateinit var storageRef: StorageReference

    private lateinit var hintsDatabase: DatabaseReference
    private lateinit var progressListener: DatabaseReference
    private lateinit var stateListener: DatabaseReference
    private lateinit var timeListener: DatabaseReference

    private lateinit var countDownTimer: CountDownTimer
    private var deadline: Instant = Instant.now()

    @get:Bindable
    var hintList = ArrayList<Hint>()

    @get:Bindable
    var playerState by bind(PlayerState.UNKNOWN, BR.playerState, BR.stateMessage, BR.showStateOverlay)

    @get:Bindable
    var progress: Int = 0
        set(value) {
            if (value == field) return
            commandHandler?.animateProgressBar(field, value)
            field = value
        }

    @get:Bindable
    var stateMessage: String = ""
        get() {
            when (playerState) {
                PlayerState.UNKNOWN -> {
                    return "Gamestate Unknown..."
                }
                PlayerState.READY -> {
                    return "Game is about to start..."
                }
                PlayerState.PAUSED -> {
                    return "Game has been paused..."
                }
                PlayerState.ENDED -> {
                    return "Game has ended..."
                }
                else -> {
                    return "Gamestate Unknown..."
                }
            }
        }

    @get:Bindable
    var showStateOverlay: Boolean = false
        get() = (playerState != PlayerState.PLAYING)

    override fun onActive() {
        super.onActive()

        stateListener = databaseReference.child(BuildConfig.gameId.toString()).child("state")
        stateListener.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                val e = error.toException().toString()
                Timber.w("Debug: Failed to read value: $e")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value is String && dataSnapshot.value != null)
                    setPlayerState(dataSnapshot.value as String)
            }
        })

        // Setup listener for time remaining
        timeListener = databaseReference.child(BuildConfig.gameId.toString()).child("deadline")
        timeListener.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                val e = error.toException().toString()
                Timber.w("Debug: Failed to read value: $e")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value is String && dataSnapshot.value != null)
                    deadline = Instant.parse(dataSnapshot.value as CharSequence?)
            }
        })

        // Setup reference to hints
        hintsDatabase = databaseReference.child(BuildConfig.gameId.toString()).child("hints")

        // Setup reference to progress
        progressListener = databaseReference.child(BuildConfig.gameId.toString()).child("progress")
    }

    private fun setPlayerState(state: String) {
        when (state.toLowerCase()) {
            "unknown" -> {
                playerState = PlayerState.UNKNOWN
            }
            "ready" -> {
                progress = 0
                hintList.clear()
                playerState = PlayerState.READY
            }
            "playing" -> {
                // Prepare and play intro video
                // TODO: Here we need a logic to handle if the movie has already been played or not
                storageRef = FirebaseStorage.getInstance().reference
                storageRef.child("video_intro.mp4").downloadUrl
                    .addOnSuccessListener {
                            taskSnapshot -> Observable.timer(2000, TimeUnit.MILLISECONDS).subscribe { commandHandler?.playVideo(taskSnapshot) }
                    }.addOnFailureListener {
                            exception -> Timber.i("Logging exception: $exception")
                    }

                // When setting game state to PLAYING we start listening for hints
                hintsDatabase.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        hintList.clear()
                        for (hintChild in dataSnapshot.children) {
                            val hint = hintChild.getValue(Hint::class.java)
                            hint?.let { hintList.add(it) }
                        }
                        commandHandler?.refreshAdapter()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        val e = error.toException().toString()
                        Timber.w("Failed to read value: $e")
                    }
                })

                // Also listen for progress
                progressListener.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.value is String && dataSnapshot.value != null)
                            progress = (dataSnapshot.value as String).toInt()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        val e = error.toException().toString()
                        Timber.w("Debug: Failed to read value: $e")
                    }
                })

                resumeTimer(Duration.between(Instant.now(), deadline))

                playerState = PlayerState.PLAYING
            }
            "paused" -> {
                playerState = PlayerState.PAUSED
            }
            "ended" -> {
                progress = 0
                hintList.clear()
                playerState = PlayerState.ENDED
            }
        }
    }

    private fun resumeTimer(between: Duration) {
        countDownTimer = object : CountDownTimer(between.abs().toMillis(), 5000) {

            override fun onTick(millisUntilFinished: Long) {
                commandHandler?.animateClockArm(360 / 60 * (Duration.between(deadline, Instant.now()).toMinutes().toFloat()))
            }

            override fun onFinish() {
                setPlayerState("ended")
                countDownTimer.cancel()
            }
        }.start()
    }

    fun requestHint() {
        Timber.i("Debug: Game ${BuildConfig.gameId} requests a hint!")
        val update = HashMap<String, Any>()
        update["requestHint"] = true
        Timber.i("Debug: Sending hint request")
        databaseReference.child(BuildConfig.gameId.toString()).updateChildren(update)
    }

    enum class PlayerState {
        UNKNOWN, READY, PLAYING, PAUSED, ENDED
    }

    interface Commands {

        fun animateProgressBar(from: Int, to: Int)

        fun animateClockArm(to: Float)

        fun refreshAdapter()

        fun playVideo(taskSnapshot: Uri)

    }
}

package com.pedersen.escaped.player

import android.databinding.Bindable
import android.os.CountDownTimer
import com.google.firebase.database.*
import com.pedersen.escaped.BR
import com.pedersen.escaped.BuildConfig
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.extensions.bind
import com.pedersen.escaped.master.controls.games.GameControlsActivityViewModel
import com.pedersen.escaped.player.PlayerActivity.VideoElement.*
import com.pedersen.escaped.player.PlayerActivityViewModel.PlayerState.*
import io.greenerpastures.mvvm.BaseViewModel
import io.reactivex.Observable
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PlayerActivityViewModel : BaseViewModel<PlayerActivityViewModel.Commands>() {

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private var databaseReference = firebaseInstance.getReference("games")

    private var hintListener: DatabaseReference
    private var progressListener: DatabaseReference
    private var stateListener: DatabaseReference
    private var timeListener: DatabaseReference
    private lateinit var introListener: DatabaseReference

    private lateinit var countDownTimer: CountDownTimer
    private var introCompleted: Boolean = false

    // DEFAULT LANGUAGE IS DANISH
    private var language: GameControlsActivityViewModel.SupportedLanguages = GameControlsActivityViewModel.SupportedLanguages.DANISH

    private var deadline: Instant? = null
        set(value) {
            field = value
            if (field != null)
                if (field!!.isBefore(Instant.now()))
                    setPlayerState("ENDED")
                else
                    resumeTimer(Duration.between(Instant.now(), value))
        }

    @get:Bindable
    var hintList = ArrayList<Hint>()

    @get:Bindable
    var playerState by bind(UNKNOWN,
                            BR.playerState,
                            BR.stateMessage,
                            BR.showStateOverlay)

    @get:Bindable
    var progress: Int = 0
        set(value) {
            if (value == field) return
            commandHandler?.animateProgressBar(field, value)
            field = value
            if (field == 100) {
                commandHandler?.playVideo(END_GOOD, language)
                if (this::countDownTimer.isInitialized) {
                    countDownTimer.cancel()
                }

            }
        }

    @get:Bindable
    var stateMessage: String = ""
        get() {
            when (playerState) {
                UNKNOWN -> {
                    return "Gamestate Unknown..."
                }
                READY -> {
                    return "Game is about to start..."
                }
                PAUSED -> {
                    return "Game has been paused..."
                }
                ENDED -> {
                    return "Game has ended..."
                }
                else -> {
                    return "Gamestate Unknown..."
                }
            }
        }

    @get:Bindable
    var showStateOverlay: Boolean = false
        get() = (playerState != PLAYING)

    init {
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
                if (dataSnapshot.value is String && dataSnapshot.value != null) {
                    deadline = Instant.parse(dataSnapshot.value as CharSequence?)
                }
            }
        })

        // Setup reference to hints
        hintListener = databaseReference.child(BuildConfig.gameId.toString()).child("hints")

        // Setup reference to progress
        progressListener = databaseReference.child(BuildConfig.gameId.toString()).child("progress")

        // Setup language listener
        databaseReference.child(BuildConfig.gameId.toString()).child("language")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value is String && dataSnapshot.value != null)
                        when (dataSnapshot.value) {
                            "DANISH" -> language = GameControlsActivityViewModel.SupportedLanguages.DANISH
                            "ENGLISH" -> language = GameControlsActivityViewModel.SupportedLanguages.ENGLISH
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    val e = error.toException().toString()
                    Timber.w("Debug: Failed to read value: $e")
                }
            })
    }


    private fun setPlayerState(state: String) {
        when (state.toLowerCase()) {
            "unknown" -> {
                playerState = UNKNOWN
            }
            "ready" -> {
                progress = 0
                hintList.clear()
                playerState = READY
            }
            "playing" -> {
                playerState = PLAYING
                // When setting game state to PLAYING we start listening for hints
                hintListener.addValueEventListener(object : ValueEventListener {
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

                // Setup listener for intro video
                introListener = databaseReference.child(BuildConfig.gameId.toString())
                    .child("introCompleted")
                introListener.addValueEventListener(object : ValueEventListener {

                    override fun onCancelled(dbError: DatabaseError?) =
                        Timber.i("Updating introListener threw a DatabaseError: $dbError")

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        if (dataSnapshot?.value != null)
                            introCompleted = dataSnapshot.value as Boolean

                        if (!introCompleted) {
                            Observable.timer(2000, TimeUnit.MILLISECONDS).subscribe {
                                if (playerState == PLAYING) {
                                    commandHandler?.playVideo(INTRO, language)
                                    val introUpdate = HashMap<String, Any>()
                                    introUpdate["introCompleted"] = true
                                    databaseReference.child(BuildConfig.gameId.toString())
                                        .updateChildren(introUpdate)
                                }
                            }.disposeOnInactive()
                        }
                    }
                })
            }
            "paused" -> {
                playerState = PAUSED
            }
            "ended" -> {
                progress = 0
                hintList.clear()
                playerState = ENDED
            }
        }
    }

    private fun resumeTimer(timeLeft: Duration) {
        if (this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }

        countDownTimer = object : CountDownTimer(timeLeft.abs().toMillis(), 5000) {

            override fun onTick(millisUntilFinished: Long) {
                if (playerState == PLAYING)
                    commandHandler?.animateClockArm(360 / 60 * (Duration.between(deadline,
                                                                                 Instant.now()).toMinutes().toFloat()))
            }

            override fun onFinish() {
                countDownTimer.cancel()
                commandHandler?.playVideo(END_BAD, language)
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

        fun animateClockArm(targetAngle: Float)

        fun refreshAdapter()

        fun playVideo(videoElement: PlayerActivity.VideoElement, language: GameControlsActivityViewModel.SupportedLanguages)

    }
}

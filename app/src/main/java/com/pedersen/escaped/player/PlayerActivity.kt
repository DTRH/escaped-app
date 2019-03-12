package com.pedersen.escaped.player

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.animations.PositionSpringAnimation
import com.pedersen.escaped.animations.PositionSpringAnimation.PullingEventListener
import com.pedersen.escaped.data.adapters.HintsAdapter
import com.pedersen.escaped.databinding.ActivityPlayerBinding
import com.pedersen.escaped.master.controls.games.GameControlsActivityViewModel.SupportedLanguages
import com.pedersen.escaped.master.controls.games.GameControlsActivityViewModel.SupportedLanguages.DANISH
import com.pedersen.escaped.master.controls.games.GameControlsActivityViewModel.SupportedLanguages.ENGLISH
import com.pedersen.escaped.player.PlayerActivity.VideoElement.*
import com.pedersen.escaped.utils.AppUtils
import io.greenerpastures.mvvm.ViewModelActivity
import timber.log.Timber

class PlayerActivity : ViewModelActivity<PlayerActivityViewModel, ActivityPlayerBinding>(),
    PlayerActivityViewModel.Commands, VideoFragment.Commands {

    // Progress bar
    private var progressBarAnimation: ObjectAnimator = ObjectAnimator()

    // Clock Arm and Angle
    private lateinit var clockArm: ImageView
    private var clockArmAngle: Float = 0.0f

    // Mediaplayer - Notification
    private lateinit var mp: MediaPlayer

    // Adapter for the hint list
    private lateinit var hintAdapter: BaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_player, BR.viewModel, ({ PlayerActivityViewModel() }))
        super.onCreate(savedInstanceState)

        mp = MediaPlayer.create(applicationContext, R.raw.notification)

        clockArm = binding.playerClockArm

        // Setup pull/spring animation for the hint puller
        val positionSpringAnimation = PositionSpringAnimation(binding.hintPull)
        positionSpringAnimation.setEventListener(object : PullingEventListener {
            override fun pullAccured() {
                viewModel.requestHint()
            }
        })

        // Setup the adapter and container that will hold the hints
        hintAdapter = HintsAdapter(this, viewModel.hintList)
        val hintContainer = binding.hintContainer
        hintContainer.adapter = hintAdapter
        hintContainer.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val hintFragment = HintFragment.newInstance(viewModel.hintList[position])
            fragmentManager.beginTransaction().replace(R.id.fragment_container, hintFragment)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        // Remove all system UI
        AppUtils.clearWindow(window)
    }

    override fun onBackPressed() {
        // DO NOTHING
    }

    override fun animateProgressBar(from: Int, to: Int) {
        progressBarAnimation = ObjectAnimator.ofInt(binding.progressBar, "progress", from, to)
        progressBarAnimation.duration = 2000
        progressBarAnimation.start()
    }

    override fun animateClockArm(targetAngle: Float) {
        val animSet = AnimationSet(true)
        animSet.interpolator = DecelerateInterpolator()
        animSet.fillAfter = true
        animSet.isFillEnabled = true
        val animRotate = RotateAnimation(clockArmAngle, targetAngle,
                                         RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                         RotateAnimation.RELATIVE_TO_SELF, .95f)
        animRotate.duration = 500
        animRotate.fillAfter = true
        animSet.addAnimation(animRotate)
        clockArm.startAnimation(animSet)
        clockArmAngle = targetAngle
    }

    override fun refreshAdapter() {
        hintAdapter.notifyDataSetChanged()
        if (!hintAdapter.isEmpty && (viewModel.playerState == PlayerActivityViewModel.PlayerState.PLAYING || viewModel.playerState == PlayerActivityViewModel.PlayerState.PAUSED)) {
            mp.start()
            val hintFragment = HintFragment.newInstance(viewModel.hintList.last())
            fragmentManager.beginTransaction().replace(R.id.fragment_container, hintFragment)
                .commit()
        }
    }

    override fun playVideo(videoElement: VideoElement, language: SupportedLanguages) {
        var path = "android.resource://$packageName/"

        when (language) {
            DANISH -> {
                path += when (videoElement) {
                    INTRO -> R.raw.danish_intro_film
                    END_GOOD -> R.raw.danish_exit_good
                    END_BAD -> R.raw.danish_exit_dead
                }
            }
            ENGLISH -> {
                path += when (videoElement) {
                    INTRO -> R.raw.english_intro_film
                    END_GOOD -> R.raw.english_exit_good
                    END_BAD -> R.raw.english_exit_dead
                }
            }
        }

        val videoFragment = VideoFragment.newInstance(Uri.parse(path))

        try {
            fragmentManager.beginTransaction()
                .replace(R.id.skype_container, videoFragment)
                .commit()
        } catch (e: Exception) {
            Timber.d("Adding the video fragment threw an exception: $e")
        }
    }

    enum class VideoElement {
        INTRO, END_GOOD, END_BAD
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, PlayerActivity::class.java)
        }
    }
}

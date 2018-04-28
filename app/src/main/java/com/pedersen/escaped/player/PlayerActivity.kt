package com.pedersen.escaped.player

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.animations.PositionSpringAnimation
import com.pedersen.escaped.databinding.ActivityPlayerBinding
import io.greenerpastures.mvvm.ViewModelActivity
import com.pedersen.escaped.data.adapters.HintsAdapter
import com.pedersen.escaped.animations.PositionSpringAnimation.IMyEventListener
import com.pedersen.escaped.utils.AppUtils

class PlayerActivity : ViewModelActivity<PlayerActivityViewModel, ActivityPlayerBinding>(), PlayerActivityViewModel.Commands {

    private var progressBarAnimation: ObjectAnimator = ObjectAnimator()

    private lateinit var hintAdapter: BaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_player, BR.viewModel, ({ PlayerActivityViewModel() }))
        super.onCreate(savedInstanceState)

        // Setup pull/spring animation for the hint puller
        val positionSpringAnimation = PositionSpringAnimation(binding.hintPull)
        positionSpringAnimation.setEventListener(object : IMyEventListener {
            override fun onEventAccured() {
                viewModel.requestHint()
            }
        })

        // Setup the adapter and container that will
        hintAdapter = HintsAdapter(this, viewModel.hintList)
        val hintContainer = binding.hintContainer
        hintContainer.adapter = hintAdapter
        hintContainer.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val hintFragment = HintFragment.newInstance(viewModel.hintList[position])
            fragmentManager.beginTransaction().replace(R.id.fragment_container, hintFragment).commit()
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

    override fun refreshAdapter() {
        hintAdapter.notifyDataSetChanged()
    }

    override fun playVideo(taskSnapshot: Uri) {
        val videoFragment = VideoFragment.newInstance(taskSnapshot)
        fragmentManager.beginTransaction().replace(R.id.fragment_container, videoFragment).commit()
    }


    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, PlayerActivity::class.java)
        }
    }
}

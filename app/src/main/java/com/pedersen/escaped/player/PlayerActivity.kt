package com.pedersen.escaped.player

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.database.*
import com.pedersen.escaped.BR
import com.pedersen.escaped.BuildConfig
import com.pedersen.escaped.R
import com.pedersen.escaped.animations.PositionSpringAnimation
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.databinding.ActivityPlayerBinding
import io.greenerpastures.mvvm.ViewModelActivity
import com.pedersen.escaped.data.models.adapters.HintsAdapter
import timber.log.Timber


class PlayerActivity : ViewModelActivity<PlayerActivityViewModel, ActivityPlayerBinding>(), PlayerActivityViewModel.Commands {

    private val gameId: Int = BuildConfig.gameId

    private var progressBarAnimation: ObjectAnimator = ObjectAnimator()

    private lateinit var hintAdapter: BaseAdapter



    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_player, BR.viewModel, ({ PlayerActivityViewModel() }))
        super.onCreate(savedInstanceState)


        // Setup pull/spring animation for the hint puller
        PositionSpringAnimation(binding.hintPull)

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
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
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

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, PlayerActivity::class.java)
        }
    }
}

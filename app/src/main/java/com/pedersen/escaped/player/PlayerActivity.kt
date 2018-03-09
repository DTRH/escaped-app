package com.pedersen.escaped.player

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.animations.PositionSpringAnimation
import com.pedersen.escaped.animations.TypeWriter
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.databinding.ActivityPlayerBinding
import io.greenerpastures.mvvm.ViewModelActivity
import timber.log.Timber

class PlayerActivity : ViewModelActivity<PlayerActivityViewModel, ActivityPlayerBinding>(), PlayerActivityViewModel.Commands {

    private var progressBarAnimation: ObjectAnimator = ObjectAnimator()
    private lateinit var progressBar: ProgressBar
    private lateinit var hintView: ConstraintLayout
    private lateinit var hintHeader: TypeWriter
    private lateinit var hintBody: TypeWriter

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_player, BR.viewModel, ({ PlayerActivityViewModel() }))
        super.onCreate(savedInstanceState)

        // Remove all system UI
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE)

        // Bind to progressbar so we can animate it later
        progressBar = binding.progressBar

        // Bind to hintView
        hintView = binding.hintLayout

        // Bind puller
        PositionSpringAnimation(binding.hintPull)

        val hintAdapter = HintsAdapter(viewModel.hintList)
        val hintContainer = binding.hintContainer

        hintContainer.adapter = hintAdapter
        hintContainer.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val hintFragment = HintFragment.newInstance(viewModel.hintList[position])
            fragmentManager.beginTransaction().replace(R.id.fragment_container, hintFragment).commit()
        }
    }

    override fun animateProgressBar(from: Int, to: Int) {
        progressBarAnimation = ObjectAnimator.ofInt(progressBar, "progress", from, to)
        progressBarAnimation.duration = 2000
        progressBarAnimation.start()
    }

    override fun closeHint() {
        if (viewModel.isHintVisible)
            viewModel.isHintVisible = false
        hintBody.clearAnimation()
        hintHeader.clearAnimation()
    }

    inner class HintsAdapter(notesList: ArrayList<Hint>) : BaseAdapter() {

        private var list = notesList

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.hint_list_item, parent, false)
                vh = ViewHolder(view)
                view.tag = vh
                Timber.i("set Tag for ViewHolder, position: $position")
            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            vh.tvTitle.text = list[position].id.toString()
            vh.tvContent.text = list[position].header

            return view
        }

        override fun getItem(position: Int): Any {
            return list[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return list.size
        }
    }

    private class ViewHolder(view: View?) {
        val tvTitle: TextView = view?.findViewById(R.id.id) as TextView
        val tvContent: TextView = view?.findViewById(R.id.header) as TextView
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, PlayerActivity::class.java)
        }
    }
}

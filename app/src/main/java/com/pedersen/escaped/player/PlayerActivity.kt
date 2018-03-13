package com.pedersen.escaped.player

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
    private lateinit var hintHeader: TypeWriter
    private lateinit var hintBody: TypeWriter
    private lateinit var hintAdapter: BaseAdapter

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

        // Setup pull/spring animation for the hint puller
        PositionSpringAnimation(binding.hintPull)

        hintAdapter = HintsAdapter(viewModel.hintList)
        val hintContainer = binding.hintContainer

        hintContainer.adapter = hintAdapter
        hintContainer.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val hintFragment = HintFragment.newInstance(viewModel.hintList[position])
            fragmentManager.beginTransaction().replace(R.id.fragment_container, hintFragment).commit()
        }
    }

    override fun animateProgressBar(from: Int, to: Int) {
        progressBarAnimation = ObjectAnimator.ofInt(binding.progressBar, "progress", from, to)
        progressBarAnimation.duration = 2000
        progressBarAnimation.start()
    }

    override fun closeHint() {
        if (viewModel.isHintVisible)
            viewModel.isHintVisible = false
        hintBody.clearAnimation()
        hintHeader.clearAnimation()
    }

    override fun updateHintList() {
        hintAdapter.notifyDataSetChanged()
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

            vh.tvContent.text = list[position].title

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
        val tvContent: TextView = view?.findViewById(R.id.header) as TextView
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, PlayerActivity::class.java)
        }
    }
}

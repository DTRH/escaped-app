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
import com.pedersen.escaped.animations.TypeWriter
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.databinding.ActivityPlayerBinding
import com.pedersen.escaped.extensions.bind
import io.greenerpastures.mvvm.ViewModelActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_player.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PlayerActivity : ViewModelActivity<PlayerActivityViewModel, ActivityPlayerBinding>(), PlayerActivityViewModel.Commands {

    private var progressBarAnimation: ObjectAnimator = ObjectAnimator()
    private lateinit var progressBar: ProgressBar
    private var hintList = ArrayList<Hint>()
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
        hintHeader = binding.hintHeaderView
        hintBody = binding.hintBodyView

        // Dummy list
        hintList.add(Hint(1, "Afrikastjerne", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
        hintList.add(Hint(2, "Bloddiamant", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
        hintList.add(Hint(3, "Menneskejagt?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
        hintList.add(Hint(4, "Butleren?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
        hintList.add(Hint(5, "lolkat", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
        hintList.add(Hint(6, "Blah bla", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))

        val hintAdapter = HintsAdapter(this, hintList)
        hint_container.adapter = hintAdapter
        hint_container.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            Toast.makeText(this, "Click on " + hintList[position].header, Toast.LENGTH_SHORT).show()
            hintBody.text = ""
            hintHeader.text = ""
            viewModel.isHintVisible = true
            animateHint(hintList[position].id)

        }
    }

    private fun animateHint(id: Int) {
        if (hintList[id-1].hasAnimated) {
            hintHeader.text = hintList[id - 1].header
            hintBody.text = hintList[id - 1].body
        } else {
            hintList[id - 1].hasAnimated = true

            Observable.timer(500, TimeUnit.MICROSECONDS, AndroidSchedulers.mainThread()).subscribe({
                hintHeader.setCharacterDelay(50)
                hintHeader.animateText(hintList[id - 1].header)
            })

            Observable.timer(2000, TimeUnit.MICROSECONDS, AndroidSchedulers.mainThread()).subscribe({
                hintBody.setCharacterDelay(50)
                hintBody.animateText(hintList[id - 1].body)
            })
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
    }

    inner class HintsAdapter(context: Context, notesList: ArrayList<Hint>) : BaseAdapter() {

        private var list = notesList
        private var context: Context? = context

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
        val tvTitle: TextView = view?.findViewById<TextView>(R.id.id) as TextView
        val tvContent: TextView = view?.findViewById<TextView>(R.id.header) as TextView
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, PlayerActivity::class.java)

        }
    }
}

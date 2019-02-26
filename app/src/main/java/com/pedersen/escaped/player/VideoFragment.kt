package com.pedersen.escaped.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.VideoView
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.FragmentVideoBinding
import io.greenerpastures.mvvm.ViewModelFragment
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_video.*
import java.util.concurrent.TimeUnit

class VideoFragment : ViewModelFragment<VideoFragmentViewModel, FragmentVideoBinding>(),
    VideoFragmentViewModel.Commands {

    private lateinit var uri: Uri
    private lateinit var videoView: VideoView
    private lateinit var videoMediaPlayer: MediaPlayer

    override fun onAttachContext(context: Context) {
        initialize(R.layout.fragment_video, BR.viewModel) {
            VideoFragmentViewModel().apply {
                uri = Uri.parse(arguments[VIDEO_URI] as String)
            }
        }
        super.onAttachContext(context)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoView = binding.videoView
        videoView.setVideoURI(uri)
        videoView.setOnPreparedListener {
            videoView.visibility = View.VISIBLE
        }
        videoMediaPlayer = MediaPlayer.create(activity.applicationContext, R.raw.ring_tone)
        videoMediaPlayer.start()
    }

    override fun closeVideo() {
        fragmentManager.beginTransaction()
            .remove(fragmentManager.findFragmentById(R.id.fragment_container)).commit()
    }

    override fun playVideo() {
        root.setBackgroundResource(R.drawable.video_ramme)
        pick_up.visibility = View.GONE
        videoMediaPlayer.stop()
        videoView.visibility = View.VISIBLE
        videoView.start()
        videoView.setOnCompletionListener {
            hang_up.alpha = 1f
            hang_up.isEnabled = true
            Observable.timer(2000, TimeUnit.MILLISECONDS, Schedulers.io()).subscribe {
                closeVideo()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoMediaPlayer.release()
    }

    companion object {

        private const val VIDEO_URI = "video_uri"

        fun newInstance(uri: Uri): VideoFragment {
            val args = Bundle(1)
            args.putString(VideoFragment.VIDEO_URI, uri.toString())
            val fragment = VideoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    interface Commands
}
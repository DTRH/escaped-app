package com.pedersen.escaped.player

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.VideoView
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.FragmentVideoBinding
import io.greenerpastures.mvvm.ViewModelFragment
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class VideoFragment : ViewModelFragment<VideoFragmentViewModel, FragmentVideoBinding>(), VideoFragmentViewModel.Commands {

    private lateinit var uri: Uri
    private lateinit var videoView: VideoView

    override fun onAttachContext(context: Context) {
        initialize(R.layout.fragment_video, BR.viewModel, {
            VideoFragmentViewModel().apply {
                uri = Uri.parse(arguments[VIDEO_URI] as String)
            }
        })
        super.onAttachContext(context)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoView = binding.videoView
        videoView.setOnPreparedListener {
            Observable.timer(2000, TimeUnit.MILLISECONDS).subscribe { videoView.start()
            }
        }
        videoView.setVideoURI(uri)
    }

    override fun closeVideo() {
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.fragment_container)).commit()
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
}
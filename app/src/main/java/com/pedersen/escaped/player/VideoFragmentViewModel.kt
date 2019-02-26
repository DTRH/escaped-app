package com.pedersen.escaped.player

import io.greenerpastures.mvvm.BaseViewModel

class VideoFragmentViewModel : BaseViewModel<VideoFragmentViewModel.Commands>() {

    fun closeVideo() {
        commandHandler?.closeVideo()
    }

    fun playVideo() {
        commandHandler?.playVideo()

    }

    interface Commands {

        fun closeVideo()
        fun playVideo()

    }
}
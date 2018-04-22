package com.pedersen.escaped.player

import io.greenerpastures.mvvm.BaseViewModel

class VideoFragmentViewModel : BaseViewModel<VideoFragmentViewModel.Commands>() {

    fun closeVideo() {
        commandHandler?.closeVideo()
    }

    interface Commands {

        fun closeVideo()

    }
}
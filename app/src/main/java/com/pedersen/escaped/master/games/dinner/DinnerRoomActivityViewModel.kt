package com.pedersen.escaped.master.games.dinner

import io.greenerpastures.mvvm.BaseViewModel

class DinnerRoomActivityViewModel :  BaseViewModel<DinnerRoomActivityViewModel.Commands>() {

    fun launchHintControls() {
        commandHandler?.launchHintControls()
    }

    fun launchVideoControls() {
        commandHandler?.launchVideoControls()
    }

    fun launchRoomCotrols() {
        commandHandler?.launchRoomControls()
    }

    interface Commands {

        fun launchHintControls()

        fun launchVideoControls()

        fun launchRoomControls()

    }
}
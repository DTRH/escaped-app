package com.pedersen.escaped.master

import io.greenerpastures.mvvm.BaseViewModel

class GameMasterActivityViewModel : BaseViewModel<GameMasterActivityViewModel.Commands>() {

    fun launchRoom(id: Int) {
        commandHandler?.launchRoom(id)
    }

    interface Commands {

        fun launchRoom(id: Int)
    }
}
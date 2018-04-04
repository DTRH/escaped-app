package com.pedersen.escaped.master

import android.databinding.Bindable
import com.pedersen.escaped.extensions.bind
import io.greenerpastures.mvvm.BaseViewModel
import com.pedersen.escaped.BR

class GameMasterActivityViewModel : BaseViewModel<GameMasterActivityViewModel.Commands>() {

    @get:Bindable
    var isGameOneHintRequested by bind(false, BR.gameOneHintRequested)

    @get:Bindable
    var isGameTwoHintRequested by bind(true, BR.gameTwoHintRequested)

    fun launchRoom(id: Int) {
        commandHandler?.launchGameActivity(id)
    }

    interface Commands {

        fun launchGameActivity(id: Int)
    }
}
package com.pedersen.escaped.master.games.one

import android.databinding.Bindable
import com.pedersen.escaped.data.models.Hint
import io.greenerpastures.mvvm.BaseViewModel

class GameOneActivityViewModel : BaseViewModel<GameOneActivityViewModel.Commands>() {

    @get:Bindable
    var hintList = ArrayList<Hint>()

    fun launchHintControls() = commandHandler?.launchHintControls()

    fun launchVideoControls() = commandHandler?.launchVideoControls()

    fun launchGameControls() = commandHandler?.launchGameControls()

    interface Commands {

        fun launchHintControls()

        fun launchVideoControls()

        fun launchGameControls()

    }
}
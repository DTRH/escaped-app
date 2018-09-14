package com.pedersen.escaped.master

import android.databinding.Bindable
import com.pedersen.escaped.data.models.Hint
import io.greenerpastures.mvvm.BaseViewModel

class RoomActivityViewModel : BaseViewModel<RoomActivityViewModel.Commands>() {

    @get:Bindable
    var hintList = ArrayList<Hint>()

    fun launchHintControls() = commandHandler?.launchHintControls()

    fun launchVideoControls() = commandHandler?.launchVideoControls()

    fun launchGameControls() = commandHandler?.launchGameControls()

    fun promptFeatureUnavailable() = commandHandler?.promptFeatureUnavailable()

    fun viewWebcam(camera: Int) = commandHandler?.viewWebCam(camera)

    interface Commands {

        fun launchHintControls()

        fun launchVideoControls()

        fun launchGameControls()

        fun promptFeatureUnavailable()

        fun viewWebCam(camera: Int)

    }
}
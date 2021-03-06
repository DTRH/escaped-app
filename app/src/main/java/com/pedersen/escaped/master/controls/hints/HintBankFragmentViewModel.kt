package com.pedersen.escaped.master.controls.hints

import android.databinding.Bindable
import io.greenerpastures.mvvm.BaseViewModel

class HintBankFragmentViewModel : BaseViewModel<HintBankFragmentViewModel.Commands>() {


    @get:Bindable
    var selectedId: Int = -1

    @get:Bindable
    var isCreatable: Boolean = false
        get() = selectedId != -1

    fun closeBank() {
        commandHandler?.closeBank()
    }

    fun sendSelected() {
        commandHandler?.sendSelected()
    }

    fun createNewChallenge() {
        commandHandler?.createNewChallenge()
    }

    interface Commands {
        fun closeBank()
        fun refreshUpdater()
        fun sendSelected()
        fun createNewChallenge()
    }

}

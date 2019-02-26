package com.pedersen.escaped.master.controls.hints

import io.greenerpastures.mvvm.BaseViewModel

class AddHintChallengeFragmentViewModel : BaseViewModel<AddHintChallengeFragmentViewModel.Commands>() {

    fun sendHint() {
        commandHandler?.sendHint()
    }

    fun closeChallengeSelector() {
        commandHandler?.closeChallengeSelector()
    }

    interface Commands {
        fun sendHint()
        fun closeChallengeSelector()
    }

}

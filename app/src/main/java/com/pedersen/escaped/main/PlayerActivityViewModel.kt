package com.pedersen.escaped.main

import io.greenerpastures.mvvm.BaseViewModel

class PlayerActivityViewModel : BaseViewModel<PlayerActivityViewModel.Commands>() {

    var progress: Int = 0
        get() = 50

    interface Commands {}
}

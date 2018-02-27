package com.pedersen.escaped.main

import io.greenerpastures.mvvm.BaseViewModel

class MainActivityViewModel : BaseViewModel<MainActivityViewModel.Commands>() {

    var progress: Int = 0
        get() = 50



    interface Commands {

    }

}

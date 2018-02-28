package com.pedersen.escaped.login

import io.greenerpastures.mvvm.BaseViewModel

/**
 * Created by anderspedersen on 28/02/2018.
 */
class LoginActivityViewModel : BaseViewModel<LoginActivityViewModel.Commands>() {

    interface Commands {

        fun launchGameMasterActivity()

        fun launchPlayerActivity()
    }
}
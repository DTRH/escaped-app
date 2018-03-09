package com.pedersen.escaped.player

import android.databinding.Bindable
import android.view.View
import com.pedersen.escaped.BR
import com.pedersen.escaped.extensions.bind
import io.greenerpastures.mvvm.BaseViewModel

class HintFragmentViewModel : BaseViewModel<HintFragmentViewModel.Commands>() {

    @get:Bindable
    var hintHeader by bind("", BR.hintHeader)

    @get:Bindable
    var hintBody by bind("", BR.hintBody)

    override fun onActive() {
        super.onActive()
        commandHandler?.animateHint(hintHeader, hintBody)
    }

    fun closeHint() {
        commandHandler?.closeHint()
    }

    interface Commands {
        fun animateHint(header: String, body: String)

        fun closeHint()
    }
}
package com.pedersen.escaped.player

import android.databinding.Bindable
import com.pedersen.escaped.BR
import com.pedersen.escaped.extensions.bind
import io.greenerpastures.mvvm.BaseViewModel

class HintFragmentViewModel : BaseViewModel<HintFragmentViewModel.Commands>() {

    @get:Bindable
    var hintHeader by bind("", BR.hintHeader)

    @get:Bindable
    var hintBody by bind("", BR.hintBody)

    interface Commands
}
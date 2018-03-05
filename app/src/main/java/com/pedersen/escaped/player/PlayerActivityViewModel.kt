package com.pedersen.escaped.player

import android.databinding.Bindable
import android.util.Log
import android.view.View

import com.pedersen.escaped.BR
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.extensions.bind
import io.greenerpastures.mvvm.BaseViewModel

class PlayerActivityViewModel : BaseViewModel<PlayerActivityViewModel.Commands>() {

    private var hintList = ArrayList<Hint>()

    @get:Bindable
    var progress: Int = 0
        set(value) {
            if (value == field) return
            commandHandler?.animateProgressBar(field, value)
            field = value
            notifyPropertyChanged(BR.progress)
        }

    @get:Bindable
    var isHintVisible by bind(false, BR.hintVisible)

    override fun onActive() {
        super.onActive()
        progress = 50
    }

    fun closeHint(view: View) {
        commandHandler?.closeHint()
    }

    interface Commands {

        fun animateProgressBar(from: Int, to: Int)

        fun closeHint()
    }
}

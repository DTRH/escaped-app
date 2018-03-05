package com.pedersen.escaped.player

import android.databinding.Bindable
import com.pedersen.escaped.BR
import io.greenerpastures.mvvm.BaseViewModel

class PlayerActivityViewModel : BaseViewModel<PlayerActivityViewModel.Commands>() {

    override fun onActive() {
        super.onActive()
        progress = 50
    }

    @Bindable
    var progress: Int = 0
        set(value) {
            if (value == field) return
            commandHandler?.animateProgressBar(field, value)
            field = value
            notifyPropertyChanged(BR.progress)
        }



    interface Commands {

        fun animateProgressBar(from: Int, to: Int)
    }
}

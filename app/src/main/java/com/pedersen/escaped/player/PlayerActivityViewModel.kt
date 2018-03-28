package com.pedersen.escaped.player

import android.annotation.SuppressLint
import android.databinding.Bindable
import android.view.View

import com.pedersen.escaped.BR
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.extensions.bind
import io.greenerpastures.mvvm.BaseViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import com.pedersen.escaped.BuildConfig


class PlayerActivityViewModel : BaseViewModel<PlayerActivityViewModel.Commands>() {

    @get:Bindable
    var hintList = ArrayList<Hint>()

    @get:Bindable
    var progress: Int = 0
        set(value) {
            if (value == field) return
            commandHandler?.animateProgressBar(field, value)
            field = value
            //notifyPropertyChanged(BR.progress)
        }

    @SuppressLint("MissingSuperCall")
    override fun onActive() {
        super.onActive()
        progress = 50
    }

    interface Commands {

        fun animateProgressBar(from: Int, to: Int)

    }
}

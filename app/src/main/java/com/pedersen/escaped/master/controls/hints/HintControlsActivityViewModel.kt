package com.pedersen.escaped.master.controls.hints

import android.databinding.Bindable
import com.google.firebase.database.*
import com.pedersen.escaped.BR
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.extensions.bind
import io.greenerpastures.mvvm.BaseViewModel
import timber.log.Timber

class HintControlsActivityViewModel : BaseViewModel<HintControlsActivityViewModel.Commands>() {

    @get:Bindable
    var selectedId: ArrayList<String> = ArrayList()

    @get:Bindable
    var hintList = ArrayList<Hint>()

    @get:Bindable
    var isDeletable: Boolean = false
        get() = !selectedId.isEmpty()

    @get:Bindable
    var isEditable: Boolean = false
        get() = selectedId.size == 1

    @get:Bindable
    var isCreatable: Boolean = false
        get() = commandHandler!!.checkCreatable()

    fun createHint() = commandHandler?.createHint()

    fun deleteHint() = commandHandler?.deleteHint()

    fun editHint() = commandHandler?.editHint()

    interface Commands {

        fun checkCreatable(): Boolean

        fun createHint()

        fun deleteHint()

        fun editHint()

    }

}

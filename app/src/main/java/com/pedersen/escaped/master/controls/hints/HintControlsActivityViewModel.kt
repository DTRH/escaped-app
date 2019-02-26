package com.pedersen.escaped.master.controls.hints

import android.databinding.Bindable
import com.pedersen.escaped.data.models.Hint
import io.greenerpastures.mvvm.BaseViewModel

class HintControlsActivityViewModel : BaseViewModel<HintControlsActivityViewModel.Commands>() {

    @get:Bindable
    var selectedId: ArrayList<Hint> = ArrayList()

    @get:Bindable
    var isDeletable: Boolean = false
        get() = selectedId.isNotEmpty()

    @get:Bindable
    var isEditable: Boolean = false
        get() = selectedId.size == 1

    @get:Bindable
    var isCreatable: Boolean = false
        get() = commandHandler!!.checkCreatable()

    fun createHint() = commandHandler?.createHint()

    fun deleteHint() = commandHandler?.deleteHint()

    fun editHint() = commandHandler?.editHint()

    fun addHintToBank() = commandHandler?.addHintToBank()

    fun openHintBank() = commandHandler?.openHintBank()

    interface Commands {

        fun checkCreatable(): Boolean

        fun createHint()

        fun deleteHint()

        fun editHint()

        fun addHintToBank()

        fun openHintBank()

    }
}

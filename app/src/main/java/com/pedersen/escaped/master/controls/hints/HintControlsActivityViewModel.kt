package com.pedersen.escaped.master.controls.hints

import android.databinding.Bindable
import com.pedersen.escaped.data.models.Hint
import io.greenerpastures.mvvm.BaseViewModel

class HintControlsActivityViewModel : BaseViewModel<HintControlsActivityViewModel.Commands>() {

    @get:Bindable
    var hintList = ArrayList<Hint>()


    interface Commands

}

package com.pedersen.escaped.player

import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.databinding.HintFragmentBinding
import io.greenerpastures.mvvm.ViewModelFragment

class HintFragment : ViewModelFragment<HintFragmentViewModel, HintFragmentBinding>() {


    companion object {

        fun newInstance(hintId: Int): HintFragment {
            return HintFragment()
        }
    }
}
package com.pedersen.escaped.master.controls.hints

import android.content.Context
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.HintControlsFragmentBinding
import com.pedersen.escaped.player.HintFragment
import io.greenerpastures.mvvm.ViewModelFragment

class HintControlsFragment : ViewModelFragment<HintControlsFragmentViewModel, HintControlsFragmentBinding>(), HintControlsFragmentViewModel.Commands {

    override fun onAttachContext(context: Context) {
        initialize(R.layout.hint_controls_fragment, BR.viewModel, { HintControlsFragmentViewModel() })
        super.onAttachContext(context)
    }

    companion object {

        fun newInstance(): HintFragment {
            return HintFragment()
        }
    }



}

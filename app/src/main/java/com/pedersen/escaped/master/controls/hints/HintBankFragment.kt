package com.pedersen.escaped.master.controls.hints

import android.content.Context
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.FragmentHintBankBinding
import io.greenerpastures.mvvm.ViewModelFragment

class HintBankFragment : ViewModelFragment<HintBankFragmentViewModel, FragmentHintBankBinding>(),
        HintBankFragmentViewModel.Commands {

    override fun onAttachContext(context: Context) {
        initialize(R.layout.fragment_hint_bank, BR.viewModel) {
            HintBankFragmentViewModel()
        }
        super.onAttachContext(context)
    }

    companion object {

        fun newInstance(): HintBankFragment {
            return HintBankFragment()
        }
    }

    interface Commands
}
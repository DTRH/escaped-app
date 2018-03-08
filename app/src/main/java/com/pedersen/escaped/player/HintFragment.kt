package com.pedersen.escaped.player

import android.content.Context
import android.os.Bundle
import android.view.View
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.databinding.HintFragmentBinding
import io.greenerpastures.mvvm.ViewModelFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class HintFragment : ViewModelFragment<HintFragmentViewModel, HintFragmentBinding>() {

    private lateinit var bodyTimer: Disposable

    override fun onAttachContext(context: Context) {
        initialize(R.layout.hint_fragment, BR.viewModel, { HintFragmentViewModel() })
        super.onAttachContext(context)

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val closeButton = binding.backButton

        closeButton.setOnClickListener {
            fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.fragment_container)).commit()
        }

        val header = binding.hintHeaderView
        val body = binding.hintBodyView

        header.setCharacterDelay(100L)
        header.animateText(arguments[HINT_HEADER] as CharSequence)
        bodyTimer = Observable.timer(2000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe({
                    body.setCharacterDelay(50)
                    body.animateText(arguments[HINT_BODY] as CharSequence)
                })
    }

    override fun onStop() {
        super.onStop()
        
        bodyTimer.dispose()
    }

    companion object {

        private const val HINT_HEADER = "hint_header"
        private const val HINT_BODY = "hint_body"

        fun newInstance(hint: Hint): HintFragment {
            val args = Bundle(2)
            args.putString(HINT_HEADER, hint.header)
            args.putString(HINT_BODY, hint.body)
            val fragment = HintFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
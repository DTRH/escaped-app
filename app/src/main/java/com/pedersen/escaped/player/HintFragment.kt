package com.pedersen.escaped.player

import android.content.Context
import android.os.Bundle
import android.view.View
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.animations.TypeWriter
import com.pedersen.escaped.data.models.Hint
import com.pedersen.escaped.databinding.HintFragmentBinding
import io.greenerpastures.mvvm.ViewModelFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class HintFragment : ViewModelFragment<HintFragmentViewModel, HintFragmentBinding>(), HintFragmentViewModel.Commands {

    private lateinit var bodyTimer: Disposable
    private lateinit var hintHeaderView: TypeWriter
    private lateinit var hintBodyView: TypeWriter

    override fun onAttachContext(context: Context) {
        initialize(R.layout.hint_fragment, BR.viewModel) {
            HintFragmentViewModel().apply {
                hintHeader = arguments[HINT_HEADER] as String
                hintBody = arguments[HINT_BODY] as String
            }
        }
        super.onAttachContext(context)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hintHeaderView = binding.hintHeaderView
        hintBodyView = binding.hintBodyView
        hintHeaderView.setCharacterDelay(100L)
        hintBodyView.setCharacterDelay(50)
    }

    override fun onStop() {
        super.onStop()
        bodyTimer.dispose()
    }

    override fun animateHint(header: String, body: String) {
        hintHeaderView.animateText(arguments[HINT_HEADER] as CharSequence)
        bodyTimer = Observable.timer(2000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe({
                    hintBodyView.animateText(arguments[HINT_BODY] as CharSequence)
                })
    }

    override fun closeHint() {
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.fragment_container)).commit()
    }

    companion object {

        private const val HINT_HEADER = "hint_header"
        private const val HINT_BODY = "hint_body"

        fun newInstance(hint: Hint): HintFragment {
            val args = Bundle(2)
            args.putString(HINT_HEADER, hint.title)
            args.putString(HINT_BODY, hint.body)
            val fragment = HintFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
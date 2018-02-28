package com.pedersen.escaped.login

import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityLoginBinding
import com.pedersen.escaped.player.PlayerActivity
import io.greenerpastures.mvvm.ViewModelActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : ViewModelActivity<LoginActivityViewModel, ActivityLoginBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_login, BR.viewModel, ({ LoginActivityViewModel() }))
        super.onCreate(savedInstanceState)

        button1.setOnClickListener {
            val intent = PlayerActivity.newIntent(this)
            startActivity(intent)
        }
    }






}

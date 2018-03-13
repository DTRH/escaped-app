package com.pedersen.escaped.login

import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.BuildConfig
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityLoginBinding
import com.pedersen.escaped.master.GameMasterActivity
import com.pedersen.escaped.player.PlayerActivity
import io.greenerpastures.mvvm.ViewModelActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : ViewModelActivity<LoginActivityViewModel, ActivityLoginBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_login, BR.viewModel, ({ LoginActivityViewModel() }))
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (!BuildConfig.isMaster) {
            val intent = PlayerActivity.newIntent(this)
            startActivity(intent)
        } else {
            val intent = GameMasterActivity.newIntent(this)
            startActivity(intent)
        }
    }
}

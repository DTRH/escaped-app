package com.pedersen.escaped.login

import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.BuildConfig
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityLoginBinding
import com.pedersen.escaped.master.GameMasterActivity
import com.pedersen.escaped.player.PlayerActivity
import io.greenerpastures.mvvm.ViewModelActivity

class LoginActivity : ViewModelActivity<LoginActivityViewModel, ActivityLoginBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_login, BR.viewModel, ({ LoginActivityViewModel() }))
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (!BuildConfig.isMaster) {
            startActivity(PlayerActivity.newIntent(this))
        } else {
            startActivity(GameMasterActivity.newIntent(this))
        }
    }
}

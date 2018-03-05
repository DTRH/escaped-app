package com.pedersen.escaped.master.rooms.dinner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityDinnerRoomBinding
import io.greenerpastures.mvvm.ViewModelActivity

class DinnerRoomActivity : ViewModelActivity<DinnerRoomActivityViewModel, ActivityDinnerRoomBinding>(), DinnerRoomActivityViewModel.Commands {

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_dinner_room, BR.viewModel, ({ DinnerRoomActivityViewModel() }))
        super.onCreate(savedInstanceState)
    }


    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, DinnerRoomActivity::class.java)
        }
    }
}
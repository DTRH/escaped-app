package com.pedersen.escaped.master.rooms.two

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityRoomTwoBinding
import io.greenerpastures.mvvm.ViewModelActivity

class RoomTwoActivity : ViewModelActivity<RoomTwoActivityViewModel, ActivityRoomTwoBinding>(), RoomTwoActivityViewModel.Commands {

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_room_two, BR.viewModel, ({ RoomTwoActivityViewModel() }))
        super.onCreate(savedInstanceState)
    }


    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, RoomTwoActivity::class.java)
        }
    }
}

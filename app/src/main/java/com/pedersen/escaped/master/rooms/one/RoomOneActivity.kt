package com.pedersen.escaped.master.rooms.one

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.pedersen.escaped.BR
import com.pedersen.escaped.R
import com.pedersen.escaped.databinding.ActivityRoomOneBinding
import io.greenerpastures.mvvm.ViewModelActivity

class RoomOneActivity : ViewModelActivity<RoomOneActivityViewModel, ActivityRoomOneBinding>(), RoomOneActivityViewModel.Commands {

    override fun onCreate(savedInstanceState: Bundle?) {
        initialize(R.layout.activity_room_one, BR.viewModel, ({ RoomOneActivityViewModel() }))
        super.onCreate(savedInstanceState)
    }


    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, RoomOneActivity::class.java)
        }
    }
}

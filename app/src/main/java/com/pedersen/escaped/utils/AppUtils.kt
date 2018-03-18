package com.pedersen.escaped.utils

import android.view.View
import android.view.Window
import com.pedersen.escaped.BuildConfig

object AppUtils {

    fun isMaster(): Boolean {
        return BuildConfig.isMaster
    }

    fun gameId(): Int {
        return BuildConfig.gameId
    }

    fun clearWindow(window: Window) {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

}
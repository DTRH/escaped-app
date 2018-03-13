package com.pedersen.escaped.utils

import com.pedersen.escaped.BuildConfig

class AppUtils {

    fun isMaster(): Boolean {
        return BuildConfig.isMaster
    }

    fun gameId(): Int {
        return BuildConfig.gameId
    }

}
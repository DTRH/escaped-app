package com.pedersen.escaped.utils

import android.support.design.widget.Snackbar
import android.view.View
import android.view.Window
import com.pedersen.escaped.BuildConfig
import java.util.concurrent.TimeUnit

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

    fun getDurationBreakdown(millis: Long): String {
        var millis = millis
        if (millis < 0) {
            throw IllegalArgumentException("Duration must be greater than zero!")
        }

        val days = TimeUnit.MILLISECONDS.toDays(millis)
        millis -= TimeUnit.DAYS.toMillis(days)
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)

        val sb = StringBuilder(64)
        sb.append(days)
        sb.append(" Days ")
        sb.append(hours)
        sb.append(" Hours ")
        sb.append(minutes)
        sb.append(" Minutes ")
        sb.append(seconds)
        sb.append(" Seconds")

        return sb.toString()
    }

    fun showSnack(s: String, w: View) {
        val snackbar: Snackbar = Snackbar.make(w, s, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

}
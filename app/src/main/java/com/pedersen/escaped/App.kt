package com.pedersen.escaped

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // We only wanna log debug stuff in debug build & demo flavor.
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                return Thread.currentThread().name + '/' +
                        super.createStackElementTag(element)
            }
        })

        AndroidThreeTen.init(this)
    }
}
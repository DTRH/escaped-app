package com.pedersen.escaped

import android.app.Application
import timber.log.Timber

/**
 * Created by anderspedersen on 19/02/2018.
 */
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
    }
}
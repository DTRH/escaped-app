package com.pedersen.escaped.extensions

import android.databinding.BaseObservable
import kotlin.reflect.KProperty

fun <T : Any?> bind(defaultValue: T, vararg bindingRes: Int) =
        BindableDelegate<BaseObservable, T>(defaultValue, bindingRes)

fun <T : Any?> bindDistinct(defaultValue: T, vararg bindingRes: Int) =
        DistinctBindableDelegate<BaseObservable, T>(defaultValue, bindingRes)

class BindableDelegate<in R : BaseObservable, T : Any?>(defaultValue: T,
                                                        private val bindingRes: IntArray) {
    private var value: T = defaultValue

    operator fun getValue(thisRef: R, property: KProperty<*>): T = value

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        this.value = value
        bindingRes.forEach { thisRef.notifyPropertyChanged(it) }
    }
}

class DistinctBindableDelegate<in R : BaseObservable, T : Any?>(defaultValue: T,
                                                                private val bindingRes: IntArray) {
    private var value: T = defaultValue

    operator fun getValue(thisRef: R, property: KProperty<*>): T = value

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        if (this.value != value) {
            this.value = value
            bindingRes.forEach { thisRef.notifyPropertyChanged(it) }
        }
    }
}
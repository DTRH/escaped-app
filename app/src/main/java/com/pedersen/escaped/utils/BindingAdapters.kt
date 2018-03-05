package com.pedersen.escaped.utils

import android.databinding.BindingAdapter
import android.view.View

@BindingAdapter("android:visibility")
fun bindVisibility(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}
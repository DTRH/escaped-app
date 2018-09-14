package com.pedersen.escaped.utils

import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.view.View

/**
 * Created by anderspedersen on 08/03/2018.
 */
object SpringAnimationUtil {

    fun createSpringAnimation(view: View,
                              property: DynamicAnimation.ViewProperty,
                              finalPosition: Float,
                              stiffness: Float,
                              dampingRatio: Float): SpringAnimation {
        val animation = SpringAnimation(view, property)
        val springForce = SpringForce(finalPosition)
        springForce.stiffness = stiffness
        springForce.dampingRatio = dampingRatio
        animation.spring = springForce
        return animation
    }

}
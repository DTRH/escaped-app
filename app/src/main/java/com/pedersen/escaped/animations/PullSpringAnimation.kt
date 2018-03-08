package com.pedersen.escaped.animations

import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import com.pedersen.escaped.utils.SpringAnimationUtil.createSpringAnimation

/**
 * Created by anderspedersen on 08/03/2018.
 */
class PositionSpringAnimation(animatedView: View?) {

    private var animatedView: View? = null

    private var xAnimation: SpringAnimation? = null
    private var yAnimation: SpringAnimation? = null

    private var dX: Float = 0.toFloat()
    private var dY: Float = 0.toFloat()

    // create X and Y animations for view's initial position once it's known
    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        yAnimation = animatedView?.let {
            createSpringAnimation(it, SpringAnimation.Y, animatedView.y,
                    SpringForce.STIFFNESS_MEDIUM, SpringForce.DAMPING_RATIO_HIGH_BOUNCY)
        }
    }

    var initY = 0f
    private val touchListener = View.OnTouchListener { v, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // capture the difference between view's top left corner and touch point
                dY = v.y - event.rawY
                initY = event.rawY
                // cancel animations
                yAnimation!!.cancel()
            }
            MotionEvent.ACTION_MOVE ->
                if ((event.rawY - initY) < 200 && (event.rawY > initY)) {
                //  a different approach would be to change the view's LayoutParams.
                animatedView!!.animate()
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                } else
                    yAnimation!!.start()
            MotionEvent.ACTION_UP -> {
                yAnimation!!.start()
            }
        }
        true
    }

    init {
        if (animatedView != null) {
            this.animatedView = animatedView
            animatedView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
            this.animatedView!!.setOnTouchListener(touchListener)
        }
    }
}

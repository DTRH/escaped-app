package com.pedersen.escaped.animations

import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import com.pedersen.escaped.utils.SpringAnimationUtil.createSpringAnimation

class PositionSpringAnimation(animatedView: View?) {

    private var animatedView: View? = null
    private var yAnimation: SpringAnimation? = null
    private var dY: Float = 0.toFloat()

    private var mEventListener: PullingEventListener? = null

    // create X and Y animations for view's initial position once it's known
    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        yAnimation = animatedView?.let {
            createSpringAnimation(it, SpringAnimation.Y, animatedView.y,
                    SpringForce.STIFFNESS_MEDIUM, SpringForce.DAMPING_RATIO_HIGH_BOUNCY)
        }
    }

    private var initY = 0f
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
                if ((event.rawY - initY) < 125 && (event.rawY > initY)) {
                    //  a different approach would be to change the view's LayoutParams.
                    animatedView!!.animate()
                            .y(event.rawY + dY)
                            .setDuration(0)
                            .start()

                    mEventListener?.pullAccured()

                } else
                    yAnimation!!.start()
            MotionEvent.ACTION_UP -> {
                yAnimation!!.start()
            }
        }
        true
    }

    fun setEventListener(mEventListener: PullingEventListener) {
        this.mEventListener = mEventListener
    }

    interface PullingEventListener {
        fun pullAccured()
    }

    init {
        if (animatedView != null) {
            this.animatedView = animatedView
            animatedView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
            this.animatedView!!.setOnTouchListener(touchListener)
        }
    }
}

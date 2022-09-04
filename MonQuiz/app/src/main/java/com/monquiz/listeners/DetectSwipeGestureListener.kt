package com.monquiz.listeners

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent

class DetectSwipeGestureListener(
    private val gestureSwipeUp: GestureSwipeUp,
    private val gestureSwipeDown: GestureSwipeDown
) : SimpleOnGestureListener() {
    fun interface GestureSwipeUp {
        fun swipeUpCallBack()
    }

    fun interface GestureSwipeDown {
        fun swipeDownCallBack()
    }

    /* This method is invoked when a swipe gesture happened. */
    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {

        // Get swipe delta value in y axis.
        val deltaY = e1.y - e2.y

        // Get absolute value.
        val deltaYAbs = Math.abs(deltaY)
        if (deltaYAbs >= MIN_SWIPE_DISTANCE_Y && deltaYAbs <= MAX_SWIPE_DISTANCE_Y) {
            if (deltaY > 0) {
                gestureSwipeUp.swipeUpCallBack()
            } else {
                gestureSwipeDown.swipeDownCallBack()
            }
        }
        return true
    }

    // Invoked when single tap screen.
    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return false
    }

    // Invoked when double tap screen.
    override fun onDoubleTap(e: MotionEvent): Boolean {
        return false
    }

    companion object {
        // Minimal x and y axis swipe distance.
        private const val MIN_SWIPE_DISTANCE_Y = 100

        // Maximal x and y axis swipe distance.
        private const val MAX_SWIPE_DISTANCE_Y = 1000
    }
}
package com.teamttdvlp.memolang.view.ui

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import java.lang.Exception
import kotlin.math.abs

open class OnSwipeUpListener :  View.OnTouchListener {

    val gestureDetector : GestureDetector

    constructor(context : Context) {
        gestureDetector = GestureDetector(context, GestureListener())
    }

    override fun onTouch(p0: View?, event : MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    open fun onSwipeUp () {}

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        val STANDARD_VELOCITY = 100

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?, e2: MotionEvent?,
            velocityX: Float, velocityY: Float ): Boolean {
            Log.e("hjk", "jkl")
            var result = false

            try {
                var deltaY= e2!!.y - e1!!.y
                val isSwipeUp = deltaY < 0
                if ( isSwipeUp/* and (velocityY > STANDARD_VELOCITY)*/) {
                    onSwipeUp()
                    result = true
                }
            } catch (ex : Exception) {
                Log.e("Error", ex.message)
            }

            return result

        }


    }

}
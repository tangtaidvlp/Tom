package com.teamttdvlp.memolang.view.customview

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.teamttdvlp.memolang.view.helper.dp
import com.teamttdvlp.memolang.view.helper.quickLog
import java.lang.Exception
import kotlin.math.absoluteValue

open class MyGestureDetector :  View.OnTouchListener {

    val gestureDetector : GestureDetector

    constructor(context : Context) {
        gestureDetector = GestureDetector(context, GestureListener())
    }

    override fun onTouch(p0: View?, event : MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    open fun onSwipeUp () {}

    open fun onSwipeRight () {}

    open fun onSwipeDown () {}

    open fun onSwipeLeft () {}

    open fun onClick () {}

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        val STANDARD_Y_VELOCITY = 60.dp()

        val STANDARD_X_VELOCITY = 40.dp()

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?, e2: MotionEvent?,
            velocityX: Float, velocityY: Float ): Boolean {
            var result = false

            try {
                var deltaY= e2!!.y - e1!!.y
                var deltaX= e2!!.x - e1!!.x
                val isSwipeUp =
                    (deltaY < 0) and (velocityY.absoluteValue > STANDARD_Y_VELOCITY) and (velocityY.absoluteValue > velocityX.absoluteValue)
                val isSwipeDown =
                    (deltaY > 0) and (velocityY.absoluteValue > STANDARD_Y_VELOCITY) and (velocityY.absoluteValue > velocityX.absoluteValue)
                val isSwipeRight = (deltaX > 0) and (velocityX.absoluteValue > STANDARD_X_VELOCITY) and (velocityX.absoluteValue > velocityY.absoluteValue )
                val isSwipeLeft = (deltaX <  0) and (velocityX.absoluteValue > STANDARD_X_VELOCITY) and (velocityX.absoluteValue > velocityY.absoluteValue )
                if ( isSwipeUp) {
                    onSwipeUp()
                    result = true
                }
                else if (isSwipeDown) {
                    onSwipeDown()
                }
                else if (isSwipeRight) {
                    onSwipeRight()
                }
                else if (isSwipeLeft) {
                    onSwipeLeft()
                }
            } catch (ex : Exception) {
                Log.e("Error", ex.message)
            }

            return result

        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            onClick()
            return true
        }

    }

}
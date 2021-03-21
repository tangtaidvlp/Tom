package com.teamttdvlp.memolang.view.customview

import android.os.Handler
import android.os.SystemClock
import android.view.View

private const val MIN_CLICK_INTERVAL = 500L
class OnSingleClickListener : View.OnClickListener {

    private var lastClickTime : Long = 0

    private var onClickEvent : (View?) -> Unit

    var clickable = true

    constructor(onClickEvent: (View?) -> Unit) {
        this.onClickEvent = onClickEvent
    }

    override fun onClick(v: View?) {

        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime: Long = currentClickTime - lastClickTime

        if (elapsedTime <= MIN_CLICK_INTERVAL) return

        if (clickable){
            clickable = false
            clickableAfter(interval = MIN_CLICK_INTERVAL)
        } else {
            return
        }

        onClickEvent(v)

        lastClickTime = currentClickTime
    }

    private fun clickableAfter (interval : Long) {
        val handler = Handler()
        handler.postDelayed(
            Runnable {
                clickable = true
            }, interval
        )
    }

}
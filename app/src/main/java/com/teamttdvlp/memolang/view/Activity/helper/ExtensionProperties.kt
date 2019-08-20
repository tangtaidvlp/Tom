package com.teamttdvlp.memolang.view.Activity.helper

import android.util.Log
import android.view.animation.Animation

fun quickLog (message : String) {
    Log.e("QuickLog: ", message)
}

fun Animation.addAnimationLister (onStart : ((Animation?) -> Unit)? = null, onEnd : ((Animation?) -> Unit)? = null, onRepeat: ((Animation?) -> Unit)? = null) {
    setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) {
            onRepeat?.invoke(p0)
        }

        override fun onAnimationEnd(p0: Animation?) {
            onEnd?.invoke(p0)
        }

        override fun onAnimationStart(p0: Animation?) {
            onStart?.invoke(p0)
        }
    })
}

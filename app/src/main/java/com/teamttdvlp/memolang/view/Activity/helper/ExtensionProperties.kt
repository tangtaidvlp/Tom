package com.teamttdvlp.memolang.view.Activity.helper

import android.util.Log
import android.view.View
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


fun View.disappear() {
    visibility = View.GONE
}

fun View.appear() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.isVisible () : Boolean{
    return visibility == View.VISIBLE
}

fun View.isGone () : Boolean{
    return visibility == View.GONE
}

fun View.isInvisible() : Boolean{
    return visibility == View.INVISIBLE
}
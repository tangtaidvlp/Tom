package com.teamttdvlp.memolang.view.helper

import android.animation.Animator
import android.animation.PropertyValuesHolder
import android.view.ViewPropertyAnimator
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd

fun Animator.infiniteRepeat() {
    addListener (onEnd = {
        start()
    })
}

fun ViewPropertyAnimator.reset() : ViewPropertyAnimator{
    return this.setDuration(0).setStartDelay(0).setInterpolator(null)
}

fun ViewPropertyAnimator.setLiteListener (
        onRepeat : (animation : Animator?) -> Unit = {},
        onEnd2 : (animation: Animator?, isReverse: Boolean) -> Unit = {_,_ -> },
        onEnd : (animation: Animator?) -> Unit = {},
        onCancel : (animation: Animator?) -> Unit = {},
        onStart2 : (animation: Animator?, isReverse: Boolean) -> Unit = {_,_ -> },
        onStart : (animation: Animator?) -> Unit = {}) : ViewPropertyAnimator{

    setListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
            onRepeat.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
            onEnd2.invoke(animation, isReverse)
        }

        override fun onAnimationEnd(animation: Animator?) {
            onEnd.invoke(animation)
        }

        override fun onAnimationCancel(animation: Animator?) {
                onCancel.invoke(animation)
        }

        override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                onStart2.invoke(animation, isReverse)
        }

        override fun onAnimationStart(animation: Animator?) {
                onStart.invoke(animation)
        }
    })

    return this
}

fun ViewPropertyAnimator.clearListeners () {
    setLiteListener()
}
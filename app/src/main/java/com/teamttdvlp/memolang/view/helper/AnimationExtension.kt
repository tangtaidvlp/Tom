package com.teamttdvlp.memolang.view.helper

import android.animation.Animator
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.Animation
import android.view.animation.Interpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.time.Duration

fun Animation.setAnimationListener (
                                    onStart : ((Animation?) -> Unit)? = null,
                                    onRepeat : ((Animation?) -> Unit)? = null,
                                    onEnd : ((Animation?) -> Unit)? = null) {

    setAnimationListener(object : Animation.AnimationListener {

        override fun onAnimationRepeat(animation: Animation?) {
            onRepeat?.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animation?) {
            onEnd?.invoke(animation)
        }

        override fun onAnimationStart(animation: Animation?) {
            onStart?.invoke(animation)
        }

    })
}


fun View.goGone_ByFadeOut(duration: Long = 100, interpolator : Interpolator = FastOutSlowInInterpolator(),
                          onRepeat : (animation : Animator?) -> Unit = {},
                          onEnd2 : (animation: Animator?, isReverse: Boolean) -> Unit = {_,_ -> },
                          onEnd : (animation: Animator?) -> Unit = {},
                          onCancel : (animation: Animator?) -> Unit = {},
                          onStart2 : (animation: Animator?, isReverse: Boolean) -> Unit = {_,_ -> },
                          onStart : (animation: Animator?) -> Unit = {}): ViewPropertyAnimator? {
    return animate().reset().alpha(0f).
    setDuration(duration).setInterpolator(interpolator).setLiteListener(
            onRepeat = onRepeat,
            onEnd2 = onEnd2,
            onEnd = if (onEnd == {}) {
                {
                    goGONE()
                }
            } else onEnd,
            onCancel = onCancel,
            onStart2 = onStart2,
            onStart = onStart)
}

fun View.goVisible_ByFadeIn(duration: Long = 100, interpolator : Interpolator = FastOutSlowInInterpolator(),
                          onRepeat : (animation : Animator?) -> Unit = {},
                          onEnd2 : (animation: Animator?, isReverse: Boolean) -> Unit = {_,_ -> },
                          onEnd : (animation: Animator?) -> Unit = {},
                          onCancel : (animation: Animator?) -> Unit = {},
                          onStart2 : (animation: Animator?, isReverse: Boolean) -> Unit = {_,_ -> },
                          onStart : (animation: Animator?) -> Unit = {}): ViewPropertyAnimator? {
    goVISIBLE()
    return animate().reset().alpha(1f).
    setDuration(duration).setInterpolator(interpolator).setLiteListener(
        onRepeat = onRepeat,
        onEnd2 = onEnd2,
        onEnd = onEnd,
        onCancel = onCancel,
        onStart2 = onStart2,
        onStart = onStart)
}
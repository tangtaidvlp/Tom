package com.teamttdvlp.memolang.view.customview.interpolator

import android.view.animation.Interpolator
import kotlin.math.pow

class StuckAtFirstInterpolator : Interpolator {

    override fun getInterpolation(x : Float): Float {
        if (x == 1f) return 1f
        return when {
            x <= 0.1024f -> {
                x * 3
            }
            x <= 0.394565f -> {
                0.3f + x * 0.07f
            }
            else -> {
                1f - (1f - x + 0.3f).pow(4f)
            }
        }
    }

}
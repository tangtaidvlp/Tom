package com.teamttdvlp.memolang.view.customview.interpolator

import android.view.animation.Interpolator
import com.teamttdvlp.memolang.view.helper.systemOutLogging
import java.lang.Exception

class ValleyInterpolator (private val deep : Float) : Interpolator {
    private var a : Float

    private var b : Float

    init {
        if (deep < 0f) {
            throw Exception("Deep value must be bigger than 0")
        }
        b = - 4 * deep
        a = - b
        systemOutLogging("a: " + a)
        systemOutLogging("b: " + b)
    }

    override fun getInterpolation(input: Float): Float {
        if (input == 0f || input == 1f) {
            return 1f
        }
        val result = (a * Math.pow(input.toDouble(), 2.0) + b * input + 1f).toFloat()
        systemOutLogging(result)
        return (a * Math.pow(input.toDouble(), 2.0) + b * input + 1f).toFloat()
    }
}
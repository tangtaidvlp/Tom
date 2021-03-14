package com.teamttdvlp.memolang.view.customview.interpolator

import android.view.animation.Interpolator
import com.teamttdvlp.memolang.view.helper.systemOutLogging
import java.lang.Exception
import kotlin.math.pow

class GearingDecelerateInterpolator : Interpolator {

    val DEFAULT_DEEP_LEVEL = 10f/100f

    val DEFAULT_GEARING_PART = 30f/100f

    private var deepLevel : Float = DEFAULT_DEEP_LEVEL

    private var gearingPart : Float = DEFAULT_GEARING_PART


    /**
     * Gearing interpolator function has form:    y = A * x^2 + B * x
     * A parapol graph which heads down has 2 solutions are 0 and #gearingPart
     */

    private var A_factor : Float = 0f

    private var B_factor : Float = 0f

    constructor(deepLevel : Float, gearingPart : Float) {
        this.deepLevel = deepLevel
        this.gearingPart = gearingPart
        calculate_A_and_B_factors_value()
    }

    constructor() {
        calculate_A_and_B_factors_value()
    }

    private fun calculate_A_and_B_factors_value () {
        systemOutLogging(gearingPart.toString() + " - " + deepLevel)
        B_factor = - 1 * deepLevel * 4f / gearingPart
        A_factor = (-1 * B_factor) / gearingPart
        systemOutLogging("A $A_factor - B $B_factor")
    }

    override fun getInterpolation(x: Float): Float {
        if (x == 1f) return 1f

        if (x <= gearingPart) {

            return A_factor * x.pow(2)  +  B_factor  * x

        } else if (x > gearingPart) {

            return 1f - (1f - x + gearingPart).pow(2)

        }

        throw Exception ("??? Interpolator with input:  " + x)
    }

}
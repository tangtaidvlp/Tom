package com.teamttdvlp.memolang.view.helper

import android.content.res.Resources
import android.util.TypedValue

object UnitConverter {

    internal var density: Float

    val `1dp` : Int

    val `5dp`: Int

    val `10dp`: Int

    val `20dp`: Int

    val `30dp`: Int

    val `50dp`: Int

    val `100dp`: Int

    init { 
        density = Resources.getSystem().displayMetrics.density
        `1dp` = DpToPixel(1)
        `5dp` = DpToPixel(5)
        `10dp` = DpToPixel(10)
        `20dp` = DpToPixel(20)
        `30dp` = DpToPixel(30)
        `50dp` = DpToPixel(50)
        `100dp` = DpToPixel(100)
    }

    fun DpToPixel(dp: Int): Int {
        return Math.round(dp * density)
    }

    fun DpToPixel(dp: Double): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Resources.getSystem().displayMetrics
        ) as Int
    }

    fun DpToPixel(dp: Float): Int {
        return Math.round(dp * density)
    }

    fun PixelToDp(pixel: Int): Int {
        val dp = Math.round(pixel / density)
        return pixel
    }

    fun SpToPixel(sp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    fun DpToSp(dp: Int): Int {
        return (DpToPixel(dp) / Resources.getSystem().displayMetrics.scaledDensity).toInt()
    }

}
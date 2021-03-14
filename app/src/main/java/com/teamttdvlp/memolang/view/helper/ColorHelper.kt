package com.teamttdvlp.memolang.view.helper

import android.graphics.Color

/**
 * This function return the value of Color that is between in range of minColor(originalColor) and maxColor (targetColor)
 * with #coverLevel 0.0 is orginalColor and 1.0 is maxColor
 */

fun getMixedColor (minColor : Int, maxColor : Int, coverLevel : Float) : Int {
    val ogR = Color.red(minColor)
    val ogG = Color.green(minColor)
    val ogB = Color.blue(minColor)

    val targetR = Color.red(maxColor)
    val targetG = Color.green(maxColor)
    val targetB = Color.blue(maxColor)

    val offsetR = targetR - ogR
    val offsetG = targetG - ogG
    val offsetB = targetB - ogB

    val resultR : Int= (ogR + offsetR * coverLevel).toInt()
    val resultG : Int= (ogG + offsetG * coverLevel).toInt()
    val resultB : Int= (ogB + offsetB * coverLevel).toInt()

    return Color.rgb(resultR, resultG, resultB)
}

fun getMixedColor (minColorCode : String, maxColorCode : String, coverLevel : Float) : Int {
    val minColor = Color.parseColor(minColorCode)
    val maxColor = Color.parseColor(maxColorCode)

    val ogR = Color.red(minColor)
    val ogG = Color.green(minColor)
    val ogB = Color.blue(minColor)

    val targetR = Color.red(maxColor)
    val targetG = Color.green(maxColor)
    val targetB = Color.blue(maxColor)

    val offsetR = targetR - ogR
    val offsetG = targetG - ogG
    val offsetB = targetB - ogB

    val resultR : Int= (ogR + offsetR * coverLevel).toInt()
    val resultG : Int= (ogG + offsetG * coverLevel).toInt()
    val resultB : Int= (ogB + offsetB * coverLevel).toInt()

    return Color.rgb(resultR, resultG, resultB)
}


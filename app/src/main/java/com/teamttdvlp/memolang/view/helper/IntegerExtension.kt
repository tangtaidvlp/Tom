package com.teamttdvlp.memolang.view.helper

fun Int.dp() : Int {
    return UnitConverter.DpToPixel(this)
}

fun Int.percent () : Int {
    return this / 100
}
package com.teamttdvlp.memolang.view.helper

import java.lang.IndexOutOfBoundsException

fun String.capitalizeFirstLetter() : String {
    return if (this.isNotEmpty() and (this.length > 1)) {
        this[0].toUpperCase() + this.substring(1)
    } else {
        this
    }
}

fun String.decapitalizeFirstLetter() : String {
    return if (this.isNotEmpty() and (this.length > 1)) {
        this[0].toLowerCase() + this.substring(1)
    } else {
        this
    }
}

fun String.removeLastChar () : String {
    return if (this.isNotEmpty()) {
        this.removeRange(length - 1, length)
    } else {
        this
    }
}

fun String.replaceAt(pos : Int, newString : String) : String {
    if ((pos > length - 1) or (pos < 0)) throw IndexOutOfBoundsException()
    var result = this
    val isAnsLastCharPos = (pos == length - 1)
    if (isAnsLastCharPos) {
        result = result.removeLastChar() + newString
    } else {
        val head = result.substring(0, pos)
        val tail = result.substring(pos + 1)
        result = "$head$newString$tail"
    }
    return result
}

fun String.clearAll (text : String) : String{
    return replace(text, "")
}


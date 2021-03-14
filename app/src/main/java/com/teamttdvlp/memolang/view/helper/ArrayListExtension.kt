package com.teamttdvlp.memolang.view.helper

import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

fun <T> ArrayList<T>.notContains (item : T?) : Boolean {
    return !this.contains(item)
}

fun <E> ArrayList<E>.getRandom () : E {
    if (size == 0) {
        throw Exception ("Empty list exception")
    }

    val randomIndex = Random().nextInt(size)
    return get(randomIndex)
}

fun <E> ArrayList<E>.foreachFromSecondElement (action: (E) -> Unit) {

    if (size <= 1) {
        return
    }

    for (index in 1..size - 1) {
        action.invoke(get(index))
    }

}
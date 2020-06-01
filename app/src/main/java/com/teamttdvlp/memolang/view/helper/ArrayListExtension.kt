package com.teamttdvlp.memolang.view.helper

fun <T> ArrayList<T>.notContains (item : T) : Boolean {
    return !this.contains(item)
}

fun <E> ArrayList<E>.foreachFromSecondElement (action: (E) -> Unit) {

    if (size <= 1) {
        return
    }

    for (index in 1..size - 1) {
        action.invoke(get(index))
    }

}
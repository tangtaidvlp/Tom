package com.teamttdvlp.memolang.view.helper

fun <T> ArrayList<T>.notContains (item : T) : Boolean {
    return !this.contains(item)
}
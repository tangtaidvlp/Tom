package com.teamttdvlp.memolang.view.helper

import androidx.lifecycle.MutableLiveData

fun MutableLiveData<Int>.selfPlusOne() {
    if (value == null) return
    if (value is Int) {
        value = (value as Int) + 1
    } else throw Exception("Data type must be Integer. Your is " + value!!::class.java.name)
}

fun MutableLiveData<Int>.selfMinusOne() {
    if (value is Int) {
        value = (value as Int) - 1
    } else throw Exception("Data type must be Integer. Your is " + value!!::class.java.name)
}
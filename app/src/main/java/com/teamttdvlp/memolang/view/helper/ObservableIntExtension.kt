package com.teamttdvlp.memolang.view.helper

import androidx.databinding.ObservableInt

fun ObservableInt.selfPlusOne () {
    set(get() + 1)
}

fun ObservableInt.selfMinusOne () {
    set(get() - 1)
}
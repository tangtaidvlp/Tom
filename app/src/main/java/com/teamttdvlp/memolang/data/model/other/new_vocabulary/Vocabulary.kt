package com.example.dictionary.model

import com.teamttdvlp.memolang.data.model.other.new_vocabulary.Using

data class Vocabulary (
    var text : String = "",
    var pronunciation : String = "//") {
    var usings: ArrayList<Using> = ArrayList()

}
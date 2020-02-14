package com.teamttdvlp.memolang.model.entity.vocabulary

data class Vocabulary (
    var text : String = "",
    var pronunciation : String = "",
    var usings: Array<Using>? = null)
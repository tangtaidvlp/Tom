package com.teamttdvlp.memolang.model.entity.flashcard

import java.io.Serializable
import java.util.*

data class Flashcard (var id : Int = 0,
                      var text : String,
                      var translation : String,
                      var languagePair : String,
                      var setName : String,
                      var example : String,
                      var exampleMean : String,
                      var synonym : String,
                      var type : String,
                      var pronunciation : String,
                      var createdAt : Date = Date(Calendar.getInstance().timeInMillis)) : Serializable {

    init {
        text = text.trim()
    }


}
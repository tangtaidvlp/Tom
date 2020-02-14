package com.teamttdvlp.memolang.model.entity.flashcard

import java.io.Serializable
import java.util.*

data class Flashcard (var id : Int = 0,
                      var text : String = "",
                      var translation : String = "",
                      var languagePair : String = "",
                      var setName : String = "",
                      var using : String = "",
                      var usingTranslation : String = "",
                      var synonym : String = "",
                      var type : String = "",
                      var pronunciation : String = "[]",
                      var createdAt : Date = Date(Calendar.getInstance().timeInMillis)) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (other is Flashcard)
            return ((other as Flashcard)!!.id == id)
        else return super.equals(other)
    }

}
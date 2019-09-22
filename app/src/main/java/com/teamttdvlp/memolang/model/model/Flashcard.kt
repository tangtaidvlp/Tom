package com.teamttdvlp.memolang.model.model

import java.io.Serializable
import java.util.*

data class Flashcard (var id : Int = 0,
                      var toBeTranslatedWord : String = "",
                      var translatedWord : String = "",
                      var  type : String = "",
                      var  using : String = "",
                      var synonym : String = "",
                      var kind : String = "",
                      var spelling : String = "",
                      var createdAt : Date = Date(Calendar.getInstance().timeInMillis)) : Serializable
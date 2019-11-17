package com.teamttdvlp.memolang.model.model

import java.io.Serializable
import java.util.*

data class Flashcard (var id : Int = 0,
                      var text : String = "",
                      var translation : String = "",
                      var  type : String = "",
                      var  using : String = "",
                      var synonym : String = "",
                      var kind : String = "",
                      var spelling : String = "",
                      var createdAt : Date = Date(Calendar.getInstance().timeInMillis)) : Serializable
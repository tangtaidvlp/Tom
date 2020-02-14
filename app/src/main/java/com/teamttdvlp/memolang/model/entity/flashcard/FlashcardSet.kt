package com.teamttdvlp.memolang.model.entity.flashcard

import java.io.Serializable

class FlashcardSet(

    var name : String = "") : Serializable {

    var flashcards: ArrayList<Flashcard> = ArrayList()

}
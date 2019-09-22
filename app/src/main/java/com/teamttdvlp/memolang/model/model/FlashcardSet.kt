package com.teamttdvlp.memolang.model.model

import java.io.Serializable

class FlashcardSet(var id : String = "") : Serializable{
    var flashcards: ArrayList<Flashcard> = ArrayList()
}
package com.teamttdvlp.memolang.view.Activity.mockmodel

data class FlashcardSet (var sourceLang : String = "", var targetLang: String =  "", var flashcards: ArrayList<Flashcard> = ArrayList()) {
    var id = "$sourceLang-$targetLang"
}
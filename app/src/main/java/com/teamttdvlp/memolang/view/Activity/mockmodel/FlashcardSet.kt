package com.teamttdvlp.memolang.view.Activity.mockmodel

import com.teamttdvlp.memolang.model.sqlite.entity.MemoCardEntity

data class FlashcardSet (
    var sourceLang : String = "",
    var targetLang: String =  "",
    var flashcards: ArrayList<MemoCard> = ArrayList()) {
    var id = "$sourceLang-$targetLang"
}
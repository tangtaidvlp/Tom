package com.teamttdvlp.memolang.data.model.entity.flashcard

import androidx.room.Relation
import androidx.room.Embedded

data class FlashcardSetWithCardList (
    @Embedded
    val flashcardSet : FlashcardSet,

    @Relation(
        parentColumn = "setName",
        entityColumn = "setOwner")
    val flashcardList: List<Flashcard>
) {

    fun toNormalFlashcardSet () : FlashcardSet {
        flashcardSet.flashcards = flashcardList as ArrayList<Flashcard>
        return flashcardSet
    }
}
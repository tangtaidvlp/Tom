package com.teamttdvlp.memolang.data.model.entity.flashcard

import androidx.room.Embedded
import androidx.room.Relation

data class  FlashcardSetWithCardList(
    @Embedded
    val deck: Deck,

    @Relation(
        parentColumn = "setName",
        entityColumn = "setOwner"
    )
    val flashcardList: List<Flashcard>
) {

    fun toNormalFlashcardSet(): Deck {
        deck.flashcards = flashcardList as ArrayList<Flashcard>
        return deck
    }
}
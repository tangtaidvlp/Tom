package com.teamttdvlp.memolang.model

import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.repository.FlashcardRepos
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import javax.inject.Inject

class AddFlashcardExecutor @Inject constructor (

    var flashcardRepos : FlashcardRepos,

    var flashcardSetRepos: FlashcardSetRepos) {

    fun addFlashcardAndUpdateFlashcardSet (newCard: Flashcard, onInsertListener : (isSuccess : Boolean, cardId : Long, ex : Exception?) -> Unit) {
        val flashcardSet = Deck(newCard.setOwner, newCard.frontLanguage, newCard.backLanguage)
        if (newCard.setOwner.isEmpty()) {
            newCard.setOwner = flashcardSet.name
        }
        addFlashcardSetToOfflineDB(flashcardSet)
        addFlashcardToOfflineDB(newCard, onInsertListener)
    }

    private fun addFlashcardToOfflineDB(
        newCard: Flashcard,
        onInsertListener: (isSuccess: Boolean, cardId: Long, ex: Exception?) -> Unit
    ) {
        flashcardRepos.insertFlashcard(newCard, onInsertListener)
    }

    private fun addFlashcardSetToOfflineDB(deck: Deck) {
        flashcardSetRepos.insert(deck)
    }
}
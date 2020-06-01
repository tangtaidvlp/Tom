package com.teamttdvlp.memolang.model

import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.model.repository.FlashcardRepos
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.view.helper.quickLog
import javax.inject.Inject

class AddFlashcardExecutor @Inject constructor (

    var flashcardRepos : FlashcardRepos,

    var flashcardSetRepos: FlashcardSetRepos) {

    fun addFlashcardAndUpdateFlashcardSet (newCard: Flashcard, onInsertListener : (isSuccess : Boolean, cardId : Long, ex : Exception?) -> Unit) {
        val flashcardSet = FlashcardSet(newCard.setOwner, newCard.frontLanguage, newCard.backLanguage)
        if (newCard.setOwner.isEmpty()) {
            newCard.setOwner = flashcardSet.name
        }
        addFlashcardSetToOfflineDB(flashcardSet)
        addFlashcardToOfflineDB (newCard, onInsertListener)
    }

    private fun addFlashcardToOfflineDB (newCard : Flashcard, onInsertListener : (isSuccess : Boolean, cardId : Long, ex : Exception?) -> Unit) {
        flashcardRepos.insertFlashcard(newCard, onInsertListener)
    }

    private fun addFlashcardSetToOfflineDB (flashcardSet: FlashcardSet) {
        flashcardSetRepos.insert(flashcardSet)
    }
}
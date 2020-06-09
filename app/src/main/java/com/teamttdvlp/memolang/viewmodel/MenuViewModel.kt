package com.teamttdvlp.memolang.viewmodel

import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.model.repository.FlashcardRepos
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.view.activity.iview.MenuView
import com.teamttdvlp.memolang.view.base.BaseViewModel

class MenuActivityViewModel(
    private var flashcardSetRepos: FlashcardSetRepos,
    private var flashcardRepos: FlashcardRepos
) : BaseViewModel<MenuView>() {

    fun getAllFlashcardSets(onGetSuccess: (ArrayList<FlashcardSet>?) -> Unit) {
        flashcardSetRepos.getAllFlashcardWithCardList(onGetSuccess)
    }

    fun removeUserFlashcardSet(setName: FlashcardSet) {
        flashcardSetRepos.deleteFlashcardSet(setName)
    }

    fun updateSetName(OLDName_FlashcardSet: FlashcardSet, newSetName: String) {
        val NEWName_flashcardSet = FlashcardSet(
            newSetName,
            OLDName_FlashcardSet.frontLanguage,
            OLDName_FlashcardSet.backLanguage
        )
        val newFlashcardSet = ArrayList<Flashcard>()
        for (flashcard in OLDName_FlashcardSet.flashcards) {
            val newFlashcard = flashcard.copy()
            newFlashcard.setOwner = newSetName
            newFlashcardSet.add(newFlashcard)
        }

        NEWName_flashcardSet.flashcards = newFlashcardSet
        flashcardSetRepos.insert(NEWName_flashcardSet)
        flashcardRepos.insertFlashcards(newFlashcardSet) { isSuccess: Boolean, ex: Exception? ->
            if (isSuccess.not()) {
                throw java.lang.Exception("CACCCC")
            }
        }
    }

    fun deleteFlashcard(flashcardSet: FlashcardSet) {
        flashcardSetRepos.deleteFlashcardSet(flashcardSet)
        flashcardRepos.deleteCards(flashcardSet.flashcards)
    }

}
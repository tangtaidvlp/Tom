package com.teamttdvlp.memolang.viewmodel

import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardDoneView
import com.teamttdvlp.memolang.view.base.BaseViewModel

class UseFlashcardDoneViewModel : BaseViewModel<UseFlashcardDoneView>() {

    private lateinit var missedCardsList: ArrayList<Flashcard>

    private lateinit var fullFlashcardSet: FlashcardSet

    fun setData(fullFlashcardSet: FlashcardSet, missedCardsList: java.util.ArrayList<Flashcard>) {
        this.fullFlashcardSet = fullFlashcardSet
        this.missedCardsList = missedCardsList
    }

    fun getFullCardListSize(): Int {
        return fullFlashcardSet.flashcards.size
    }

    fun getMissedCardsListSize(): Int {
        return missedCardsList.size
    }

    fun getPassedCardsCount(): Int {
        return getFullCardListSize() - getMissedCardsListSize()
    }

    fun getMissCardRatio(): Float {
        return missedCardsList.size.toFloat() / fullFlashcardSet.flashcards.size.toFloat()
    }

    fun getPassCardRatio(): Float {
        return 1f - getMissCardRatio()
    }

    fun userGetMaxScore(): Boolean {
        return missedCardsList.size == 0
    }

    fun getFlashcardSetWithMissedCardList(): FlashcardSet {
        val cloneFlashcardSet = FlashcardSet(
            fullFlashcardSet.name,
            fullFlashcardSet.frontLanguage,
            fullFlashcardSet.backLanguage
        )

        cloneFlashcardSet.flashcards = missedCardsList

        return cloneFlashcardSet
    }

    fun getFlashcardSet(): FlashcardSet {
        return fullFlashcardSet
    }


}
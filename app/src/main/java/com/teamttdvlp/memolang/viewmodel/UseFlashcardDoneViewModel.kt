package com.teamttdvlp.memolang.viewmodel

import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardDoneView
import com.teamttdvlp.memolang.view.base.BaseViewModel

class UseFlashcardDoneViewModel : BaseViewModel<UseFlashcardDoneView>() {

    private lateinit var missedCardsList: ArrayList<Flashcard>

    private lateinit var fullDeck: Deck

    fun setData(fullDeck: Deck, missedCardsList: java.util.ArrayList<Flashcard>) {
        this.fullDeck = fullDeck
        this.missedCardsList = missedCardsList
    }

    fun getFullCardListSize(): Int {
        return fullDeck.flashcards.size
    }

    fun getMissedCardsListSize(): Int {
        return missedCardsList.size
    }

    fun getPassedCardsCount(): Int {
        return getFullCardListSize() - getMissedCardsListSize()
    }

    fun getMissCardRatio(): Float {
        return missedCardsList.size.toFloat() / fullDeck.flashcards.size.toFloat()
    }

    fun getPassCardRatio(): Float {
        return 1f - getMissCardRatio()
    }

    fun userGetMaxScore(): Boolean {
        return missedCardsList.size == 0
    }

    fun getFlashcardSetWithMissedCardList(): Deck {
        val cloneFlashcardSet = Deck(
            fullDeck.name,
            fullDeck.frontLanguage,
            fullDeck.backLanguage
        )

        cloneFlashcardSet.flashcards = missedCardsList

        return cloneFlashcardSet
    }

    fun getFlashcardSet(): Deck {
        return fullDeck
    }


}
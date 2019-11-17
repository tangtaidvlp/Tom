package com.teamttdvlp.memolang.viewmodel.use_flashcard_activity

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.model.activity.CardListManager
import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.selfMinusOne
import com.teamttdvlp.memolang.view.helper.selfPlusOne

class UseFlashcardActivityViewModel : BaseViewModel<UseFlashcardView>() {

    val currentCard = ObservableField<Flashcard>()

    var cardLeftCount = ObservableInt()

    var fogottenCardsCount = ObservableInt()

    var currentCardOrder = ObservableInt()


    private val hardCardList = ArrayList<Flashcard>()

    private val cardListManager = CardListManager()


    fun setData (cardList : ArrayList<Flashcard>) {
        cardListManager.setData(cardList)
    }


    fun beginUsing() {
        val firstCard = cardListManager.getFirstOne()
        currentCard.set(firstCard)
        currentCardOrder.set(1)
        cardLeftCount.set(cardListManager.getSize())
    }


    fun moveToNextCard () {
        if (thereIsCardLefts()) {
            val nextCard = cardListManager.focusOnNextCardAndGetIt()
            updateCurrentCard(nextCard)
            updateCardOrder()
        } else {
            view.onNoCardsLeft()
        }
    }


    fun moveToPreviousCard () {
        if (hasPrevious()) {
            val thePreviousCard = cardListManager.focusOnPrevCardAndGetIt()
            updateCurrentCard(thePreviousCard)
            cardLeftCount.selfPlusOne()
            updateCardOrder()

        }
    }


    fun thereIsCardLefts () : Boolean {
        return cardListManager.hasNext()
    }


    fun checkIfThereIsPreviousCard () {
        if (hasPrevious()) {
            view.showPreviousCardButton()
        } else {
            view.hidePreviousCardButton()
        }
    }

    fun updateCurrentCard (card : Flashcard) {
        currentCard.set(card)
    }


    fun hasNext () : Boolean {
        return cardListManager.hasNext()
    }

    fun hasPrevious () : Boolean {
        return cardListManager.hasPrevious()
    }


    fun handleEasyCard () {
        cardLeftCount.selfMinusOne()
    }

    fun handleHardCard () {
        if (!hardCardList.contains(cardListManager.getCurrentCard())) {
            fogottenCardsCount.selfPlusOne()
            hardCardList.add(cardListManager.getCurrentCard())
        }
        cardListManager.handleHardCard()
    }

    fun getFoggotenCardList () : ArrayList<Flashcard> {
        return hardCardList
    }

    fun updateCardOrder () {
        currentCardOrder.set(cardListManager.currentIndex + 1)
    }
}

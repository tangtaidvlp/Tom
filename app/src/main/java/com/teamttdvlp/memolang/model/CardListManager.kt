package com.teamttdvlp.memolang.model

import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard

class CardListManager {

    var cardList : ArrayList<Flashcard> = ArrayList()

    var currentIndex : Int = 0

    fun getSize () : Int {
        return cardList.size
    }

    fun hasNext () : Boolean {
        return currentIndex + 1 < cardList.size
    }

    fun hasPrevious () : Boolean {
        return currentIndex > 0
    }

    fun addData (newData : ArrayList<Flashcard>) {
        cardList.addAll(newData)
    }

    fun addNewCard (newCard : Flashcard) {
        cardList.add(newCard)
    }

    fun setData (newCardList : ArrayList<Flashcard>) {
        cardList.clear()
        cardList.addAll(newCardList)
        randomData()
    }

    private fun randomData () {
        cardList.shuffle()
    }

    fun getFirstOne () : Flashcard {
        return cardList[0]
    }

    fun focusOnNextCardAndGetIt() : Flashcard {
        currentIndex++
        return cardList.get(currentIndex)
    }

    fun focusOnPrevCardAndGetIt() : Flashcard {
        currentIndex--
        return cardList.get(currentIndex)
    }

    fun getCurrentCard () : Flashcard {
        return cardList.get(currentIndex)
    }

    fun handleHardCard () {
        val item = cardList.get(currentIndex)
        cardList.remove(item)
        cardList.add(item)
        currentIndex--
    }
}
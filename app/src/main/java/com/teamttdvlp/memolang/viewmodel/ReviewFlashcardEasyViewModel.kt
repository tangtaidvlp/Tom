package com.teamttdvlp.memolang.viewmodel

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.model.canUseUsingForTestSubject
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardEasyView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.selfPlusOne

class ReviewFlashcardEasyViewModel : BaseViewModel<ReviewFlashcardEasyView>() {

    val currentPos = ObservableInt()

    val setName = ObservableField<String>()

    val cardLeftCount = ObservableInt()

    private lateinit var currentCard: Flashcard

    private lateinit var cardList: ArrayList<Flashcard>

    fun setUp (cardList : ArrayList<Flashcard>) {
        this.cardList = cardList
        currentCard = cardList.first()
        currentPos.set(0)
    }

    fun checkAnswer (userAnswer : String) {
        val cardAnswer = currentCard.text
        val answerIsComplete = (userAnswer.length == cardAnswer.length)
        if (answerIsComplete) {
            if (userAnswer == cardAnswer) {
                view.performPassAnims()
            } else {
                view.performIncorrectAnsElemtsOrderAnims()
            }
        } else {
            if (cardAnswer.startsWith(userAnswer)) {
                view.performCorrectAnsElemtsOrderAnims()
            } else {
                view.performIncorrectAnsElemtsOrderAnims()
            }
        }
    }

    fun nextCard () {
        currentPos.selfPlusOne()
        val thereIsNoCardLeft = currentPos.get() >= cardList.size
        if (thereIsNoCardLeft) {
            view.endReviewing()
        } else {
            currentCard = cardList[currentPos.get()]
            cardLeftCount.set(cardList.size - currentPos.get())
            useCard(currentCard)
        }
    }

    fun useCard (card : Flashcard) {
        view.showTestSubjectOnScreen(
            card,
            ansElements = convertAnswerToElements(card.text),
            useUsingForTestSubject = canUseUsingForTestSubject(card))
    }

    fun convertAnswerToElements (input : String) : Array<String> {
        var answer = input.trim()
        val result : Array<String>
        if (answer.contains(" ")) {
            while (answer.contains("  ")) {
                answer = answer.replace("  ", " ")
            }
            result = answer.split(" ").toTypedArray()
        } else {
            result = Array<String>(answer.length) { index ->
                return@Array answer[index].toString()
            }
        }

        return result
    }
}
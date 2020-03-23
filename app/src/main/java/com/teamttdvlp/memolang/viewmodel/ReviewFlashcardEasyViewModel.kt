package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.model.CardListRandomer
import com.teamttdvlp.memolang.model.TextSpeaker
import com.teamttdvlp.memolang.model.canUseUsingForTestSubject
import com.teamttdvlp.memolang.model.entity.Language.Companion.LANG_DIVIDER
import com.teamttdvlp.memolang.model.entity.Language.Companion.TARGET_LANGUAGE
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardEasyView
import com.teamttdvlp.memolang.view.base.BaseAndroidViewModel
import com.teamttdvlp.memolang.view.helper.notContains
import com.teamttdvlp.memolang.view.helper.selfPlusOne

class ReviewFlashcardEasyViewModel(var app : Application) : BaseAndroidViewModel<ReviewFlashcardEasyView>(app) {

    val currentPos = ObservableInt()

    val setName = ObservableField<String>()

    val cardLeftCount = ObservableInt()

    private lateinit var currentCard: Flashcard

    private lateinit var cardList: ArrayList<Flashcard>

    private lateinit var textSpeaker : TextSpeaker

    private var cardListRandomer : CardListRandomer = CardListRandomer()

    private var forgottenCardList = ArrayList<Flashcard>()

    fun setUp (cardList : ArrayList<Flashcard>) {
        this.cardList = cardListRandomer.random(cardList)
        currentCard = this.cardList.first()
        currentPos.set(0)
        useCard(currentCard)
        setName.set(currentCard.setName)
        cardLeftCount.set(this.cardList.size)
        textSpeaker = TextSpeaker(app, currentCard.languagePair.split(LANG_DIVIDER).get(TARGET_LANGUAGE))
    }

    fun speakAnswer (text : String, onEnd : () -> Unit) {
        textSpeaker.setOnSpeakTextDoneListener {
            onEnd()
        }
        textSpeaker.speak(text)
    }

    fun checkAnswer (userAnswer : String) {
        val cardAnswer = currentCard.text
        val answerIsComplete = (userAnswer.length == cardAnswer.length)
        if (answerIsComplete) {
            if (userAnswer == cardAnswer) {
                view.performPassBehaviours()
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

    fun processForgottenCard () {
        addCurrentCardTo_ListOfForgottenCards()
        moveCurrentCardToEndOfCardList()
    }

    private fun addCurrentCardTo_ListOfForgottenCards () {
        if (forgottenCardList.notContains(currentCard)) {
            forgottenCardList.add(currentCard)
        }
    }

    private fun moveCurrentCardToEndOfCardList () {
        cardList.add(currentCard)
    }

    fun nextCard () {
        currentPos.selfPlusOne()
        currentCard = cardList[currentPos.get()]
        cardLeftCount.set(cardList.size - currentPos.get())
        useCard(currentCard)
    }

    fun checkIsThereCardLeft () : Boolean {
        val thereIsCardLeft = currentPos.get() + 1 <= cardList.size - 1
        return thereIsCardLeft
    }

    fun useCard (card : Flashcard) {
        val listType = if (card.text.trim().contains(" "))
                                        ReviewFlashcardEasyView.WORD_LIST
                                else
                                        ReviewFlashcardEasyView.CHARACTER_LIST

        val answerElement = convertAnswerToElements(card.text)
        val completeAnswerElement = getShuffledElementsAnswer(answerElement)
        view.onGetTestSubject(
            testSubject = card,
            ansElements = completeAnswerElement,
            useUsingForTestSubject = canUseUsingForTestSubject(card),
            listType = listType
            )
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

    fun getShuffledElementsAnswer (answerElements : Array<String>) : Array<String> {
        val ansElementsHolder = answerElements

        fun invertPosition (pos1 : Int, pos2 : Int) {
            val valueHolder = ansElementsHolder[pos1]
            ansElementsHolder[pos1] = ansElementsHolder[pos2]
            ansElementsHolder[pos2] = valueHolder
        }

        val threeTimes = (1..3)
        val random = java.util.Random()
        threeTimes.forEach {
            for (pos in 0..ansElementsHolder.size - 1) {
                val randomPos = random.nextInt(ansElementsHolder.size)
                invertPosition(pos, randomPos)
            }
        }

        return ansElementsHolder
    }

    fun getFoggotenCardList(): ArrayList<Flashcard> {
        return forgottenCardList
    }
}
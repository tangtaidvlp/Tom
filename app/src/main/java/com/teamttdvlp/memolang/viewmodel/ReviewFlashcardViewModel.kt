package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.model.TextSpeaker
import com.teamttdvlp.memolang.model.canUseUsingForTestSubject
import com.teamttdvlp.memolang.model.entity.Language
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardView
import com.teamttdvlp.memolang.view.base.BaseAndroidViewModel
import com.teamttdvlp.memolang.view.helper.*
import kotlin.random.Random

class ReviewFlashcardViewModel(var app : Application) : BaseAndroidViewModel<ReviewFlashcardView>(app) {

    val currentPos = ObservableInt()

    private lateinit var currentCard : Flashcard

    private lateinit var cardList : ArrayList<Flashcard>

    private var answerWrongTimes = 0

    private val MAX_ANSWER_WRONG_TIMES = 3

    val forgottenCardList = ArrayList<Flashcard>()

    val setName = ObservableField<String>()

    val cardLeftCount = ObservableInt ()

    val hint = ObservableField<String>()

    var answerLength = 0

    private lateinit var answerTextSpeaker : TextSpeaker

    fun setUpInfo (cardList : ArrayList<Flashcard>) {
        this.cardList = cardList
        currentPos.set(0)
        currentCard = cardList.first()
        val sourceLang = currentCard.languagePair.split(Language.LANG_DIVIDER).get(Language.SOURCE_LANGUAGE)
        val targetLang = currentCard.languagePair.split(Language.LANG_DIVIDER).get(Language.TARGET_LANGUAGE)
        setName.set(currentCard.setName)
        cardLeftCount.set(cardList.size)
        answerTextSpeaker = TextSpeaker(app, sourceLang.trim())
        useCard(currentCard)

    }

    fun nextCard () {
        currentPos.selfPlusOne()
        val currentPosVal = currentPos.get()
        val thereIsNoCardLeft = (currentPosVal >= cardList.size)
        if (thereIsNoCardLeft) {
            view.endReviewing()
        } else {
            currentCard = cardList.get(currentPosVal)
            useCard(currentCard)
            cardLeftCount.set(cardList.size - currentPosVal)
        }
        resetAnswerWrongTimes()
    }

    fun speakAnswer (text : String) {
        answerTextSpeaker.speak(text)
        if (answerTextSpeaker.error != null) {
            // We just want to show this error only once
            // because although there is error, but text speaker still work
            // I also don't know exactly how it work
            view.showSpeakTextError(answerTextSpeaker.error + "")
            answerTextSpeaker.error = null
        }
    }

    private fun useCard (card : Flashcard) {
        answerLength = card.text.length
        hint.set(convertToHint(card.text))
        view.showTestSubjectOnScreen(card, canUseUsingForTestSubject(card))
    }

    fun submitAnswer (answer : String) {
        val isCorrect = (answer.toLowerCase().trim() == currentCard.text.toLowerCase().trim())
        if (isCorrect) {
            if (answerWrongTimes == 0) {
                view.showExcelentAnswerAnimation()
            } else if ((answerWrongTimes > 0) and (answerWrongTimes < MAX_ANSWER_WRONG_TIMES)) {
                view.showGoodAnswerAnimation()
            }
        } else {
            answerWrongTimes++
            if (answerWrongTimes < MAX_ANSWER_WRONG_TIMES) {
                view.showWrongAnswerAnimation()
                if (answerWrongTimes == MAX_ANSWER_WRONG_TIMES - 1) {
                    view.highlightHintOption()
                }
            } else if (answerWrongTimes == MAX_ANSWER_WRONG_TIMES) {
                view.showNotPassAnswerAnimation()
            }
        }
    }

    fun resetAnswerWrongTimes () {
        answerWrongTimes = 0
    }

    fun convertToHint (answer : String) : String {

        if (answerLength == 1) {

            return currentCard.text

        } else {

            var result = answer
            val isOdd = (answerLength % 2 == 1)
            val hiddenCharCount = if (isOdd)
                (answerLength + 1) / 2
            else
                answerLength / 2

            val randomPosList = ArrayList<Int>()
            // Make sure it show the first char of answer
            while ((randomPosList.size < hiddenCharCount)) {
                val ranPos = Random.nextInt(answerLength)
                if (randomPosList.notContains(ranPos) and (ranPos != 0)) {
                    randomPosList.add(ranPos)
                }
            }

            for (pos in randomPosList) {
                if (result[pos].toString() != " ") {
                    result = result.replaceAt(pos, "_")
                }
            }

            return result.replace(" ", "   ")

        }
    }

    fun checkAnswer (answer : String) {
        val answerIsValid = (answer.trim().length >= answerLength)
        if (answerIsValid) {
            view.showValidAnsBehaviours()
        } else {
            view.showInvalidAnsBehaviours()
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

    fun getFoggotenCardList(): ArrayList<Flashcard> {
        return forgottenCardList
    }
}


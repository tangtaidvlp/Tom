package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.model.TextSpeaker
import com.teamttdvlp.memolang.model.checkCanUseExampleForTestSubject
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.model.CardListLanguageReverser.Companion.reverse_ListCard_TextAndTranslation
import com.teamttdvlp.memolang.model.ReviewActivitiesSpeakerStatusManager
import com.teamttdvlp.memolang.model.ReviewActivitiesSpeakerStatusManager.SpeakerStatus.Companion.SPEAK_ANSWER_ONLY
import com.teamttdvlp.memolang.model.ReviewActivitiesSpeakerStatusManager.SpeakerStatus.Companion.SPEAK_QUESTION_AND_ANSWER
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.*
import kotlin.random.Random

class ReviewFlashcardViewModel(var app : Application) : BaseViewModel<ReviewFlashcardView>() {

    val currentPos = ObservableInt()

    private lateinit var currentCard : Flashcard

    private var cardList : ArrayList<Flashcard> = ArrayList()

    private var answerWrongTimes = 0

    private val MAX_ANSWER_WRONG_TIMES = 3

    private val forgottenCardList = ArrayList<Flashcard>()

    val setName = ObservableField<String>()

    val cardLeftCount = ObservableInt ()

    val hint = ObservableField<String>()

    var answerLength = 0

    private lateinit var answerTextSpeaker : TextSpeaker

    private lateinit var questionTextSpeaker : TextSpeaker

    private lateinit var reviewFCActivity_StatusManager: ReviewActivitiesSpeakerStatusManager

    fun setUpInfo (flashcardSet : FlashcardSet, reverseLanguages : Boolean) {
        val questionLanguage : String
        val answerLanguage : String

        if (reverseLanguages) {
            questionLanguage = flashcardSet.frontLanguage
            answerLanguage = flashcardSet.backLanguage
            reverse_ListCard_TextAndTranslation(flashcardSet.flashcards)
        } else {
            answerLanguage = flashcardSet.frontLanguage
            questionLanguage = flashcardSet.backLanguage
        }

        this.cardList.clear()
        this.cardList.addAll(flashcardSet.flashcards)
        currentCard = flashcardSet.flashcards.first()

        currentPos.set(0)

        setName.set(currentCard.setOwner)
        cardLeftCount.set(flashcardSet.flashcards.size)

        reviewFCActivity_StatusManager = ReviewActivitiesSpeakerStatusManager(app, flashcardSet.name, setNameFormat = { setName ->
            return@ReviewActivitiesSpeakerStatusManager "Review<$setName>"
        })

        val textSpokenFirst = if (doesTextNeedSpeakingAtStart()) {
            if (checkCanUseExampleForTestSubject(currentCard)) {
                this.cardList.first().meanOfExample
            } else {
                this.cardList.first().translation
            }
        } else ""

        answerTextSpeaker = TextSpeaker(app, answerLanguage.trim())
        questionTextSpeaker = TextSpeaker(app, questionLanguage.trim(), textSpokenFirst)
        useCard(currentCard)
    }

    fun nextCard () {
        currentPos.selfPlusOne()
        val currentPosVal = currentPos.get()
        currentCard = cardList.get(currentPosVal)
        useCard(currentCard)
        cardLeftCount.set(cardList.size - currentPosVal)

        resetAnswerWrongTimes()
    }

    fun checkThereIs_NO_CardLeft () : Boolean {
        val nextPosition = currentPos.get() + 1
        return (nextPosition >= cardList.size)
    }

    fun speakAnswer (text : String, onSpeakDone : () -> Unit) {
        answerTextSpeaker.setOnSpeakTextDoneListener(onSpeakDone)
        answerTextSpeaker.speak(text)
        if (answerTextSpeaker.error != null) {
            // We just want to show this error only once
            // because although there is error, but text speaker still work
            // I also don't know exactly how it work
            view.showSpeakTextError(answerTextSpeaker.error + "")
            answerTextSpeaker.error = null
        }
    }

    fun speakQuestion (text : String) {
        questionTextSpeaker.speak(text)
        if (questionTextSpeaker.error != null) {
            // We just want to show this error only once
            // because although there is error, but text speaker still work
            // I also don't know exactly how it work
            view.showSpeakTextError(questionTextSpeaker.error + "")
            questionTextSpeaker.error = null
        }
    }

    private fun useCard (card : Flashcard) {
        answerLength = card.text.length
        hint.set(convertToHint(card.text))
        view.showTestSubjectOnScreen(card, checkCanUseExampleForTestSubject(card))
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

    fun getForgottenCardList(): ArrayList<Flashcard> {
        return forgottenCardList
    }

    fun getSpeakerStatus(): Boolean {
        return reviewFCActivity_StatusManager.speakerStatusManager.getStatus()
    }

    fun saveAllStatus (speakerFunction : Int, speakerStatus : Boolean) {
        if (::reviewFCActivity_StatusManager.isInitialized) {
            reviewFCActivity_StatusManager.speakerStatusManager.apply {
                saveFunction(speakerFunction)
                saveStatus(speakerStatus)
            }
        } else {
            quickLog("ReviewFlashcardViewModel.kt:: reviewFCActivity_StatusManager is not initialized")
        }
    }

    fun getSpeakerFunction () : Int {
        return reviewFCActivity_StatusManager.speakerStatusManager.getFunction()
    }

    private fun doesTextNeedSpeakingAtStart () : Boolean {
        val speakerFunc = reviewFCActivity_StatusManager.speakerStatusManager.getFunction()
        val speakerIsOn = reviewFCActivity_StatusManager.speakerStatusManager.getStatus()
        return ((speakerFunc == SPEAK_ANSWER_ONLY) or (speakerFunc == SPEAK_QUESTION_AND_ANSWER)) and speakerIsOn
    }
}


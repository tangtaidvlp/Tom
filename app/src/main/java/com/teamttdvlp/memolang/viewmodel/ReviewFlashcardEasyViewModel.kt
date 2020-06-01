package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.model.*
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardEasyView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.notContains
import com.teamttdvlp.memolang.view.helper.quickLog
import com.teamttdvlp.memolang.view.helper.selfPlusOne

class ReviewFlashcardEasyViewModel(var app : Application) : BaseViewModel<ReviewFlashcardEasyView>() {

    val currentPos = ObservableInt()

    val setName = ObservableField<String>()

    val cardLeftCount = ObservableInt()

    private lateinit var currentCard: Flashcard

    private var cardList: ArrayList<Flashcard> = ArrayList()

    private var cardListRandomer : CardListRandomer = CardListRandomer()

    private var forgottenCardList = ArrayList<Flashcard>()

    private lateinit var reviewFCEasyActivity_StatusManager : ReviewActivitiesSpeakerStatusManager

    private lateinit var answerTextSpeaker : TextSpeaker

    private lateinit var questionTextSpeaker : TextSpeaker

    private var reverseLanguages : Boolean = false

    fun setUp (flashcardSet : FlashcardSet, reverseLanguages : Boolean) {
        this.reverseLanguages = reverseLanguages

        // Show to UI by Databinding
        setName.set(flashcardSet.name)
        cardLeftCount.set(cardList.size)
        //

        reviewFCEasyActivity_StatusManager = ReviewActivitiesSpeakerStatusManager(app, "", setNameFormat = { setName ->
            return@ReviewActivitiesSpeakerStatusManager "Easy_Review<$setName>"
        })

        val questionLanguage : String
        val answerLanguage : String

        if (reverseLanguages) {
            questionLanguage = flashcardSet.frontLanguage
            answerLanguage = flashcardSet.backLanguage
            CardListLanguageReverser.reverse_ListCard_TextAndTranslation(flashcardSet.flashcards)
        } else {
            answerLanguage = flashcardSet.frontLanguage
            questionLanguage = flashcardSet.backLanguage
        }


        // These statements must be called after #CardListLanguageReverser.reverse_ListCard_TextAndTranslation(flashcardSet.flashcards)
        cardList.clear()
        cardList.addAll(cardListRandomer.random(flashcardSet.flashcards))
        currentCard = cardList.first()

        val textSpokenFirst = if (doesTextNeedSpeakingAtStart()) {
            if (checkCanUseExampleForTestSubject(currentCard)) {
                cardList.first().meanOfExample
            } else {
                cardList.first().translation
            }
        } else ""

        answerTextSpeaker = TextSpeaker(app, answerLanguage.trim())
        questionTextSpeaker = TextSpeaker(app, questionLanguage.trim(), textSpokenFirst)


        currentPos.set(0)
        useCard(currentCard)
    }

    fun useCard (card : Flashcard) {
        val cellOfListType = getCellOfListType(card)

        val answerElement = convertAnswerToElements(card.text, cellOfListType)
        val completeAnswerElement = getShuffledElementsAnswer(answerElement)

        val useExampleForTestSubject :  Boolean
        if (reverseLanguages) {
            // Reverse for #checkCanUseExampleForTestSubject(card)
            CardListLanguageReverser.reverse_Card_ExampleAndMeanExample(card)
            useExampleForTestSubject = checkCanUseExampleForTestSubject(card)
            if (useExampleForTestSubject.not()) {
                // Reverse again, get it back to normal because example can't be used
                CardListLanguageReverser.reverse_Card_ExampleAndMeanExample(card)
            }
        } else {
            useExampleForTestSubject = checkCanUseExampleForTestSubject(card)
        }

        view.onGetTestSubject(
            testSubject = card,
            ansElements = completeAnswerElement,
            useExampleForTestSubject = useExampleForTestSubject,
            listType = cellOfListType
        )
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



    private val SPECIFIED_CELL_AMOUNT = 15
    private fun getCellOfListType (card : Flashcard) : ReviewFlashcardEasyView.ListOfCellType {
        quickLog("jkaf: " + card.text)
        if (card.text.trim().contains(" ")) {
            val clearedAllSpaceText = card.text.replace(" ", "")
            if (clearedAllSpaceText.length > SPECIFIED_CELL_AMOUNT) {
                quickLog("Text: $clearedAllSpaceText and Length: ${clearedAllSpaceText.length}")
                return ReviewFlashcardEasyView.ListOfCellType.WORD_LIST
            } else { // Text is too short to devide it into words
                quickLog("Too short length: ${clearedAllSpaceText.length}")
                return ReviewFlashcardEasyView.ListOfCellType.CHARACTER_LIST
            }
        }
        else
                return ReviewFlashcardEasyView.ListOfCellType.CHARACTER_LIST
    }

    fun convertAnswerToElements (input : String, type : ReviewFlashcardEasyView.ListOfCellType) : Array<String> {
        var answer = input.trim()
        val result : Array<String>
        if (type == ReviewFlashcardEasyView.ListOfCellType.WORD_LIST) {
            while (answer.contains("  ")) {
                answer = answer.replace("  ", " ")
            }
            result = answer.split(" ").toTypedArray()
        } else { // ReviewFlashcardEasyView.ListOfCellType.CHARACTER_LIST
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

    fun getForgottenCardList(): ArrayList<Flashcard> {
        return forgottenCardList
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

    fun getSpeakerStatus(): Boolean {
        return reviewFCEasyActivity_StatusManager.speakerStatusManager.getStatus()
    }

    fun saveAllStatus (speakerFunction : Int, speakerStatus : Boolean) {

        if (::reviewFCEasyActivity_StatusManager.isInitialized) {
            reviewFCEasyActivity_StatusManager.speakerStatusManager.apply {
                saveFunction(speakerFunction)
                saveStatus(speakerStatus)
            }
        } else {
            quickLog("ReviewFlashcardEasyViewModel.kt:: reviewFCEasyActivity_StatusManager is not initialized")
        }
    }

    fun getSpeakerFunction () : Int {
        return reviewFCEasyActivity_StatusManager.speakerStatusManager.getFunction()
    }

    private fun doesTextNeedSpeakingAtStart () : Boolean {
        val speakerFunc = reviewFCEasyActivity_StatusManager.speakerStatusManager.getFunction()
        val speakerIsOn = reviewFCEasyActivity_StatusManager.speakerStatusManager.getStatus()
        return ((speakerFunc == ReviewActivitiesSpeakerStatusManager.SpeakerStatus.SPEAK_ANSWER_ONLY) or (speakerFunc == ReviewActivitiesSpeakerStatusManager.SpeakerStatus.SPEAK_QUESTION_AND_ANSWER)) and speakerIsOn
    }
}
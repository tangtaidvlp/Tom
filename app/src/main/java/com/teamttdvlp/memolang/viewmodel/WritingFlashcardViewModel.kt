package com.teamttdvlp.memolang.viewmodel

//import com.teamttdvlp.memolang.view.helper.selfPlusOne
import android.app.Application
import android.graphics.Bitmap
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.CardListLanguageReverser.Companion.reverse_LIST_Card_TextAndTranslation
import com.teamttdvlp.memolang.model.IllustrationLoader
import com.teamttdvlp.memolang.model.ReviewActivitiesSpeakerStatusManager
import com.teamttdvlp.memolang.model.ReviewActivitiesSpeakerStatusManager.SpeakerStatus.Companion.SPEAK_ANSWER_ONLY
import com.teamttdvlp.memolang.model.ReviewActivitiesSpeakerStatusManager.SpeakerStatus.Companion.SPEAK_QUESTION_AND_ANSWER
import com.teamttdvlp.memolang.model.TextSpeaker
import com.teamttdvlp.memolang.model.checkCanUseExampleForTestSubject
import com.teamttdvlp.memolang.view.activity.iview.WritingFlashcardView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.notContains
import com.teamttdvlp.memolang.view.helper.replaceAt
import com.teamttdvlp.memolang.view.helper.systemOutLogging
import com.teamttdvlp.memolang.viewmodel.abstraction.CardPlayableViewModel
import java.lang.Exception
import kotlin.random.Random

class WritingFlashcardViewModel (var app : Application, private val illustrationLoader : IllustrationLoader) : BaseViewModel<WritingFlashcardView>()
    , CardPlayableViewModel {


    private lateinit var currentCard: Flashcard

    private var cardList: ArrayList<Flashcard> = ArrayList()

    private lateinit var deck: Deck

    private var answerWrongTimes = 0

    private val MAX_ANSWER_WRONG_TIMES = 3

    private val missedCardList = ArrayList<Flashcard>()

    val setName = ObservableField<String>()

    val cardLeftCount = ObservableInt ()

    val hint = ObservableField<String>()

    var answerLength = 0

    private lateinit var answerTextSpeaker: TextSpeaker

    private lateinit var questionTextSpeaker: TextSpeaker

    private lateinit var reviewFCActivity_StatusManager: ReviewActivitiesSpeakerStatusManager

    /**
     * String: picture name
     * Bitmap?: illustrstion content
     */
    private val illustrationMap = HashMap<String, Bitmap?>()

    /**
     * String: picture name
     * Exception?: load illustration exception
     */
    private val load_illustrationExceptionMap = HashMap<String, Exception?>()


    var isDeckReversed = false

    private var passedCardCount = 0

    private var currentPos = 0

    private var missedCardCount = 0

    override fun setUpData(deck: Deck, reverseTextAndTranslation: Boolean) {
        this.deck = deck
        this.isDeckReversed = reverseTextAndTranslation

        loadAllCardIllustrations(deck.flashcards)
        view.onLoadDataStart()

        val questionLanguage: String
        val answerLanguage: String
        if (reverseTextAndTranslation) {
            questionLanguage = deck.frontLanguage
            answerLanguage = deck.backLanguage
            reverse_LIST_Card_TextAndTranslation(deck.flashcards)
        } else {
            answerLanguage = deck.frontLanguage
            questionLanguage = deck.backLanguage
        }

        this.cardList.clear()
        this.cardList.addAll(deck.flashcards)
        val firstCard = cardList.first()

        currentPos = 0

        setName.set(firstCard.setOwner)
        cardLeftCount.set(deck.flashcards.size)

        reviewFCActivity_StatusManager =
            ReviewActivitiesSpeakerStatusManager(app, deck.name, setNameFormat = { setName ->
                return@ReviewActivitiesSpeakerStatusManager "Review<$setName>"
            })

        val textSpokenFirst = if (doesTextNeedSpeakingAtStart()) {
            if (checkCanUseExampleForTestSubject(firstCard)) {
                this.cardList.first().meanOfExample
            } else {
                this.cardList.first().translation
            }
        } else ""

        answerTextSpeaker = TextSpeaker(app, answerLanguage.trim())
        questionTextSpeaker = TextSpeaker(app, questionLanguage.trim(), textSpokenFirst)
    }

    override fun beginUsing () {
        currentCard = deck.flashcards.first()
        useCard(currentCard)
    }

    override fun nextCard () {
        currentPos++
        currentCard = cardList.get(currentPos)
        useCard(currentCard)
        cardLeftCount.set(cardList.size - currentPos)

        resetAnswerWrongTimes()
    }

    override fun hasNext () : Boolean {
        val nextPosition = currentPos + 1
        return (nextPosition < cardList.size)
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

        val illustration : Bitmap?
        val loadIllustrationException : Exception?
        if (isDeckReversed) {
            illustration = illustrationMap.get(card.frontIllustrationPictureName)
            loadIllustrationException = load_illustrationExceptionMap.get(card.frontIllustrationPictureName)
        } else {
            illustration = illustrationMap.get(card.backIllustrationPictureName)
            loadIllustrationException = load_illustrationExceptionMap.get(card.backIllustrationPictureName)
        }

        view.onGetTestSubject(card, illustration, loadIllustrationException, checkCanUseExampleForTestSubject(card))
    }

    override fun loadAllCardIllustrations(flashcardList: ArrayList<Flashcard>) {
        val illustrationsNameList = ArrayList<String>()
        if (isDeckReversed.not()) {
            deck.flashcards.forEach { flashcard ->
                if (isCardValid_In_This_Mode(flashcard)) {
                    if (flashcard.backIllustrationPictureName != null) {
                        illustrationsNameList.add(flashcard.backIllustrationPictureName!!)
                    }
                }
            }
        } else {
            deck.flashcards.forEach { flashcard ->
                if (isCardValid_In_This_Mode(flashcard)) {
                    if (flashcard.frontIllustrationPictureName != null) {
                        illustrationsNameList.add(flashcard.frontIllustrationPictureName!!)
                    }
                }
            }
        }

        illustrationLoader.loadListOfBitmap(illustrationsNameList, onGetResult =  { dataMap, exMap ->
            this.illustrationMap.putAll(dataMap)
            beginUsing()
            view.onLoadDataFinish()
        })
    }

    private fun isCardValid_In_This_Mode (card : Flashcard) : Boolean {

        val notReverse_But_FrontCard_EmptyText = isDeckReversed.not() && card.text.isEmpty()
        val reverse_But_BackCard_TextEmpty = isDeckReversed && card.translation.isEmpty()

        if (notReverse_But_FrontCard_EmptyText) {
            return false
        } else if (reverse_But_BackCard_TextEmpty) {
            return false
        }

        return true
    }

    override fun submitAnswer (answer : String) : Boolean{
        val isCorrect = (answer.toLowerCase().trim() == currentCard.text.toLowerCase().trim())
        if (isCorrect) {
            handleUserRememberCard()
            return true
        } else {
            handleUserForgetCard()
            return false
        }
    }


    override fun handleUserRememberCard() {

        if (answerWrongTimes == 0) {
            view.showExcellentAnswerAnimation()
        } else if ((answerWrongTimes > 0) and (answerWrongTimes < MAX_ANSWER_WRONG_TIMES)) {
            view.showGoodAnswerAnimation()
        }
        processPassedCard()
        view.onPassACard(passedCardCount, missedCardCount)
    }

    override fun handleUserForgetCard() {
        answerWrongTimes++
        if (answerWrongTimes < MAX_ANSWER_WRONG_TIMES) {
            view.showWrongAnswerAnimation()
            if (answerWrongTimes == MAX_ANSWER_WRONG_TIMES - 1) {
                view.highlightHintOption()
            }
        } else if (answerWrongTimes == MAX_ANSWER_WRONG_TIMES) {
            view.showNotPassAnswerAnimation()
            processMissedCard()
            view.onPassACard(passedCardCount, missedCardCount)
        }
    }

    fun giveUpCard () {
        view.showNotPassAnswerAnimation()
        processMissedCard()
        view.onPassACard(passedCardCount, missedCardCount)
    }

    private fun resetAnswerWrongTimes () {
        answerWrongTimes = 0
    }

    private fun convertToHint (answer : String) : String {

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

    private fun processMissedCard() {
        if (missedCardList.notContains(currentCard)) {
            missedCardCount++
        }
        addCurrentCardTo_ListOfForgottenCards()
        moveCurrentCardToEndOfCardList()
     }

    private fun processPassedCard() {
        passedCardCount++
        if (missedCardList.contains(currentCard)) {
            missedCardCount--
        }
    }


    private fun addCurrentCardTo_ListOfForgottenCards() {
        if (missedCardList.notContains(currentCard)) {
            missedCardList.add(currentCard)
        }
    }

    private fun moveCurrentCardToEndOfCardList() {
        cardList.add(currentCard)
    }

    override fun getForgottenCardList(): ArrayList<Flashcard> {
        return missedCardList
    }

    fun getOriginalFlashcardSet(): Deck {
        if (isDeckReversed) {
            val cloneFlashcardSet = Deck(
                this.deck.name,
                this.deck.frontLanguage,
                this.deck.backLanguage
            )
            val cloneFlashcardList = ArrayList<Flashcard>()
            this.deck.flashcards.forEach { flashcard ->
                cloneFlashcardList.add(flashcard.copy())
            }
            reverse_LIST_Card_TextAndTranslation(cloneFlashcardList)
            cloneFlashcardSet.flashcards = cloneFlashcardList

            return cloneFlashcardSet
        } else {
            // Set is original, not reversed
            return deck
        }
    }

    fun getSpeakerStatus(): Boolean {
        return reviewFCActivity_StatusManager.speakerStatusManager.getStatus()
    }

    fun saveAllStatus(speakerFunction: Int, speakerStatus: Boolean) {
        if (::reviewFCActivity_StatusManager.isInitialized) {
            reviewFCActivity_StatusManager.speakerStatusManager.apply {
                saveFunction(speakerFunction)
                saveStatus(speakerStatus)
            }
        } else {
            systemOutLogging("ReviewFlashcardViewModel.kt:: reviewFCActivity_StatusManager is not initialized")
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

    override fun getDeckSize(): Int {
        return deck.flashcards.size
    }

    fun getCurrentAnswerHint(): String {
        return convertToHint(currentCard.text)
    }
}


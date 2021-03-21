package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.*
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardEasyView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.notContains
import com.teamttdvlp.memolang.view.helper.systemOutLogging
import com.teamttdvlp.memolang.viewmodel.abstraction.CardPlayableViewModel
import java.lang.Exception

//import com.teamttdvlp.memolang.view.helper.selfPlusOne

class PuzzleFlashcardViewModel (
    private var app: Application,
    private val illustrationLoader: IllustrationLoader
) : BaseViewModel<ReviewFlashcardEasyView>(), CardPlayableViewModel {

    val currentPos = ObservableInt()

    val setName = ObservableField<String>()

    val cardLeftCount = ObservableInt()

    val missedCardCount = ObservableInt()

    val passedCardCount = ObservableInt()

    private lateinit var currentCard: Flashcard

    private lateinit var deck: Deck

    private var cardList: ArrayList<Flashcard> = ArrayList()

    private var missedCardList = ArrayList<Flashcard>()

    private lateinit var reviewFCEasyActivity_StatusManager: ReviewActivitiesSpeakerStatusManager

    private lateinit var answerTextSpeaker: TextSpeaker

    private lateinit var questionTextSpeaker: TextSpeaker

    private val userForgottenCards = ArrayList<Flashcard> ()


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


    var isDeckReversed: Boolean = false
        private set

    override fun setUpData (deck: Deck, isDeckReversed: Boolean) {
        this.isDeckReversed = isDeckReversed
        this.deck = deck

        loadAllCardIllustrations(deck.flashcards)
        view.onLoadDataStart()

        // Show to UI by Databinding
        setName.set(deck.name)
        cardLeftCount.set(deck.flashcards.size)

        reviewFCEasyActivity_StatusManager =
            ReviewActivitiesSpeakerStatusManager(app, "", setNameFormat = { deckName ->
                return@ReviewActivitiesSpeakerStatusManager "Easy_Review<$deckName>"
            })

        val questionLanguage: String
        val answerLanguage: String

        if (isDeckReversed) {
            questionLanguage = deck.frontLanguage
            answerLanguage = deck.backLanguage
            CardListLanguageReverser.reverse_LIST_Card_TextAndTranslation(deck.flashcards)
        } else {
            answerLanguage = deck.frontLanguage
            questionLanguage = deck.backLanguage
        }

        // These statements must be called after #CardListLanguageReverser.reverse_ListCard_TextAndTranslation(Deck.flashcards)
        cardList.clear()
        deck.flashcards.shuffle()
        cardList.addAll(deck.flashcards)
        currentCard = cardList.first()


        val textWhichIsSpokenFirst = if (doesTextNeedSpeakingAtStart()) {
            if (checkCanUseExampleForTestSubject(currentCard)) {
                cardList.first().meanOfExample
            } else {
                cardList.first().translation
            }
        } else ""

        answerTextSpeaker = TextSpeaker(app, answerLanguage.trim())
        questionTextSpeaker = TextSpeaker(app, questionLanguage.trim(), textWhichIsSpokenFirst)

    }

    override fun beginUsing () {
        currentPos.set(0)
        useCard(currentCard)
    }

    override fun loadAllCardIllustrations(flashcardList: ArrayList<Flashcard>) {
        val illustrationsNameList = ArrayList<String>()
        if (isDeckReversed.not()) {
            flashcardList.forEach { flashcard ->
                if (isCardValid_In_This_Mode(flashcard)) {
                    if (flashcard.backIllustrationPictureName != null) {
                        illustrationsNameList.add(flashcard.backIllustrationPictureName!!)
                    }
                }
            }
        } else {
            flashcardList.forEach { flashcard ->
                if (isCardValid_In_This_Mode(flashcard)) {
                    if (flashcard.frontIllustrationPictureName != null) {
                        illustrationsNameList.add(flashcard.frontIllustrationPictureName!!)
                    }
                }
            }
        }

        illustrationLoader.loadListOfBitmap(illustrationsNameList, onGetResult =  { dataMap, exMap ->
            this.illustrationMap.putAll(dataMap)
            systemOutLogging("Map size: " + illustrationMap.size)
            illustrationMap.forEach {
                systemOutLogging("Name: " + it.key)
                systemOutLogging("Value: " + it.value)
            }
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

    private fun useCard (card : Flashcard) {
        val cellOfListType = getCellOfListType(card)

        val answerElement = convertAnswerToElements(card.text, cellOfListType)
        val completeAnswerElement = getShuffledElementsAnswer(answerElement)
        var illustration : Bitmap?
        var load_illustrationException : Exception?

        val useExampleForTestSubject :  Boolean
        if (isDeckReversed) {
            illustration = illustrationMap.get(card.frontIllustrationPictureName)
            load_illustrationException = load_illustrationExceptionMap.get(card.frontIllustrationPictureName)

            // Reverse for #checkCanUseExampleForTestSubject(card)
            CardListLanguageReverser.reverse_Card_ExampleAndMeanExample(card)
            useExampleForTestSubject = checkCanUseExampleForTestSubject(card)
            if (useExampleForTestSubject.not()) {
                // Reverse again, get it back to normal because example can't be used
                CardListLanguageReverser.reverse_Card_ExampleAndMeanExample(card)
            }
        } else { // Normal, not reverse
            illustration = illustrationMap.get(card.backIllustrationPictureName)
            load_illustrationException = load_illustrationExceptionMap.get(card.backIllustrationPictureName)
            useExampleForTestSubject = checkCanUseExampleForTestSubject(card)
        }

        view.onGetTestSubject(
            testSubject = card,
            illustration = illustration,
            load_illustrationException = load_illustrationException,
            ansElements = completeAnswerElement,
            useExampleForTestSubject = useExampleForTestSubject,
            listType = cellOfListType
        )
    }


    override fun submitAnswer (userAnswer : String) : Boolean{
        val cardAnswer = currentCard.text
        val answerIsComplete = (userAnswer.length == cardAnswer.length)
        if (answerIsComplete) {
            val CORRECT_ANSWER = (userAnswer == cardAnswer)
            if (CORRECT_ANSWER) {
                handleUserRememberCard()
                return true
            } else {
                view.perform_INcorrectAnsElemtsOrderAnims()
                return false
            }
        } else {
            if (cardAnswer.startsWith(userAnswer)) {
                view.perform_CorrectAnswerElementsOrderBehaviours()
                return true
            } else {
                view.perform_INcorrectAnsElemtsOrderAnims()
                return false
            }
        }
    }

    override fun handleUserRememberCard() {
        val userRelearnAcard = missedCardList.contains(currentCard)
        if (userRelearnAcard) {
            missedCardCount.set(missedCardCount.get() - 1)
        }
        passedCardCount.set(passedCardCount.get() + 1)
        view.onPassACard(passedCardCount.get(), missedCardCount.get())
        view.performPassBehaviours()
        cardLeftCount.set(cardList.size - (currentPos.get() + 1))
    }

    override fun handleUserForgetCard() {
        addCurrentCardTo_ListOfForgottenCards()
        missedCardCount.set(missedCardList.size)
        view.onPassACard(passedCardCount.get(), missedCardCount.get())
        view.performNotPassBehaviours()
    }

    override fun getDeckSize() = deck.flashcards.size

    override fun getForgottenCardList(): ArrayList<Flashcard> {

        return userForgottenCards

    }

    private fun addCurrentCardTo_ListOfForgottenCards() {
        if (missedCardList.notContains(currentCard)) {
            missedCardList.add(currentCard)
        }
    }

    override fun nextCard () {
        currentPos.set(currentPos.get() + 1)
        currentCard = cardList[currentPos.get()]
        useCard(currentCard)
    }

    override fun hasNext () : Boolean {
        val thereIsCardLeft = passedCardCount.get() + missedCardCount.get() < cardList.size
        return thereIsCardLeft
    }

    private val SPECIFIED_CELL_AMOUNT = 15
    private fun getCellOfListType (card : Flashcard) : ReviewFlashcardEasyView.ListOfCellType {
        if (card.text.trim().contains(" ")) {
            if (card.text.length > SPECIFIED_CELL_AMOUNT) {
                return ReviewFlashcardEasyView.ListOfCellType.WORD_LIST
            } else { // Text is too short to devide it into words
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

    fun getMissedCardsList(): ArrayList<Flashcard> {
        return missedCardList
    }

    fun getOriginalDeck(): Deck {
        if (isDeckReversed) {
            val cloneDeck = Deck(
                this.deck.name,
                this.deck.frontLanguage,
                this.deck.backLanguage
            )
            val cloneFlashcardList = ArrayList<Flashcard>()
            this.deck.flashcards.forEach { flashcard ->
                cloneFlashcardList.add(flashcard.copy())
            }
            CardListLanguageReverser.reverse_LIST_Card_TextAndTranslation(cloneFlashcardList)
            cloneDeck.flashcards = cloneFlashcardList

            return cloneDeck
        } else {
            // Set is original, not reversed
            return deck
        }
    }


    fun speakAnswer(text: String, onSpeakDone: () -> Unit) {
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
            systemOutLogging("ReviewFlashcardEasyViewModel.kt:: reviewFCEasyActivity_StatusManager is not initialized")
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
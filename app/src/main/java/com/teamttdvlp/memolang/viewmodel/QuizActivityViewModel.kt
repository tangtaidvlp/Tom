package com.teamttdvlp.memolang.viewmodel

import android.graphics.Bitmap
import com.teamttdvlp.memolang.data.model.entity.flashcard.CardQuizInfor
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.*
import com.teamttdvlp.memolang.model.repository.CardQuizInforRepos
import com.teamttdvlp.memolang.view.activity.iview.QuizView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.systemOutLogging
import com.teamttdvlp.memolang.viewmodel.abstraction.CardPlayableViewModel
import com.teamttdvlp.memolang.viewmodel.abstraction.CardRelearnableViewModel
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class QuizActivityViewModel (
    private val illustrationLoader: IllustrationLoader,
    private val cardQuizInforRepos: CardQuizInforRepos,
    private val engVietVocabularyLoader: EngVietVocabularyLoader
) : BaseViewModel<QuizView>() , CardPlayableViewModel, CardRelearnableViewModel {

    enum class AnswerMode {
        QUIZ,
        WRITING
    }

    var isDeckReversed = false

    private var passedCardCount = 0

    private var currentPos = 0

    private var missedCardCount = 0

    private lateinit var deck : Deck

    private var userForgottenCards = ArrayList<Flashcard>()

    private lateinit var quizCardListPlayer: QuizCardListPlayer

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
    private lateinit var currentCard : Flashcard

    private lateinit var answerTextSpeaker: TextSpeaker

    private lateinit var questionTextSpeaker: TextSpeaker


    override fun setUpData(deck: Deck, isReverseCards: Boolean) {
        this.deck = deck
        this.isDeckReversed = isReverseCards

        view.onLoadAllIllustrationStart()
        loadCardQuizInforFromDatabase (onEnd = {
            loadAllCardIllustrations(deck.flashcards)
        })
    }

    private fun loadCardQuizInforFromDatabase (onEnd : (() -> Unit)? = null ) {

        cardQuizInforRepos.getQuizInfors_ByDeckId(deckId = deck.name, onGetCardQuizInfors = { cardQuizInforList ->
            quizCardListPlayer = QuizCardListPlayer(deck.flashcards, cardQuizInforList)
            onEnd?.invoke()
        })

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
            beginUsing()
            view.onLoadAllIllustrationFinish()
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

    override fun beginUsing() {
        currentPos = 1
        passedCardCount = 0
        nextCard()
    }

    override fun submitAnswer(answer: String): Boolean {
        val matched = answer.equals(currentCard.text, true)
        if (matched) {
            handleUserRememberCard()
            nextCard()

        } else {
            handleUserForgetCard()
            nextCard()
        }


        view.extendedOnPassACard (quizCardListPlayer.passedCardCount,
                                                          quizCardListPlayer.familiarCardCount,
                                                          quizCardListPlayer.missedCardCount)

        return matched
    }

    override fun hasNext(): Boolean {
        return quizCardListPlayer.hasNext()
    }

    override fun nextCard() {
        if (!hasNext()) {
            view.onEndReviewing()
            return
        }
        val cardData = quizCardListPlayer.nextCard()
        currentCard = cardData.flashcard
        useCard(cardData)
    }

    private fun useCard (cardData : QuizCardListPlayer.CardData) {
        val flashcard = cardData.flashcard

        val answerMode : AnswerMode
        var answerSet : ArrayList<String>? = null
        val illustration : Bitmap?
        val load_illustrationException : Exception?

        val useExampleForTestSubject :  Boolean
        if (isDeckReversed) {
            illustration = illustrationMap.get(flashcard.frontIllustrationPictureName)
            load_illustrationException = load_illustrationExceptionMap.get(flashcard.frontIllustrationPictureName)

            // Reverse for #checkCanUseExampleForTestSubject(card)
            CardListLanguageReverser.reverse_Card_ExampleAndMeanExample(flashcard)
            useExampleForTestSubject = checkCanUseExampleForTestSubject(flashcard)
            if (useExampleForTestSubject.not()) {
                // Reverse again, get it back to normal because example can't be used
                CardListLanguageReverser.reverse_Card_ExampleAndMeanExample(flashcard)
            }
        } else { // Normal, not reverse
            illustration = illustrationMap.get(flashcard.backIllustrationPictureName)
            load_illustrationException = load_illustrationExceptionMap.get(flashcard.backIllustrationPictureName)
            useExampleForTestSubject = checkCanUseExampleForTestSubject(flashcard)
        }

        when (cardData) {
            is QuizCardListPlayer.QuizCardData -> {
                if (cardData.cardQuizInfor != null) {
                    answerSet = createAnswerSet(theCorrectAnswer = flashcard.text, cardQuizInfor = cardData.cardQuizInfor)
                } else {
                    val prefix = flashcard.text.get(0).toString()
                    answerSet = createRandomAnswerSet(prefix)
                    answerSet.add(flashcard.text)
                    answerSet.shuffle()
                }
                answerMode  = AnswerMode.QUIZ
            }

            is QuizCardListPlayer.WritingCardData -> {
                answerMode = AnswerMode.WRITING
            }

            else -> {
                throw Exception ("Unknown answer mode")
            }
        }

        view.onGetTestSubject(
            testSubject = flashcard,
            illustration = illustration,
            load_illustrationException = load_illustrationException,
            useExampleForTestSubject = useExampleForTestSubject,
            answerMode = answerMode,
            answerSet = answerSet
        )
    }


    private fun createAnswerSet (theCorrectAnswer : String, cardQuizInfor : CardQuizInfor) : ArrayList<String> {
        val answerSet = ArrayList<String>()
        answerSet.add(theCorrectAnswer)

        var answer1 = cardQuizInfor.getAnswer1()
        var answer2 = cardQuizInfor.getAnswer2()
        var answer3 = cardQuizInfor.getAnswer3()

        answerSet.add(answer1)
        answerSet.add(answer2)
        answerSet.add(answer3)

        answerSet.shuffle()
        return answerSet
    }

    private fun createRandomAnswerSet(prefix : String) : ArrayList<String> {
        val answerSource = engVietVocabularyLoader.getOfflineVocaFromRawFile_ByPrefix(prefix)
        val randomer = Random()

        val answer1 = answerSource.get(randomer.nextInt(answerSource.size - 1))
        val answer2 = answerSource.get(randomer.nextInt(answerSource.size - 1))
        val answer3 = answerSource.get(randomer.nextInt(answerSource.size - 1))

        return ArrayList<String>().apply {
            add(answer1.key)
            add(answer2.key)
            add(answer3.key)
        }
    }

    override fun handleUserRememberCard() {
        quizCardListPlayer.processRememberCard(currentCard)
    }

    override fun handleUserForgetCard() {
        quizCardListPlayer.processForgottenCard(currentCard)
    }

    override fun handleUserRelearnCard() {

    }

    override fun getDeckSize(): Int {
        return deck.flashcards.size
    }

    override fun getForgottenCardList(): ArrayList<Flashcard> {
        return userForgottenCards
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

}
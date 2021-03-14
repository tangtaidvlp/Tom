package com.teamttdvlp.memolang.model

import com.teamttdvlp.memolang.data.model.entity.flashcard.CardQuizInfor
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.view.helper.notContains
import com.teamttdvlp.memolang.view.helper.systemOutLogging
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

const val QUIZ_MODE = 0
const val WRITING_MODE = 1

class QuizCardListPlayer {

    private val cardQuizInforMap = HashMap<Flashcard, CardQuizInfor?>()

    private val familiarCardList = ArrayList<Flashcard>()

    private val originalCardList = ArrayList<Flashcard>()

    private val allForgottenCardList = ArrayList<Flashcard>()

    private lateinit var currentCard : Flashcard

    var passedCardCount : Int = 0
    private set

    var familiarCardCount : Int = 0
    private set

    var missedCardCount : Int = 0

    /**
     * Mean that after 3 times play card in #unfamiliarCardList, we will pop a card from Familiar List
     */
    private val TURN_COUNT_TO_PLAY_FAMILIAR_CARD = 3
    private var turn = 0

    constructor (flashcardList : ArrayList<Flashcard>, cardQuizInforDataList: ArrayList<CardQuizInfor>) {
        originalCardList.clear()
        originalCardList.addAll(flashcardList)
        setUpCardQuizInforMap (flashcardList, cardQuizInforDataList)
    }

    private fun setUpCardQuizInforMap(
        flashcardList: ArrayList<Flashcard>,
        cardQuizInforDataList: ArrayList<CardQuizInfor>
    ) {
        for (flashcard in flashcardList) {
            var hasMatched = false

            for (cardQuizInfor in cardQuizInforDataList) {
                if (cardQuizInfor.cardId == flashcard.id) {
                    cardQuizInforMap[flashcard] = cardQuizInfor
                    cardQuizInforDataList.remove(cardQuizInfor)
                    hasMatched  = true
                    break
                }
            }

            if (hasMatched.not()) {
                cardQuizInforMap[flashcard] = null
            }
        }

    }

    fun nextCard () : CardData {

        systemOutLogging("Forgotten: " + missedCardCount)

        systemOutLogging("Familiar: " + familiarCardCount)

        systemOutLogging("Original: " + originalCardList.size)

        val noUnfamiliarCardLeft = hasUnfamiliarCardLeft().not() && familiarCardList.isNotEmpty()
        val familiarCardTurn = (turn == TURN_COUNT_TO_PLAY_FAMILIAR_CARD && hasFamiliarCardLeft())
        val playFamiliarCards = familiarCardTurn or noUnfamiliarCardLeft

        val playWritingMode = playFamiliarCards
        val playQuizMode = playWritingMode.not()

        if (playWritingMode) {
            turn = 1 // Reset turn
            val randomFamiliarCard = familiarCardList.get(0)
            currentCard = randomFamiliarCard
            return WritingCardData (randomFamiliarCard)

        } else if (playQuizMode) {
            turn ++
            val currentPlayCard = originalCardList.get(0)
            currentCard = currentPlayCard
            return QuizCardData (currentPlayCard, getCardQuizInformation(currentPlayCard))
        }

        throw Exception("Unreachable exception")
    }

    private fun getCardQuizInformation(flashcard: Flashcard): CardQuizInfor? {
        return cardQuizInforMap.get(flashcard)
    }

    /**
     * Put the forgotten card in a the random position from 2 to the list size - 1 (If list size > 2)
     * Or from 0 to list size
     */
    fun processForgottenCard (forgottenCard : Flashcard) {
        if (currentCard != forgottenCard) {
            throw Exception ("Unmatched card order exception")
        }

        if (originalCardList.contains(forgottenCard)) {
            originalCardList.remove(forgottenCard)
            systemOutLogging("Removed forgotten")

        } else if (familiarCardList.contains(forgottenCard)) {
            familiarCardList.remove(forgottenCard)
            familiarCardCount--
            systemOutLogging("Removed familiar")
        } else throw Exception ("Lost card exception")

        if (originalCardList.isEmpty()) {
            originalCardList.add(0, forgottenCard)
        } else {
            val randomPosition : Int
            val startBound = if (originalCardList.size > 2) 2 else 0
            randomPosition = Random().nextInt(originalCardList.size + 1 - startBound) + startBound
            originalCardList.add(randomPosition, forgottenCard)
        }

        if (allForgottenCardList.notContains(forgottenCard)) {
            allForgottenCardList.add(forgottenCard)
            missedCardCount++
        }
    }

    fun processRememberCard (passedCard : Flashcard) {
        if (currentCard != passedCard) {
            throw Exception ("Unmatched card order exception")
        }

        val familiarCard = familiarCardList.contains(passedCard)
        val userFirstTimePass = familiarCard.not()

        if (familiarCard) {
            familiarCardList.remove(passedCard)
            passedCardCount++

        } else if (userFirstTimePass){
            originalCardList.remove(passedCard)
            familiarCardList.add(passedCard)
            familiarCardCount++
        }

        if (allForgottenCardList.contains(passedCard)) {
            missedCardCount--
        }
    }


    private fun hasUnfamiliarCardLeft () : Boolean {
        return originalCardList.isNotEmpty()
    }

    private fun hasFamiliarCardLeft () : Boolean {
        return familiarCardList.isNotEmpty()
    }

    fun hasNext () : Boolean {
        return hasUnfamiliarCardLeft() || hasFamiliarCardLeft()
    }

    fun getCardList(): ArrayList<Flashcard> {
        return ArrayList(cardQuizInforMap.keys)
    }

    fun getForgottenCardList(): ArrayList<Flashcard> {
        return allForgottenCardList
    }

    abstract class CardData (val flashcard : Flashcard)

    class WritingCardData (flashcard: Flashcard) : CardData (flashcard)

    class QuizCardData (flashcard : Flashcard, val cardQuizInfor : CardQuizInfor?) : CardData (flashcard)

}
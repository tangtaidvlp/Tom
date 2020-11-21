package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.CardListLanguageReverser.Companion.reverse_LIST_Card_TextAndTranslation
import com.teamttdvlp.memolang.model.CardListManager
import com.teamttdvlp.memolang.model.IllustrationLoader
import com.teamttdvlp.memolang.model.TextSpeaker
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager.SpeakerStatus.Companion.SPEAK_TEXT_AND_TRANSLATION
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager.SpeakerStatus.Companion.SPEAK_TEXT_ONLY
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.systemOutLogging

class UseFlashcardViewModel(
    private val context: Application,
    private val illustrationLoader: IllustrationLoader
) : BaseViewModel<UseFlashcardView>() {

    val currentCard = MutableLiveData<Flashcard>()

    var passedCardCount: Int = 0

    var forgottenCardCount: Int = 0

    val currentCardOrder = MutableLiveData<Int>()

    val deckName = MutableLiveData<String>()

    var isReversedTextAndTranslation = false


    private val cardListManager = CardListManager()

    private val allForgottenCardList = ArrayList<Flashcard>()

    private val hardCardList = ArrayList<Flashcard>()

    private lateinit var deck: Deck

    private lateinit var srcLangTextSpeaker: TextSpeaker

    private lateinit var tgtLangTextSpeaker: TextSpeaker

    private lateinit var useFCActivityStatusManager: UseFCActivity_StatusManager

    // <Picture Name, Bitmap>
    private var cardIllustrationMap = HashMap<String, Bitmap?>()

    fun setData(fcDeck: Deck, reverseTextAndTrans: Boolean) {
        val frontLang: String
        val backLang: String
        this.deck = fcDeck
        this.isReversedTextAndTranslation = reverseTextAndTrans
        if (reverseTextAndTrans) {
            reverse_LIST_Card_TextAndTranslation(fcDeck.flashcards)
            frontLang = fcDeck.backLanguage.trim()
            backLang = fcDeck.frontLanguage.trim()
        } else {
            frontLang = fcDeck.frontLanguage.trim()
            backLang = fcDeck.backLanguage.trim()
        }

        cardListManager.setData(fcDeck.flashcards)
        deckName.value = fcDeck.name

        useFCActivityStatusManager = UseFCActivity_StatusManager(context, fcDeck.name)

        val textSpokenFirst = if (doesTextNeedSpeakingAtStart()) {
            cardListManager.getFirstOne().text
        } else ""

        srcLangTextSpeaker = TextSpeaker(context, frontLang, textSpokenFirst)
        tgtLangTextSpeaker = TextSpeaker(context, backLang.trim())

    }

    private fun beginUsing() {
        val firstCard = cardListManager.getFirstOne()
        updateCurrentCard(firstCard)
        currentCardOrder.value = 1
        passedCardCount = 0
        forgottenCardCount = 0
    }

    fun loadAllCardIllustrations(flashcardList: ArrayList<Flashcard>) {
        val allPictureList = ArrayList<String>()
        flashcardList.forEach { card ->
            if (card.frontIllustrationPictureName != null) {
                allPictureList.add(card.frontIllustrationPictureName!!)
            }

            if (card.backIllustrationPictureName != null) {
                allPictureList.add(card.backIllustrationPictureName!!)
            }
        }
        illustrationLoader.loadListOfBitmap(allPictureList, onGetResult = { data, exceptionMap ->
            cardIllustrationMap = data
            beginUsing()
            view.onLoadAllIllustrationFinish()
        })
    }

    fun moveToNextCard() {
        val nextCard = cardListManager.focusOnNextCardAndGetIt()
        updateCurrentCard(nextCard)
        updateCardOrder()
    }

    fun moveToPreviousCard() {
        if (hasPrevious()) {
            val thePreviousCard = cardListManager.focusOnPrevCardAndGetIt()
            updateCurrentCard(thePreviousCard)
            updateCardOrder()
        }
    }

    fun checkThereIsCardLefts () : Boolean {
        return cardListManager.hasNext()
    }

    fun checkIfThereIsPreviousCard () {
        if (hasPrevious()) {
            view.unlock_ShowPreviousCard_Function()
        } else {
            view.lock_ShowPreviousCard_Function()
        }
    }

    private fun updateCurrentCard (card : Flashcard) {
        currentCard.value = card
        if (card.frontIllustrationPictureName != null) {
            val frontImage = cardIllustrationMap.get(card.frontIllustrationPictureName!!)
            if (frontImage != null) {
                view.onGetFrontIllustration(frontImage)
            } else {
                systemOutLogging("Error happen when get this image: ")
            }
        }

        if (card.backIllustrationPictureName != null) {
            val backImage = cardIllustrationMap.get(card.backIllustrationPictureName!!)
            if (backImage != null) {
                view.onGetBackIllustration(backImage)
            } else {
                systemOutLogging("Error happen when get this image: ")
            }
        }
    }

    private fun hasPrevious () : Boolean {
        return cardListManager.hasPrevious()
    }

    fun handleEasyCard () {
        passedCardCount++
        if (hardCardList.contains(currentCard.value)) {
            forgottenCardCount--
            hardCardList.remove(currentCard.value)
        }
        view.onPassACard(passedCardCount, forgottenCardCount)
    }

    fun handleHardCard() {
        if (hardCardList.contains(cardListManager.getCurrentCard()).not()) {
            forgottenCardCount++
            hardCardList.add(cardListManager.getCurrentCard())
        }
        cardListManager.handleHardCard()
        view.onPassACard(passedCardCount, forgottenCardCount)
    }

    private fun updateCardOrder() {
        currentCardOrder.value = cardListManager.currentIndex + 1
    }

    fun getForgottenCardList(): ArrayList<Flashcard> {
        return hardCardList
    }

    fun getCardListSize(): Int {
        return cardListManager.getSize()
    }


    private fun doesTextNeedSpeakingAtStart(): Boolean {
        val speakerFunc = useFCActivityStatusManager.speakerStatusManager.getFunction()
        val speakerIsOn = useFCActivityStatusManager.speakerStatusManager.getStatus()
        return ((speakerFunc == SPEAK_TEXT_ONLY) or (speakerFunc == SPEAK_TEXT_AND_TRANSLATION)) and speakerIsOn
    }

    fun speakFrontCardText(text: String) {
        srcLangTextSpeaker.speak(text)
        if (srcLangTextSpeaker.error != null) {
            // We just want to show this error only once
            // because although there is error, but text speaker still work
            // I also don't know exactly how it work
            view.showSpeakTextError(srcLangTextSpeaker.error + "")
            srcLangTextSpeaker.error = null
        }
    }

    fun speakBackCardText(text: String) {
        tgtLangTextSpeaker.speak(text)
        if (tgtLangTextSpeaker.error != null) {
            // We just want to show this error only once
            // because although there is error, but text speaker still work
            // I also don't know exactly how it work
            view.showSpeakTextError(tgtLangTextSpeaker.error + "")
        }
    }

    fun getSpeakerFunction(): Int {
        return useFCActivityStatusManager.speakerStatusManager.getFunction()
    }

    fun saveAllStatus(speakerFunction: Int, speakerStatus: Boolean) {
        if (::useFCActivityStatusManager.isInitialized) {
            useFCActivityStatusManager.speakerStatusManager.saveFunction(speakerFunction)
            useFCActivityStatusManager.speakerStatusManager.saveStatus(speakerStatus)
        } else {
            systemOutLogging("UseFlashcardViewModel.kt:: useFCActivityStatusManager is not initialized")
        }
    }

    fun stopAllTextSpeaker() {
        tgtLangTextSpeaker.shutDown()
        srcLangTextSpeaker.shutDown()
    }

    fun getSpeakerStatus(): Boolean {
        return useFCActivityStatusManager.speakerStatusManager.getStatus()
    }

    fun getOriginalFlashcardSet(): Deck {
        if (isReversedTextAndTranslation) {
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

}

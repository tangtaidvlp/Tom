package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.model.CardListLanguageReverser.Companion.reverse_LIST_Card_TextAndTranslation
import com.teamttdvlp.memolang.model.CardListManager
import com.teamttdvlp.memolang.model.TextSpeaker
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager.SpeakerStatus.Companion.SPEAK_TEXT_AND_TRANSLATION
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager.SpeakerStatus.Companion.SPEAK_TEXT_ONLY
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.log
import com.teamttdvlp.memolang.view.helper.selfMinusOne
import com.teamttdvlp.memolang.view.helper.selfPlusOne

class UseFlashcardViewModel (private val context : Application): BaseViewModel<UseFlashcardView>() {

    val currentCard = ObservableField<Flashcard>()

    val cardLeftCount = ObservableInt()

    val forgottenCardsCount = ObservableInt()

    val currentCardOrder = ObservableInt()

    val setName = ObservableField<String>()

    private val hardCardList = ArrayList<Flashcard>()

    val cardListManager = CardListManager()

    private lateinit var flashcardSet: FlashcardSet

    private lateinit var srcLangTextSpeaker: TextSpeaker

    private lateinit var tgtLangTextSpeaker: TextSpeaker

    private lateinit var useFCActivityStatusManager: UseFCActivity_StatusManager

    var isReversedTextAndTranslation = false

    fun setData(fcSet: FlashcardSet, reverseTextAndTrans: Boolean) {
        val frontLang: String
        val backLang: String
        this.flashcardSet = fcSet
        this.isReversedTextAndTranslation = reverseTextAndTrans
        if (reverseTextAndTrans) {
            reverse_LIST_Card_TextAndTranslation(fcSet.flashcards)
            frontLang = fcSet.backLanguage.trim()
            backLang = fcSet.frontLanguage.trim()
        } else {
            frontLang = fcSet.frontLanguage.trim()
            backLang = fcSet.backLanguage.trim()
        }

        cardListManager.setData(fcSet.flashcards)
        setName.set(fcSet.name)

        useFCActivityStatusManager = UseFCActivity_StatusManager(context, fcSet.name)

        val textSpokenFirst = if (doesTextNeedSpeakingAtStart()) {
            cardListManager.getFirstOne().text
        } else ""

        srcLangTextSpeaker = TextSpeaker(context, frontLang, textSpokenFirst)
        tgtLangTextSpeaker = TextSpeaker(context, backLang.trim())

    }

    private fun doesTextNeedSpeakingAtStart () : Boolean {
        val speakerFunc = useFCActivityStatusManager.speakerStatusManager.getFunction()
        val speakerIsOn = useFCActivityStatusManager.speakerStatusManager.getStatus()
        return ((speakerFunc == SPEAK_TEXT_ONLY) or (speakerFunc == SPEAK_TEXT_AND_TRANSLATION)) and speakerIsOn
    }

    fun speakFrontCardText (text : String) {
        srcLangTextSpeaker.speak(text)
        if (srcLangTextSpeaker.error != null) {
            // We just want to show this error only once
            // because although there is error, but text speaker still work
            // I also don't know exactly how it work
            view.showSpeakTextError(srcLangTextSpeaker.error + "")
            srcLangTextSpeaker.error = null
        }
    }

    fun speakBackCardText (text : String) {
        tgtLangTextSpeaker.speak(text)
        if (tgtLangTextSpeaker.error != null) {
            // We just want to show this error only once
            // because although there is error, but text speaker still work
            // I also don't know exactly how it work
            view.showSpeakTextError(tgtLangTextSpeaker.error + "")
        }
    }

    fun moveToNextCard () {
        val nextCard = cardListManager.focusOnNextCardAndGetIt()
        updateCurrentCard(nextCard)
        updateCardOrder()
    }

    fun beginUsing() {
        val firstCard = cardListManager.getFirstOne()
        currentCard.set(firstCard)
        currentCardOrder.set(1)
        cardLeftCount.set(cardListManager.getSize())
    }

    fun moveToPreviousCard () {
        if (hasPrevious()) {
            val thePreviousCard = cardListManager.focusOnPrevCardAndGetIt()
            updateCurrentCard(thePreviousCard)
            cardLeftCount.selfPlusOne()
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
        currentCard.set(card)
    }

    private fun hasPrevious () : Boolean {
        return cardListManager.hasPrevious()
    }

    fun handleEasyCard () {
        cardLeftCount.selfMinusOne()
    }

    fun handleHardCard () {
        if (!hardCardList.contains(cardListManager.getCurrentCard())) {
            forgottenCardsCount.selfPlusOne()
            hardCardList.add(cardListManager.getCurrentCard())
        }
        cardListManager.handleHardCard()
    }

    fun getForgottenCardList () : ArrayList<Flashcard> {
        return hardCardList
    }

    private fun updateCardOrder () {
        currentCardOrder.set(cardListManager.currentIndex + 1)
    }

    fun getSpeakerFunction () : Int {
        return useFCActivityStatusManager.speakerStatusManager.getFunction()
    }

    fun saveAllStatus (speakerFunction : Int, speakerStatus : Boolean) {
        if (::useFCActivityStatusManager.isInitialized) {
            useFCActivityStatusManager.speakerStatusManager.saveFunction(speakerFunction)
            useFCActivityStatusManager.speakerStatusManager.saveStatus(speakerStatus)
        } else {
            log("UseFlashcardViewModel.kt:: useFCActivityStatusManager is not initialized")
        }
    }

    fun stopAllTextSpeaker() {
        tgtLangTextSpeaker.shutDown()
        srcLangTextSpeaker.shutDown()
    }

    fun getSpeakerStatus(): Boolean {
        return useFCActivityStatusManager.speakerStatusManager.getStatus()
    }

    fun getOriginalFlashcardSet(): FlashcardSet {
        if (isReversedTextAndTranslation) {
            val cloneFlashcardSet = FlashcardSet(
                this.flashcardSet.name,
                this.flashcardSet.frontLanguage,
                this.flashcardSet.backLanguage
            )
            val cloneFlashcardList = ArrayList<Flashcard>()
            this.flashcardSet.flashcards.forEach { flashcard ->
                cloneFlashcardList.add(flashcard.copy())
            }
            reverse_LIST_Card_TextAndTranslation(cloneFlashcardList)
            cloneFlashcardSet.flashcards = cloneFlashcardList

            return cloneFlashcardSet
        } else {
            // Set is original, not reversed
            return flashcardSet
        }
    }

}

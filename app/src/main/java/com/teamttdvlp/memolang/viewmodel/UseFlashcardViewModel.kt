package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.model.CardListManager
import com.teamttdvlp.memolang.model.TextSpeaker
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager.SpeakerStatus.Companion.SPEAK_TEXT_AND_TRANSLATION
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager.SpeakerStatus.Companion.SPEAK_TEXT_ONLY
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.entity.Language
import com.teamttdvlp.memolang.model.entity.Language.Companion.LANG_DIVIDER
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardView
import com.teamttdvlp.memolang.view.base.BaseAndroidViewModel
import com.teamttdvlp.memolang.view.helper.selfMinusOne
import com.teamttdvlp.memolang.view.helper.selfPlusOne
import kotlin.collections.ArrayList

class UseFlashcardViewModel (var context : Application): BaseAndroidViewModel<UseFlashcardView>(context) {

    val currentCard = ObservableField<Flashcard>()

    val cardLeftCount = ObservableInt()

    val fogottenCardsCount = ObservableInt()

    val currentCardOrder = ObservableInt()

    val languageInfo = ObservableField<String>()

    val setName = ObservableField<String>()

    private val hardCardList = ArrayList<Flashcard>()

    private val cardListManager = CardListManager()

    private lateinit var srcLangTextSpeaker : TextSpeaker

    private lateinit var tgtLangTextSpeaker : TextSpeaker

    private lateinit var useFCActivityStatusManager : UseFCActivity_StatusManager

    fun setData (cardList : ArrayList<Flashcard>, changeLanguageFlow : Boolean) {

        cardListManager.setData(cardList)

        val langInfo = cardList.first().languagePair.split(LANG_DIVIDER)
        val sourceLang = langInfo.get(Language.SOURCE_LANGUAGE)
        val targetLang = langInfo.get(Language.TARGET_LANGUAGE)
        languageInfo.set("$sourceLang - $targetLang")
        setName.set(cardList.first().setName)
        useFCActivityStatusManager = UseFCActivity_StatusManager(context, cardList.first().setName)

        val textSpokenFirst = if (doesTextNeedSpeakingAtStart()) {
            cardListManager.getFirstOne().text
        } else ""

        srcLangTextSpeaker = TextSpeaker(context, sourceLang.trim(), textSpokenFirst)
        tgtLangTextSpeaker = TextSpeaker(context, targetLang.trim())

    }

    fun doesTextNeedSpeakingAtStart () : Boolean {
        val speakerFunc = useFCActivityStatusManager.speakerStatusManager.getFunction()
        val speakerIsOn = useFCActivityStatusManager.speakerStatusManager.getStatus()
        return ((speakerFunc == SPEAK_TEXT_ONLY) or (speakerFunc == SPEAK_TEXT_AND_TRANSLATION)) and speakerIsOn
    }

    fun speakSrcLangText (text : String) {
        srcLangTextSpeaker.speak(text)
        if (srcLangTextSpeaker.error != null) {
            // We just want to show this error only once
            // because although there is error, but text speaker still work
            // I also don't know exactly how it work
            view.showSpeakTextError(srcLangTextSpeaker.error + "")
            srcLangTextSpeaker.error = null
        }
    }

    fun speakTgtLangText (text : String) {
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
            view.showPreviousCardButton()
        } else {
            view.hidePreviousCardButton()
        }
    }

    fun updateCurrentCard (card : Flashcard) {
        currentCard.set(card)
    }

    fun hasNext () : Boolean {
        return cardListManager.hasNext()
    }

    fun hasPrevious () : Boolean {
        return cardListManager.hasPrevious()
    }


    fun handleEasyCard () {
        cardLeftCount.selfMinusOne()
    }

    fun handleHardCard () {
        if (!hardCardList.contains(cardListManager.getCurrentCard())) {
            fogottenCardsCount.selfPlusOne()
            hardCardList.add(cardListManager.getCurrentCard())
        }
        cardListManager.handleHardCard()
    }

    fun getFoggotenCardList () : ArrayList<Flashcard> {
        return hardCardList
    }

    fun updateCardOrder () {
        currentCardOrder.set(cardListManager.currentIndex + 1)
    }

    fun getSpeakerFunction () : Int {
        return useFCActivityStatusManager.speakerStatusManager.getFunction()
    }

    fun saveAllStatus (speakerFunction : Int, speakerStatus : Boolean) {
        useFCActivityStatusManager.speakerStatusManager.saveFunction(speakerFunction)
        useFCActivityStatusManager.speakerStatusManager.saveStatus(speakerStatus)
    }

    fun stopAllTextSpeaker() {
        tgtLangTextSpeaker.shutDown()
        srcLangTextSpeaker.shutDown()
    }

    fun getSpeakerStatus(): Boolean {
        return useFCActivityStatusManager.speakerStatusManager.getStatus()
    }
}

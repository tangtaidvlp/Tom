package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.model.CardListManager
import com.teamttdvlp.memolang.model.TextSpeaker
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.entity.Language
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

    private val hardCardList = ArrayList<Flashcard>()

    private val cardListManager = CardListManager()

    private lateinit var srcLangTextSpeaker : TextSpeaker

    private lateinit var tgtLangTextSpeaker : TextSpeaker

    fun setData (cardList : ArrayList<Flashcard>) {
        cardListManager.setData(cardList)
        val langInfo = cardList.first().languagePair.split("-")
        val sourceLang = langInfo.get(Language.SOURCE_LANGUAGE)
        val targetLang = langInfo.get(Language.TARGET_LANGUAGE)
        languageInfo.set("$sourceLang - $targetLang")
        srcLangTextSpeaker = TextSpeaker(context, sourceLang.trim())
        tgtLangTextSpeaker = TextSpeaker(context, targetLang.trim())
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

    fun beginUsing() {
        val firstCard = cardListManager.getFirstOne()
        currentCard.set(firstCard)
        currentCardOrder.set(1)
        cardLeftCount.set(cardListManager.getSize())
    }


    fun moveToNextCard () {
        if (thereIsCardLefts()) {
            val nextCard = cardListManager.focusOnNextCardAndGetIt()
            updateCurrentCard(nextCard)
            updateCardOrder()
        } else {
            view.onNoCardsLeft()
        }
    }


    fun moveToPreviousCard () {
        if (hasPrevious()) {
            val thePreviousCard = cardListManager.focusOnPrevCardAndGetIt()
            updateCurrentCard(thePreviousCard)
            cardLeftCount.selfPlusOne()
            updateCardOrder()
        }
    }

    fun thereIsCardLefts () : Boolean {
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
}

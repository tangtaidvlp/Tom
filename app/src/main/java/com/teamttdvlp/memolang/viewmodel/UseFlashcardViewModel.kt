package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.CardListLanguageReverser.Companion.reverse_LIST_Card_TextAndTranslation
import com.teamttdvlp.memolang.model.CardListPlayer
import com.teamttdvlp.memolang.model.IllustrationLoader
import com.teamttdvlp.memolang.model.TextSpeaker
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager.SpeakerStatus.Companion.SPEAK_TEXT_AND_TRANSLATION
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager.SpeakerStatus.Companion.SPEAK_TEXT_ONLY
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.systemOutLogging
import com.teamttdvlp.memolang.viewmodel.abstraction.CardPlayableViewModel
import com.teamttdvlp.memolang.viewmodel.abstraction.CardRelearnableViewModel
import java.lang.Exception

const val I_REMEMBER_IT = "I rememeber it"
const val I_FORGET_IT = "I forget it"
class UseFlashcardViewModel(
    private val context: Application,
    private val illustrationLoader: IllustrationLoader
) : BaseViewModel<UseFlashcardView>(), CardPlayableViewModel, CardRelearnableViewModel {

    val currentCard = MutableLiveData<Flashcard>()

    private var currentCardOrder : Int = 0

    val deckName = MutableLiveData<String>()

    var isReversedTextAndTranslation = false
    private set

    private val cardListPlayer = CardListPlayer()

    private val passedCardList = ArrayList<Flashcard>()

    private val forgottenCardList = ArrayList<Flashcard>()

    private val userForgottenCards = ArrayList<Flashcard> ()

    private lateinit var deck: Deck

    private lateinit var srcLangTextSpeaker: TextSpeaker

    private lateinit var tgtLangTextSpeaker: TextSpeaker

    private lateinit var useFCActivityStatusManager: UseFCActivity_StatusManager

    // <Picture Name, Bitmap>
    private var cardIllustrationMap = HashMap<String, Bitmap?>()

    override fun setUpData(fcDeck: Deck, reverseTextAndTrans: Boolean) {
        this.deck = fcDeck
        this.isReversedTextAndTranslation = reverseTextAndTrans

        loadAllCardIllustrations(fcDeck.flashcards)
        view.onLoadDataStart()

        val frontLang: String
        val backLang: String
        if (reverseTextAndTrans) {
            reverse_LIST_Card_TextAndTranslation(fcDeck.flashcards)
            frontLang = fcDeck.backLanguage.trim()
            backLang = fcDeck.frontLanguage.trim()
        } else {
            frontLang = fcDeck.frontLanguage.trim()
            backLang = fcDeck.backLanguage.trim()
        }

        cardListPlayer.setData(fcDeck.flashcards)
        deckName.value = fcDeck.name

        useFCActivityStatusManager = UseFCActivity_StatusManager(context, fcDeck.name)

        val textSpokenFirst = if (doesTextNeedSpeakingAtStart()) {
            cardListPlayer.getFirstOne().text
        } else ""

        srcLangTextSpeaker = TextSpeaker(context, frontLang, textSpokenFirst)
        tgtLangTextSpeaker = TextSpeaker(context, backLang.trim())

    }

    override fun beginUsing() {
        val firstCard = cardListPlayer.getFirstOne()
        updateCurrentCard(firstCard)
        currentCardOrder = 1
    }

    override fun loadAllCardIllustrations (flashcardList: ArrayList<Flashcard>) {
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
            view.onLoadDataFinish()
        })
    }

    override fun submitAnswer (answer : String): Boolean {
       if (answer == I_REMEMBER_IT)
            return true
       else if (answer == I_FORGET_IT){
           return false
       }
        throw Exception ("Must return Remember or Forget")
    }

    override fun handleUserRememberCard () {

        val userRelearnACard = forgottenCardList.contains(currentCard.value)

        passedCardList.add(currentCard.value!!)

        if (userRelearnACard) {
            handleUserRelearnCard()
        }

        view.onPassACard(passedCardList.size, forgottenCardList.size)
    }

    override fun handleUserForgetCard() {
        if (forgottenCardList.contains(currentCard.value).not()) {
            forgottenCardList.add(currentCard.value!!)
            userForgottenCards.add(currentCard.value!!)
        }
        cardListPlayer.handleHardCard()
        view.onPassACard(passedCardList.size, forgottenCardList.size)
    }

    override fun handleUserRelearnCard() {
        forgottenCardList.remove(currentCard.value)
    }

    override fun nextCard() {
        val nextCard = cardListPlayer.focusOnNextCardAndGetIt()
        updateCurrentCard(nextCard)
        updateCardOrder()
    }

    fun previousCard() {
        if (hasPrevious()) {
            val thePreviousCard = cardListPlayer.focusOnPrevCardAndGetIt()
            updateCurrentCard(thePreviousCard)
            updateCardOrder()
        }
    }

    override fun hasNext () : Boolean {
        return cardListPlayer.hasNext()
    }

    private fun hasPrevious () : Boolean {
        return cardListPlayer.hasPrevious()
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

    private fun updateCardOrder() {
        currentCardOrder = cardListPlayer.currentIndex + 1
    }

    override fun getForgottenCardList(): ArrayList<Flashcard> {
        return userForgottenCards
    }

    override fun getDeckSize(): Int {
        return cardListPlayer.getSize()
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

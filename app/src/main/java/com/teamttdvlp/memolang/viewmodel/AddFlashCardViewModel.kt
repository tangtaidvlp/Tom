package com.teamttdvlp.memolang.viewmodel

import android.graphics.Bitmap
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.SetNameUtils
import com.teamttdvlp.memolang.model.AddFlashcardExecutor
import com.teamttdvlp.memolang.model.IllustrationLoader
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.model.repository.UserRepos
import com.teamttdvlp.memolang.model.repository.UserUsingHistoryRepos
import com.teamttdvlp.memolang.model.sharepref.BaseAppInfoSharedPreference
import com.teamttdvlp.memolang.view.activity.iview.AddFlashcardView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.systemOutLogging

class AddFlashCardViewModel(
    private var userRepos: UserRepos,
    private var addFlashcardExecutor: AddFlashcardExecutor,
    private var flashcardSetRepos: FlashcardSetRepos,
    private var userUsingHistoryRepos: UserUsingHistoryRepos,
    private var addFlashcardSharedPref: BaseAppInfoSharedPreference,
    private var illustrationLoader: IllustrationLoader
) : BaseViewModel<AddFlashcardView>() {

    private lateinit var userDeckList: ArrayList<Deck>

    lateinit var currentFocusDeck: Deck

    fun proceedAddFlashcard(
        newCard: Flashcard,
        front_IllustrationPicture: Bitmap? = null,
        back_IllustrationPicture: Bitmap? = null
    ) {

        if (newCard.setOwner == "") {
            newCard.setOwner = currentFocusDeck.name
        }

        if ((front_IllustrationPicture == null) and newCard.text.isEmpty()) {
            view.showFrontCardInputError()
            return
        }

        if ((back_IllustrationPicture == null) and newCard.translation.isEmpty()) {
            view.showBackCardInputError()
            return
        }

        saveFlashcardAndUpdateUserInfo(newCard, front_IllustrationPicture, back_IllustrationPicture)
    }

    fun loadIllustrationPicture(name: String, onGet: (Exception?, Bitmap?) -> Unit) {
        illustrationLoader.loadBitmap(name, onGet)
    }

    fun createNewFlashcardSetIfValid(
        setName: String,
        frontLanguage: String,
        backLanguage: String
    ): Deck? {
        val newFlashcardSet = Deck(setName, frontLanguage, backLanguage)
        val checkingInfo: Pair<Boolean, String?> = checkFlashcardSetIsValid(newFlashcardSet)
        val setIsValid = checkingInfo.first
        if (setIsValid) {
            flashcardSetRepos.insert(newFlashcardSet)
            view.hideCreateNewFlashcardSetPanel()
            return newFlashcardSet

        } else {
            val errorMessage = checkingInfo.second + ""
            view.showInvalidFlashcardSetError(errorMessage)
            return null
        }

    }

    private fun checkFlashcardSetIsValid(currentSet: Deck): Pair<Boolean, String?> {
        for (userFCSet in userDeckList) {
            val hasANameInList = currentSet.name.trim() == userFCSet.name.trim()
            if (hasANameInList) {
                val errorMessage =
                    "The set \"${currentSet.name}\" has already existed, please choose another one"
                return Pair(false, errorMessage)
            }
        }
        return Pair(true, null)
    }

    private fun saveFlashcardAndUpdateUserInfo(
        newCard: Flashcard,
        front_IllustrationPicture: Bitmap?,
        back_IllustrationPicture: Bitmap?
    ) {

        newCard.cardProperty.apply {
            frontSideHasImage = front_IllustrationPicture != null
            backSideHasImage = back_IllustrationPicture != null
            frontSideHasText =
                newCard.text.isNotEmpty() || newCard.type.isNotEmpty() || newCard.pronunciation.isNotEmpty()
            backSideHasText =
                newCard.translation.isNotEmpty() || newCard.example.isNotEmpty() || newCard.meanOfExample.isNotEmpty()
        }

        if (front_IllustrationPicture != null) {
            //1600000000000: Sun Sep 13 2020 12:26:40 in milisecond
            newCard.frontIllustrationPictureName = "FR${System.currentTimeMillis() - 1600000000000}"
            illustrationLoader.saveFile(
                front_IllustrationPicture,
                newCard.frontIllustrationPictureName
            )
        }

        if (back_IllustrationPicture != null) {
            newCard.backIllustrationPictureName = "BA${System.currentTimeMillis() - 1600000000000}"
            illustrationLoader.saveFile(
                back_IllustrationPicture,
                newCard.backIllustrationPictureName
            )
        }

        addFlashcardExecutor.addFlashcardAndUpdateFlashcardSet(newCard) { isSuccess, insertedCardId, exception ->
            if (isSuccess) {
                view.onAddFlashcardSuccess()
                newCard.id = insertedCardId.toInt()
                updateUserInfo(newCard)
            } else {
                systemOutLogging("Storing this flashcard to local storage failed. Please check again")
                exception!!.printStackTrace()
            }
        }
    }

    private fun updateUserInfo(newCard : Flashcard) {
        userUsingHistoryRepos.addToRecent_AddedFlashcardList(newCard)
        if (newCard.type.isNotEmpty()) {
            addToUserOwnCardTypes(newCard.type)
        }
        updateUserLastedUsedFlashcardSet(newCard.setOwner)
    }

    private fun updateUserLastedUsedFlashcardSet (setName : String) {
        addFlashcardSharedPref.lastUsedFlashcardSetName = setName
        userRepos.updateUser(getUser())
    }

    fun getLastedUseFlashcardSetName () : String {
        return addFlashcardSharedPref.lastUsedFlashcardSetName
    }

    fun addToUsedLanguageList (language : String) {
        userUsingHistoryRepos.addToUsedLanguageList(language)
    }

    fun updateCurrentFrontLang (frontLanguage : String) {
        addFlashcardSharedPref.currentFrontCardLanguage = frontLanguage
        userRepos.updateUser(getUser())
    }

    private fun addToUserOwnCardTypes (cardType : String) {
        getUser().addToCardTypeList(cardType)
    }

    fun getUserOwnCardTypes() : ArrayList<String> {
        return getUser().ownCardTypeList
    }


    fun updateCurrentBackLanguage (backLanguage : String) {
        addFlashcardSharedPref.currentBackCardLanguage = backLanguage
        userRepos.updateUser(getUser())
    }


    fun getCurrentBackLanguage () : String {
        return addFlashcardSharedPref.currentBackCardLanguage
    }

    fun getCurrentFrontLanguage () : String {
        return addFlashcardSharedPref.currentFrontCardLanguage
    }


    fun getUsedLanguageList (onGet : (ArrayList<String>) -> Unit){
        userUsingHistoryRepos.getUsedLanguage(onGet)
    }


    fun getAllFlashcardSetWithNoCardList(onGet: (ArrayList<Deck>) -> Unit) {
        flashcardSetRepos.getAll_CardSet_WithNOCardList {
            if (it != null) {
                onGet.invoke(it)
                cachedFlashcardSetList(it)
            }
        }
    }

    fun getFlashcardSetByName(setName: String, onGetListener: (Deck?, Exception?) -> Unit) {
        flashcardSetRepos.getFlashcardSetByName(setName, onGetListener)
    }

    private fun cachedFlashcardSetList(deckList: ArrayList<Deck>) {
        if (this::userDeckList.isInitialized.not()) {
            this.userDeckList = deckList
        }
    }


    fun saveUsingHistory() {
        userUsingHistoryRepos.saveUsingHistoryInfo()
        userRepos.updateUser(getUser())
    }

    fun getDefaultFlashcardSet(): Deck {
        val frontLanguage = getCurrentFrontLanguage()
        val backLanguage = getCurrentBackLanguage()

        return Deck(
            SetNameUtils.getSetNameFromLangPair(frontLanguage, backLanguage),
            frontLanguage,
            backLanguage
        )
    }

}
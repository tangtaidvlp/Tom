package com.teamttdvlp.memolang.viewmodel

import android.graphics.Bitmap
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.data.model.entity.flashcard.SetNameUtils
import com.teamttdvlp.memolang.model.AddFlashcardExecutor
import com.teamttdvlp.memolang.model.IllustrationManager
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.model.repository.UserRepos
import com.teamttdvlp.memolang.model.repository.UserUsingHistoryRepos
import com.teamttdvlp.memolang.model.sharepref.BaseAppInfoSharedPreference
import com.teamttdvlp.memolang.view.activity.iview.AddFlashcardView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.log

class AddFlashCardViewModel(
    private var userRepos: UserRepos,
    private var addFlashcardExecutor: AddFlashcardExecutor,
    private var flashcardSetRepos: FlashcardSetRepos,
    private var userUsingHistoryRepos: UserUsingHistoryRepos,
    private var addFlashcardSharedPref: BaseAppInfoSharedPreference,
    private var illustrationManager: IllustrationManager
) : BaseViewModel<AddFlashcardView>() {

    private lateinit var userFlashcardSetList: ArrayList<FlashcardSet>

    lateinit var currentFocusFlashcardSet: FlashcardSet

    fun proceedAddFlashcard(newCard: Flashcard, illustrationPicture: Bitmap?) {

        if (newCard.setOwner == "") {
            newCard.setOwner = currentFocusFlashcardSet.name
        }

        if (newCard.text.isEmpty()) {
            view.showTextInputError()
            return
        }

        if (newCard.translation.isEmpty()) {
            view.showTranslationInputError()
            return
        }

        saveFlashcardAndUpdateUserInfo(newCard, illustrationPicture)
    }

    fun loadIllustrationPicture(name: String, onGet: (Exception?, Bitmap?) -> Unit) {
        illustrationManager.loadBitmap(name, onGet)
    }

    fun createNewFlashcardSetIfValid(
        setName: String,
        frontLanguage: String,
        backLanguage: String
    ): FlashcardSet? {
        val newFlashcardSet = FlashcardSet(setName, frontLanguage, backLanguage)
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

    private fun checkFlashcardSetIsValid (currentSet : FlashcardSet) : Pair<Boolean, String?> {
        for (userFCSet in userFlashcardSetList) {
            val hasANameInList = currentSet.name.trim() == userFCSet.name.trim()
            if (hasANameInList) {
                val errorMessage =
                    "The set \"${currentSet.name}\" has already existed, please choose another one"
                return Pair(false, errorMessage)
            }
        }
        return Pair(true, null)
    }

    private fun saveFlashcardAndUpdateUserInfo(newCard: Flashcard, illustrationPicture: Bitmap?) {
        if (illustrationPicture != null) {
            //1600000000000: Sun Sep 13 2020 12:26:40 in milisecond
            newCard.illustrationPictureName = "C${System.currentTimeMillis() - 1600000000000}"
            illustrationManager.saveFile(illustrationPicture, newCard.illustrationPictureName)
        }
        addFlashcardExecutor.addFlashcardAndUpdateFlashcardSet(newCard) { isSuccess, insertedCardId, exception ->
            if (isSuccess) {
                view.onAddFlashcardSuccess()
                newCard.id = insertedCardId.toInt()
                updateUserInfo(newCard)
            } else {
                log("Storing this flashcard to local storage failed. Please check again")
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


    fun getAllFlashcardSetWithNoCardList (onGet : (ArrayList<FlashcardSet>) -> Unit) {
        flashcardSetRepos.getAll_CardSet_WithNOCardList {
            if (it != null) {
                onGet.invoke(it)
                cachedFlashcardSetList(it)
            }
        }
    }

    fun getFlashcardSetByName (setName : String, onGetListener : (FlashcardSet?, Exception?) -> Unit) {
        flashcardSetRepos.getFlashcardSetByName(setName, onGetListener)
    }

    private fun cachedFlashcardSetList (flashcardSetList : ArrayList<FlashcardSet>) {
        if (this::userFlashcardSetList.isInitialized.not()) {
            this.userFlashcardSetList = flashcardSetList
        }
    }


    fun saveUsingHistory() {
        userUsingHistoryRepos.saveUsingHistoryInfo()
        userRepos.updateUser(getUser())
    }

    fun getDefaultFlashcardSet(): FlashcardSet {
        val frontLanguage = getCurrentFrontLanguage()
        val backLanguage = getCurrentBackLanguage()

        return FlashcardSet(
            SetNameUtils.getSetNameFromLangPair(frontLanguage, backLanguage),
            frontLanguage,
            backLanguage
        )
    }

}
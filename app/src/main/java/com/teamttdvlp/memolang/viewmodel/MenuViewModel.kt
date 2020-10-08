package com.teamttdvlp.memolang.viewmodel

import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.repository.FlashcardRepos
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.model.repository.UserUsingHistoryRepos
import com.teamttdvlp.memolang.model.sharepref.AddFlashcardActivitySharePref
import com.teamttdvlp.memolang.model.sharepref.EngVietDictionaryActivitySharePref
import com.teamttdvlp.memolang.model.sharepref.SearchOnlineActivitySharePref
import com.teamttdvlp.memolang.view.activity.iview.MenuView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.log

class MenuActivityViewModel(
    private var flashcardSetRepos: FlashcardSetRepos,
    private var flashcardRepos: FlashcardRepos,
    private var userUsingHistoryRepos: UserUsingHistoryRepos,
    private var addFlashcard_SharedPref: AddFlashcardActivitySharePref,
    private var searchOnline_SharedPref: SearchOnlineActivitySharePref,
    private var dictionaryActivity_SharedPref: EngVietDictionaryActivitySharePref
) : BaseViewModel<MenuView>() {

    private var userDeckList: ArrayList<Deck> = ArrayList()

    fun getAllFlashcardSets_And_CacheIt(onGetSuccess: (ArrayList<Deck>?) -> Unit) {
        flashcardSetRepos.getAllFlashcardWithCardList {
            onGetSuccess.invoke(it)
            if (it != null) {
                userDeckList = it
            }
        }
    }

    fun updateSetName(OLDName_Deck: Deck, newSetName: String) {
        val NEWName_flashcardSet = Deck(
            newSetName,
            OLDName_Deck.frontLanguage,
            OLDName_Deck.backLanguage
        )
        val newFlashcardSet = ArrayList<Flashcard>()
        for (flashcard in OLDName_Deck.flashcards) {
            val newFlashcard = flashcard.copy()
            newFlashcard.setOwner = newSetName
            newFlashcardSet.add(newFlashcard)
        }

        NEWName_flashcardSet.flashcards = newFlashcardSet
        flashcardSetRepos.insert(NEWName_flashcardSet)
        flashcardRepos.insertFlashcards(newFlashcardSet) { isSuccess: Boolean, ex: Exception? ->
            if (isSuccess.not()) {
                throw java.lang.Exception("Insert Flashcard failed. MenuViewModel.kt")
            }
        }

    }

    fun update_OtherActivitiesSharePref_Info(oldSetName: String, newSetName: String) {
        if (addFlashcard_SharedPref.lastUsedFlashcardSetName == oldSetName) {
            addFlashcard_SharedPref.lastUsedFlashcardSetName = newSetName
        }

        if (searchOnline_SharedPref.lastUsedFlashcardSetName == oldSetName) {
            searchOnline_SharedPref.lastUsedFlashcardSetName = newSetName
        }

        if (dictionaryActivity_SharedPref.lastUsedFlashcardSetName == oldSetName) {
            dictionaryActivity_SharedPref.lastUsedFlashcardSetName = newSetName
        }

    }

    fun set_OtherActivitiesSharePref_Info(setName: String) {
        addFlashcard_SharedPref.lastUsedFlashcardSetName = setName

        searchOnline_SharedPref.lastUsedFlashcardSetName = setName

        dictionaryActivity_SharedPref.lastUsedFlashcardSetName = setName

    }

    fun deleteFlashcardSet(deck: Deck) {
        try {
            flashcardSetRepos.deleteFlashcardSet(deck)
            flashcardRepos.deleteCards(deck.flashcards)
            userDeckList.remove(deck)
            if (deck.name == addFlashcard_SharedPref.lastUsedFlashcardSetName) {
                if (userDeckList.size != 0)
                    set_OtherActivitiesSharePref_Info(userDeckList.first().name)
                else
                    set_OtherActivitiesSharePref_Info("")
            }
        } catch (ex: java.lang.Exception) {
            log("Error happens")
            ex.printStackTrace()
        }


    }

    fun updateHistory() {

    }

    fun getCurrentUse_FrontLanguage(): String {
        return addFlashcard_SharedPref.currentFrontCardLanguage
    }

    fun getCurrentUse_BackLanguage(): String {
        return addFlashcard_SharedPref.currentBackCardLanguage
    }

    fun createNewFlashcardSetIfValid(
        setName: String,
        frontLanguage: String, backLanguage: String
    ): Deck {
        val newFlashcardSet = Deck(setName, frontLanguage, backLanguage)
        val checkingInfo: Pair<Boolean, String?> = checkFlashcardSetIsValid(newFlashcardSet)
        val setIsValid = checkingInfo.first
        if (setIsValid) {
            flashcardSetRepos.insert(newFlashcardSet)
            view.hideCreateNewFlashcardSetPanel()
        } else {
            val errorMessage = checkingInfo.second + ""
            view.showInvalidFlashcardSetError(errorMessage)
        }

        return newFlashcardSet
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

    fun getUsedLanguageList(onGet: (ArrayList<String>) -> Unit) {
        userUsingHistoryRepos.getUsedLanguage(onGet)
    }

}
package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.dictionary.model.Vocabulary
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.data.model.entity.flashcard.SetNameUtils
import com.teamttdvlp.memolang.data.model.entity.language.Language
import com.teamttdvlp.memolang.data.model.entity.user.User
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import com.teamttdvlp.memolang.model.AddFlashcardExecutor
import com.teamttdvlp.memolang.model.TextSpeaker
import com.teamttdvlp.memolang.model.VocabularyConverter
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.model.repository.UserRepos
import com.teamttdvlp.memolang.model.repository.UserUsingHistoryRepos
import com.teamttdvlp.memolang.model.sharepref.EngVietDictionaryActivitySharePref
import com.teamttdvlp.memolang.view.activity.iview.FloatAddServiceView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.log
import javax.inject.Inject

class FloatAddServiceViewModel
@Inject
constructor(
    private var app: Application,
    private var userRepos: UserRepos,
    private var addFlashcardExecutor: AddFlashcardExecutor,
    private var flashcardSetRepos: FlashcardSetRepos,
    private var userUsingHistoryRepos: UserUsingHistoryRepos,
    private val engVietDictionaryActivity_SharePref: EngVietDictionaryActivitySharePref
) : BaseViewModel<FloatAddServiceView>() {


    private lateinit var userFlashcardSetList: ArrayList<FlashcardSet>

    private val vocabularyConverter: VocabularyConverter = VocabularyConverter()

    var vocabulary: MutableLiveData<Vocabulary> = MutableLiveData()
        private set

    private val textSpeaker = TextSpeaker(app, Language.ENGLISH_VALUE)

    companion object {
        var user: User? = null
    }

    init {
        flashcardSetRepos.getAll_CardSet_WithNOCardList { cardSetList ->
            userFlashcardSetList = if (cardSetList != null) {
                cardSetList
            } else {
                                                     ArrayList<FlashcardSet>()
                                                 }
        }
        userRepos.triggerGetUser {
            user = it
        }
    }

    // SEARCH DICTIONARY FUNCTIONS

    fun getAll_RecentSearchedVocaList (onGetAllSearchHistory : (ArrayList<TypicalRawVocabulary>) -> Unit) {
        userUsingHistoryRepos.getRecent_SearchedVocabularyList (onGetAllSearchHistory)
    }

    fun addVoca_ToRecentSearchedList (rawVocabulary: TypicalRawVocabulary) {
        userUsingHistoryRepos.addToRecent_SearchedVocabularyList(rawVocabulary)
    }

    fun saveSearchingHistory () {
        userUsingHistoryRepos.saveUsingHistoryInfo()
    }

    fun convertToVocabularyAndSendToObserver (holder: TypicalRawVocabulary) {
        val vocabulary = vocabularyConverter.convertToVocabulary(holder)
        sendVocabularyToObserver(vocabulary)
    }

    private fun sendVocabularyToObserver (vocabulary : Vocabulary) {
        this.vocabulary.value = vocabulary
    }


    // ADD FLASHCARD FUNCTIONS

    private fun checkFlashcardSetIsValid (currentSet : FlashcardSet) : Boolean {
        for (userFCSet in userFlashcardSetList) {
            val hasANameInList = currentSet.name.trim() == userFCSet.name.trim()
            if (hasANameInList) {
                val hasDifferentLanguages = (currentSet.frontLanguage != userFCSet.frontLanguage) or (currentSet.backLanguage != userFCSet.backLanguage)
                if (hasDifferentLanguages) {
                    val invalidLangPair = SetNameUtils.getLanguagePairForm(
                        currentSet.frontLanguage,
                        currentSet.backLanguage
                    )
                    val validLangPair = SetNameUtils.getLanguagePairForm(
                        userFCSet.frontLanguage,
                        userFCSet.backLanguage
                    )
                    val errorMessage = "You can not add $invalidLangPair card to $validLangPair set"
                    view.showInvalidFlashcardSetError(errorMessage)
                    return false
                }
            }
        }
        return true
    }

    fun proceedAddFlashcard(newCard: Flashcard) {
        val flashcardSet =
            FlashcardSet(newCard.setOwner, newCard.frontLanguage, newCard.backLanguage)
        val setIsValid = checkFlashcardSetIsValid(flashcardSet)
        if (setIsValid.not()) {
            return
        }

        if ((newCard.text == "") or (newCard.text == null)) {
            view.showTextInputError()
            return
        }

        if ((newCard.translation == "") or (newCard.translation == null)) {
            view.showTranslationInputError()
            return
        }

        addFlashcard_And_UpdateUserInfo(newCard)
    }

    private fun addFlashcard_And_UpdateUserInfo (newCard: Flashcard) {
        addFlashcardExecutor.addFlashcardAndUpdateFlashcardSet (newCard) { isSuccess, insertedCardId, exception ->
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

    private fun updateUserInfo(newCard: Flashcard) {
        userUsingHistoryRepos.addToRecent_AddedFlashcardList(newCard)
        updateLastUsedFlashcardSetName(newCard.setOwner)
        addToUserOwnCardTypes(newCard.type)
    }

    fun updateLastUsedFlashcardSetName(setName: String) {
        engVietDictionaryActivity_SharePref.lastUsedFlashcardSetName = setName
    }

    private fun addToUserOwnCardTypes(cardType: String) {
        getUser().addToCardTypeList(cardType)
    }

    fun addToUsedLanguageList(language: String) {
        userUsingHistoryRepos.addToUsedLanguageList(language)
    }

    fun getLastedUseFlashcardSetName () : String {
        return engVietDictionaryActivity_SharePref.lastUsedFlashcardSetName
    }

    fun getUserOwnCardTypes() : ArrayList<String> {
        return getUser().ownCardTypeList
    }


    fun getProcessedLanguageList (onGet : (ArrayList<String>) -> Unit) {
        userUsingHistoryRepos.getUsedLanguage { usedLanguageLists ->
            val processedLanguageList = ArrayList<String>().apply {
                addAll(Language.languageList)
            }
            bringUsedLanguagesOnTop(fullLanguageList = processedLanguageList, usedLanguageList = usedLanguageLists)
            onGet.invoke(processedLanguageList)
        }
    }

    private fun bringUsedLanguagesOnTop (fullLanguageList : ArrayList<String>, usedLanguageList : ArrayList<String>) {
        for (usedLang in usedLanguageList) {
            fullLanguageList.remove(usedLang)
        }

        for (usedLang in usedLanguageList) {
            fullLanguageList.add(0, usedLang)
        }
    }

    fun getAllFlashcardSetWithNoCardList (onGet : (ArrayList<FlashcardSet>) -> Unit) {
        flashcardSetRepos.getAll_CardSet_WithNOCardList {
            if (it != null) {
                onGet.invoke(it)
                cachedFlashcardSetList(it)
            }
        }
    }

    private fun cachedFlashcardSetList(flashcardSetList: ArrayList<FlashcardSet>) {
        if (this::userFlashcardSetList.isInitialized.not()) {
            this.userFlashcardSetList = flashcardSetList
        }
    }

    fun speak(text: String) {
        textSpeaker.speak(text)
    }

    fun saveUsingHistory() {
        userUsingHistoryRepos.saveUsingHistoryInfo()
        userRepos.updateUser(getUser())
    }

    fun setView(view: FloatAddServiceView) {
        this.view = view
    }


}
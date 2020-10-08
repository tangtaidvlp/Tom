package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.dictionary.model.Vocabulary
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.language.Language.Companion.ENGLISH_VALUE
import com.teamttdvlp.memolang.data.model.entity.language.Language.Companion.VIETNAMESE_VALUE
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import com.teamttdvlp.memolang.model.AddFlashcardExecutor
import com.teamttdvlp.memolang.model.TextSpeaker
import com.teamttdvlp.memolang.model.VocabularyConverter
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.model.repository.UserRepos
import com.teamttdvlp.memolang.model.repository.UserUsingHistoryRepos
import com.teamttdvlp.memolang.model.sharepref.EngVietDictionaryActivitySharePref
import com.teamttdvlp.memolang.view.activity.iview.SeeVocabularyView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.log
import java.util.*

class EngVietDictionaryActivityViewModel(
    app: Application,
    private var userRepos: UserRepos,
    private var flashcardSetRepos: FlashcardSetRepos,
    private var addFlashcardExecutor: AddFlashcardExecutor,
    private var userUsingHistoryRepos: UserUsingHistoryRepos,
    private var dictionaryActivity_SharePref: EngVietDictionaryActivitySharePref
) : BaseViewModel<SeeVocabularyView>() {

    private val vocabulary = MutableLiveData<Vocabulary>()

    private val textSpeaker = TextSpeaker(app, ENGLISH_VALUE)

    private val vocabularyConverter: VocabularyConverter = VocabularyConverter()

    fun getVocabulary(): MutableLiveData<Vocabulary> = vocabulary

    fun getAll_RecentSearchedVocaList(onGetAllSearchHistory: (ArrayList<TypicalRawVocabulary>) -> Unit) {
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

    private fun sendVocabularyToObserver (vocabulary: Vocabulary) {
        this.vocabulary.value = vocabulary
    }

    fun speak (text : String) {
        textSpeaker.speak(text)
    }

    fun proceedAddFlashcard(newCard: Flashcard) {

        addFlashcardExecutor.addFlashcardAndUpdateFlashcardSet(newCard) { isSuccess, insertedCardId, exception ->
            if (isSuccess) {
                view.onAddFlashcardSuccess()
                newCard.id = insertedCardId.toInt()
                addToRecentAddedFlashcardList(newCard)
                userUsingHistoryRepos.addToRecent_AddedFlashcardList(newCard)
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

    private fun addToRecentAddedFlashcardList (newCard: Flashcard) {
        userUsingHistoryRepos.addToRecent_AddedFlashcardList(newCard)
    }

    private fun updateUserLastedUsedFlashcardSet (setName : String) {
        dictionaryActivity_SharePref.lastUsedFlashcardSetName = setName
        userRepos.updateUser(getUser())
    }

    fun getLastedUseFlashcardSetName () : String {
        return dictionaryActivity_SharePref.lastUsedFlashcardSetName
    }

    private fun addToUserOwnCardTypes (cardType : String) {
        getUser().addToCardTypeList(cardType)
    }

    fun stopAllTextSpeaker() {
        textSpeaker.shutDown()
    }

    fun getAllEnglishVNFlashcardSets(onGet: (ArrayList<Deck>) -> Unit) {
        flashcardSetRepos.getAll_CardSet_WithNOCardList { flashcardSetList ->
            if (flashcardSetList != null) {
                val engVNCardSetList = ArrayList<Deck>()
                for (set in flashcardSetList) {
                    if ((set.frontLanguage == ENGLISH_VALUE) and (set.backLanguage == VIETNAMESE_VALUE)) {
                        engVNCardSetList.add(set)
                    }
                }
                onGet.invoke(engVNCardSetList)
            }
        }
    }
}

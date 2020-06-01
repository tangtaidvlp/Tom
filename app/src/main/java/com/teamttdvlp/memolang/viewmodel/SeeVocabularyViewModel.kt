package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.dictionary.model.TransAndExamp
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.Using
import com.example.dictionary.model.Vocabulary
import com.teamttdvlp.memolang.model.TextSpeaker
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.data.model.entity.language.Language.Companion.ENGLISH_VALUE
import com.teamttdvlp.memolang.data.model.entity.language.Language.Companion.VIETNAMESE_VALUE
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.SingleMeanExample
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import com.teamttdvlp.memolang.data.model.other.vocabulary.MultiMeanExample
import com.teamttdvlp.memolang.model.AddFlashcardExecutor
import com.teamttdvlp.memolang.model.VocabularyConverter
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.model.repository.UserRepos
import com.teamttdvlp.memolang.model.repository.UserUsingHistoryRepos
import com.teamttdvlp.memolang.view.activity.iview.SeeVocabularyView
import com.teamttdvlp.memolang.view.adapter.RCVSearchDictionaryAdapter
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.clearAll
import com.teamttdvlp.memolang.view.helper.foreachFromSecondElement
import com.teamttdvlp.memolang.view.helper.quickLog
import java.lang.Exception
import java.util.*

const val NAVIGATION_TAG = "<nav>"

const val TEXT = 0
const val PRONUNCIATION = 1
const val TYPE = 2
const val USING = 3
const val USINGS = 2
const val NONE = "_"

const val DETAIL_TYPE = 0
const val DETAIL_TRANSLATION = 1

const val GROUP_MEAN = 0
const val GROUP_EXAMPLES = 1

const val EXAMPLE_TEXT = 0
const val EXAMPLE_MEAN = 1

const val USINGS_DIVIDER = "]["
const val EXAMPLES_DEVIDER = "<exd>"
const val MEAN_AND_EXAMPLE_GROUPS_DIVIDER = "<md>"
const val MEAN_AND_EXAMPLE_JOINT= "<ex>"
const val MEAN_AND_EXAMPLE_DEVIDER= ":"
const val PARTS_DEVIDER = "<pd>"
const val USING_DETAILS_JOINT= "/"
const val NAVIGATE_TAG = "<nav>"

class SeeVocabularyActivityViewModel (
                                    var app : Application,
                                    var userRepos: UserRepos,
                                    var flashcardSetRepos: FlashcardSetRepos,
                                    var addFlashcardExecutor: AddFlashcardExecutor,
                                    var userUsingHistoryRepos: UserUsingHistoryRepos) : BaseViewModel<SeeVocabularyView>() {

    private val vocabulary = MutableLiveData<Vocabulary>()

    private val textSpeaker = TextSpeaker(app, ENGLISH_VALUE)

    private val vocabularyConverter : VocabularyConverter = VocabularyConverter()

    fun getVocabulary () : MutableLiveData<Vocabulary> = vocabulary

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

    private fun sendVocabularyToObserver (vocabulary: Vocabulary) {
        this.vocabulary.value = vocabulary
    }

    fun speak (text : String) {
        textSpeaker.speak(text)
    }

    fun addFlashcard (frontLanguage : String, backLanguage : String, setName : String,
                      type : String, text : String,
                      translation : String, example : String, meanExample : String, pronunciation : String) {

        val newCard = Flashcard(id = 0, text = text, translation = translation,
            frontLanguage = frontLanguage, backLanguage = backLanguage,
            setOwner = setName, type = type, example = example, meanOfExample = meanExample,
            pronunciation = pronunciation)

        addFlashcardExecutor.addFlashcardAndUpdateFlashcardSet(newCard) { isSuccess, insertedCardId, exception ->
            if (isSuccess) {
                view.onAddFlashcardSuccess()
                newCard.id = insertedCardId.toInt()
                addToRecentAddedFlashcardList(newCard)
                userUsingHistoryRepos.addToRecent_AddedFlashcardList(newCard)
                updateUserInfo(newCard)
            } else {
                quickLog("Storing this flashcard to local storage failed. Please check again")
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
        getUser().lastest_Used_FlashcardSetName = setName
        userRepos.updateUser(getUser())
    }

    fun getLastedUseFlashcardSetName () : String {
        return getUser().lastest_Used_FlashcardSetName
    }

    private fun addToUserOwnCardTypes (cardType : String) {
        getUser().addToCardTypeList(cardType)
    }

    fun stopAllTextSpeaker() {
        textSpeaker.shutDown()
    }

    fun getAllEnglishVNFlashcardSets(onGet : (ArrayList<FlashcardSet>) -> Unit) {
        flashcardSetRepos.getAll_CardSet_WithNOCardList { flashcardSetList ->
            if (flashcardSetList != null) {
                val engVNCardSetList = ArrayList<FlashcardSet>()
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

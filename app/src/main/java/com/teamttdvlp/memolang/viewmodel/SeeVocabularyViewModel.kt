package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.teamttdvlp.memolang.model.TextSpeaker
import com.teamttdvlp.memolang.model.entity.*
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.database.sql.repository.UserSearchHistoryRepository
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.entity.vocabulary.Example
import com.teamttdvlp.memolang.model.entity.vocabulary.TransAndExamp
import com.teamttdvlp.memolang.model.entity.vocabulary.Using
import com.teamttdvlp.memolang.model.entity.vocabulary.Vocabulary
import com.teamttdvlp.memolang.view.activity.iview.SeeVocabularyView
import com.teamttdvlp.memolang.view.base.BaseAndroidViewModel
import com.teamttdvlp.memolang.view.helper.quickLog
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
const val PARTS_DEVIDER = "<pd>"
const val USING_DETAILS_JOINT= "/"
const val NAVIGATE_TAG = "<nav>"

class SeeVocabularyActivityViewModel (var app : Application, var userSearchHistoryRepos: UserSearchHistoryRepository, var flashcardRepository: FlashcardRepository) : BaseAndroidViewModel<SeeVocabularyView>(app) {

    private val vocabulary = MutableLiveData<Vocabulary>()

    private val textSpeaker = TextSpeaker(app, Language.ENGLISH_VALUE)

    fun getVocabulary () : MutableLiveData<Vocabulary> = vocabulary

    fun getVocabularyFromVocaInfo (vocaInfo : String) {
        val item : Vocabulary
        val vocaParts = vocaInfo.split(PARTS_DEVIDER)

        val vocaHasManyUsings = vocaInfo.contains(USINGS_DIVIDER)
        val vocaHasOnlyOneUsing = !vocaHasManyUsings
        var usingList : List<String>? = null

        if (vocaHasManyUsings) {
            val usingsArray = vocaParts.get(USINGS).split(USINGS_DIVIDER)
            usingList = List<String>(usingsArray.size) { index ->
                return@List usingsArray.get(index).replace(
                    USING_DETAILS_JOINT,
                    PARTS_DEVIDER
                )
            }
        } else if (vocaHasOnlyOneUsing) {
            val using = vocaParts.get(TYPE) + PARTS_DEVIDER + vocaParts.get(
                USING
            )
            usingList = List<String>(1) { using }
        }

        val item_Usings = decodeUsingArray(usingList!!)
        val item_text = vocaParts.get(TEXT)
        val item_pronunciation = vocaParts.get(PRONUNCIATION)
        item = Vocabulary(
            item_text,
            item_pronunciation,
            item_Usings
        )

        sendVocabularyToObserver(item)
    }

    fun speak (text : String) {
        textSpeaker.speak(text)
    }

    private fun sendVocabularyToObserver (voca : Vocabulary) {
        vocabulary.value = voca
    }

    private fun decodeUsingArray(usingArray: List<String>): Array<Using>? {
        val vocabularyUsings = Array<Using>(usingArray.size) { index ->

            val using = usingArray.get(index)
            val usingDetails = using.split(PARTS_DEVIDER)
            val detailType = when(usingDetails.get(DETAIL_TYPE)) {
                "V" -> "Nội động từ"
                "TransV" -> "Ngoại động từ"
                "Adj" -> "Tính từ"
                "Acr" -> "Từ viết tắt"
                "N" -> "Danh từ"
                "Adv" -> "Trạng từ"
                else -> usingDetails.get(DETAIL_TYPE)
            }
            val detailTrans = usingDetails.get(DETAIL_TRANSLATION)

            val meanAndExGroupArray = detailTrans.split(MEAN_AND_EXAMPLE_GROUPS_DIVIDER)
            val item_MeanAndExGroupArray = Array<TransAndExamp>(meanAndExGroupArray.size) { index ->
                val meanAndExGroup = meanAndExGroupArray[index]

                val groupDetail = meanAndExGroup.split(MEAN_AND_EXAMPLE_JOINT)
                val mean = groupDetail.get(GROUP_MEAN)
                val hasExample = (groupDetail.size == 2)

                val meanAndExample =
                    TransAndExamp(mean)
                if (hasExample) {
                    val examples = groupDetail.get(GROUP_EXAMPLES)
                    val exampleArray = examples.split(EXAMPLES_DEVIDER)
                    // Create Examples Array
                    meanAndExample.examples = Array<Example>(exampleArray.size) { index ->
                        val examInfo = exampleArray.get(index).split(":")
                        val examHasMean = (examInfo.size == 2)
                        val examText = examInfo.get(EXAMPLE_TEXT)
                        val examMean = if (examHasMean) "   " + examInfo.get(EXAMPLE_MEAN)
                                                   else ""
                        return@Array Example(
                            examText,
                            examMean
                        )
                    }
                }
                return@Array meanAndExample
            }
            return@Array Using(
                detailType,
                item_MeanAndExGroupArray
            )
        }
        return vocabularyUsings
    }

    fun addFlashcardToOfflineDB (newCard : Flashcard, onInsertListener : (isSuccess : Boolean, cardId : Long, ex : Exception?) -> Unit) {
        flashcardRepository.insertFlashcard(newCard, onInsertListener)
    }

    fun addFlashcard (sourceLang : String, targetLang : String, type : String, text : String, translation : String, using : String, pronunciation : String) {
        val langInfo = "$sourceLang-$targetLang"
        val newCard = Flashcard(
            0,
            text,
            translation,
            langInfo,
            using,
            "",
            type,
            pronunciation
        )
        addFlashcardToOfflineDB(newCard) { isSuccess, insertedCardId, exception ->
            if (isSuccess) {
                view.onAddFlashcardSuccess()
                newCard.id = insertedCardId.toInt()
                addSearchedCardToHistory(newCard.id, newCard.createdAt)
            } else {
                quickLog("Storing this flashcard to local storage failed. Please check again")
                exception!!.printStackTrace()
            }
        }
    }

    fun addSearchedCardToHistory (cardId : Int, searchedTime : Date) {
        userSearchHistoryRepos.addNewCard(cardId, searchedTime)
    }
}

package com.teamttdvlp.memolang.viewmodel

import androidx.databinding.ObservableField
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.view.activity.iview.EditFlashcardView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.quickLog
//import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager
import java.lang.Exception

class EditFlashcardViewModel
    (/*var onlineFlashcardDBManager: OnlineFlashcardDBManager, */var flashcardRepository
: FlashcardRepository) : BaseViewModel<EditFlashcardView>() {

    private lateinit var originalCard : Flashcard

    private var bindOGCard = ObservableField<Flashcard>()

    fun setOriginalCard (flashcard : Flashcard) {
        this.originalCard = flashcard
        bindOGCard.set(flashcard)
    }

    fun updateCard (sourceLang : String, targetLang : String,
                    setName : String, type : String,
                    text : String, translation : String,
                    example : String, meanOfExample : String,  pronunciation : String) {

        quickLog("SOURCE LANG: '$sourceLang'")
        quickLog("TARGET LANG: '$targetLang'")
        val langPair = sourceLang + " - " + targetLang
        val newFlashcard = Flashcard(originalCard.id, text, translation,
            langPair, setName, example, meanOfExample,
            "", type, pronunciation, originalCard.createdAt)

        if (doesUserChangeInfo(newFlashcard)) {
            if (text.isEmpty()) {
                view.showTextInputError()
                return
            }

            if (translation.isEmpty()) {
                view.showTranslationInputError()
                return
            }

            updateOfflineFlashcard(newFlashcard) { isSuccess, insertedCardId, ex ->
                if (isSuccess) {
                    newFlashcard.id = insertedCardId.toInt()
                    view.onUpdateFlashcardSuccess(newFlashcard)
                } else {
                    quickLog("Update flashcard in offline database failed. Please try again")
                }
            }
        } else {
            view.endEditing()
        }
    }

    fun doesUserChangeInfo (newFlashcard : Flashcard) : Boolean {
        quickLog("Does change info: ${originalCard != newFlashcard}")
        quickLog("id: ${originalCard.id == newFlashcard.id}")
        quickLog("text: ${originalCard.text == newFlashcard.text}")
        quickLog("translation: ${originalCard.translation == newFlashcard.translation}")
        quickLog("createdAt: ${originalCard.createdAt == newFlashcard.createdAt}")
        quickLog("example: ${originalCard.example == newFlashcard.example}")
        quickLog("exampleMean: ${originalCard.exampleMean == newFlashcard.exampleMean}")
        quickLog("type: ${originalCard.type == newFlashcard.type}")
        quickLog("pronunciation: ${originalCard.pronunciation == newFlashcard.pronunciation}")
        quickLog("setName OG: ${originalCard.setName} vs New: ${newFlashcard.setName}: ${originalCard.setName == newFlashcard.setName}")
        quickLog("languagePair: OG: ${originalCard.languagePair} vs New : ${newFlashcard.languagePair} ${originalCard.languagePair == newFlashcard.languagePair}")
        quickLog("synonym: ${originalCard.synonym == newFlashcard.synonym}")
        return originalCard != newFlashcard
    }

    fun updateOfflineFlashcard (flashcard : Flashcard, onInsertListener : (Boolean, Long, Exception?) -> Unit) {
        flashcardRepository.updateFlashcard(flashcard, onInsertListener)
    }

    fun getBindOGCard () : ObservableField<Flashcard> = bindOGCard


//    fun updateOnlineFlashcard (flashcardSetId : String, oldCard : Flashcard, newCard : Flashcard, onInsertListener : (Boolean, Exception?) -> Unit) {
//        onlineFlashcardDBManager.updateFlashcard(getSingletonUser()!!.id, flashcardSetId, oldCard, newCard, onInsertListener)
//    }

}
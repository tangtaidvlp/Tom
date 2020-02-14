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

    fun updateCard (sourceLang : String, targetLang : String, type : String, text : String, translation : String, using : String, pronunciation : String) {
        if (doesUserChangeInfo(sourceLang+ "-" + targetLang, type, text, translation, using, pronunciation)) {
            if ((text == "") or (text == null)) {
                view.showTextInputError()
                return
            }

            if ((translation == "") or (translation == null)) {
                view.showTranslationInputError()
                return
            }

            val languagePair = "$sourceLang-$targetLang"
            val newCard = Flashcard(
                originalCard.id,
                text,
                translation,
                languagePair,
                using,
                "",
                type,
                pronunciation
            )
            updateOfflineFlashcard(newCard) { isSuccess, insertedCardId, ex ->
                if (isSuccess) {
                    newCard.id = insertedCardId.toInt()
                    view.onUpdateFlashcardSuccess(newCard)
                } else {
                    quickLog("Update flashcard in offline database failed. Please try again")
                }
            }
        }
    }

    fun doesUserChangeInfo (newLanguagePair: String, newType : String, newText : String, newTranslation : String, newUsing : String, newPronunciation : String) : Boolean {
         val isNewLanguagePair = originalCard.languagePair != newLanguagePair
         val isNewType = originalCard.type != newType
         val isNewText = originalCard.text != newText
         val isNewTranslation = originalCard.translation != newTranslation
         val isNewUsing = originalCard.using != newUsing
         val isNewPronunciation = originalCard.pronunciation!= newPronunciation
         return isNewLanguagePair || isNewType || isNewText || isNewTranslation || isNewUsing || isNewPronunciation
    }

    fun updateOfflineFlashcard (flashcard : Flashcard, onInsertListener : (Boolean, Long, Exception?) -> Unit) {
        flashcardRepository.updateFlashcard(flashcard, onInsertListener)
    }

    fun getBindOGCard () : ObservableField<Flashcard> = bindOGCard


//    fun updateOnlineFlashcard (flashcardSetId : String, oldCard : Flashcard, newCard : Flashcard, onInsertListener : (Boolean, Exception?) -> Unit) {
//        onlineFlashcardDBManager.updateFlashcard(getSingletonUser()!!.id, flashcardSetId, oldCard, newCard, onInsertListener)
//    }

}
package com.teamttdvlp.memolang.viewmodel

import androidx.databinding.ObservableField
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.model.AddFlashcardExecutor
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.view.activity.iview.EditFlashcardView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.quickLog
//import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager
import java.lang.Exception

class EditFlashcardViewModel (
    var addFlashcardExecutor: AddFlashcardExecutor,
    var flashcardSetRepos: FlashcardSetRepos) : BaseViewModel<EditFlashcardView>() {

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

        val newFlashcard = Flashcard(id = originalCard.id,
            text = text, translation = translation,
            frontLanguage = sourceLang, backLanguage = targetLang,
            setOwner = setName, example = example, meanOfExample = meanOfExample,
            type = type, pronunciation = pronunciation)

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
        newFlashcard.id = originalCard.id
        return originalCard != newFlashcard
    }

    private fun updateOfflineFlashcard (flashcard : Flashcard, onInsertListener : (Boolean, Long, Exception?) -> Unit) {
        addFlashcardExecutor.addFlashcardAndUpdateFlashcardSet(flashcard, onInsertListener)
    }

    fun getAll_SameLanguagesFCSet_WithNoCardList (frontLang : String, backLang : String, onGet : (ArrayList<FlashcardSet>) -> Unit) {
        flashcardSetRepos.getAll_CardSet_WithNOCardList {
            if (it != null) {
                val result = ArrayList<FlashcardSet>()
                for (set in it) {
                    if ((set.frontLanguage == frontLang) and (set.backLanguage == backLang))  {
                        result.add(set)
                    }
                }
                onGet.invoke(result)
            }
        }
    }

    fun getBindOGCard () : ObservableField<Flashcard> = bindOGCard

}
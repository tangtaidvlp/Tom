package com.teamttdvlp.memolang.viewmodel.add_flashcard

import com.teamttdvlp.memolang.model.sqlite.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.model.sqlite.repository.FlashcardRepository
import com.teamttdvlp.memolang.view.activity.iview.AddFlashcardView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.quickLog
import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager

class AddFlashCardActivityViewModel(var database : MemoLangSqliteDataBase, var onlineFlashcardDBManager: OnlineFlashcardDBManager, var flashcardRepository: FlashcardRepository) : BaseViewModel() {


    private lateinit var view : AddFlashcardView


    fun addFlashcardToOfflineDB (newCard : Flashcard, onInsertListener : (isSuccess : Boolean, cardId : Long, ex : Exception?) -> Unit) {
        flashcardRepository.insertFlashcard(newCard, onInsertListener)
    }

    fun addFlashcardToOnlineDB (flashcardSetId : String, flashcard : Flashcard, onInsertListener : (isSuccess : Boolean, ex : Exception?) -> Unit) {
        onlineFlashcardDBManager.writeFlashcard(getSingletonUser()!!.id, flashcardSetId, flashcard, onInsertListener)
    }

    fun checkIfFlashcardExisInOfflineDatabase (flashCard : Flashcard) {

    }

    fun setView (view : AddFlashcardView) {
        this.view = view
    }

    fun addFlashcard (sourceLang : String, targetLang : String, text : String, translation : String, using : String) {
        val type = "$sourceLang-$targetLang"
        val newCard = Flashcard(0, text, translation, type, using)
        addFlashcardToOfflineDB(newCard) { isSuccess, insertedCardId, exception ->
            if (isSuccess) {
                view.onAddFlashcardSuccess()
                newCard.id = insertedCardId.toInt()
                val flashcardSetId = type
                addFlashcardToOnlineDB(flashcardSetId, newCard) { isSuccess, exception ->
                    if (isSuccess) {
                        quickLog("Storing this flashcard to local online storage success")
                    } else {
                        quickLog("Storing this flashcard to local online storage failed")
                        exception!!.printStackTrace()
                    }
                }
            } else {
                quickLog("Storing this flashcard to local storage failed. Please check again")
                exception!!.printStackTrace()
            }
        }
    }

}
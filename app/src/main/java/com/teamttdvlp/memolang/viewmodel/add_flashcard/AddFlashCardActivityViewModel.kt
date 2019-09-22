package com.teamttdvlp.memolang.viewmodel.add_flashcard

import com.teamttdvlp.memolang.model.sqlite.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.model.sqlite.repository.FlashcardRepository
import com.teamttdvlp.memolang.view.activity.base.BaseViewModel
import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager

class AddFlashCardActivityViewModel(var database : MemoLangSqliteDataBase, var onlineFlashcardDBManager: OnlineFlashcardDBManager, var flashcardRepository: FlashcardRepository) : BaseViewModel() {

    fun addFlashcardToOfflineDB (newCard : Flashcard, onInsertListener : (isSuccess : Boolean, cardId : Long, ex : Exception?) -> Unit) {
        flashcardRepository.insertFlashcard(newCard, onInsertListener)
    }

    fun addFlashcardToOnlineDB (flashcardSetId : String, flashcard : Flashcard, onInsertListener : (isSuccess : Boolean, ex : Exception?) -> Unit) {
        onlineFlashcardDBManager.writeFlashcard(getSingletonUser()!!.id, flashcardSetId, flashcard, onInsertListener)
    }

    fun checkIfFlashcardExisInOfflineDatabase (flashCard : Flashcard) {

    }

}
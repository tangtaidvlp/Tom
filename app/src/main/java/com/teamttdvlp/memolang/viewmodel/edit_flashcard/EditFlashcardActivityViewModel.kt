package com.teamttdvlp.memolang.viewmodel.edit_flashcard

import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.model.sqlite.repository.FlashcardRepository
import com.teamttdvlp.memolang.view.activity.base.BaseViewModel
import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager
import java.lang.Exception

class EditFlashcardActivityViewModel (var onlineFlashcardDBManager: OnlineFlashcardDBManager, var flashcardRepository : FlashcardRepository) : BaseViewModel() {

    fun updateOfflineFlashcard (flashcard : Flashcard, onInsertListener : (Boolean, Long, Exception?) -> Unit) {
        flashcardRepository.updateFlashcard(flashcard, onInsertListener)
    }

    fun updateOnlineFlashcard (flashcardSetId : String, oldCard : Flashcard, newCard : Flashcard, onInsertListener : (Boolean, Exception?) -> Unit) {
        onlineFlashcardDBManager.updateFlashcard(getSingletonUser()!!.id, flashcardSetId, oldCard, newCard, onInsertListener)
    }

}
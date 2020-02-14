package com.teamttdvlp.memolang.viewmodel

import androidx.databinding.ObservableField
import com.teamttdvlp.memolang.database.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.entity.User
import com.teamttdvlp.memolang.database.sql.entity.user.UserConverter
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.database.sql.repository.UserRepository
import com.teamttdvlp.memolang.view.activity.iview.AddFlashcardView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.quickLog
//import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager

class AddFlashCardViewModel(var database : MemoLangSqliteDataBase, var userRepository: UserRepository, var flashcardRepository: FlashcardRepository) : BaseViewModel<AddFlashcardView>() {

    val languageHolderCard = ObservableField<Flashcard>()

    fun bindLanguageInfoToUI (sourceLang: String, targetLang: String) {
        languageHolderCard.set(Flashcard(languagePair = "$sourceLang-$targetLang"))
    }

    fun addFlashcardToOfflineDB (newCard : Flashcard, onInsertListener : (isSuccess : Boolean, cardId : Long, ex : Exception?) -> Unit) {
        flashcardRepository.insertFlashcard(newCard, onInsertListener)
    }

    fun addFlashcard (sourceLang : String, targetLang : String, setName : String, type : String, text : String, translation : String, using : String, pronunciation : String) {

        if ((text == "") or (text == null)) {
            view.showTextInputError()
            return
        }

        if ((translation == "") or (translation == null)) {
            view.showTranslationInputError()
            return
        }

        val langInfo = "$sourceLang - $targetLang"
        val validSetName = if (setName.isNotEmpty()) setName else langInfo
        val newCard = Flashcard(0,
            text, translation, langInfo, validSetName,
            using,"", type, pronunciation)

        addFlashcardToOfflineDB (newCard) { isSuccess, insertedCardId, exception ->
            if (isSuccess) {
                view.onAddFlashcardSuccess()
                newCard.id = insertedCardId.toInt()
            } else {
                quickLog("Storing this flashcard to local storage failed. Please check again")
                exception!!.printStackTrace()
            }
        }
    }

    fun updateUserRecentTargetLang (targetLang : String) {
        User.getInstance()!!.recentTargetLanguage = targetLang
        userRepository.updateUser(UserConverter.toUserEntity(User.getInstance()!!))
    }

    fun updateUserRecentSourceLang (sourceLang : String) {
        User.getInstance()!!.recentSourceLanguage = sourceLang
        userRepository.updateUser(UserConverter.toUserEntity(User.getInstance()!!))
    }

}
package com.teamttdvlp.memolang.viewmodel.search_vocabulary

import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.model.sqlite.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.model.sqlite.converter.FlashcardConverter
import com.teamttdvlp.memolang.model.sqlite.entity.UserSearchHistoryHolderEntity
import com.teamttdvlp.memolang.model.sqlite.repository.FlashcardRepository
import com.teamttdvlp.memolang.model.sqlite.repository.UserSearchHistoryRepository
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager
import java.util.*

class SearchVocabularyActivityViewModel(
    var database : MemoLangSqliteDataBase,
    var onlineFlashcardDBManager: OnlineFlashcardDBManager,
    var flashcardRepository: FlashcardRepository,
    var userSearchHistoryRepository: UserSearchHistoryRepository) : BaseViewModel() {

    fun addFlashcardToOfflineDB (newCard : Flashcard, onInsertListener : (isSuccess : Boolean, cardId : Long, ex : Exception?) -> Unit) {
        flashcardRepository.insertFlashcard(newCard, onInsertListener)
    }

    fun addFlashcardToOnlineDB (flashcardSetId : String, flashcard : Flashcard, onInsertListener : (isSuccess : Boolean, ex : Exception?) -> Unit) {
        onlineFlashcardDBManager.writeFlashcard(getSingletonUser()!!.id, flashcardSetId, flashcard, onInsertListener)
    }

    fun search (sourceLang : String, targetLang : String, text : String, onSuccess: ((String?) -> Unit)? = null, onFailed: ((String?) -> Unit)? = null) {
        onSuccess!!.invoke(text)
    }

    fun addSearchedCardToHistory (cardId : Int, searchedTime : Date) {
        userSearchHistoryRepository.addNewCard(cardId, searchedTime)
    }

    fun getAllSearchHistoryInfo (onGetAllSearchHistory : (ArrayList<UserSearchHistoryHolderEntity>) -> Unit) {
        userSearchHistoryRepository.getAllHistory (onGetAllSearchHistory)
    }

    fun getAllFlashcardById (ids : ArrayList<Int>, onGetSuccess : (ArrayList<Flashcard>) -> Unit) {
        flashcardRepository.getFlashcardsByIds(ids) {
            onGetSuccess.invoke(FlashcardConverter.toNormalCardCollection(it))
        }
    }

}
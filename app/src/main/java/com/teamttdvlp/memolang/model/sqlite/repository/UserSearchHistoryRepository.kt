package com.teamttdvlp.memolang.model.sqlite.repository

import android.os.AsyncTask
import com.teamttdvlp.memolang.model.sqlite.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.model.sqlite.dao.UserSearchHistoryDAO
import com.teamttdvlp.memolang.model.sqlite.entity.UserSearchHistoryHolderEntity
import java.util.Date

class UserSearchHistoryRepository (database : MemoLangSqliteDataBase) {

    private val dao : UserSearchHistoryDAO  = database.getUserSearchHistoryDAO()

    fun addNewCard (cardId : Int, searchedDate : Date) {
        val cardSearchHistoryHolderEntity = UserSearchHistoryHolderEntity().apply {
            this.cardId = cardId
            this.searchedDate = searchedDate
        }
        InsertNewCardTask().execute(cardSearchHistoryHolderEntity)
    }

    fun getAllHistory (onGetAllHistory: (ArrayList<UserSearchHistoryHolderEntity>) -> Unit) {
        GetHistoryTask().getAllHistory(onGetAllHistory)
    }

    private inner class InsertNewCardTask : AsyncTask<UserSearchHistoryHolderEntity, Unit, Unit>() {

        override fun doInBackground(vararg p0: UserSearchHistoryHolderEntity?) {
            val searchHistoryHolder = p0[0]
            dao.insert(searchHistoryHolder!!)
        }

    }

    private inner class GetHistoryTask : AsyncTask<Unit, Unit, ArrayList<UserSearchHistoryHolderEntity>>() {

        private lateinit var onGetAllHistory : ((ArrayList<UserSearchHistoryHolderEntity>) -> Unit)

        override fun doInBackground(vararg p0: Unit?): ArrayList<UserSearchHistoryHolderEntity> {
            return ArrayList<UserSearchHistoryHolderEntity>().apply {
                addAll(dao.getAllRecentSearchFlashcards())
            }
        }

        override fun onPostExecute(result: ArrayList<UserSearchHistoryHolderEntity>?) {
            onGetAllHistory.invoke(result!!)
        }

        fun getAllHistory (onGetAllHistory : (ArrayList<UserSearchHistoryHolderEntity>) -> Unit) {
            this.onGetAllHistory = onGetAllHistory
            execute()
        }
    }
}
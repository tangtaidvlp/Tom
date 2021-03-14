package com.teamttdvlp.memolang.model.repository

import android.os.AsyncTask
import com.teamttdvlp.memolang.data.model.entity.flashcard.CardQuizInfor
import com.teamttdvlp.memolang.data.sql.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.data.sql.dao.CardQuizInforDAO
import java.lang.Exception

class CardQuizInforRepos(database: MemoLangSqliteDataBase) {

    private val cardQuizInforDAO: CardQuizInforDAO = database.getCardQuizInforDAO()

    fun addNewQuizInfor(newQuiz: CardQuizInfor) {
        AddTask().execute(newQuiz)
    }

    fun getQuizInfors_ByDeckId(
        deckId: String,
        onGetCardQuizInfors: ((ArrayList<CardQuizInfor>) -> Unit)
    ) {
        GetByDeckIdTask().get(deckId, onGetCardQuizInfors)
    }

    fun deleteQuizInfors_ByDeckId(deckId: String) {
        DeleteByDeckIdTask().execute()
    }

    fun updateQuizInfor(newQuiz: CardQuizInfor) {
        UpdateTask().execute()
    }

    private inner class GetByDeckIdTask : AsyncTask<String, Unit, ArrayList<CardQuizInfor>>() {

        lateinit var onGetCardQuizInfors: ((ArrayList<CardQuizInfor>) -> Unit)

        override fun doInBackground(vararg p0: String?): ArrayList<CardQuizInfor> {
            return ArrayList<CardQuizInfor>().apply {
                addAll(cardQuizInforDAO.getAllCardQuizInformation_BySetId(p0[0]!!))
            }
        }

        override fun onPostExecute(result: ArrayList<CardQuizInfor>?) {
            onGetCardQuizInfors.invoke(result!!)
        }

        fun get(deckID: String, onGetCardQuizInfors: ((ArrayList<CardQuizInfor>) -> Unit)) {
            this.onGetCardQuizInfors = onGetCardQuizInfors
            execute(deckID)
        }

    }

    private inner class DeleteByDeckIdTask : AsyncTask<String, Unit, Unit>() {

        override fun doInBackground(vararg params: String) {
            try {
                val setID = params[0]
                cardQuizInforDAO.deleteAll_BySetId(setID)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            }

    }

    private inner class UpdateTask : AsyncTask<CardQuizInfor, Unit, Unit>() {

        override fun doInBackground(vararg params: CardQuizInfor) {
            try {
                val cardQuizInfor = params[0]
                cardQuizInforDAO.update(cardQuizInfor)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            }

    }

    private inner class AddTask : AsyncTask<CardQuizInfor, Unit, Unit>() {

        override fun doInBackground(vararg params: CardQuizInfor) {

            try {
                val cardQuizInfor = params[0]
                cardQuizInforDAO.insert(cardQuizInfor)
            } catch (ex: Exception) {
                        ex.printStackTrace()
            }

        }
    }
}
package com.teamttdvlp.memolang.model.repository

import android.os.AsyncTask
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.sql.MemoLangSqliteDataBase

class FlashcardRepos  (database : MemoLangSqliteDataBase) {

    private val flashcardDAO = database.getFlashcardDAO()

    /**
     * @param insertedCardId used to get the auto generate id of flashcard which is inserted to Offline Database
     */
    fun insertFlashcard (cardEntity: Flashcard, onInsertListener : (isSuccess: Boolean, insertedCardId : Long, ex : Exception?) -> Unit) {
        InsertFlashcardTask(onInsertListener).insert(cardEntity)
    }

    fun insertFlashcards (cardEntities: ArrayList<Flashcard>, onInsertListener : (isSuccess: Boolean, ex : Exception?) -> Unit) {
        InsertSetOfFlashcardsTask(onInsertListener).insert(cardEntities)
    }

    // Because FlashcardDAO.insertFlashCard() has conflict strategy REPLACE, so if we insert a flashcard whose id is the same with
    // some one in offline database, that flashcard will be updated
    fun updateFlashcard(cardEntity: Flashcard, onInsertListener : (isSuccess: Boolean, insertedCardId : Long, ex : Exception?) -> Unit) {
        insertFlashcard(cardEntity, onInsertListener)
    }

    fun deleteCards (flashcard : ArrayList<Flashcard>) {
        val flashcardEntityArray = Array(flashcard.size) { index ->
            return@Array flashcard[index]
        }
        DeleteFlashcardsTask().execute(flashcardEntityArray)
    }

    fun getFlashcardsByIds (ids : ArrayList<Int>, onGetFlashcards: (ArrayList<Flashcard>) -> Unit) {
        GetFlashcardsByIdsTask().get(ids, onGetFlashcards)
    }

    fun getAllFlashcards(onGetAllFlashcards: (ArrayList<Flashcard>) -> Unit) {
        GetAllFlashcards().getAllFlashcards {
            onGetAllFlashcards(it)
        }
    }

    private inner class GetAllFlashcards : AsyncTask<Unit, Unit, ArrayList<Flashcard>> () {

        private lateinit var onGetAllFlashcards : (ArrayList<Flashcard>) -> Unit

        override fun doInBackground(vararg p0: Unit?): ArrayList<Flashcard> {
            return ArrayList<Flashcard>().apply {
                addAll(flashcardDAO.getAllCard())
            }
        }

        override fun onPostExecute(result: ArrayList<Flashcard>?) {
            onGetAllFlashcards(result!!)
        }

        fun getAllFlashcards (onGetFlashcards: (ArrayList<Flashcard>) -> Unit) {
            this.onGetAllFlashcards = onGetFlashcards
            execute()
        }

    }

    private inner class InsertFlashcardTask
        (private var onInsertListener : (isSuccess : Boolean,
                                         cardId : Long, error : Exception?) -> Unit) : AsyncTask<Flashcard, Unit, Long?>() {

        private val ERROR_ID = -1456L

        private var exception : java.lang.Exception? = null

        override fun doInBackground(vararg params : Flashcard?) : Long? {
            return try {
                val insertedCardId = flashcardDAO.insertNewCard(params[0]!!)
                insertedCardId
            } catch (ex : java.lang.Exception) {
                // Background Thread
                this.exception = ex
                ERROR_ID
            }
        }

        override fun onPostExecute(resultID: Long?) {
            if (resultID != ERROR_ID) {
                onInsertListener.invoke(true, resultID!!, null)
            } else {
                onInsertListener.invoke(false, ERROR_ID,  exception)
            }
        }

        fun insert (memocard: Flashcard) {
            execute(memocard)
        }
    }

    private inner class InsertSetOfFlashcardsTask (private var onInsertListener : (isSuccess : Boolean, error : Exception?) -> Unit): AsyncTask<ArrayList<Flashcard>, Unit, Unit>() {

        override fun doInBackground(vararg params : ArrayList<Flashcard>?) {
            try {
                flashcardDAO.insertSetOfCards(params[0]!!)
            } catch (ex : java.lang.Exception) {
                // Background Thread
                onInsertListener.invoke(false,  ex)
            }
        }

        override fun onPostExecute(result: Unit?) {
            // UI Thread
            onInsertListener.invoke(true,null)
        }

        fun insert (memocard: ArrayList<Flashcard>) {
            execute(memocard)
        }
    }

    private inner class GetFlashcardsByIdsTask : AsyncTask<ArrayList<Int>, Unit, ArrayList<Flashcard>>() {
        lateinit var onGetFlashcards : ((ArrayList<Flashcard>) -> Unit)

        override fun doInBackground(vararg p0: ArrayList<Int>?): ArrayList<Flashcard> {
            return ArrayList<Flashcard>().apply {
                addAll(flashcardDAO.getCardsByIds(p0[0]!!))
            }
        }

        override fun onPostExecute(result: ArrayList<Flashcard>?) {
            onGetFlashcards.invoke(result!!)
        }

        fun get (ids : ArrayList<Int>, onGetFlashcards : ((ArrayList<Flashcard>) -> Unit)) {
            this.onGetFlashcards = onGetFlashcards
            execute(ids)
        }
    }

    private inner class DeleteFlashcardsTask : AsyncTask<Array<Flashcard>, Unit, Unit>() {

        override fun doInBackground(vararg params: Array<Flashcard>?) {
            val cardList = params[0]
            flashcardDAO.deleteCards(*cardList!!)
        }

    }
}
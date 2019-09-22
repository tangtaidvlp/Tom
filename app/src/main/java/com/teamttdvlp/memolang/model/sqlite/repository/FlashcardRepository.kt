package com.teamttdvlp.memolang.model.sqlite.repository

import android.os.AsyncTask
import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.model.sqlite.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.model.sqlite.converter.FlashcardConverter
import com.teamttdvlp.memolang.model.sqlite.entity.FlashCardEntity

class FlashcardRepository  (database : MemoLangSqliteDataBase){

    private val flashcardDAO = database.getMemoCardDAO()

    /**
     * @param insertedCardId used to get the auto generate id of flashcard which is inserted to Offline Database
     */
    fun insertFlashcard (cardEntity: Flashcard, onInsertListener : (isSuccess: Boolean, insertedCardId : Long,  ex : Exception?) -> Unit) {
        InsertFlashcardTask(onInsertListener).insert(FlashcardConverter.toCardEntity(cardEntity))
    }

    fun insertFlashcards (cardEntities: ArrayList<Flashcard>, onInsertListener : (isSuccess: Boolean, ex : Exception?) -> Unit) {
        InsertSetOfFlashcardsTask(onInsertListener).insert(FlashcardConverter.toCardEntityCollection(cardEntities))
    }

    // Because FlashcardDAO.insertFlashCard() has conflict strategy REPLACE, so if we insert a flashcard whose id is the same with
    // some one in offline database, that flashcard will be updated
    fun updateFlashcard(cardEntity: Flashcard, onInsertListener : (isSuccess: Boolean, insertedCardId : Long,  ex : Exception?) -> Unit) {
        insertFlashcard(cardEntity, onInsertListener)
    }

    fun getFlashcardsByIds (ids : ArrayList<Int>, onGetFlashcards: (ArrayList<FlashCardEntity>) -> Unit) {
        GetFlashcardsByIdsTask().get(ids, onGetFlashcards)
    }

    fun getAllFlashcards(onGetAllFlashcards: (ArrayList<Flashcard>) -> Unit) {
        GetAllFlashcards().getAllFlashcards {
            onGetAllFlashcards(FlashcardConverter.toNormalCardCollection(it))
        }
    }

    private inner class GetAllFlashcards : AsyncTask<Unit, Unit, ArrayList<FlashCardEntity>> () {

        private lateinit var onGetAllFlashcards : (ArrayList<FlashCardEntity>) -> Unit

        override fun doInBackground(vararg p0: Unit?): ArrayList<FlashCardEntity> {
            return ArrayList<FlashCardEntity>().apply {
                addAll(flashcardDAO.getAllCard())
            }
        }

        override fun onPostExecute(result: ArrayList<FlashCardEntity>?) {
            onGetAllFlashcards(result!!)
        }

        fun getAllFlashcards (onGetFlashcards: (ArrayList<FlashCardEntity>) -> Unit) {
            this.onGetAllFlashcards = onGetFlashcards
            execute()
        }

    }

    private inner class InsertFlashcardTask (private var onInsertListener : (isSuccess : Boolean, cardId : Long, error : Exception?) -> Unit): AsyncTask<FlashCardEntity, Unit, Long>() {

        private val INSERT_CARD_FAILED_ID = -1L

        override fun doInBackground(vararg params : FlashCardEntity?) : Long{
            try {
                val insertedCardId = flashcardDAO.insertNewCard(params[0]!!)
                return insertedCardId
            } catch (ex : java.lang.Exception) {
                // Background Thread
                onInsertListener.invoke(false, -1,  ex)
                return INSERT_CARD_FAILED_ID
            }
        }

        override fun onPostExecute(insertedCardId: Long?) {
            if (insertedCardId != INSERT_CARD_FAILED_ID) {
                // UI Thread
                onInsertListener.invoke(true, insertedCardId!! ,null)
            }
        }

        fun insert (memoCardEntity: FlashCardEntity) {
                execute(memoCardEntity)
        }
    }

    private inner class InsertSetOfFlashcardsTask (private var onInsertListener : (isSuccess : Boolean, error : Exception?) -> Unit): AsyncTask<ArrayList<FlashCardEntity>, Unit, Unit>() {

        override fun doInBackground(vararg params : ArrayList<FlashCardEntity>?) {
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

        fun insert (memoCardEntity: ArrayList<FlashCardEntity>) {
            execute(memoCardEntity)
        }
    }


    private inner class GetFlashcardsByIdsTask : AsyncTask<ArrayList<Int>, Unit, ArrayList<FlashCardEntity>>() {
        lateinit var onGetFlashcards : ((ArrayList<FlashCardEntity>) -> Unit)

        override fun doInBackground(vararg p0: ArrayList<Int>?): ArrayList<FlashCardEntity> {
            return ArrayList<FlashCardEntity>().apply {
                addAll(flashcardDAO.getCardsByIds(p0[0]!!))
            }
        }

        override fun onPostExecute(result: ArrayList<FlashCardEntity>?) {
            onGetFlashcards.invoke(result!!)
        }

        fun get (ids : ArrayList<Int>, onGetFlashcards : ((ArrayList<FlashCardEntity>) -> Unit)) {
            this.onGetFlashcards = onGetFlashcards
            execute(ids)
        }
    }
}
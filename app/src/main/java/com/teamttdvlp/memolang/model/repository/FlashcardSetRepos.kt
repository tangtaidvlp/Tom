package com.teamttdvlp.memolang.model.repository

import android.os.AsyncTask
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSetWithCardList
import com.teamttdvlp.memolang.data.sql.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.view.helper.log


private const val WITHOUT_CARD_LIST = false

private const val WITH_CARD_LIST = true

class FlashcardSetRepos (database : MemoLangSqliteDataBase) {

    val flashcardSetDAO = database.getFlashcardSetDAO()

    fun getAllFlashcardWithCardList (onGetSuccess: (ArrayList<FlashcardSet>?) -> Unit) {
        GetAllFlashcardSetTask(onGetSuccess).get(WITH_CARD_LIST)
    }

    fun getAll_CardSet_WithNOCardList (onGetSuccess: (ArrayList<FlashcardSet>?) -> Unit) {
        GetAllFlashcardSetTask(onGetSuccess).get(WITHOUT_CARD_LIST)
    }

    fun deleteFlashcardSet (flashcardSet : FlashcardSet) {
        DeleteFlashcardSetTask().delete(flashcardSet)
    }

    fun insert(flashcardSet: FlashcardSet) {
        InsertFlashcardSetTask().insert(flashcardSet)
    }

    fun getFlashcardSetByName(name: String, onGetListener: (FlashcardSet?, Exception?) -> Unit) {
        GetFlashcardSet(onGetListener).execute(name)
    }

    private inner class GetAllFlashcardSetTask(private val onGetSuccess: (ArrayList<FlashcardSet>?) -> Unit) :
        AsyncTask<Boolean, Unit, List<FlashcardSet>?>() {

        override fun doInBackground(vararg params: Boolean?): List<FlashcardSet>? {
            val needCardList = params[0]!!
            return try {
                if (needCardList) {
                    val flashcardSetWithCardList_List: List<FlashcardSetWithCardList> =
                        flashcardSetDAO.getAllFlashcardSetWithCardList()
                    return convertToFlashcardSetList(flashcardSetWithCardList_List)
                } else {
                    return flashcardSetDAO.getAllFlashcardSetWithNOCardList()
                }
            } catch (ex : Exception) {
                ex.printStackTrace()
                log("Get  Flashcard Set Task failed")
                null
            }
        }

        override fun onPostExecute(result: List<FlashcardSet>?) {
            onGetSuccess.invoke(result as ArrayList<FlashcardSet>)
        }

        private fun convertToFlashcardSetList
                    (flashcardSetWithCardList_List : List<FlashcardSetWithCardList>) : List<FlashcardSet> {

            val fcSetList = ArrayList<FlashcardSet>()
            for (fcSetWithCardList in flashcardSetWithCardList_List) {
                val flashcardSet = fcSetWithCardList.flashcardSet
                flashcardSet.flashcards = fcSetWithCardList.flashcardList as ArrayList<Flashcard>
                fcSetList.add(flashcardSet)
            }
            return fcSetList
        }

        fun get (needCardList : Boolean) {
            execute(needCardList)
        }
    }

    private inner class DeleteFlashcardSetTask : AsyncTask<FlashcardSet, Unit, Unit> () {

        override fun doInBackground(vararg params: FlashcardSet?) {
            try {
                flashcardSetDAO.deleteFlashcardSet(params[0] as FlashcardSet)
            } catch (ex : Exception) {
                log("Delete Flashcard Set Task failed")
                ex.printStackTrace()
            }  }

        fun delete (flashcardSet : FlashcardSet) {
            execute(flashcardSet)
        }
    }

    private inner class InsertFlashcardSetTask : AsyncTask<FlashcardSet, Unit, Unit> () {
        override fun doInBackground(vararg params: FlashcardSet?) {
            try {
                flashcardSetDAO.insertFlashcardSet(params[0] as FlashcardSet)
            } catch (ex : Exception) {
                log("Insert Flashcard Set Task failed")
                ex.printStackTrace()
            }
        }

        fun insert (flashcardSet : FlashcardSet) {
            execute(flashcardSet)
        }
    }

    private inner class GetFlashcardSet(private val onGetListener: (FlashcardSet?, err : Exception?) -> Unit) : AsyncTask<String, Unit, FlashcardSet>() {

        private var err : Exception? = null

        override fun doInBackground(vararg params: String?): FlashcardSet? {
            try {
                val setName = params[0] as String
                val flashcardSet : FlashcardSet? = flashcardSetDAO.getFlashcardSetByName(setName)!!.toNormalFlashcardSet()
                return flashcardSet
            } catch (ex : Exception) {
                ex.printStackTrace()
                err = ex
                return null
            }
        }

        override fun onPostExecute(result: FlashcardSet?) {
            onGetListener(result, err)
        }

    }
}
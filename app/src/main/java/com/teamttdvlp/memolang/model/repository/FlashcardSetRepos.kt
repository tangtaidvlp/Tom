package com.teamttdvlp.memolang.model.repository

import android.os.AsyncTask
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSetWithCardList
import com.teamttdvlp.memolang.data.sql.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.view.helper.log


private const val WITHOUT_CARD_LIST = false

private const val WITH_CARD_LIST = true

class FlashcardSetRepos (database : MemoLangSqliteDataBase) {

    val flashcardSetDAO = database.getFlashcardSetDAO()

    fun getAllFlashcardWithCardList(onGetSuccess: (ArrayList<Deck>?) -> Unit) {
        GetAllFlashcardSetTask(onGetSuccess).get(WITH_CARD_LIST)
    }

    fun getAll_CardSet_WithNOCardList(onGetSuccess: (ArrayList<Deck>?) -> Unit) {
        GetAllFlashcardSetTask(onGetSuccess).get(WITHOUT_CARD_LIST)
    }

    fun deleteFlashcardSet(deck: Deck) {
        DeleteFlashcardSetTask().delete(deck)
    }

    fun insert(deck: Deck) {
        InsertFlashcardSetTask().insert(deck)
    }

    fun getFlashcardSetByName(name: String, onGetListener: (Deck?, Exception?) -> Unit) {
        GetFlashcardSet(onGetListener).execute(name)
    }

    private inner class GetAllFlashcardSetTask(private val onGetSuccess: (ArrayList<Deck>?) -> Unit) :
        AsyncTask<Boolean, Unit, List<Deck>?>() {

        override fun doInBackground(vararg params: Boolean?): List<Deck>? {
            val needCardList = params[0]!!
            return try {
                if (needCardList) {
                    val flashcardSetWithCardList_List: List<FlashcardSetWithCardList> =
                        flashcardSetDAO.getAllFlashcardSetWithCardList()
                    return convertToFlashcardSetList(flashcardSetWithCardList_List)
                } else {
                    return flashcardSetDAO.getAllFlashcardSetWithNOCardList()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                log("Get  Flashcard Set Task failed")
                null
            }
        }

        override fun onPostExecute(result: List<Deck>?) {
            onGetSuccess.invoke(result as ArrayList<Deck>)
        }

        private fun convertToFlashcardSetList(flashcardSetWithCardList_List: List<FlashcardSetWithCardList>): List<Deck> {

            val fcSetList = ArrayList<Deck>()
            for (fcSetWithCardList in flashcardSetWithCardList_List) {
                val flashcardSet = fcSetWithCardList.deck
                flashcardSet.flashcards = fcSetWithCardList.flashcardList as ArrayList<Flashcard>
                fcSetList.add(flashcardSet)
            }
            return fcSetList
        }

        fun get (needCardList : Boolean) {
            execute(needCardList)
        }
    }

    private inner class DeleteFlashcardSetTask : AsyncTask<Deck, Unit, Unit>() {

        override fun doInBackground(vararg params: Deck?) {
            try {
                flashcardSetDAO.deleteFlashcardSet(params[0] as Deck)
            } catch (ex: Exception) {
                log("Delete Flashcard Set Task failed")
                ex.printStackTrace()
            }
        }

        fun delete(deck: Deck) {
            execute(deck)
        }
    }

    private inner class InsertFlashcardSetTask : AsyncTask<Deck, Unit, Unit>() {
        override fun doInBackground(vararg params: Deck?) {
            try {
                flashcardSetDAO.insertFlashcardSet(params[0] as Deck)
            } catch (ex: Exception) {
                log("Insert Flashcard Set Task failed")
                ex.printStackTrace()
            }
        }

        fun insert(deck: Deck) {
            execute(deck)
        }
    }

    private inner class GetFlashcardSet(private val onGetListener: (Deck?, err: Exception?) -> Unit) :
        AsyncTask<String, Unit, Deck>() {

        private var err: Exception? = null

        override fun doInBackground(vararg params: String?): Deck? {
            try {
                val setName = params[0] as String
                val deck: Deck? =
                    flashcardSetDAO.getFlashcardSetByName(setName)!!.toNormalFlashcardSet()
                return deck
            } catch (ex: Exception) {
                ex.printStackTrace()
                err = ex
                return null
            }
        }

        override fun onPostExecute(result: Deck?) {
            onGetListener(result, err)
        }

    }
}
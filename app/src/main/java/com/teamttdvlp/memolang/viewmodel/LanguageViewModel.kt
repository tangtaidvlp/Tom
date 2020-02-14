package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import android.os.AsyncTask
import androidx.room.Room
import com.teamttdvlp.memolang.database.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.database.MemoLangSqliteDataBase.Companion.DB_NAME
import com.teamttdvlp.memolang.database.sql.entity.flashcard.FlashcardConverter
import com.teamttdvlp.memolang.database.sql.entity.flashcard.FlashCardEntity
import com.teamttdvlp.memolang.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.view.activity.iview.LanguageView
import com.teamttdvlp.memolang.view.base.BaseAndroidViewModel
import com.teamttdvlp.memolang.view.helper.quickLog

class LanguageViewModel(app : Application) : BaseAndroidViewModel<LanguageView>(app) {

    private val database = Room.databaseBuilder(app, MemoLangSqliteDataBase::class.java, DB_NAME).build()

    fun triggerGetAllSetOfCard(onGetCards : (ArrayList<FlashcardSet>) -> Unit){
       GetAllCardsTask(object  :
           OnGetDataListener {
           override fun onGet(data: ArrayList<FlashCardEntity>) {
               val flashcardList = FlashcardConverter.toNormalCardCollection(data)
               val hashMap = HashMap<String, FlashcardSet>()
               for (card in flashcardList) {
                   quickLog("Set name " + card.setName)
                   val isNewSet = hashMap[card.setName] == null
                    if (isNewSet) {
                        hashMap[card.setName] = FlashcardSet(card.setName)
                    }
                   hashMap[card.setName]!!.flashcards.add(card)
                }
               onGetCards(ArrayList<FlashcardSet>().apply { addAll(hashMap.values) })
           }
       }).execute()
    }

    private inner class GetAllCardsTask (
        private var onGetDataListener: OnGetDataListener? = null
    ) : AsyncTask<Unit, Unit, List<FlashCardEntity>>() {

        override fun doInBackground(vararg p0: Unit?) : List<FlashCardEntity> {
            var data = database.getMemoCardDAO().getAllCard()
            return data
        }

        override fun onPostExecute(result: List<FlashCardEntity>?) {
            onGetDataListener?.onGet(ArrayList<FlashCardEntity>().apply {
                addAll(result!!)
            })
        }

    }

    private interface OnGetDataListener {
        fun onGet (data : ArrayList<FlashCardEntity>)
    }

}

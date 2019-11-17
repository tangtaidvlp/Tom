package com.teamttdvlp.memolang.viewmodel.language

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import com.teamttdvlp.memolang.model.sqlite.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.model.sqlite.MemoLangSqliteDataBase.Companion.DB_NAME
import com.teamttdvlp.memolang.model.sqlite.converter.FlashcardConverter
import com.teamttdvlp.memolang.model.sqlite.entity.FlashCardEntity
import com.teamttdvlp.memolang.model.model.FlashcardSet
import com.teamttdvlp.memolang.model.sqlite.MemoLangSqliteDataBase.Companion.MIGRATION_2_3
import com.teamttdvlp.memolang.view.activity.iview.LanguageView
import com.teamttdvlp.memolang.view.base.BaseAndroidViewModel

class LanguageViewModel(app : Application) : BaseAndroidViewModel<LanguageView>(app) {

    private val database = Room.databaseBuilder(app, MemoLangSqliteDataBase::class.java, DB_NAME)
        .addMigrations(MIGRATION_2_3).build()

    fun triggerGetAllSetOfCard(onGetCards : (ArrayList<FlashcardSet>) -> Unit){
       GetAllCardsTask(object  : OnGetDataListener {
           override fun onGet(data: ArrayList<FlashCardEntity>) {
               val memoCardList = FlashcardConverter.toNormalCardCollection(data)
               val hashMap = HashMap<String, FlashcardSet>()
               for (card in memoCardList) {
                    if (hashMap[card.type] == null)
                        hashMap[card.type] = FlashcardSet(card.type)
                   hashMap[card.type]!!.flashcards.add(card)
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

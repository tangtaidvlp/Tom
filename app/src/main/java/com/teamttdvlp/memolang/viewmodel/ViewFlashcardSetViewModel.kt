package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import android.os.AsyncTask
import androidx.room.Room
import com.teamttdvlp.memolang.database.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.database.MemoLangSqliteDataBase.Companion.DB_NAME
import com.teamttdvlp.memolang.database.sql.entity.flashcard.FlashcardConverter
import com.teamttdvlp.memolang.database.sql.entity.flashcard.FlashCardEntity
import com.teamttdvlp.memolang.database.sql.entity.user.UserConverter
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.database.sql.repository.UserRepository
import com.teamttdvlp.memolang.model.entity.User
import com.teamttdvlp.memolang.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.view.activity.iview.ViewFlashcardSetView
import com.teamttdvlp.memolang.view.base.BaseAndroidViewModel
import com.teamttdvlp.memolang.view.helper.quickLog

class ViewFlashcardSetViewModel(app : Application,
                                var flashcardRepository: FlashcardRepository,
                                var userRepository: UserRepository) : BaseAndroidViewModel<ViewFlashcardSetView>(app) {

    private val database = Room.databaseBuilder(app, MemoLangSqliteDataBase::class.java, DB_NAME).build()

    fun triggerGetAllSetOfCard(onGetCards : (ArrayList<FlashcardSet>) -> Unit){
        flashcardRepository.getAllFlashcards { flashcardList ->
            val hashMap = HashMap<String, FlashcardSet>()
            for (card in flashcardList) {
                val isNewSet = hashMap[card.setName] == null
                if (isNewSet) {
                    hashMap[card.setName] = FlashcardSet(card.setName)
                }
                hashMap[card.setName]!!.flashcards.add(card)
            }
            onGetCards(ArrayList<FlashcardSet>().apply { addAll(hashMap.values) })
        }
    }

    fun removeUserFlashcardSet (setName : String) {
        val user = User.getInstance()
        if (user.flashcardSetNames.contains(setName)) {
            user.flashcardSetNames.remove(setName)
        }
        if (user.recentUseFlashcardSet == setName) {
            user.recentUseFlashcardSet = ""
        }
        userRepository.updateUser(UserConverter.toUserEntity(user))
    }

}

package com.teamttdvlp.memolang.viewmodel

import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.model.repository.UserUsingHistoryRepos
import com.teamttdvlp.memolang.view.activity.iview.MenuView
import com.teamttdvlp.memolang.view.base.BaseViewModel

class MenuActivityViewModel (private var flashcardSetRepos : FlashcardSetRepos): BaseViewModel<MenuView>() {

    fun getAllFlashcardSets(onGetSuccess : (ArrayList<FlashcardSet>?) -> Unit) {
        flashcardSetRepos.getAllFlashcardWithCardList(onGetSuccess)
    }

    fun removeUserFlashcardSet (setName : FlashcardSet) {
        flashcardSetRepos.deleteFlashcardSet(setName)
    }

}
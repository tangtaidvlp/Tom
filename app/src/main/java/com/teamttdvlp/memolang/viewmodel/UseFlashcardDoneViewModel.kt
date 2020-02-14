package com.teamttdvlp.memolang.viewmodel

import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardDoneView
import com.teamttdvlp.memolang.view.base.BaseViewModel

class UseFlashcardDoneViewModel : BaseViewModel<UseFlashcardDoneView>() {

    fun checkUserWellDone (foggottenCardsList : ArrayList<Flashcard>) {
        val isWellDone = foggottenCardsList.size == 0
        if (isWellDone) {
            view.showCongratulationText()
            view.waitForAWhileAndFinish()
        } else {
            view.showFoggotenCardListAndNavigations()
        }
    }

}
package com.teamttdvlp.memolang.viewmodel.use_flashcard_done

import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardDoneView
import com.teamttdvlp.memolang.view.base.BaseViewModel

class UseFlashcardDoneAcivityViewModel : BaseViewModel() {

    private lateinit var view : UseFlashcardDoneView

    fun setView (view : UseFlashcardDoneView) {
        this.view = view
    }

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
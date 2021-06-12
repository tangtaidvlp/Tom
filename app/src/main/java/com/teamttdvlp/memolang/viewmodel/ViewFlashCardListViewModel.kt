package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.QuizCardListPlayer
import com.teamttdvlp.memolang.model.repository.CardQuizInforRepos
import com.teamttdvlp.memolang.model.repository.FlashcardRepos
import com.teamttdvlp.memolang.view.activity.iview.ViewFlashcardListView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.systemOutLogging
import java.io.File

class ViewFlashCardListViewModel(
    var app: Application,
    private val cardQuizInforRepos: CardQuizInforRepos,
    var flashcardRepos: FlashcardRepos
) : BaseViewModel<ViewFlashcardListView>() {

    private var flashcardCount: ObservableInt = ObservableInt()

    lateinit var beingViewedflashcardSet: Deck

    fun setUpData(deck: Deck) {
        this.beingViewedflashcardSet = deck
        flashcardCount.set(beingViewedflashcardSet.flashcards.size)
        loadCardQuizInforFromDatabase()
    }

    fun getFlashcardCount(): ObservableInt {
        return flashcardCount
    }

    fun deleteCards(cardList: ArrayList<Flashcard>) {
        deleteCardsPicture(cardList)
        flashcardRepos.deleteCards(cardList)
    }


    private fun loadCardQuizInforFromDatabase () {

        cardQuizInforRepos.getQuizInfors_ByDeckId(deckId = beingViewedflashcardSet.name, onGetCardQuizInfors = { cardQuizInforList ->
             view.onLoadDataSuccess(cardQuizInforList)
        })

    }

    private fun deleteCardsPicture(cardList: ArrayList<Flashcard>) {
        for (card in cardList) {
            val path =
                app.filesDir.absolutePath + File.separator + card.frontIllustrationPictureName
            val file = File(path)
            if (file.exists()) {
                systemOutLogging("File found")
                val hasSuccess = file.delete()
                if (hasSuccess) {
                    systemOutLogging("Delete success")
                } else {
                    systemOutLogging("Delete failed")
                }
            } else {
                systemOutLogging("FIle not found")
            }
        }
    }

    fun moveSelectedCardToTargetSet(
        selectedFlashcardList: java.util.ArrayList<Flashcard>,
        targetSet: String
    ) {

        for (card in selectedFlashcardList) {
            card.setOwner = targetSet
        }

        flashcardRepos.updateFlashcards(selectedFlashcardList) { isSuccess, ex ->
            if (isSuccess) {
                systemOutLogging("Move to target succesfully")
            } else {
                systemOutLogging("Move to target failed")
                ex?.printStackTrace()
            }
        }

    }
}
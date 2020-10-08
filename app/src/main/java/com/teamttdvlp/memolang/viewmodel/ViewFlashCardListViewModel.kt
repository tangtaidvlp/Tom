package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.repository.FlashcardRepos
import com.teamttdvlp.memolang.view.activity.iview.ViewFlashcardListView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.log
import java.io.File

class ViewFlashCardListViewModel(
    var app: Application,
    var flashcardRepos: FlashcardRepos
) : BaseViewModel<ViewFlashcardListView>() {

    private var flashcardCount: ObservableInt = ObservableInt()

    lateinit var beingViewedflashcardSet: Deck

    fun setFlashcardSet(deck: Deck) {
        this.beingViewedflashcardSet = deck
        flashcardCount.set(beingViewedflashcardSet.flashcards.size)
    }

    fun getFlashcardCount(): ObservableInt {
        return flashcardCount
    }

    fun deleteCards(cardList: ArrayList<Flashcard>) {
        deleteCardsPicture(cardList)
        flashcardRepos.deleteCards(cardList)
    }

    fun deleteCardsPicture(cardList: ArrayList<Flashcard>) {
        for (card in cardList) {
            val path =
                app.filesDir.absolutePath + File.separator + card.frontIllustrationPictureName
            val file = File(path)
            if (file.exists()) {
                log("File found")
                val hasSuccess = file.delete()
                if (hasSuccess) {
                    log("Delete success")
                } else {
                    log("Nhu con caặc")
                }
            } else {
                log("FIle not found")
            }
        }
    }
}
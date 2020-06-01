package com.teamttdvlp.memolang.viewmodel

import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.model.repository.FlashcardRepos
import com.teamttdvlp.memolang.view.activity.iview.ViewFlashcardListView
import com.teamttdvlp.memolang.view.base.BaseViewModel

class ViewFlashCardListViewModel (var flashcardRepos: FlashcardRepos): BaseViewModel<ViewFlashcardListView>() {

    private var flashcardCount : ObservableInt = ObservableInt()

    lateinit var beingViewedflashcardSet : FlashcardSet

    fun setFlashcardSet (flashcardSet: FlashcardSet) {
        this.beingViewedflashcardSet = flashcardSet
        flashcardCount.set(beingViewedflashcardSet.flashcards.size)
    }

    fun getFlashcardCount () : ObservableInt {
        return flashcardCount
    }

    fun deleteCards (cardList : ArrayList<Flashcard>) {
        flashcardRepos.deleteCards(cardList)
    }
}
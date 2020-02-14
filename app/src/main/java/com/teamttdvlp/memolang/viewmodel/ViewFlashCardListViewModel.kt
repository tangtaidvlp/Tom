package com.teamttdvlp.memolang.viewmodel

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.view.activity.iview.ViewFlashcardListView
import com.teamttdvlp.memolang.view.base.BaseViewModel

class ViewFlashCardListViewModel (var flashcardRepository: FlashcardRepository): BaseViewModel<ViewFlashcardListView>() {

    private var flashcardCount : ObservableInt = ObservableInt()

    private var languageType : ObservableField<String> = ObservableField<String>()

    lateinit var beingViewedflashcardSet : FlashcardSet

    fun setFlashcardSet (flashcardSet: FlashcardSet) {
        this.beingViewedflashcardSet = flashcardSet
        val someCardInList = flashcardSet.flashcards[0]
        languageType.set(someCardInList.languagePair)
        flashcardCount.set(beingViewedflashcardSet.flashcards.size)
    }

    fun getFlashcardCount () : ObservableInt {
        return flashcardCount
    }

    fun getLanguageType () : ObservableField<String> {
        return languageType
    }

    fun deleteCards (cardList : ArrayList<Flashcard>) {
        flashcardRepository.deleteCards(cardList)
    }
}
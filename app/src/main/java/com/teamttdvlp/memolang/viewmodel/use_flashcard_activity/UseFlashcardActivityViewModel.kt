package com.teamttdvlp.memolang.viewmodel.use_flashcard_activity

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.view.base.BaseViewModel

class UseFlashcardActivityViewModel : BaseViewModel(){

    var totalCards : ObservableInt = ObservableInt()

    var hardCardLeft : ObservableInt = ObservableInt()

    var cardList = ObservableField<ArrayList<Flashcard>>()

    var hardCardList = ObservableField<ArrayList<Flashcard>>()

    var presentCardPos = 0

    fun updateHardCardLeft() {
        hardCardLeft.set(5)
    }

    fun hasNext () : Boolean {
        return presentCardPos < cardList.get()!!.size - 1
    }

    fun hasPrevious () : Boolean {
        return presentCardPos > 0
    }

    fun getNextCard() : Flashcard {
        presentCardPos += 1
        return cardList.get()!!.get(presentCardPos)
    }

    fun getPreviousCard() : Flashcard {
        presentCardPos -= 1
        return cardList.get()!!.get(presentCardPos)
    }

}

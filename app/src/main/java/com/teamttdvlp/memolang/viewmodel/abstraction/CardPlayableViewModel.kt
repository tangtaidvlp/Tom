package com.teamttdvlp.memolang.viewmodel.abstraction

import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard

interface CardPlayableViewModel {

    fun setUpData (deck : Deck, isReverseCards : Boolean)

    fun loadAllCardIllustrations (flashcardList: ArrayList<Flashcard>)

    fun beginUsing ()

    fun submitAnswer (answer : String) : Boolean

    fun hasNext () : Boolean

    fun nextCard ()

    fun handleUserRememberCard ()

    fun handleUserForgetCard ()

    fun getDeckSize () : Int

    fun getForgottenCardList () : ArrayList<Flashcard>

}

interface CardRelearnableViewModel {

    fun handleUserRelearnCard ()

}
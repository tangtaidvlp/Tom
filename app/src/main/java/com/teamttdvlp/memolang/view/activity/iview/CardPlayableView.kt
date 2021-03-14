package com.teamttdvlp.memolang.view.activity.iview

import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.view.activity.FLASHCARD_SET_KEY
import com.teamttdvlp.memolang.view.activity.REVERSE_CARD_TEXT_AND_TRANSLATION

interface CardPlayableView {

    fun onEndReviewing()

    fun onLoadAllIllustrationFinish()

    fun onLoadAllIllustrationStart()

    fun onPassACard(passedCardCount: Int, forgottenCardCount: Int)

    fun getRequestedFlashcardSet(): Deck

    fun getIsReverseTextAndTranslation(): Boolean

}
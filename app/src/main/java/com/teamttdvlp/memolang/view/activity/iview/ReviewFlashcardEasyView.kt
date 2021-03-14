package com.teamttdvlp.memolang.view.activity.iview

import android.graphics.Bitmap
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import java.lang.Exception


interface ReviewFlashcardEasyView : View, CardPlayableView {

    enum class ListOfCellType {
        WORD_LIST, CHARACTER_LIST
    }

    fun onGetTestSubject (
        testSubject: Flashcard,
        illustration: Bitmap?,
        load_illustrationException: Exception?,
        useExampleForTestSubject: Boolean,
        ansElements: Array<String>,
        listType: ListOfCellType
    )

    fun perform_CorrectAnswerElementsOrderBehaviours()

    fun perform_INcorrectAnsElemtsOrderAnims()

    fun performPassBehaviours ()

    fun performNotPassBehaviours ()

    fun nextCard()

    fun showSpeakTextError(error: String)

}

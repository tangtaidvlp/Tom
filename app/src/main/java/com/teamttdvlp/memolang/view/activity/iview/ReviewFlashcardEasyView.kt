package com.teamttdvlp.memolang.view.activity.iview

import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard


interface ReviewFlashcardEasyView : View {

    enum class ListOfCellType {
        WORD_LIST, CHARACTER_LIST
    }

    fun onGetTestSubject (testSubject : Flashcard, useExampleForTestSubject : Boolean,
                          ansElements : Array<String>, listType : ListOfCellType)

    fun perform_CorrectAnswerElementsOrderBehaviours()

    fun perform_INcorrectAnsElemtsOrderAnims()

    fun performPassBehaviours ()

    fun performNotPassBehaviours ()

    fun endReviewing()

    fun nextCard()

    fun showSpeakTextError(error: String)
}

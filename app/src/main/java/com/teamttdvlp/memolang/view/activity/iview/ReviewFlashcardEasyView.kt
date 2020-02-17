package com.teamttdvlp.memolang.view.activity.iview

import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard

interface ReviewFlashcardEasyView : View {

    companion object {
        val CHARACTER_LIST = -1

        val WORD_LIST = -2
    }

    fun showTestSubjectOnScreen (testSubject : Flashcard, useUsingForTestSubject : Boolean,
                                                          ansElements : Array<String>, listType : Int)

    fun performCorrectAnsElemtsOrderAnims ()

    fun performIncorrectAnsElemtsOrderAnims ()

    fun performPassBehaviours ()

    fun performNotPassBehaviours ()

    fun endReviewing()
    fun showNextCardAnims()
}

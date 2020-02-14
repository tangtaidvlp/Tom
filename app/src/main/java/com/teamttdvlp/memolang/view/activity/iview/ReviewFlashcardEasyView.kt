package com.teamttdvlp.memolang.view.activity.iview

import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard

interface ReviewFlashcardEasyView : View {

    fun showTestSubjectOnScreen (testSubject : Flashcard, ansElements : Array<String>, useUsingForTestSubject : Boolean)

    fun performCorrectAnsElemtsOrderAnims ()

    fun performIncorrectAnsElemtsOrderAnims ()

    fun performPassAnims ()

    fun performNotPassAnim ()

    fun endReviewing()
}

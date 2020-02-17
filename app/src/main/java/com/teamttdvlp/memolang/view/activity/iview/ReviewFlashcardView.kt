package com.teamttdvlp.memolang.view.activity.iview

import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard

interface ReviewFlashcardView : View {

    fun endReviewing ()

    fun highlightHintOption ()

    fun resetHintOptionState ()

    fun showGoodAnswerAnimation ()

    fun showExcelentAnswerAnimation ()

    fun showWrongAnswerAnimation ()

    fun showNotPassAnswerAnimation ()

    fun showValidAnsBehaviours()

    fun showInvalidAnsBehaviours()

    fun showTestSubjectOnScreen (testSubject : Flashcard, useUsingForTestSubject : Boolean)

    fun showNextCardAnims()
}
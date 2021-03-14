package com.teamttdvlp.memolang.view.activity.iview

import android.graphics.Bitmap
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.viewmodel.QuizActivityViewModel
import java.lang.Exception

interface QuizView : View, CardPlayableView {


    fun onGetTestSubject (
        testSubject: Flashcard,
        illustration: Bitmap?,
        load_illustrationException: Exception?,
        useExampleForTestSubject: Boolean,
        answerMode: QuizActivityViewModel.AnswerMode,
        answerSet: ArrayList<String>?
    )

    fun showSpeakTextError(s: String)

    fun extendedOnPassACard (passedCardCount: Int, familiarCardCount : Int, forgottenCardCount: Int)

}
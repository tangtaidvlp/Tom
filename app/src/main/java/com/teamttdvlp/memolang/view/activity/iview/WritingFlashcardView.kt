package com.teamttdvlp.memolang.view.activity.iview

import android.graphics.Bitmap
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard

interface WritingFlashcardView : View, CardPlayableView {   

    fun highlightHintOption ()

    fun resetHintOptionState ()

    fun showGoodAnswerAnimation ()

    fun showExcellentAnswerAnimation ()

    fun showWrongAnswerAnimation ()

    fun showNotPassAnswerAnimation ()

    fun showValidAnsBehaviours()

    fun showInvalidAnsBehaviours()

    fun onGetTestSubject (testSubject : Flashcard, illustration : Bitmap?, load_illustrationException: Exception?, useUsingForTestSubject : Boolean)

    fun nextCard(startDelay : Long)

    fun showSpeakTextError(error: String)

}
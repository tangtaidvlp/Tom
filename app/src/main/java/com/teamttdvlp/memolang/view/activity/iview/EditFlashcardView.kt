package com.teamttdvlp.memolang.view.activity.iview

import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard

interface EditFlashcardView : View {

    fun showTextInputError ()

    fun showTranslationInputError ()

    fun onUpdateFlashcardSuccess (newFlashcard : Flashcard)

    fun endEditing()

}
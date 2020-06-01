package com.teamttdvlp.memolang.view.activity.iview

import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet

interface AddFlashcardView : View {

    fun showTextInputError ()

    fun showTranslationInputError ()

    fun onAddFlashcardSuccess ()

    fun showInvalidFlashcardSetError (errorMessage : String)

    fun hideCreateNewFlashcardSetPanel()

}
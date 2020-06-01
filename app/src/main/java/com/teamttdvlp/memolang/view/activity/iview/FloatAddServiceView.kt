package com.teamttdvlp.memolang.view.activity.iview

interface FloatAddServiceView : View {

    fun showInvalidFlashcardSetError (errorMessage : String)

    fun showTextInputError ()

    fun showTranslationInputError()

    fun onAddFlashcardSuccess()
}
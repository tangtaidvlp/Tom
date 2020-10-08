package com.teamttdvlp.memolang.view.activity.iview

interface AddFlashcardView : View {

    fun showTextInputError()

    fun showTranslationInputError()

    fun onAddFlashcardSuccess()

    fun showInvalidFlashcardSetError(errorMessage: String)

    fun hideCreateNewFlashcardSetPanel()

    fun showFrontEmptyImageError()

    fun showBackEmptyImageError()

}
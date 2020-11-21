package com.teamttdvlp.memolang.view.activity.iview

interface AddFlashcardView : View {

    fun showFrontCardInputError()

    fun showBackCardInputError()

    fun onAddFlashcardSuccess()

    fun showInvalidFlashcardSetError(errorMessage: String)

    fun hideCreateNewFlashcardSetPanel()

}
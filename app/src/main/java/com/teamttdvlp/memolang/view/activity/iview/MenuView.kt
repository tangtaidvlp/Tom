package com.teamttdvlp.memolang.view.activity.iview

interface MenuView : View{

    fun hideCreateNewFlashcardSetPanel()

    fun showInvalidFlashcardSetError(errorMessage: String)

    fun hideInvalidFlashcardSetError()
}
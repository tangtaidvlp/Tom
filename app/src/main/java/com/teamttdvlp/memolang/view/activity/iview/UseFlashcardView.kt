package com.teamttdvlp.memolang.view.activity.iview

interface UseFlashcardView : View{

    fun onNoCardsLeft ()

    fun showPreviousCardButton ()

    fun hidePreviousCardButton ()

    fun showSpeakTextError(error: String)
}
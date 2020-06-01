package com.teamttdvlp.memolang.view.activity.iview

interface UseFlashcardView : View{

    fun onNoCardsLeft ()

    fun showSpeakTextError(error: String)

    fun lock_ShowPreviousCard_Function()

    fun unlock_ShowPreviousCard_Function()

}
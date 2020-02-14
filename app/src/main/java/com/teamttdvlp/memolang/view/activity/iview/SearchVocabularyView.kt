package com.teamttdvlp.memolang.view.activity.iview

interface SearchVocabularyView : View{

    fun hideTranslatingProgressBar()

    fun showTranslatingProgressBar()

    fun onCheckConnectionWhenSearch(hasConnection : Boolean)
}
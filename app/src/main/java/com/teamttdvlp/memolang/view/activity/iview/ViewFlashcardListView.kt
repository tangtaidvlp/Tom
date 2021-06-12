package com.teamttdvlp.memolang.view.activity.iview

import com.teamttdvlp.memolang.data.model.entity.flashcard.CardQuizInfor

interface ViewFlashcardListView : View{

    fun onLoadDataSuccess (quizDataList : ArrayList<CardQuizInfor>)

}
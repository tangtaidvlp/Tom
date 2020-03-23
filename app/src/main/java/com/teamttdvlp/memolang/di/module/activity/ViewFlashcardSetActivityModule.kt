package com.teamttdvlp.memolang.di.module.activity

import com.teamttdvlp.memolang.view.activity.ViewFlashcardSetActivity
import com.teamttdvlp.memolang.view.adapter.RCV_FlashcardSetAdapter
import dagger.Module
import dagger.Provides

@Module
class ViewFlashcardSetActivityModule {

    @Provides
    fun provideLanguageRCVAdapter (activity: ViewFlashcardSetActivity) : RCV_FlashcardSetAdapter{
        return RCV_FlashcardSetAdapter(activity)
    }

}
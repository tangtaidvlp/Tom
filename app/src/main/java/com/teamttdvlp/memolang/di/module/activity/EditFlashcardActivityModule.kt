package com.teamttdvlp.memolang.di.module.activity

import com.teamttdvlp.memolang.di.MemoLang
import com.teamttdvlp.memolang.view.activity.EditFlashcardActivity
import com.teamttdvlp.memolang.view.adapter.RCV_FlashcardSetNameAdapter
import dagger.Module
import dagger.Provides

@Module
class EditFlashcardActivityModule {

    @Provides
    fun provideSetNameAdapter (app : EditFlashcardActivity) : RCV_FlashcardSetNameAdapter {
        return RCV_FlashcardSetNameAdapter(app)
    }

}
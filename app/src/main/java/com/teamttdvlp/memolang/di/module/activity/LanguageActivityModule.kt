package com.teamttdvlp.memolang.di.module.activity

import com.teamttdvlp.memolang.view.activity.LanguageActivity
import com.teamttdvlp.memolang.view.adapter.RCV_LanguageAdapter
import dagger.Module
import dagger.Provides

@Module
class LanguageActivityModule {

    @Provides
    fun provideLanguageRCVAdapter (activity: LanguageActivity) : RCV_LanguageAdapter{
        return RCV_LanguageAdapter(activity)
    }

}
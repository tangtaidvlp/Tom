package com.teamttdvlp.memolang.di.module.activity

import com.teamttdvlp.memolang.view.activity.LanguageActivity
import com.teamttdvlp.memolang.view.activity.adapter.LanguageRCVAdapter
import dagger.Module
import dagger.Provides

@Module
class LanguageActivityModule {

    @Provides
    fun provideLanguageRCVAdapter (activity: LanguageActivity) : LanguageRCVAdapter{
        return LanguageRCVAdapter(activity)
    }

}
package com.teamttdvlp.memolang.di.module.activity

import com.teamttdvlp.memolang.view.activity.SearchEngVNDictionaryActivity
import com.teamttdvlp.memolang.view.adapter.RCVRecentSearchDictionaryAdapter
import com.teamttdvlp.memolang.view.adapter.RCVSearchDictionaryAdapter
import dagger.Module
import dagger.Provides

@Module
class SearchEngVNDictionaryModule {

    @Provides
    fun provideRCVDictionaryAdapter (context : SearchEngVNDictionaryActivity) : RCVSearchDictionaryAdapter {
        return RCVSearchDictionaryAdapter(context)
    }

    @Provides
    fun provideRCVRecentSearchDictHistoryAdapter (context : SearchEngVNDictionaryActivity) : RCVRecentSearchDictionaryAdapter {
        return RCVRecentSearchDictionaryAdapter(context)
    }

}
package com.teamttdvlp.memolang.di.module.activity

import com.teamttdvlp.memolang.data.model.other.new_vocabulary.Example
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.SingleMeanExample
import com.teamttdvlp.memolang.model.EngVietVocabularyLoader
import com.teamttdvlp.memolang.view.activity.SearchEngVNDictionaryActivity
import com.teamttdvlp.memolang.view.adapter.RCVRecent_SearchDictionary_Adapter
import com.teamttdvlp.memolang.view.adapter.RCVSearchDictionaryAdapter
import com.teamttdvlp.memolang.view.adapter.RCV_Generic_SimpleListAdapter
import dagger.Module
import dagger.Provides

@Module
class SearchEngVNDictionaryModule {

    @Provides
    fun provideRCVDictionaryAdapter (context : SearchEngVNDictionaryActivity, engVietVocabularyLoader: EngVietVocabularyLoader) : RCVSearchDictionaryAdapter {
        return RCVSearchDictionaryAdapter(context, engVietVocabularyLoader)
    }

    @Provides
    fun provideRCVRecentSearchDictHistoryAdapter (context : SearchEngVNDictionaryActivity) : RCVRecent_SearchDictionary_Adapter {
        return RCVRecent_SearchDictionary_Adapter(context)
    }

}
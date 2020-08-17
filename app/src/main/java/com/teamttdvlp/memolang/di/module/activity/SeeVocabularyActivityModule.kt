package com.teamttdvlp.memolang.di.module.activity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.SingleMeanExample
import com.teamttdvlp.memolang.view.activity.EngVietDictionaryActivity
import com.teamttdvlp.memolang.view.adapter.*
import dagger.Module
import dagger.Provides

@Module
class SeeVocabularyActivityModule {

    @Provides
    fun provideSimpleAdater2(context: EngVietDictionaryActivity): RCVSimpleListAdapter2 {
        return RCVSimpleListAdapter2(context)
    }

    @Provides
    fun provideExampleAdapter(context: EngVietDictionaryActivity): RCVGenericSimpleListAdapter2<SingleMeanExample> {
        return RCVGenericSimpleListAdapter2<SingleMeanExample>(context, getItem = { example ->
            example.text + ": " + example.mean
        })
    }

    @Provides
    fun provideSimpleChooseFlashcardSetAdapter(context: EngVietDictionaryActivity): RCVSimpleListChooseSetNameAdapter {
        return RCVSimpleListChooseSetNameAdapter(context)
    }

    @Provides
    fun provideLayoutManager(context: EngVietDictionaryActivity): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, VERTICAL, false)
    }

    @Provides
    fun provideRCVDictionaryAdapter(context: EngVietDictionaryActivity): RCVSearchDictionaryAdapter {
        return RCVSearchDictionaryAdapter(context)
    }

    @Provides
    fun provideRCVRecentSearchDictHistoryAdapter(context: EngVietDictionaryActivity): RCVRecent_SearchDictionary_Adapter {
        return RCVRecent_SearchDictionary_Adapter(context)
    }

}

package com.teamttdvlp.memolang.di.module.activity

import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.SingleMeanExample
import com.teamttdvlp.memolang.di.MemoLang
import com.teamttdvlp.memolang.view.adapter.*
import dagger.Module
import dagger.Provides

@Module
class FloatAddServiceModule {

    @Provides
    fun provideRCV_Generic_SimpleAdapter(context: MemoLang): RCV_Generic_SimpleListAdapter<Deck> {
        return RCV_Generic_SimpleListAdapter(context) { flashcardSet ->
            flashcardSet.name
        }
    }

    @Provides
    fun provideRCVRecentUsedLanguageAdapter (context : MemoLang) : RCVRecentUsedLanguageAdapter {
        return RCVRecentUsedLanguageAdapter(context)
    }

    @Provides
    fun provideRCVRecentSearchDictHistoryAdapter (context : MemoLang) : RCVRecent_SearchDictionary_Adapter {
        return RCVRecent_SearchDictionary_Adapter(context)
    }

    @Provides
    fun provideRCVDictionaryAdapter (context : MemoLang) : RCVSearchDictionaryAdapter {
        return RCVSearchDictionaryAdapter(context)
    }

    @Provides
    fun provideSimpleAdater2 (context : MemoLang) : RCVSimpleListAdapter2 {
        return RCVSimpleListAdapter2(context)
    }

    @Provides
    fun provideExampleAdapter (context : MemoLang) : RCVGenericSimpleListAdapter2<SingleMeanExample> {
        return RCVGenericSimpleListAdapter2<SingleMeanExample>(context, getItem = { example ->
            example.text + ": " + example.mean
        })
    }

    @Provides
    fun provideSimpleChooseFlashcardSetAdapter (context : MemoLang) : RCVSimpleListChooseSetNameAdapter {
        return RCVSimpleListChooseSetNameAdapter(context)
    }


    @Provides
    fun provideRCVChooseLanguageAdapter (context : MemoLang) : RCVChooseLanguageAdapter {
        return RCVChooseLanguageAdapter(context)
    }


}
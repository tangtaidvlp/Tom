package com.teamttdvlp.memolang.di.module.activity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.teamttdvlp.memolang.view.activity.SeeVocabularyActivity
import com.teamttdvlp.memolang.view.adapter.RCVSimpleListAdapter2
import dagger.Module
import dagger.Provides

@Module
class SeeVocabularyActivityModule {

    @Provides
    fun provideSimpleAdater (context : SeeVocabularyActivity) : RCVSimpleListAdapter2 {
        return RCVSimpleListAdapter2(context)
    }

    @Provides
    fun provideLayoutManager (context : SeeVocabularyActivity) : RecyclerView.LayoutManager {
        return LinearLayoutManager(context, VERTICAL, false)
    }

}

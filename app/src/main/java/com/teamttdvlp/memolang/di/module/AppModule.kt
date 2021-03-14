package com.teamttdvlp.memolang.di.module

import android.app.Application
import androidx.room.Room
import com.teamttdvlp.memolang.data.sql.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.data.sql.MemoLangSqliteDataBase.Companion.DB_NAME
import com.teamttdvlp.memolang.di.MemoLang
import com.teamttdvlp.memolang.model.EngVietVocabularyLoader
import com.teamttdvlp.memolang.model.UserInfoStatusSharedPreference
import com.teamttdvlp.memolang.model.repository.*
import com.teamttdvlp.memolang.model.sharepref.AddFlashcardActivitySharePref
import com.teamttdvlp.memolang.model.sharepref.EngVietDictionaryActivitySharePref
import com.teamttdvlp.memolang.model.sharepref.SearchOnlineActivitySharePref
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideEngVietVocabularyLoader (context : MemoLang) : EngVietVocabularyLoader{
        return EngVietVocabularyLoader(context)
    }

     @Provides
    @Singleton
    fun providesUserRepository (dataBase: MemoLangSqliteDataBase) : UserRepos {
        return UserRepos(dataBase)
    }

    @Provides
    @Singleton
    fun providesFlashcardRepository (dataBase: MemoLangSqliteDataBase) : FlashcardRepos{
        return FlashcardRepos(dataBase)
    }

    @Provides
    @Singleton
    fun providesCardQuizInforRepository (dataBase: MemoLangSqliteDataBase) : CardQuizInforRepos{
        return CardQuizInforRepos(dataBase)
    }

    @Provides
    @Singleton
    fun providesFlashcardSetRepository (dataBase: MemoLangSqliteDataBase) : FlashcardSetRepos{
        return FlashcardSetRepos(dataBase)
    }

    @Provides
    @Singleton
    fun providesUserUsingHistoryReposity (dataBase: MemoLangSqliteDataBase) : UserUsingHistoryRepos{
        return UserUsingHistoryRepos(dataBase)
    }

    @Provides
    @Singleton
    fun providesSqliteDatabase (application: MemoLang) : MemoLangSqliteDataBase {
        return Room
            .databaseBuilder(application, MemoLangSqliteDataBase::class.java, DB_NAME)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserPrimaryInfoSharedPref (application: Application) : UserInfoStatusSharedPreference {
        return UserInfoStatusSharedPreference(application)
    }

    @Provides
    @Singleton
    fun provideSearchOnlineSharedPreference(application: Application): SearchOnlineActivitySharePref {
        return SearchOnlineActivitySharePref(application)
    }

    @Provides
    @Singleton
    fun provideAddFlashcardAcitivytSharedPreference(application: Application): AddFlashcardActivitySharePref {
        return AddFlashcardActivitySharePref(application)
    }

    @Provides
    @Singleton
    fun provideEngVietSharedPreference(application: Application): EngVietDictionaryActivitySharePref {
        return EngVietDictionaryActivitySharePref(application)
    }

    @Provides
    @Singleton
    fun providesApplication(application: MemoLang): Application {
        return application
    }

}
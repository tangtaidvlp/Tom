package com.teamttdvlp.memolang.di.module

import android.app.Application
import androidx.room.Room
import com.teamttdvlp.memolang.di.MemoLang
import com.teamttdvlp.memolang.data.sql.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.data.sql.MemoLangSqliteDataBase.Companion.DB_NAME
import com.teamttdvlp.memolang.model.repository.FlashcardRepos
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.model.repository.UserRepos
import com.teamttdvlp.memolang.model.repository.UserUsingHistoryRepos
import com.teamttdvlp.memolang.model.AddFlashcardSharedPreference
import com.teamttdvlp.memolang.model.SearchOnlineSharedPreference
import com.teamttdvlp.memolang.model.UserInfoStatusSharedPreference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

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
    fun provideAddFlashcardSharedPreference (application: Application) : AddFlashcardSharedPreference {
        return AddFlashcardSharedPreference(application)
    }

    @Provides
    @Singleton
    fun provideSearchOnlineSharedPreference (application: Application) : SearchOnlineSharedPreference {
        return SearchOnlineSharedPreference(application)
    }

    @Provides
    @Singleton
    fun providesApplication (application: MemoLang) : Application {
        return application
    }


}
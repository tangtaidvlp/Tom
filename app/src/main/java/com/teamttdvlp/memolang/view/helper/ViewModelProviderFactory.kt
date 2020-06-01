package com.teamttdvlp.memolang.view.helper

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teamttdvlp.memolang.data.sql.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.model.AddFlashcardExecutor
import com.teamttdvlp.memolang.model.repository.FlashcardRepos
import com.teamttdvlp.memolang.model.repository.UserRepos
import com.teamttdvlp.memolang.model.repository.UserUsingHistoryRepos
import com.teamttdvlp.memolang.model.AddFlashcardSharedPreference
import com.teamttdvlp.memolang.model.SearchOnlineSharedPreference
import com.teamttdvlp.memolang.model.UserInfoStatusSharedPreference
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.viewmodel.*
import java.lang.Exception
import javax.inject.Inject

typealias DaggerLazy<T> = dagger.Lazy<T>

class ViewModelProviderFactory
    @Inject
    constructor (
        private var application: Application,
        private var userRepos : DaggerLazy<UserRepos>,
        private var flashcardRepos : DaggerLazy<FlashcardRepos>,
        private var flashcardSetRepos : DaggerLazy<FlashcardSetRepos>,
        private var addFlashcardExecutor: DaggerLazy<AddFlashcardExecutor>,
        private val userUsingHistoryRepos: DaggerLazy<UserUsingHistoryRepos>,
        private var userInfoStatusSharedPreference : DaggerLazy<UserInfoStatusSharedPreference>,
        private var addFlashcardSharedPreference: DaggerLazy<AddFlashcardSharedPreference>,
        private var searchOnlineSharedPreference: DaggerLazy<SearchOnlineSharedPreference>): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) : T {

        if (modelClass.isAssignableFrom(AddFlashCardViewModel::class.java)) {
                return AddFlashCardViewModel(userRepos.get(), addFlashcardExecutor.get(),
                    flashcardSetRepos.get(), userUsingHistoryRepos.get(), addFlashcardSharedPreference.get()) as T
        }

        if (modelClass.isAssignableFrom(EditFlashcardViewModel::class.java)) {
            return EditFlashcardViewModel(addFlashcardExecutor.get(), flashcardSetRepos.get()) as T
        }

        if (modelClass.isAssignableFrom(MenuActivityViewModel::class.java)) {
            return MenuActivityViewModel(flashcardSetRepos.get()) as T
        }


        if (modelClass.isAssignableFrom(ReviewFlashcardEasyViewModel::class.java)) {
            return ReviewFlashcardEasyViewModel(application) as T
        }

        if (modelClass.isAssignableFrom(ReviewFlashcardViewModel::class.java)) {
            return ReviewFlashcardViewModel(application) as T
        }

        if (modelClass.isAssignableFrom(SearchEngVNDictionaryViewModel::class.java)) {
            return SearchEngVNDictionaryViewModel(application, userUsingHistoryRepos.get()) as T
        }

        if (modelClass.isAssignableFrom(SearchOnlineViewModel::class.java)) {
            return SearchOnlineViewModel(application, userRepos.get(),
                flashcardSetRepos.get(), addFlashcardExecutor.get(), userUsingHistoryRepos.get()
                , searchOnlineSharedPreference.get()) as T
        }

        if (modelClass.isAssignableFrom(SeeVocabularyActivityViewModel::class.java)) {
            return SeeVocabularyActivityViewModel(application, userRepos.get(),
                flashcardSetRepos.get(), addFlashcardExecutor.get(), userUsingHistoryRepos.get()) as T
        }

        if (modelClass.isAssignableFrom(SetUpAccountViewModel::class.java)) {
            return SetUpAccountViewModel(userRepos.get(), userUsingHistoryRepos.get(), flashcardSetRepos.get(),
                userInfoStatusSharedPreference.get(), addFlashcardSharedPreference.get(), searchOnlineSharedPreference.get()) as T
        }

        if (modelClass.isAssignableFrom(UseFlashcardDoneViewModel::class.java)) {
            return UseFlashcardDoneViewModel() as T
        }

        if (modelClass.isAssignableFrom(UseFlashcardViewModel::class.java)) {
            return UseFlashcardViewModel(application) as T
        }

        if (modelClass.isAssignableFrom(ViewFlashcardSetViewModel::class.java)) {
            return ViewFlashcardSetViewModel(flashcardSetRepos.get()) as T
        }

        if (modelClass.isAssignableFrom(ViewFlashCardListViewModel::class.java)) {
            return ViewFlashCardListViewModel(flashcardRepos.get()) as T
        }

        throw Exception ("ViewModelProviderFactory (Unknown viewModel): ${modelClass.simpleName}")
    }

}
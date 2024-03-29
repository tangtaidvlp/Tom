package com.teamttdvlp.memolang.view.helper

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teamttdvlp.memolang.model.AddFlashcardExecutor
import com.teamttdvlp.memolang.model.EngVietVocabularyLoader
import com.teamttdvlp.memolang.model.IllustrationLoader
import com.teamttdvlp.memolang.model.UserInfoStatusSharedPreference
import com.teamttdvlp.memolang.model.repository.*
import com.teamttdvlp.memolang.model.sharepref.AddFlashcardActivitySharePref
import com.teamttdvlp.memolang.model.sharepref.EngVietDictionaryActivitySharePref
import com.teamttdvlp.memolang.model.sharepref.SearchOnlineActivitySharePref
import com.teamttdvlp.memolang.viewmodel.*
import javax.inject.Inject

typealias DaggerLazy<T> = dagger.Lazy<T>

class ViewModelProviderFactory
@Inject
constructor(
    private var application: Application,
    private var userRepos: DaggerLazy<UserRepos>,
    private var flashcardRepos: DaggerLazy<FlashcardRepos>,
    private var cardQuizInforRepos: DaggerLazy<CardQuizInforRepos>,
    private var flashcardSet_Repos: DaggerLazy<FlashcardSetRepos>,
    private var addFlashcardExecutor: DaggerLazy<AddFlashcardExecutor>,
    private val userUsingHistoryRepos: DaggerLazy<UserUsingHistoryRepos>,
    private var userInfoStatusSharedPreference: DaggerLazy<UserInfoStatusSharedPreference>,
    private var addFlashcardSharedPreference: DaggerLazy<AddFlashcardActivitySharePref>,
    private var searchOnlineSharedPreference: DaggerLazy<SearchOnlineActivitySharePref>,
    private var engVietOnlineSharedPreference: DaggerLazy<EngVietDictionaryActivitySharePref>,
    private var engVietVocabularyLoader: DaggerLazy<EngVietVocabularyLoader>,
    private var illustrationLoader: IllustrationLoader
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AddFlashCardViewModel::class.java)) {
            return AddFlashCardViewModel(
                userRepos.get(),
                addFlashcardExecutor.get(),
                flashcardSet_Repos.get(),
                userUsingHistoryRepos.get(),
                addFlashcardSharedPreference.get(),
                illustrationLoader
            ) as T
        }

        if (modelClass.isAssignableFrom(EditFlashcardViewModel::class.java)) {
            return EditFlashcardViewModel(addFlashcardExecutor.get(), flashcardSet_Repos.get()) as T
        }

        if (modelClass.isAssignableFrom(MenuActivityViewModel::class.java)) {
            return MenuActivityViewModel(
                flashcardSet_Repos.get(),
                flashcardRepos.get(),
                userUsingHistoryRepos.get(),
                addFlashcardSharedPreference.get(),
                searchOnlineSharedPreference.get(),
                engVietOnlineSharedPreference.get()
            ) as T
        }

        if (modelClass.isAssignableFrom(PuzzleFlashcardViewModel::class.java)) {
            return PuzzleFlashcardViewModel(application, illustrationLoader) as T
        }

        if (modelClass.isAssignableFrom(WritingFlashcardViewModel::class.java)) {
            return WritingFlashcardViewModel(application, illustrationLoader) as T
        }

        if (modelClass.isAssignableFrom(SearchEngVNDictionaryViewModel::class.java)) {
            return SearchEngVNDictionaryViewModel(application, userUsingHistoryRepos.get()) as T
        }

        if (modelClass.isAssignableFrom(SearchOnlineViewModel::class.java)) {
            return SearchOnlineViewModel(
                application, userRepos.get(),
                flashcardSet_Repos.get(),
                addFlashcardExecutor.get(),
                userUsingHistoryRepos.get()
                , searchOnlineSharedPreference.get()
            ) as T
        }

        if (modelClass.isAssignableFrom(EngVietDictionaryActivityViewModel::class.java)) {
            return EngVietDictionaryActivityViewModel(
                application, userRepos.get(),
                flashcardSet_Repos.get(), addFlashcardExecutor.get(),
                userUsingHistoryRepos.get(),
                engVietOnlineSharedPreference.get()
            ) as T
        }

        if (modelClass.isAssignableFrom(SetUpAccountViewModel::class.java)) {
            return SetUpAccountViewModel(
                userRepos.get(), userUsingHistoryRepos.get(), flashcardSet_Repos.get(),
                userInfoStatusSharedPreference.get(),
                addFlashcardSharedPreference.get(),
                engVietOnlineSharedPreference.get(),
                searchOnlineSharedPreference.get()
            ) as T
        }

        if (modelClass.isAssignableFrom(UseFlashcardDoneViewModel::class.java)) {
            return UseFlashcardDoneViewModel() as T
        }

        if (modelClass.isAssignableFrom(UseFlashcardViewModel::class.java)) {
            return UseFlashcardViewModel(application, illustrationLoader) as T
        }

        if (modelClass.isAssignableFrom(ViewFlashcardSetViewModel::class.java)) {
            return ViewFlashcardSetViewModel(flashcardSet_Repos.get()) as T
        }

        if (modelClass.isAssignableFrom(ViewFlashCardListViewModel::class.java)) {
            return ViewFlashCardListViewModel(application, cardQuizInforRepos.get(), flashcardRepos.get()) as T
        }

        if (modelClass.isAssignableFrom(QuizActivityViewModel::class.java)) {
            return QuizActivityViewModel(illustrationLoader, cardQuizInforRepos.get(), engVietVocabularyLoader.get()) as T
        }

        throw Exception ("ViewModelProviderFactory (Unknown viewModel): ${modelClass.simpleName}")
    }

}
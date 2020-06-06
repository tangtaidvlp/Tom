package com.teamttdvlp.memolang.di.builder

import com.teamttdvlp.memolang.di.module.activity.*
import com.teamttdvlp.memolang.di.module.activity.view_flashcard_list.ViewFlashcardListActivityModule
import com.teamttdvlp.memolang.view.activity.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [AddFlashcardActivityModule::class])
    abstract fun injectAddFlashcardActivity() : AddFlashcardActivity

    @ContributesAndroidInjector(modules = [RetrofitAddFlashcardActivityModule::class])
    abstract fun injectRetrofitAddFlashcardActivity() : RetrofitAddFlashcardActivity

    @ContributesAndroidInjector(modules = [ViewFlashcardSetActivityModule::class])
    abstract fun injectLanguageActivity() : ViewFlashcardSetActivity

    @ContributesAndroidInjector(modules = [EditFlashcardActivityModule::class])
    abstract fun injectEditFlashcardActivity() : EditFlashcardActivity

    @ContributesAndroidInjector(modules = [MenuActivityModule::class])
    abstract fun injectMenuActivity() : MenuActivity

    @ContributesAndroidInjector(modules = [ViewFlashcardListActivityModule::class])
    abstract fun injectViewFlashCardListActivity() : ViewFlashCardListActivity


    @ContributesAndroidInjector(modules = [SetUpAccountActivityModule::class])
    abstract fun ịnjectSetUpAccountActivity() : SetUpAccountActivity

    @ContributesAndroidInjector(modules = [SearchOnlineActivityModule::class])
    abstract fun ịnjectSearchVocabularyActivity() : SearchOnlineActivity

    @ContributesAndroidInjector(modules = [UseFlashcardActivityModule::class])
    abstract fun ịnjectUseFlashcardActivity() : UseFlashcardActivity

    @ContributesAndroidInjector(modules = [UseFlashcardDoneActivityModule::class])
    abstract fun injectUseFlashcardDoneActivity(): ResultReportActivity

    @ContributesAndroidInjector(modules = [SearchEngVNDictionaryModule::class])
    abstract fun injectSearchEngVNActivity() : SearchEngVNDictionaryActivity

    @ContributesAndroidInjector(modules = [SeeVocabularyActivityModule::class])
    abstract fun injectSeeVocabularyAcitivty() : SeeVocabularyActivity

    @ContributesAndroidInjector(modules = [ReviewFlashcardActivityModule::class])
    abstract fun injectReviewFlashcardActivity() : ReviewFlashcardActivity

    @ContributesAndroidInjector(modules = [ReviewFlashcardEasyActivityModule::class])
    abstract fun injectReviewFlashcardEasyActivity() : ReviewFlashcardEasyActivity

}
package com.teamttdvlp.memolang.view.Activity.di.builder

import com.teamttdvlp.memolang.view.Activity.*
import com.teamttdvlp.memolang.view.Activity.di.module.activity.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [AuthActivityModule::class])
    abstract fun injectAuthActivity() : AuthActivity

    @ContributesAndroidInjector(modules = [SignUpActivityModule::class])
    abstract fun injectSignUpActivity() : SignUpActivity

    @ContributesAndroidInjector(modules = [AddFlashcardActivityModule::class])
    abstract fun injectAddFlashcardActivity() : AddFlashcardActivity

    @ContributesAndroidInjector
    abstract fun injectLanguageActivity() : LanguageActivity

    @ContributesAndroidInjector(modules = [EditFlashcardActivityModule::class])
    abstract fun injectEditFlashcardActivity() : EditFlashcardActivity

    @ContributesAndroidInjector(modules = [MenuActivityModule::class])
    abstract fun injectMenuActivity() : MenuActivity

    @ContributesAndroidInjector
    abstract fun injectViewFlashCardListActivity() : ViewFlashCardListActivity

    @ContributesAndroidInjector
    abstract fun injectSearchEditFlashcardActivity() : SearchEditFlashcardActivity

}
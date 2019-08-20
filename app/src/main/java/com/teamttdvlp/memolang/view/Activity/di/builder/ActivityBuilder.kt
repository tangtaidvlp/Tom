package com.teamttdvlp.memolang.view.Activity.di.builder

import com.teamttdvlp.memolang.view.Activity.*
import com.teamttdvlp.memolang.view.Activity.di.module.activity.AddFlashcardActivityModule
import com.teamttdvlp.memolang.view.Activity.di.module.activity.AuthActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [AuthActivityModule::class])
    abstract fun injectAuthActivity() : AuthActivity

    @ContributesAndroidInjector
    abstract fun injectSignUpActivity() : SignUpActivity

    @ContributesAndroidInjector(modules = [AddFlashcardActivityModule::class])
    abstract fun injectAddFlashcardActivity() : AddFlashcardActivity

    @ContributesAndroidInjector
    abstract fun injectLanguageActivity() : LanguageActivity

    @ContributesAndroidInjector
    abstract fun injectEditFlashcardActivity() : EditFlashcardActivity

    @ContributesAndroidInjector
    abstract fun injectMenuActivity() : MenuActivity

    @ContributesAndroidInjector
    abstract fun injectViewFlashCardListActivity() : ViewFlashCardListActivity

    @ContributesAndroidInjector
    abstract fun injectSearchEditFlashcardActivity() : SearchEditFlashcardActivity

}
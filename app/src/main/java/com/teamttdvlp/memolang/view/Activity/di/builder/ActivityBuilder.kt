package com.teamttdvlp.memolang.view.Activity.di.builder

import com.teamttdvlp.memolang.view.Activity.*
import com.teamttdvlp.memolang.view.Activity.di.module.activity.AuthModule
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.intellij.lang.annotations.Language

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [AuthModule::class])
    abstract fun injectAuthActivity() : AuthActivity

    @ContributesAndroidInjector
    abstract fun injectSignUpActivity() : SignUpActivity

    @ContributesAndroidInjector
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
package com.teamttdvlp.memolang.view.Activity.di.component

import android.app.Application
import com.teamttdvlp.memolang.view.Activity.di.MemoLang
import com.teamttdvlp.memolang.view.Activity.di.builder.ActivityBuilder
import com.teamttdvlp.memolang.view.Activity.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = [AndroidInjectionModule::class, ActivityBuilder::class,AppModule::class])
interface AppComponent {

    fun inject (app :  MemoLang)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

}
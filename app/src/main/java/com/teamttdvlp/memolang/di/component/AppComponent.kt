package com.teamttdvlp.memolang.di.component

import android.app.Application
import com.teamttdvlp.memolang.di.MemoLang
import com.teamttdvlp.memolang.di.builder.ActivityBuilder
import com.teamttdvlp.memolang.di.builder.ServiceBuilder
import com.teamttdvlp.memolang.di.module.AnimationModule
import com.teamttdvlp.memolang.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class,
    ActivityBuilder::class, ServiceBuilder::class,
    AppModule::class, AnimationModule::class])
interface AppComponent {

    fun inject (app : MemoLang)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application (app : MemoLang) : Builder

        fun build () : AppComponent
    }

}

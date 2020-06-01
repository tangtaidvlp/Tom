package com.teamttdvlp.memolang.di.builder

import com.teamttdvlp.memolang.di.module.activity.FloatAddServiceModule
import com.teamttdvlp.memolang.view.customview.floating_library.FloatingAddServiceManager
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBuilder {

    @ContributesAndroidInjector(modules = [FloatAddServiceModule::class])
    abstract fun injectFloatingAddService () : FloatingAddServiceManager.FloatAddService

}
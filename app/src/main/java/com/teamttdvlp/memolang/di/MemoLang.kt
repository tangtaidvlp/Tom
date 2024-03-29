package com.teamttdvlp.memolang.di

import android.app.Application
import com.teamttdvlp.memolang.di.component.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
//import leakcanary.LeakCanary
import javax.inject.Inject

class MemoLang : Application(), HasAndroidInjector {

    var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Any>? = null
    @Inject set

    override fun androidInjector(): DispatchingAndroidInjector<Any> {
        return activityDispatchingAndroidInjector!!
    }

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent
            .builder()
            .application(this)
            .build()
            .inject(this)
    }

}
package com.teamttdvlp.memolang.view.Activity.di.module.activity

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.Activity.MenuActivity
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class MenuActivityModule {

    @Provides
    @Named("AppearThenDisappearAnimation")
    fun provideAppearAnim (activity: MenuActivity) : Animation {
        return AnimationUtils.loadAnimation(activity, R.anim.appear_then_disappear)
    }

}
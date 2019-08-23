package com.teamttdvlp.memolang.view.Activity.di.module.activity

import android.animation.Animator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.vectordrawable.graphics.drawable.AnimatorInflaterCompat
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.Activity.SignUpActivity
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class SignUpActivityModule {

    @Provides
    @Named("RotateForever")
    fun provideRotateAnimation (activity : SignUpActivity) : Animation {
        return AnimationUtils.loadAnimation(activity, R.anim.rotate_forever)
    }

    @Provides
    @Named("FromNormalSizeToNothing")
    fun provideFromNormalSizeToNothing_Animator(activity: SignUpActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.zoom_from_normal_size_to_nothing
        )
    }

    @Provides
    @Named("FromNothingToNormalSize")
    fun provideFromNothingToNormalSize_Animator(activity: SignUpActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity, R.animator.zoom_from_nothing_to_normal_size
        )
    }

    @Provides
    @Named("Disappear100percents")
    fun provideDisappear100percents_Animator(activity: SignUpActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity, R.animator.disappear_100_percents
        )
    }

    @Provides
    @Named("Appear100percents")
    fun provideAppear100percents_Animator(activity: SignUpActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity, R.animator.appear_100_percents
        )
    }

    @Provides
    @Named("Vibrate")
    fun provideVibrate_Animation(activity: SignUpActivity): Animation{
        return AnimationUtils.loadAnimation(activity, R.anim.vibrate)
    }


}
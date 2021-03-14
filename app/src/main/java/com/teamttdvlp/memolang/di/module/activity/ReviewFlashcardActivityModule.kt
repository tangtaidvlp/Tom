package com.teamttdvlp.memolang.di.module.activity

import android.animation.Animator
import android.animation.AnimatorInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.activity.WritingFlashcardActivity
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ReviewFlashcardActivityModule {

    @Provides
    @Named("VibrateAnim")
    fun provideVibrate_Animation(activity: WritingFlashcardActivity): Animation {
        return AnimationUtils.loadAnimation(activity, R.anim.vibrate)
    }

    @Provides
    @Named("AppearAnim")
    fun provideAppearAnimation(activity: WritingFlashcardActivity): Animation {
        return AnimationUtils.loadAnimation(activity, R.anim.appear)
    }

    @Provides
    @Named("DisappearAnim")
    fun provideDisappearAnimation(activity: WritingFlashcardActivity): Animation {
        return AnimationUtils.loadAnimation(activity, R.anim.disappear)
    }

    @Provides
    @Named("ZoomTo0.5XAndFadeOut")
    fun provideZoomSmallerAndFadeOut(activity: WritingFlashcardActivity) : Animator{
        return AnimatorInflater.loadAnimator(activity, R.animator.zoom_to_haft_and_fade_out).apply {
            duration = 200
            interpolator = FastOutLinearInInterpolator()
        }
    }

    @Provides
    @Named("ZoomFrom0.5XTo1XAndFadeIn")
    fun provideZoomToNormalSizeAndFadeIn (activity: WritingFlashcardActivity) : Animator{
        return AnimatorInflater.loadAnimator(activity, R.animator.zoom_from_haft_to_normal_size_and_fade_in).apply {
            duration = 200
            interpolator = FastOutLinearInInterpolator()
        }
    }
}
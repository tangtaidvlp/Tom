package com.teamttdvlp.memolang.di.module.activity

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.activity.ReviewFlashcardActivity
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ReviewFlashcardActivityModule {

    @Provides
    @Named("VibrateAnim")
    fun provideVibrate_Animation(activity: ReviewFlashcardActivity): Animation {
        return AnimationUtils.loadAnimation(activity, R.anim.vibrate)
    }

    @Provides
    @Named("AppearAnim")
    fun provideAppearAnimation(activity: ReviewFlashcardActivity): Animation {
        return AnimationUtils.loadAnimation(activity, R.anim.appear)
    }

    @Provides
    @Named("DisappearAnim")
    fun provideDisappearAnimation(activity: ReviewFlashcardActivity): Animation {
        return AnimationUtils.loadAnimation(activity, R.anim.disappear)
    }

    @Provides
    @Named("Float")
    fun provideFloatAnim () : Animator {
        return ValueAnimator.ofFloat(0f, 15f)
    }

    @Provides
    @Named("MoveRight120%AndFadeOut")
    fun provideMoveRightAndFadeOutAnim (activity: ReviewFlashcardActivity) : Animator {
        return AnimatorInflater.loadAnimator(activity, R.animator.move_right_and_fade_out).apply {
            duration = 400
            interpolator = FastOutLinearInInterpolator()
        }
    }

    @Provides
    @Named("ZoomTo0.5XAndFadeOut")
    fun provideZoomSmallerAndFadeOut(activity: ReviewFlashcardActivity) : Animator{
        return AnimatorInflater.loadAnimator(activity, R.animator.zoom_to_haft_and_fade_out).apply {
            duration = 200
            interpolator = FastOutLinearInInterpolator()
        }
    }

    @Provides
    @Named("ZoomFrom0.5XTo1XAndFadeIn")
    fun provideZoomToNormalSizeAndFadeIn (activity: ReviewFlashcardActivity) : Animator{
        return AnimatorInflater.loadAnimator(activity, R.animator.zoom_from_haft_to_normal_size_and_fade_in).apply {
            duration = 200
            interpolator = FastOutLinearInInterpolator()
        }
    }
}
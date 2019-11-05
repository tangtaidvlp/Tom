package com.teamttdvlp.memolang.di.module.activity

import android.animation.*
import android.view.animation.DecelerateInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.activity.UseFlashcardActivity
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class UseFlashcardActivityModule {

    val animDuration = 500L

    val animInterpolator = DecelerateInterpolator()

    @Provides
    @Named("FlipClose")
    fun provideFlipCloseAnimator (activity: UseFlashcardActivity) : Animator {
        return AnimatorInflater.loadAnimator(activity, R.animator.from_normal_y_size_to_nothing).apply {
            interpolator = animInterpolator
        }
    }

    @Provides
    @Named("FlipOpen")
    fun provideFlipOpenAnimator (activity: UseFlashcardActivity) : Animator {
        return AnimatorInflater.loadAnimator(activity, R.animator.from_nothing_y_to_normal_size).apply {
            interpolator = animInterpolator
        }
    }

    @Provides
    @Named("Disappear100Percents")
    fun provideFadeOutAnim (activity : UseFlashcardActivity) : Animator {
        return AnimatorInflater.loadAnimator(activity, R.animator.disappear_100_percents).apply {
            duration = 250
            interpolator = animInterpolator
        }
    }

    @Provides
    @Named("Appear100Percents")
    fun provideFadeInAnim (activity : UseFlashcardActivity) : Animator {
        return AnimatorInflater.loadAnimator(activity, R.animator.appear_100_percents).apply {
            duration = 250
            interpolator = animInterpolator
        }
    }

    @Provides
    @Named("Float")
    fun provideFloatAnim () : Animator {
        return ValueAnimator.ofFloat(0f, 15f).apply {
            duration = animDuration
            interpolator = animInterpolator
        }
    }

    @Provides
    @Named("Sink")
    fun provideSinkAnim () : Animator {
        return ValueAnimator.ofFloat(15f, 0f).apply {
            duration = animDuration
            interpolator = animInterpolator
        }
    }

    @Provides
    @Named("FromRightToCentreAndFadeIn")
    fun provideFromRightToCentre(activity: UseFlashcardActivity): Animator {
        return AnimatorInflater.loadAnimator(activity, R.animator.from_right_to_center_and_fade_in)
    }

    @Provides
    @Named("MoveRightAndFadeOut")
    fun provideMoveRightAndFadeOut (activity: UseFlashcardActivity): Animator {
        return AnimatorInflater.loadAnimator(activity, R.animator.move_right_and_fade_out)
    }



}

package com.teamttdvlp.memolang.di.module.activity

import android.animation.*
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.activity.UseFlashcardActivity
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class UseFlashcardActivityModule {

    val animDuration = 200L

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
    @Named("ScaleBiggerAndFadeOut")
    fun provideScaleBiggerAndFadeOut (activity: UseFlashcardActivity) : Animator{
        return AnimatorInflater.loadAnimator(activity, R.animator.zoom_bigger_and_fade_out)
    }

}

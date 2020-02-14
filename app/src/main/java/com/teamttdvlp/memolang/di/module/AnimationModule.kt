package com.teamttdvlp.memolang.di.module

import android.animation.Animator
import android.animation.AnimatorInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.di.MemoLang
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class AnimationModule {

    @Provides
    @Named("AppearThenDisappearAnimation")
    fun provideAppearAnim (context: MemoLang) : Animation {
        return AnimationUtils.loadAnimation(context, R.anim.appear_then_disappear).apply {            interpolator = FastOutLinearInInterpolator()
        }
    }

    @Provides
    @Named("QuickAppearThenDisappearAnimation")
    fun provideQuickAppearAnim (context: MemoLang) : Animation {
        return AnimationUtils.loadAnimation(context, R.anim.quick_appear_then_disappear).apply {
            interpolator = FastOutLinearInInterpolator()
        }
    }

    @Provides
    @Named("FromNormalSizeToNothing")
    fun provideFromNormalSizeToNothingAnimator (context : MemoLang) : Animator {
        return AnimatorInflater.loadAnimator(context, R.animator.zoom_from_normal_size_to_nothing)
    }

    @Provides
    @Named("FromNothingToNormalSize")
    fun provideFromNothingToNormalSizeAnimator (context : MemoLang) : Animator {
        return AnimatorInflater.loadAnimator(context, R.animator.zoom_from_nothing_to_normal_size)
    }

    @Provides
    @Named("Appear100Percents")
    fun provideAppear100PercentsAnimator (context : MemoLang) : Animator {
        return AnimatorInflater.loadAnimator(context, R.animator.appear_100_percents)
    }

    @Provides
    @Named("Disappear100Percents")
    fun provideDisappear100PercentsAnimator (context : MemoLang) : Animator {
        return AnimatorInflater.loadAnimator(context, R.animator.disappear_100_percents)
    }

    @Provides
    @Named("Appear50Percents")
    fun provideAppear50PercentsAnimator (context : MemoLang) : Animator {
        return AnimatorInflater.loadAnimator(context, R.animator.appear_50_percents)
    }

    @Provides
    @Named("Disappear50Percents")
    fun provideDisappear50PercentsAnimator (context : MemoLang) : Animator {
        return AnimatorInflater.loadAnimator(context, R.animator.disappear_50_percents)
    }

    @Provides
    @Named("ZoomFromNothingToOversizeThenNormalSize")
    fun provideZoomFromNothingToOversizeThenNormalSize (context : MemoLang) : Animator {
        return AnimatorInflater.loadAnimator(context, R.animator.zoom_from_nothing_to_oversize_then_normal_size)
    }

    @Provides
    @Named("MoveRightAndFadeOutAnimtr")
    fun provideMoveRightAndFadeOut (context : MemoLang) : Animator {
        return AnimatorInflater.loadAnimator(context, R.animator.move_right_and_fade_out)
    }

    @Provides
    @Named("ZoomToNothingAndRotate")
    fun provideZoomToNothingAndRotate(context : MemoLang) : Animator {
        return AnimatorInflater.loadAnimator(context, R.animator.zoom_to_nothing_and_rotate)
    }
}
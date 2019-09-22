package com.teamttdvlp.memolang.di.module.activity

import android.animation.Animator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.vectordrawable.graphics.drawable.AnimatorInflaterCompat
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.activity.AddFlashcardActivity
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class AddFlashcardActivityModule() {

    @Provides
    @Named("HighlightTextAnim")
    fun provideHighlightTextAnim(activity: AddFlashcardActivity): Animation {
        return AnimationUtils.loadAnimation(activity, R.anim.hightlight_text)
    }

    @Provides
    @Named("FromNormalSizeToNothing_")
    fun provideFromNormalSizeToNothing_Animator(activity: AddFlashcardActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.zoom_from_normal_size_to_nothing
        )
    }

    @Provides
    @Named("Disappear50percents_")
    fun provideBlackBackgroundDisappear_Animator(activity: AddFlashcardActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.disappear_50_percents
        )
    }

    @Provides
    @Named("FromNothingToNormalSize_")
    fun provideViewgroupCancelSavingScaleBigger_Animator(activity: AddFlashcardActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity, R.animator.zoom_from_nothing_to_normal_size
        )
    }

    @Provides
    @Named("Appear50percents_")
    fun provideBlackBackgroundAppear_Animator(activity: AddFlashcardActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.appear_50_percents
        )
    }

    @Provides
    @Named("ZoomToNothingAndRotate_")
    fun provideZoomToNothingAndRotate_Animator(activity: AddFlashcardActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(activity
            , R.animator.zoom_to_nothing_and_rotate
        )

    }

    @Provides
    @Named("ZoomFromNothingToOversizeThenNormalSize_")
    fun provideZoomFromNothingToOversizeThenNormalSize_Animator(activity: AddFlashcardActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(activity
            , R.animator.zoom_from_nothing_to_oversize_then_normal_size
        )
    }

    @Provides
    @Named("MoveRightAndFadeOut_")
    fun provideMoveRightAndFadeOut_Animator(activity: AddFlashcardActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(activity
            , R.animator.move_right_and_fade_out
        )
    }

}
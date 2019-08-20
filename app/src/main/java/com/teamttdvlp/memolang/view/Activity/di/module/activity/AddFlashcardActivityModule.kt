package com.teamttdvlp.memolang.view.Activity.di.module.activity

import android.animation.Animator
import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.vectordrawable.graphics.drawable.AnimatorInflaterCompat
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.Activity.AddFlashcardActivity
import com.teamttdvlp.memolang.view.Activity.di.MemoLang
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class AddFlashcardActivityModule() {

    @Provides
    fun provideApp (app : MemoLang) : Context {
        return app
    }

    @Provides
    fun provideHighlightTextAnim(activity: AddFlashcardActivity): Animation {
        return AnimationUtils.loadAnimation(activity, R.anim.hightlight_text)
    }

    @Provides
    @Named("FromNormalSizeToNothing")
    fun provideFromNormalSizeToNothing_Animator(activity: AddFlashcardActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.zoom_from_normal_size_to_nothing
        )
    }

    @Provides
    @Named("Disappear50percents")
    fun provideBlackBackgroundDisappear_Animator(activity: AddFlashcardActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.disappear_50_percents
        )
    }

    @Provides
    @Named("FromNothingToNormalSize")
    fun provideViewgroupCancelSavingScaleBigger_Animator(activity: AddFlashcardActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity, R.animator.zoom_from_nothing_to_normal_size
        )
    }

    @Provides
    @Named("Appear50percents")
    fun provideBlackBackgroundAppear_Animator(activity: AddFlashcardActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.appear_50_percents
        )
    }

    @Provides
    @Named("ZoomToNothingAndRotate")
    fun provideZoomToNothingAndRotate_Animator(activity: AddFlashcardActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(activity
            , R.animator.zoom_to_nothing_and_rotate
        )

    }

    @Provides
    @Named("ZoomFromNothingToOversizeThenNormalSize")
    fun provideZoomFromNothingToOversizeThenNormalSize_Animator(activity: AddFlashcardActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(activity
            , R.animator.zoom_from_nothing_to_oversize_then_normal_size
        )
    }

    @Provides
    @Named("MoveRightAndFadeOut")
    fun provideMoveRightAndFadeOut_Animator(activity: AddFlashcardActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(activity
            , R.animator.move_right_and_fade_out
        )
    }

}
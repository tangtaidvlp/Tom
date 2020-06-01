package com.teamttdvlp.memolang.di.module.activity

import android.animation.Animator
import androidx.vectordrawable.graphics.drawable.AnimatorInflaterCompat
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.activity.SetUpAccountActivity
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class SetUpAccountActivityModule {

    @Provides
    fun provideRCVChooseLanguageAdapter (context : SetUpAccountActivity) : RCVChooseLanguageAdapter {
        return RCVChooseLanguageAdapter(context)
    }

    // ANIMATIONS

    @Provides
    @Named("_FromNormalSizeToNothing_")
    fun provideFromNormalSizeToNothing_Animator(activity: SetUpAccountActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.zoom_from_normal_size_to_nothing
        )
    }

    @Provides
    @Named("_Disappear50percents_")
    fun provideBlackBackgroundDisappear_Animator(activity: SetUpAccountActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.disappear_50_percents
        )
    }

    @Provides
    @Named("_FromNothingToNormalSize_")
    fun provideViewgroupCancelSavingScaleBigger_Animator(activity: SetUpAccountActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity, R.animator.zoom_from_nothing_to_normal_size
        )
    }

    @Provides
    @Named("_Appear50percents_")
    fun provideBlackBackgroundAppear_Animator(activity: SetUpAccountActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.appear_50_percents
        )
    }


}
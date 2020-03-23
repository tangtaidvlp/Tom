package com.teamttdvlp.memolang.di.module.activity

import android.animation.Animator
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatorInflaterCompat
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.activity.MenuActivity
import com.teamttdvlp.memolang.view.adapter.RCVRecent_USE_FlashcardAdapter
import com.teamttdvlp.memolang.view.customview.MenuBackgroundListViewAdapter
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class MenuActivityModule {

    @Provides
    fun provideAdapterBackground (activity : MenuActivity) : MenuBackgroundListViewAdapter {
        return MenuBackgroundListViewAdapter(activity)
    }

    @Provides
    fun providerRecentUseFlashcardAdapter (activity : MenuActivity) : RCVRecent_USE_FlashcardAdapter{
        return RCVRecent_USE_FlashcardAdapter(activity)
    }

    @Provides
    fun provideLayoutManager (activity: MenuActivity) : RecyclerView.LayoutManager {
        return LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
    }


    @Provides
    @Named("Menu_FromNormalSizeToNothing")
    fun provideFromNormalSizeToNothing_Animator(activity: MenuActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.zoom_from_normal_size_to_nothing
        ).apply {
            duration = 200
            interpolator = FastOutLinearInInterpolator()
        }
    }

    @Provides
    @Named("Menu_Disappear50Percents")
    fun provideBlackBackgroundDisappear_Animator(activity: MenuActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.disappear_50_percents
        ).apply {
            duration = 200
            interpolator = FastOutLinearInInterpolator()
        }
    }

    @Provides
    @Named("Menu_FromNothingToNormalSize")
    fun provideViewgroupCancelSavingScaleBigger_Animator(activity: MenuActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity, R.animator.zoom_from_nothing_to_normal_size
        ).apply {
            duration = 200
            interpolator = FastOutLinearInInterpolator()
        }
    }

    @Provides
    @Named("Menu_Appear50Percents")
    fun provideBlackBackgroundAppear_Animator(activity: MenuActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.appear_50_percents
        ).apply {
            duration = 200
            interpolator = FastOutLinearInInterpolator()
        }
    }

}
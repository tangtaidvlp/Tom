package com.teamttdvlp.memolang.di.module.activity

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatorInflaterCompat
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.activity.SearchVocabularyActivity
import com.teamttdvlp.memolang.view.activity.adapter.ChooseLanguageRCVAdapter
import com.teamttdvlp.memolang.view.activity.adapter.RecentSearchFlashcardRCVAdapter
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class SearchVocabularyActivityModule  {

    @Provides
    @Named("ViewGroupLanguageOptionShowAnimator")
    fun provideViewGroupLangOption_ShowAnimator(
        @Named("ViewGroupLangOptionHeight") viewgroupLangOptionHeight : Float,
        duration : Long, interpolator: Interpolator) : ValueAnimator {
        return ValueAnimator.ofInt(1, viewgroupLangOptionHeight.toInt()).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }

    @Provides
    @Named("ViewGroupLanguageOptionHideAnimator")
    fun provideViewGroupLangOption_HideAnimator(
        @Named("ViewGroupLangOptionHeight") viewgroupLangOptionHeight : Float,
        duration : Long, interpolator: Interpolator) : ValueAnimator {
        return ValueAnimator.ofInt(viewgroupLangOptionHeight.toInt(), 1).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }


    @Provides
    @Named("AppearAnimator")
    fun provideAppearAnimator (activity: SearchVocabularyActivity, duration : Long, interpolator: Interpolator) : Animator {
        return AnimatorInflaterCompat.loadAnimator(activity, R.animator.appear_100_percents).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }


    @Provides
    @Named("DisappearAnimator")
    fun provideDisappearAnimator (activity: SearchVocabularyActivity, duration : Long, interpolator: Interpolator) : Animator{
        return AnimatorInflaterCompat.loadAnimator(activity, R.animator.disappear_100_percents).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }


    @Provides
    @Named("ScaleSmallerAnimator")
    fun provideScaleSmallerAnimator (
        @Named("ViewGroupLangOptionHeight") viewgroupLangOptionHeight : Float,
        duration: Long, interpolator: Interpolator) : ValueAnimator{
        return ValueAnimator.ofInt(viewgroupLangOptionHeight.toInt(), 1).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }


    @Provides
    @Named("ScaleBiggerAnimator")
    fun provideScaleBiggerAnimator (
        @Named("ViewGroupLangOptionHeight") viewgroupLangOptionHeight : Float,
        duration: Long, interpolator: Interpolator) : ValueAnimator{
        return ValueAnimator.ofInt(1, viewgroupLangOptionHeight.toInt()).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }


    @Provides
    @Named("FromNothingToNormalSizeAnimator")
    fun provideFromNothingToNormalSizeAnimator (
        @Named("AddButtonAnimationsDuration") duration: Long,
        interpolator: Interpolator,
        activity: SearchVocabularyActivity) : Animator {
        return AnimatorInflaterCompat.loadAnimator(activity, R.animator.zoom_from_nothing_to_normal_size).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }


    @Provides
    @Named("FromNormalSizeToNothingAnimator")
    fun provideFromNormalSizeToNothingAnimator (
        @Named("AddButtonAnimationsDuration") duration: Long,
        interpolator: Interpolator,
        activity: SearchVocabularyActivity) : Animator {
        return AnimatorInflaterCompat.loadAnimator(activity, R.animator.zoom_from_normal_size_to_nothing).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }


    @Provides
    @Named("EditTextTextScaleSmallerAnimator")
    fun provideEditTextTextScaleSmallerAnimator (
        @Named ("EditTextTextHeight") edtTextHeight : Float,
        @Named ("ViewGroupLangOptionHeight") expectedHeight : Float,
        duration: Long, interpolator: Interpolator) : ValueAnimator {
        return ValueAnimator.ofInt(edtTextHeight.toInt(), expectedHeight.toInt()).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }


    @Provides
    @Named("EditTextTextScaleBiggerAnimator")
    fun provideEditTextTextScaleBiggerAnimator (
        @Named ("EditTextTextHeight") expectedHeight : Float,
        @Named ("ViewGroupLangOptionHeight") currentHeight : Float,
        duration: Long, interpolator: Interpolator) : ValueAnimator {
        return ValueAnimator.ofInt(currentHeight.toInt(), expectedHeight.toInt()).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }

    @Provides
    @Named("MoveRightAndFadeOut")
    fun provideMoveRightAndFadeOutAnimation (activity: SearchVocabularyActivity) : Animation {
        return AnimationUtils.loadAnimation(activity, R.anim.move_right_and_fade_out)
    }


    @Provides
    @Named("MoveLeftAndFadeOut")
    fun provideMoveLeftAndFadeOutAnimation (activity: SearchVocabularyActivity) : Animation {
        return AnimationUtils.loadAnimation(activity, R.anim.move_left_and_fade_out)
    }

    @Provides
    @Named("FromNormalSizeToNothing_")
    fun provideFromNormalSizeToNothing_Animator(activity: SearchVocabularyActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.zoom_from_normal_size_to_nothing
        )
    }

    @Provides
    @Named("Disappear50percents_")
    fun provideBlackBackgroundDisappear_Animator(activity: SearchVocabularyActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.disappear_50_percents
        )
    }

    @Provides
    @Named("FromNothingToNormalSize_")
    fun provideViewgroupCancelSavingScaleBigger_Animator(activity: SearchVocabularyActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity, R.animator.zoom_from_nothing_to_normal_size
        )
    }

    @Provides
    @Named("Appear50percents_")
    fun provideBlackBackgroundAppear_Animator(activity: SearchVocabularyActivity): Animator {
        return AnimatorInflaterCompat.loadAnimator(
            activity
            , R.animator.appear_50_percents
        )
    }


    /**
     * Provides Stuffs
     */

    @Provides
    fun provideRecentSearchFlashcardAdapter (activity: SearchVocabularyActivity) : RecentSearchFlashcardRCVAdapter {
        return RecentSearchFlashcardRCVAdapter(activity)
    }

    @Provides
    fun provideChooseLanguageAdapter (activity: SearchVocabularyActivity) : ChooseLanguageRCVAdapter{
        return ChooseLanguageRCVAdapter(activity)
    }

    @Provides
    fun provideLayoutManager (activity: SearchVocabularyActivity) : LinearLayoutManager {
        return LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
    }

    /**
     * Provides Animations Properties
     */

    @Provides
    @Named("ViewGroupLangOptionHeight")
    fun provideViewGroupHeight (activity: SearchVocabularyActivity) : Float{
        return activity.dataBinding.viewgroupLanguageOption.layoutParams.height.toFloat()
    }

    @Provides
    @Named("EditTextTextHeight")
    fun provideTextViewTextHeight  (activity: SearchVocabularyActivity) : Float {
        return activity.dataBinding.edtText.layoutParams.height.toFloat()
    }


    @Provides
    fun provideAllAnimationInterpolator () : Interpolator {
        return FastOutSlowInInterpolator()
    }

    @Provides
    fun provideDuration () : Long {
        return 700
    }

    @Provides
    @Named("AddButtonAnimationsDuration")
    fun provideAddButtonAnimations () : Long {
        return 250
    }


}

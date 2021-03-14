package com.teamttdvlp.memolang.di.module.activity

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.activity.SearchOnlineActivity
import com.teamttdvlp.memolang.view.adapter.*
import com.teamttdvlp.memolang.view.helper.dp
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class SearchOnlineActivityModule  {

    @Provides
    fun provideRecentUsedLanguageAdapter (activity : SearchOnlineActivity) : RCVRecentUsedLanguageAdapter{
        return RCVRecentUsedLanguageAdapter(activity)
    }

    @Provides
    @Named("MoveUpAndAppear")
    fun provideMoveUpAndAppear_Animator (activity: SearchOnlineActivity) : Animator {
        return AnimatorInflater.loadAnimator(activity
            , R.animator.move_up_and_fade_in)
    }

    @Provides
    @Named("ViewGroupLanguageOptionShowAnimator")
    fun provideViewGroupLangOption_ShowAnimator(
        @Named("ViewGroupLangOptionHeight") viewgroupLangOptionHeight : Float,
        GBDRduration : Long, interpolator: Interpolator) : ValueAnimator {
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
    fun provideAppearAnimator (activity: SearchOnlineActivity, duration : Long, interpolator: Interpolator) : Animator {
        return AnimatorInflater.loadAnimator(activity, R.animator.appear_100_percents).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }


    @Provides
    @Named("DisappearAnimator")
    fun provideDisappearAnimator (activity: SearchOnlineActivity, duration : Long, interpolator: Interpolator) : Animator{
        return AnimatorInflater.loadAnimator(activity, R.animator.disappear_100_percents).apply {
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
        activity: SearchOnlineActivity) : Animator {
        return AnimatorInflater.loadAnimator(activity, R.animator.zoom_from_nothing_to_normal_size)
            .apply {
                this.duration = duration
                this.interpolator = interpolator
            }
    }


    @Provides
    @Named("FromNormalSizeToNothingAnimator")
    fun provideFromNormalSizeToNothingAnimator (
        @Named("AddButtonAnimationsDuration") duration: Long,
        interpolator: Interpolator,
        activity: SearchOnlineActivity) : Animator {
        return AnimatorInflater.loadAnimator(activity, R.animator.zoom_from_normal_size_to_nothing)
            .apply {
                this.duration = duration
                this.interpolator = interpolator
            }
    }


    @Provides
    @Named("EditTextTextScaleSmallerAnimator")
    fun provideEditTextTextScaleSmallerAnimator (
        @Named ("EditTextTextHeight") expectedHeight : Float,
        @Named ("ViewGroupLangOptionHeight") currentHeight : Float,
        duration: Long, interpolator: Interpolator) : ValueAnimator {
        return ValueAnimator.ofInt(currentHeight.toInt(), expectedHeight.toInt()).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }

    @Provides
    @Named("ViewgroupLangOptionHide")
    fun provideViewGroupLangHide (
        @Named ("ViewGroupLangOptionHeight") currentHeight : Float
        , duration: Long, interpolator: Interpolator) : ValueAnimator {
        return ValueAnimator.ofInt(currentHeight.toInt(), 1.dp() / 2).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }

    @Provides
    @Named("TxtTranslationScaleToNothing")
    fun provideTxtTranslationDisappear (
        @Named("EditTextTextHeight") currentHeight: Float
    ) : ValueAnimator {
        return ValueAnimator.ofInt(currentHeight.toInt(), 2.dp()).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }

    @Provides
    @Named("TxtTranslationAppear")
    fun provideTxtTranslationAppear (
        @Named("EditTextTextHeight") expectedTxtTranslationHeight : Float
    ) : ValueAnimator {
        return ValueAnimator.ofInt(2.dp(), expectedTxtTranslationHeight.toInt()).apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }


    @Provides
    @Named("MoveRightAndFadeOut")
    fun provideMoveRightAndFadeOutAnimation (activity: SearchOnlineActivity) : Animation {
        return AnimationUtils.loadAnimation(activity, R.anim.move_right_and_fade_out)
    }


    @Provides
    @Named("MoveLeftAndFadeOut")
    fun provideMoveLeftAndFadeOutAnimation (activity: SearchOnlineActivity) : Animation {
        return AnimationUtils.loadAnimation(activity, R.anim.move_left_and_fade_out)
    }


    @Provides
    @Named("TranslatingText")
    fun provideTranslatingTextAnimation () : Animator {
        return ValueAnimator.ofFloat(0f, 1f)
    }


    @Provides
    fun provideRecentSearchFlashcardAdapter (activity: SearchOnlineActivity) : RCVRecent_Search_FlashcardAdapter {
        return RCVRecent_Search_FlashcardAdapter(activity)
    }

    @Provides
    fun provideChooseLanguageAdapter (activity: SearchOnlineActivity) : RCVChooseLanguageAdapter{
        return RCVChooseLanguageAdapter(activity)
    }


    @Provides
    fun provideChooseTypeAdapter(activity: SearchOnlineActivity) : RCVSimpleListAdapter2{
        return RCVSimpleListAdapter2(activity)
    }


    @Provides
    fun provideSimpleListChooseSetNameAdapter(activity: SearchOnlineActivity) : RCVSimpleListChooseSetNameAdapter{
        return RCVSimpleListChooseSetNameAdapter(activity)
    }


    @Provides
    fun provideLayoutManager (activity: SearchOnlineActivity) : LinearLayoutManager {
        return LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
    }


    /**
     * Provides Animations Properties
     */

    @Provides
    @Named("ViewGroupLangOptionHeight")
    fun provideViewGroupHeight (activity: SearchOnlineActivity) : Float{
        return 43.dp().toFloat()
    }

    @Provides
    @Named("EditTextTextHeight")
    fun provideTextViewTextHeight  (activity: SearchOnlineActivity) : Float {
        return  150.dp().toFloat()
    }


    @Provides
    fun provideAllAnimationInterpolator () : Interpolator {
        return FastOutSlowInInterpolator()
    }

    @Provides
    fun provideDuration () : Long {
        return 350
    }

    @Provides
    @Named("AddButtonAnimationsDuration")
    fun provideAddButtonAnimations () : Long {
        return 125
    }
}
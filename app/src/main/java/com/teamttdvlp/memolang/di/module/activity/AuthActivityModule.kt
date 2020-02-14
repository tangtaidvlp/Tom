package com.teamttdvlp.memolang.di.module.activity
//
//import android.animation.Animator
//import android.animation.ObjectAnimator
//import android.animation.PropertyValuesHolder
//import android.animation.ValueAnimator
//import android.view.View
//import android.view.animation.Animation
//import android.view.animation.AnimationUtils
//import androidx.interpolator.view.animation.FastOutSlowInInterpolator
//import androidx.vectordrawable.graphics.drawable.AnimatorInflaterCompat
//import com.teamttdvlp.memolang.R
//import com.teamttdvlp.memolang.view.activity.AuthActivity
//import dagger.Module
//import dagger.Provides
//import javax.inject.Named
//
//@Module
//class AuthActivityModule {
//
//    @Provides
//    @Named("RotateForever")
//    fun provideRotateAnimation (activity : AuthActivity) : Animation {
//        return AnimationUtils.loadAnimation(activity, R.anim.rotate_forever)
//    }
//
//    @Provides
//    @Named("FromNormalSizeToNothing")
//    fun provideFromNormalSizeToNothing_Animator(activity: AuthActivity): Animator {
//        return AnimatorInflaterCompat.loadAnimator(
//            activity
//            , R.animator.zoom_from_normal_size_to_nothing
//        )
//    }
//
//    @Provides
//    @Named("FromNothingToNormalSize")
//    fun provideFromNothingToNormalSize_Animator(activity: AuthActivity): Animator {
//        return AnimatorInflaterCompat.loadAnimator(
//            activity, R.animator.zoom_from_nothing_to_normal_size
//        )
//    }
//
//    @Provides
//    @Named("Disappear100percents")
//    fun provideDisappear100percents_Animator(activity: AuthActivity): Animator {
//        return AnimatorInflaterCompat.loadAnimator(
//            activity, R.animator.disappear_100_percents
//        )
//    }
//
//    @Provides
//    @Named("Appear100percents")
//    fun provideAppear100percents_Animator(activity: AuthActivity): Animator {
//        return AnimatorInflaterCompat.loadAnimator(
//            activity, R.animator.appear_100_percents
//        )
//    }
//
//    @Provides
//    @Named("Vibrate")
//    fun provideVibrate_Animation(activity: AuthActivity): Animation{
//        return AnimationUtils.loadAnimation(activity, R.anim.vibrate)
//    }
//
//    @Provides
//    @Named("Waiting")
//    fun provideWaiting_Animatior(activity: AuthActivity): Animator{
//        return ValueAnimator.ofInt(0, 2).apply {
//            duration = 500
//        }
//    }
//
//    @Provides
//    @Named("MoveRightAndFadeOut")
//    fun provideMoveRightAndFadeOut_Animation () : Animator {
//        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)
//        val x = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, 300f)
//        return ObjectAnimator.ofPropertyValuesHolder(alpha, x).apply {
//            duration = 200
//            interpolator = FastOutSlowInInterpolator()
//        }
//    }
//
//
//}
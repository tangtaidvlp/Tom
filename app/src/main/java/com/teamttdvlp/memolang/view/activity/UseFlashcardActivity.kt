package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.animation.addListener
import com.teamttdvlp.memolang.BR
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityUseFlashcardBinding
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.view.ui.OnSwipeUpListener
import com.teamttdvlp.memolang.viewmodel.use_flashcard_activity.UseFlashcardActivityViewModel
import javax.inject.Inject
import javax.inject.Named

class UseFlashcardActivity : BaseActivity<ActivityUseFlashcardBinding, UseFlashcardActivityViewModel>() {

    val onSwipeFrontCardAppearAnimatorSet = AnimatorSet ()

    val onSwipeBackCardDisappearAnimatorSet = AnimatorSet ()

    val onSwipeAnimatorSet = AnimatorSet()

    val onNextCardAnimatorSet = AnimatorSet()

    val onNextCardBackCardDisappearAnimatorSet = AnimatorSet()

    val onNextCardFrontCardAppearAnimatorSet = AnimatorSet()

    val onPrevCardAnimatorSet = AnimatorSet()

    val onPrevCardBackCardDisappearAnimatorSet = AnimatorSet()

    val onPrevCardFrontCardAppearAnimatorSet = AnimatorSet()

    val onPreviousCardAnimatorSet = AnimatorSet()

    override fun getLayoutId(): Int = R.layout.activity_use_flashcard

    override fun takeViewModel() : UseFlashcardActivityViewModel = getActivityViewModel()

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun initProperties() {
        dataBinding.setVariable(BR.viewModel, viewModel)
    }

    override fun addAnimationEvents() {

    }

    override fun addViewEvents() { dataBinding.apply {

        val swipeUpAnim = AnimationUtils.loadAnimation(this@UseFlashcardActivity, R.anim.move_up_fade_out_then_bottom_to_top_fade_out)

        swipeUpAnim.addAnimationLister (onEnd = {
            imgSwipeUp.startAnimation(swipeUpAnim)
        })

        imgSwipeUp.startAnimation(swipeUpAnim)

        viewgroupImgSwipeUp.setOnTouchListener(object : OnSwipeUpListener(this@UseFlashcardActivity) {
            override fun onSwipeUp() {
                onSwipeAnimatorSet.start()
                viewModel!!.updateHardCardLeft()
            }
        })

        btnEasy.setOnClickListener {
            onNextCardAnimatorSet.start()
        }

        btnHard.setOnClickListener {
            onNextCardAnimatorSet.start()
        }

        btnPreviousCard.setOnClickListener {
            onPrevCardAnimatorSet.start()
        }

    }}


    @Inject
    fun init_ON_SWIPE_CardFrontCardDisappearAnimation (
        @Named("Disappear100Percents") imgSwipeUpFadeOutAnimator : Animator,
        @Named("Disappear100Percents") txtTextFadeOutAnimator : Animator,
        @Named("Disappear100Percents") txtCardNumberFadeOutAnimator : Animator,
        @Named("FlipClose") flashcardFlipAnimator : Animator) { dataBinding.apply {

        imgSwipeUpFadeOutAnimator .setTarget(imgSwipeUp)
        flashcardFlipAnimator .setTarget(viewgroupFrontFlashcard)

        onSwipeFrontCardAppearAnimatorSet.play(imgSwipeUpFadeOutAnimator).with(flashcardFlipAnimator)
        onSwipeFrontCardAppearAnimatorSet.addListener (
            onStart = {
                groupFrontCard.appear()
            }, onEnd = {
                groupFrontCard.disappear()
            })
    }}

    @Inject
    fun init_ON_SWIPE_CardBackFlashcardAppearAnimation (
        @Named("FlipOpen") flashcardOpenAnimator : Animator,
        @Named("Appear100Percents") otherInfoAppear : Animator,
        @Named("Appear100Percents") buttonPreviousAppear : Animator,
        @Named("Appear100Percents") buttonEasyAppear : Animator,
        @Named("Appear100Percents") buttonHardAppear : Animator
    ) { dataBinding.apply {

        flashcardOpenAnimator.setTarget(viewgroupBackFlashcard)
        otherInfoAppear.setTarget(viewgroupCardOtherInfo)
        buttonEasyAppear.setTarget(btnEasy)
        buttonHardAppear.setTarget(btnHard)
        buttonPreviousAppear.setTarget(btnPreviousCard)

        val navigateAppearSet= AnimatorSet().apply {
            play(buttonEasyAppear).with(buttonHardAppear)
        }

        val otherInfoAppearSet= AnimatorSet().apply {
            play(otherInfoAppear).with(buttonPreviousAppear)
        }

        onSwipeBackCardDisappearAnimatorSet.playSequentially(flashcardOpenAnimator, otherInfoAppearSet, navigateAppearSet)
        onSwipeBackCardDisappearAnimatorSet.addListener(onStart = {
            groupBackCard.appear()
            viewgroupFrontFlashcard.elevation = 0f
        })
        onSwipeAnimatorSet.play(onSwipeFrontCardAppearAnimatorSet).before(onSwipeBackCardDisappearAnimatorSet)
    }}


    @Inject
    fun init_ON_NEXT_CardFrontCardAppearAnimation (
        @Named("Float") flashcardFloatAnimator : Animator,
        @Named("Appear100Percents") buttonSwipeUpAppearAnimator : Animator
    ) {dataBinding.apply {

        (flashcardFloatAnimator as ValueAnimator).addUpdateListener {
            viewgroupFrontFlashcard.elevation = it.animatedValue as Float
        }

        flashcardFloatAnimator.startDelay = 150

        flashcardFloatAnimator.addListener(
            onEnd = {
                viewgroupBackFlashcard.alpha = 1.0f
            })

        flashcardFloatAnimator.setTarget(dataBinding.viewgroupFrontFlashcard)

        buttonSwipeUpAppearAnimator.setTarget(imgSwipeUp)

        onNextCardFrontCardAppearAnimatorSet.play(flashcardFloatAnimator).with(buttonSwipeUpAppearAnimator)
        onNextCardFrontCardAppearAnimatorSet.addListener(onStart = {
            viewgroupFrontFlashcard.scaleX = 1.0f
            viewgroupFrontFlashcard.scaleY= 1.0f
            viewgroupFrontFlashcard.alpha = 1.0f
            groupFrontCard.appear()
        })
        onNextCardAnimatorSet.play(onNextCardBackCardDisappearAnimatorSet).before(onNextCardFrontCardAppearAnimatorSet)
    }}


    @Inject
    fun init_ON_NEXT_CardBackCardDisappearAnimation (
        @Named("MoveRightAndFadeOut") flashcardMoveOutAnimator : Animator,
        @Named("Disappear100Percents") otherInfoDisappear : Animator,
        @Named("Disappear100Percents") buttonPreviousDisappear : Animator,
        @Named("Disappear100Percents") buttonEasyDisappear : Animator,
        @Named("Disappear100Percents") buttonHardDisappear : Animator
    ) { dataBinding.apply {
        flashcardMoveOutAnimator.setTarget(viewgroupBackFlashcard)
        otherInfoDisappear.setTarget(viewgroupCardOtherInfo)
        buttonEasyDisappear.setTarget(btnEasy)
        buttonHardDisappear.setTarget(btnHard)
        buttonPreviousDisappear.setTarget(btnPreviousCard)

        val otherInfoDisappearSet = AnimatorSet().apply {
            play(otherInfoDisappear).with(buttonPreviousDisappear)
        }

        val navigateDisappearSet = AnimatorSet().apply {
            play(buttonEasyDisappear).with(buttonHardDisappear)
        }

        onNextCardBackCardDisappearAnimatorSet.playSequentially(flashcardMoveOutAnimator, otherInfoDisappearSet, navigateDisappearSet)
        onNextCardBackCardDisappearAnimatorSet.addListener(onEnd = {
            groupBackCard.disappear()
            viewgroupBackFlashcard.translationX = 0f
            viewgroupBackFlashcard.translationY = 0f

        })
    }}


    @Inject
    fun init_ON_PREV_CardFrontCardAppearAnimation (
        @Named("FromRightToCentreAndFadeIn") flashcardMoveFromRightToCentre: Animator,
        @Named("Appear100Percents") buttonSwipeUpAppear: Animator
    ) {dataBinding.apply {

        flashcardMoveFromRightToCentre.startDelay = 150

        flashcardMoveFromRightToCentre.addListener(onStart = {
            viewgroupFrontFlashcard.elevation = 15f
        },
            onEnd = {
                viewgroupBackFlashcard.alpha = 1.0f
            })

        flashcardMoveFromRightToCentre.setTarget(dataBinding.viewgroupFrontFlashcard)

        buttonSwipeUpAppear.setTarget(imgSwipeUp)

        onPrevCardFrontCardAppearAnimatorSet.play(flashcardMoveFromRightToCentre).with(buttonSwipeUpAppear)
        onPrevCardAnimatorSet.play(onPrevCardBackCardDisappearAnimatorSet).before(onPrevCardFrontCardAppearAnimatorSet)

    }}

    @Inject
    fun init_ON_PREV_CardBackCardDisappearAnimation (

        @Named("Sink") flashcardSink : Animator,
        @Named("Disappear100Percents") flashcardDisappear: Animator,
        @Named("Disappear100Percents") otherInfoDisappear : Animator,
        @Named("Disappear100Percents") buttonPreviousDisappear : Animator,
        @Named("Disappear100Percents") buttonEasyDisappear : Animator,
        @Named("Disappear100Percents") buttonHardDisappear : Animator
    ) { dataBinding.apply {

        (flashcardSink as ValueAnimator).addUpdateListener {
            viewgroupBackFlashcard.elevation = it.animatedValue as Float
        }
        flashcardSink.setTarget(viewgroupBackFlashcard)
        flashcardDisappear.setTarget(viewgroupBackFlashcard)
        otherInfoDisappear.setTarget(viewgroupCardOtherInfo)
        buttonEasyDisappear.setTarget(btnEasy)
        buttonHardDisappear.setTarget(btnHard)
        buttonPreviousDisappear.setTarget(btnPreviousCard)


        val otherInfoDisappearSet = AnimatorSet().apply {
            play(otherInfoDisappear).with(buttonPreviousDisappear)
        }

        val navigateDisappearSet = AnimatorSet().apply {
            play(buttonEasyDisappear).with(buttonHardDisappear)
        }

        val flashcardDisappearSet = AnimatorSet().apply {
            play(flashcardSink).before(flashcardDisappear)
        }

        onPrevCardBackCardDisappearAnimatorSet.playSequentially( navigateDisappearSet, otherInfoDisappearSet, flashcardDisappearSet)
        onPrevCardBackCardDisappearAnimatorSet.addListener(onEnd = {
            groupBackCard.disappear()
            groupBackCard.elevation = 15f
            viewgroupBackFlashcard.translationX = 0f
            viewgroupBackFlashcard.translationY = 0f
            groupFrontCard.appear()
            viewgroupFrontFlashcard.scaleX = 1.0f
            viewgroupFrontFlashcard.scaleY= 1.0f
            viewgroupFrontFlashcard.alpha = 1.0f
            viewgroupFrontFlashcard.translationX = viewgroupFrontFlashcard.width.toFloat()
        })
    }}


}

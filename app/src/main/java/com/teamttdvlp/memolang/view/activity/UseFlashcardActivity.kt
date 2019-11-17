package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.core.animation.addListener
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityUseFlashcardBinding
import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardView
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.view.ui.OnSwipeUpListener
import com.teamttdvlp.memolang.viewmodel.use_flashcard_activity.UseFlashcardActivityViewModel
import javax.inject.Inject
import javax.inject.Named

const val FORGOTTEN_FLASHCARDS_LIST = "fgl"

class UseFlashcardActivity : BaseActivity<ActivityUseFlashcardBinding, UseFlashcardActivityViewModel>(), UseFlashcardView {

    private val RED_TEXT_COLOR : Int by lazy {
        resources.getColor(R.color.app_red)
    }

    private val NORMAL_TEXT_COLOR : Int by lazy {
        resources.getColor(R.color.use_flashcard_grey_text_color)
    }


    private val on_OPEN_FrontCardAppearAnimation = AnimatorSet ()

    private val on_OPEN_BackCardDisappearAnimation = AnimatorSet ()

    private val on_OPEN_Animation = AnimatorSet()


    private val on_NEXT_CardAnimation = AnimatorSet()

    private val on_NEXT_CardBackCardDisappearAnimation = AnimatorSet()

    private val on_NEXT_CardFrontCardAppearAnimation = AnimatorSet()


    private val on_NEXT_HARD_CardAnimation = AnimatorSet()

    private val on_NEXT_HARD_BackCardDisappearAnimation = AnimatorSet()

    private val on_NEXT_HARD_FrontCardAppearThenDisappear = AnimatorSet()


    private val on_PREV_CardAnimation = AnimatorSet()

    private val on_PREV_CardBackCardDisappearAnimation = AnimatorSet()

    private val on_PREV_CardFrontCardAppearAnimation = AnimatorSet()


    override fun getLayoutId(): Int = R.layout.activity_use_flashcard

    override fun takeViewModel() : UseFlashcardActivityViewModel = getActivityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setView(this)
        viewModel.setData(getFlashcardList())
        viewModel.beginUsing()
    }

    override fun initProperties() {
        dataBinding.viewModel = viewModel
    }

    override fun addAnimationEvents() { dataBinding.apply {

        on_NEXT_CardFrontCardAppearAnimation.addListener(onStart = {
            viewgroupFrontFlashcard.scaleX = 1.0f
            viewgroupFrontFlashcard.scaleY = 1.0f
            viewgroupFrontFlashcard.alpha = 1.0f
            groupFrontCard.appear()

            this@UseFlashcardActivity.viewModel.moveToNextCard()
            showPreviousCardButton()

        })

        on_PREV_CardBackCardDisappearAnimation.addListener(onEnd = {
            groupBackCard.disappear()
            groupBackCard.elevation = 15f
            viewgroupBackFlashcard.translationX = 0f
            viewgroupBackFlashcard.translationY = 0f

            groupFrontCard.appear()
            viewgroupFrontFlashcard.scaleX = 1.0f
            viewgroupFrontFlashcard.scaleY= 1.0f
            viewgroupFrontFlashcard.alpha = 1.0f
            viewgroupFrontFlashcard.translationX = viewgroupFrontFlashcard.width.toFloat()

            this@UseFlashcardActivity.viewModel.checkIfThereIsPreviousCard()
            this@UseFlashcardActivity.viewModel.moveToPreviousCard()
        })

    }}

    override fun addViewEvents() { dataBinding.apply {

        val swipeUpAnim = AnimationUtils.loadAnimation(this@UseFlashcardActivity, R.anim.move_up_fade_out_then_bottom_to_top_fade_out)

        swipeUpAnim.addAnimationLister (onEnd = {
            imgSwipeUp.startAnimation(swipeUpAnim)
        })

        imgSwipeUp.startAnimation(swipeUpAnim)

        viewgroupImgSwipeUp.setOnTouchListener(object : OnSwipeUpListener(this@UseFlashcardActivity) {
            override fun onSwipeUp() {
                on_OPEN_Animation.start()
            }
        })

        btnEasy.setOnClickListener {
            this@UseFlashcardActivity.viewModel.handleEasyCard()
            on_NEXT_CardAnimation.start()
        }

        btnHard.setOnClickListener {
            this@UseFlashcardActivity.viewModel.handleHardCard()
            on_NEXT_HARD_CardAnimation.start()
        }

        btnPreviousCard.setOnClickListener {
            on_PREV_CardAnimation.start()
        }

    }}


    // VIEW PROCESSING

    override fun onNoCardsLeft() {
        quickToast("There is no cards left")
        dataBinding.groupFrontCard.disappear()
        sendHardCardListToEndActivity()
        finish()
    }

    override fun showPreviousCardButton() {
        dataBinding.btnPreviousCard.appear()
    }

    override fun hidePreviousCardButton() {
        dataBinding.btnPreviousCard.disappear()
    }

    fun sendHardCardListToEndActivity () {
        val hardCardList = viewModel.getFoggotenCardList()
        val intent = Intent(this@UseFlashcardActivity, UseFlashcardDoneActivity::class.java)
        intent.putExtra(FORGOTTEN_FLASHCARDS_LIST, hardCardList)
        startActivity(intent)
    }

    fun getFlashcardList () : ArrayList<Flashcard> {
        val flashcardList = intent.extras!!.getSerializable(FLASHCARD_LIST_KEY) as ArrayList<Flashcard>
        return flashcardList
    }


    // ON OPEN

    @Inject
    fun init_ON_OPEN_CardFrontCardDisappearAnimation (
        @Named("Disappear100Percents") imgSwipeUpFadeOutAnimator : Animator,
        @Named("Disappear100Percents") txtTextFadeOutAnimator : Animator,
        @Named("Disappear100Percents") txtCardNumberFadeOutAnimator : Animator,
        @Named("FlipClose") flashcardFlipAnimator : Animator) { dataBinding.apply {

        imgSwipeUpFadeOutAnimator .setTarget(imgSwipeUp)
        flashcardFlipAnimator .setTarget(viewgroupFrontFlashcard)

        on_OPEN_FrontCardAppearAnimation.play(imgSwipeUpFadeOutAnimator).with(flashcardFlipAnimator)
        on_OPEN_FrontCardAppearAnimation.addListener (
            onStart = {
                groupFrontCard.appear()
            }, onEnd = {
                groupFrontCard.disappear()
            })
    }}

    @Inject
    fun init_ON_OPEN_CardBackFlashcardAppearAnimation (
        @Named("FlipOpen") flashcardOPENAnimator : Animator,
        @Named("Appear100Percents") otherInfoAppear : Animator,
        @Named("Appear100Percents") button_PREV_iousAppear : Animator,
        @Named("Appear100Percents") buttonEasyAppear : Animator,
        @Named("Appear100Percents") buttonHardAppear : Animator
    ) { dataBinding.apply {

        flashcardOPENAnimator.setTarget(viewgroupBackFlashcard)
        otherInfoAppear.setTarget(viewgroupCardOtherInfo)
        buttonEasyAppear.setTarget(btnEasy)
        buttonHardAppear.setTarget(btnHard)
        button_PREV_iousAppear.setTarget(btnPreviousCard)

        val navigateAppearSet= AnimatorSet().apply {
            play(buttonEasyAppear).with(buttonHardAppear)
        }

        val otherInfoAppearSet= AnimatorSet().apply {
            play(otherInfoAppear).with(button_PREV_iousAppear)
        }

        on_OPEN_BackCardDisappearAnimation.playSequentially(flashcardOPENAnimator, otherInfoAppearSet, navigateAppearSet)
        on_OPEN_BackCardDisappearAnimation.addListener(onStart = {
            groupBackCard.appear()
            viewgroupFrontFlashcard.elevation = 0f
        })
        on_OPEN_Animation.play(on_OPEN_FrontCardAppearAnimation).before(on_OPEN_BackCardDisappearAnimation)
    }}


    // ON NEXT


    @Inject
    fun init_ON_NEXT_CardFrontCardAppearAnimation (
        @Named("Float") flashcardFloatAnimator : Animator,
        @Named("Appear100Percents") buttonSwipeUpAppearAnimator : Animator,
        @Named("Appear100Percents") txtTextAppearAnimator : Animator
    ) {dataBinding.apply {

        (flashcardFloatAnimator as ValueAnimator)


        flashcardFloatAnimator.apply {

            duration = 150

            startDelay = 100

            (this as ValueAnimator).addUpdateListener {
                viewgroupFrontFlashcard.elevation = it.animatedValue as Float
            }

            addListener(
                onEnd = {
                    viewgroupBackFlashcard.alpha = 1.0f
                })
        }

        txtTextAppearAnimator.apply {
            startDelay = 30
            duration = 100
        }

        txtTextAppearAnimator.setTarget(dataBinding.txtFrontCardText)

        flashcardFloatAnimator.setTarget(dataBinding.viewgroupFrontFlashcard)

        buttonSwipeUpAppearAnimator.setTarget(imgSwipeUp)

        on_NEXT_CardFrontCardAppearAnimation.play(flashcardFloatAnimator).with(buttonSwipeUpAppearAnimator).after(txtTextAppearAnimator)

        on_NEXT_CardAnimation.play(on_NEXT_CardBackCardDisappearAnimation).before(on_NEXT_CardFrontCardAppearAnimation)
    }}


    @Inject
    fun init_ON_NEXT_EASY_CardBackCardDisappearAnimation (
        @Named("MoveRightAndFadeOut") flashcardMoveOutAnimator : Animator,
        @Named("Disappear100Percents") otherInfoDisappear : Animator,
        @Named("Disappear100Percents") button_PREV_iousDisappear : Animator,
        @Named("Disappear100Percents") buttonEasyDisappear : Animator,
        @Named("Disappear100Percents") buttonHardDisappear : Animator
    ) { dataBinding.apply {
        flashcardMoveOutAnimator.setTarget(viewgroupBackFlashcard)
        otherInfoDisappear.setTarget(viewgroupCardOtherInfo)
        buttonEasyDisappear.setTarget(btnEasy)
        buttonHardDisappear.setTarget(btnHard)
        button_PREV_iousDisappear.setTarget(btnPreviousCard)

        val otherInfoDisappearSet = AnimatorSet().apply {
            play(otherInfoDisappear).with(button_PREV_iousDisappear)
        }

        val navigateDisappearSet = AnimatorSet().apply {
            play(buttonEasyDisappear).with(buttonHardDisappear)
        }

        on_NEXT_CardBackCardDisappearAnimation.playSequentially(flashcardMoveOutAnimator, otherInfoDisappearSet, navigateDisappearSet)
        on_NEXT_CardBackCardDisappearAnimation.addListener(onEnd = {
            groupBackCard.disappear()
            viewgroupBackFlashcard.translationX = 0f
            viewgroupBackFlashcard.translationY = 0f

            txtFrontCardText.alpha = 0f
        })
    }}



    @Inject
    fun init_ON_NEXT_HARD_CardBackCardDisappearAnimation (
        @Named("Disappear100Percents") otherInfoDisappear : Animator,
        @Named("Disappear100Percents") buttonpreviousDisappear : Animator,
        @Named("Disappear100Percents") buttonEasyDisappear : Animator,
        @Named("Disappear100Percents") buttonHardDisappear : Animator,

        @Named("ScaleBiggerAndFadeOut") textHighlight : Animator,
        @Named("FlipClose") backCardClose : Animator,

        @Named("FlipOpen") frontCardOpen : Animator,
        @Named("ScaleBiggerAndFadeOut") translationHighlight: Animator,
        @Named("MoveRightAndFadeOut") frontCardMoveOut : Animator

    ) { dataBinding.apply {

        otherInfoDisappear.setTarget(viewgroupCardOtherInfo)
        buttonEasyDisappear.setTarget(btnEasy)
        buttonHardDisappear.setTarget(btnHard)
        buttonpreviousDisappear.setTarget(btnPreviousCard)

        translationHighlight.setTarget(txtBackHightlightCardTranslation)
        backCardClose.apply {
            startDelay = 750
            setTarget(viewgroupBackFlashcard)
        }

        frontCardOpen.setTarget(viewgroupBackReplicaFrontFlashcard)
        textHighlight.setTarget(txtBackReplicaHighlightFrontCardText)

        frontCardMoveOut.apply {
            startDelay = 1000
            setTarget(viewgroupBackReplicaFrontFlashcard)
        }

        val otherInfoDisappearSet = AnimatorSet().apply {
            play(otherInfoDisappear).with(buttonpreviousDisappear)
        }

        val navigateDisappearSet = AnimatorSet().apply {
            play(buttonEasyDisappear).with(buttonHardDisappear)
        }

        
        on_NEXT_HARD_BackCardDisappearAnimation.playSequentially(navigateDisappearSet,otherInfoDisappearSet,
            translationHighlight, backCardClose)
        on_NEXT_HARD_BackCardDisappearAnimation.addListener(
            onStart = {
                txtBackCardTranslation.setTextColor(RED_TEXT_COLOR)
            },
            onEnd = {
                groupBackCard.disappear()
                viewgroupBackReplicaFrontFlashcard.appear()
                txtBackCardTranslation.setTextColor(NORMAL_TEXT_COLOR)
            })

            on_NEXT_HARD_FrontCardAppearThenDisappear.playSequentially(frontCardOpen, textHighlight, frontCardMoveOut)
            on_NEXT_HARD_FrontCardAppearThenDisappear.addListener(onEnd = {
                viewgroupBackReplicaFrontFlashcard.disappear()
                viewgroupBackReplicaFrontFlashcard.translationX = 0f
                viewgroupBackReplicaFrontFlashcard.translationY = 0f
                txtFrontCardText.alpha = 0f
            })

            on_NEXT_HARD_CardAnimation.playSequentially(
                on_NEXT_HARD_BackCardDisappearAnimation,
                on_NEXT_HARD_FrontCardAppearThenDisappear,
                on_NEXT_CardFrontCardAppearAnimation)
        }
    }




    // ON PREV


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

        on_PREV_CardFrontCardAppearAnimation.play(flashcardMoveFromRightToCentre).with(buttonSwipeUpAppear)


    }}


    @Inject
    fun init_ON_PREV_CardBackCardDisappearAnimation (

        @Named("Sink") flashcardSink : Animator,
        @Named("Disappear100Percents") flashcardDisappear: Animator,
        @Named("Disappear100Percents") otherInfoDisappear : Animator,
        @Named("Disappear100Percents") button_PREV_iousDisappear : Animator,
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
        button_PREV_iousDisappear.setTarget(btnPreviousCard)


        val otherInfoDisappearSet = AnimatorSet().apply {
            play(otherInfoDisappear).with(button_PREV_iousDisappear)
        }

        val navigateDisappearSet = AnimatorSet().apply {
            play(buttonEasyDisappear).with(buttonHardDisappear)
        }

        val flashcardDisappearSet = AnimatorSet().apply {
            play(flashcardSink).before(flashcardDisappear)
        }

        on_PREV_CardBackCardDisappearAnimation.playSequentially( navigateDisappearSet, otherInfoDisappearSet, flashcardDisappearSet)

        on_PREV_CardAnimation.play(on_PREV_CardBackCardDisappearAnimation)
            .before(on_PREV_CardFrontCardAppearAnimation)
    }}


}

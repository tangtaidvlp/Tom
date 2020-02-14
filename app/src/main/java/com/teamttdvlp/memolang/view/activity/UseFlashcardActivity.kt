package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.animation.AnimationUtils
import androidx.core.animation.addListener
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityUseFlashcardBinding
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardView
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.view.ui.OnSwipeUpListener
import com.teamttdvlp.memolang.viewmodel.UseFlashcardViewModel
import javax.inject.Inject
import javax.inject.Named

const val FORGOTTEN_FLASHCARDS_LIST = "fgl"

class UseFlashcardActivity : BaseActivity<ActivityUseFlashcardBinding, UseFlashcardViewModel>()
                            ,UseFlashcardView {

    private var RED_TEXT_COLOR : Int = 0

    private var NORMAL_TEXT_COLOR : Int = 0


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

    override fun takeViewModel() : UseFlashcardViewModel = getActivityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        viewModel.setData(getFlashcardList())
        viewModel.beginUsing()
    }

    override fun initProperties() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RED_TEXT_COLOR = getColor(R.color.app_red)
            NORMAL_TEXT_COLOR = getColor(R.color.use_flashcard_grey_text_color)
        } else {
            RED_TEXT_COLOR = resources.getColor(R.color.app_red)
            NORMAL_TEXT_COLOR = resources.getColor(R.color.use_flashcard_grey_text_color)
        }

        dB.viewModel = viewModel
    }

    override fun addViewControls() { dB.apply {
        txtFrontCardText.movementMethod = ScrollingMovementMethod()
    }}

    override fun addAnimationEvents() { dB.apply {

        on_NEXT_CardFrontCardAppearAnimation.addListener( onStart = {
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

    override fun addViewEvents() { dB.apply {

        btnYesQuit.setOnClickListener {
            finish()
        }

        btnNoExit.setOnClickListener {
            dialogExit.hide()
        }

        btnFrontSpeak.setOnClickListener {
            this@UseFlashcardActivity.viewModel.speakSrcLangText(txtFrontCardText.text.toString())
        }

        btnBackSpeak.setOnClickListener {
            this@UseFlashcardActivity.viewModel.speakTgtLangText(txtBackCardTranslation.text.toString())
        }

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

    override fun onBackPressed() {
        dB.dialogExit.show()
    }

    // VIEW PROCESSING

    override fun onNoCardsLeft() {
        quickToast("There is no cards left")
        dB.groupFrontCard.disappear()
        sendHardCardListToEndActivity()
        finish()
    }

    override fun showPreviousCardButton() {
        dB.btnPreviousCard.appear()
    }

    override fun hidePreviousCardButton() {
        dB.btnPreviousCard.disappear()
    }

    override fun showSpeakTextError(error: String) {
        quickToast(error)
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
        @Named("Disappear100Percents") btnSpeakDisappearAnimator: Animator,
        @Named("Disappear100Percents") txtTextFadeOutAnimator : Animator,
        @Named("Disappear100Percents") txtCardNumberFadeOutAnimator : Animator,
        @Named("FlipClose") flashcardFlipAnimator : Animator) { dB.apply {

        imgSwipeUpFadeOutAnimator .setTarget(imgSwipeUp)
        flashcardFlipAnimator .setTarget(viewgroupFrontFlashcard)
        btnSpeakDisappearAnimator.setTarget(btnFrontSpeak)

        on_OPEN_FrontCardAppearAnimation.playTogether(imgSwipeUpFadeOutAnimator,btnSpeakDisappearAnimator, flashcardFlipAnimator)
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
        @Named("Appear100Percents") button_SpeakerAppear : Animator,
        @Named("Appear100Percents") buttonEasyAppear : Animator,
        @Named("Appear100Percents") buttonHardAppear : Animator
    ) { dB.apply {

        flashcardOPENAnimator.setTarget(viewgroupBackFlashcard)
        otherInfoAppear.setTarget(viewgroupCardOtherInfo)
        buttonEasyAppear.setTarget(btnEasy)
        buttonHardAppear.setTarget(btnHard)
        button_PREV_iousAppear.setTarget(btnPreviousCard)
        button_SpeakerAppear.setTarget(btnBackSpeak)

        val navigateAppearSet= AnimatorSet().apply {
            play(buttonEasyAppear).with(buttonHardAppear)
        }

        val otherInfoAppearSet= AnimatorSet().apply {
            play(otherInfoAppear).with(AnimatorSet().apply {
                play(button_PREV_iousAppear).with(button_SpeakerAppear)
            })
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
        @Named("Appear100Percents") txtTextAppearAnimator : Animator,
        @Named("Appear100Percents") btnSpeakAppearAnimator : Animator
    ) {dB.apply {

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

        txtTextAppearAnimator.setTarget(dB.txtFrontCardText)

        flashcardFloatAnimator.setTarget(dB.viewgroupFrontFlashcard)

        buttonSwipeUpAppearAnimator.setTarget(imgSwipeUp)

        btnSpeakAppearAnimator.setTarget(btnFrontSpeak)

        on_NEXT_CardFrontCardAppearAnimation.play(flashcardFloatAnimator).with(buttonSwipeUpAppearAnimator).after(AnimatorSet().apply {
            play(txtTextAppearAnimator).with(btnSpeakAppearAnimator)
        })

        on_NEXT_CardAnimation.play(on_NEXT_CardBackCardDisappearAnimation).before(on_NEXT_CardFrontCardAppearAnimation)
        on_NEXT_CardAnimation.addListener (onStart = {
            txtFrontCardText.scrollY = 0
        })
    }}


    @Inject
    fun init_ON_NEXT_EASY_CardBackCardDisappearAnimation (
        @Named("MoveRightAndFadeOutAnimtr") flashcardMoveOutAnimator : Animator,
        @Named("Disappear100Percents") otherInfoDisappear : Animator,
        @Named("Disappear100Percents") button_PREV_iousDisappear : Animator,
        @Named("Disappear100Percents") button_SpeakerDisappear : Animator,
        @Named("Disappear100Percents") buttonEasyDisappear : Animator,
        @Named("Disappear100Percents") buttonHardDisappear : Animator
    ) { dB.apply {
        flashcardMoveOutAnimator.setTarget(viewgroupBackFlashcard)
        otherInfoDisappear.setTarget(viewgroupCardOtherInfo)
        buttonEasyDisappear.setTarget(btnEasy)
        buttonHardDisappear.setTarget(btnHard)
        button_PREV_iousDisappear.setTarget(btnPreviousCard)
        button_SpeakerDisappear.setTarget(btnBackSpeak)

        val otherInfoDisappearSet = AnimatorSet().apply {
            play(otherInfoDisappear).with(AnimatorSet().apply {
                play(button_PREV_iousDisappear).with(button_SpeakerDisappear)
            })
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
        @Named("Disappear100Percents") buttonPreviousDisappear : Animator,
        @Named("Disappear100Percents") buttonSpeakerDisappear : Animator,
        @Named("Disappear100Percents") buttonEasyDisappear : Animator,
        @Named("Disappear100Percents") buttonHardDisappear : Animator,

        @Named("ScaleBiggerAndFadeOut") textHighlight : Animator,
        @Named("FlipClose") backCardClose : Animator,

        @Named("FlipOpen") frontCardOpen : Animator,
        @Named("ScaleBiggerAndFadeOut") translationHighlight: Animator,
        @Named("MoveRightAndFadeOutAnimtr") frontCardMoveOut : Animator

    ) { dB.apply {

        otherInfoDisappear.setTarget(viewgroupCardOtherInfo)
        buttonEasyDisappear.setTarget(btnEasy)
        buttonHardDisappear.setTarget(btnHard)
        buttonPreviousDisappear.setTarget(btnPreviousCard)
        buttonSpeakerDisappear.setTarget(btnBackSpeak)

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
            play(otherInfoDisappear).with(AnimatorSet().apply {
                play(buttonPreviousDisappear).with(buttonSpeakerDisappear)
            })
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
        @Named("Appear100Percents") buttonSwipeUpAppear: Animator,
        @Named("Appear100Percents") buttonSpeakAppear : Animator
    ) {dB.apply {

        flashcardMoveFromRightToCentre.startDelay = 150

        flashcardMoveFromRightToCentre.addListener(onStart = {
            viewgroupFrontFlashcard.elevation = 15f
        },
            onEnd = {
                viewgroupBackFlashcard.alpha = 1.0f
            })

        flashcardMoveFromRightToCentre.setTarget(dB.viewgroupFrontFlashcard)

        buttonSwipeUpAppear.setTarget(imgSwipeUp)

        buttonSpeakAppear.setTarget(dB.btnFrontSpeak)

        on_PREV_CardFrontCardAppearAnimation.play(buttonSpeakAppear).with(buttonSwipeUpAppear).after(flashcardMoveFromRightToCentre)


    }}


    @Inject
    fun init_ON_PREV_CardBackCardDisappearAnimation (

        @Named("Sink") flashcardSink : Animator,
        @Named("Disappear100Percents") flashcardDisappear: Animator,
        @Named("Disappear100Percents") otherInfoDisappear : Animator,
        @Named("Disappear100Percents") button_PREV_iousDisappear : Animator,
        @Named("Disappear100Percents") button_SpeakDisappear : Animator,
        @Named("Disappear100Percents") buttonEasyDisappear : Animator,
        @Named("Disappear100Percents") buttonHardDisappear : Animator
    ) { dB.apply {

        (flashcardSink as ValueAnimator).addUpdateListener {
            viewgroupBackFlashcard.elevation = it.animatedValue as Float
        }
        flashcardSink.setTarget(viewgroupBackFlashcard)
        flashcardDisappear.setTarget(viewgroupBackFlashcard)
        otherInfoDisappear.setTarget(viewgroupCardOtherInfo)
        buttonEasyDisappear.setTarget(btnEasy)
        buttonHardDisappear.setTarget(btnHard)
        button_PREV_iousDisappear.setTarget(btnPreviousCard)
        button_SpeakDisappear.setTarget(btnBackSpeak)

        val otherInfoDisappearSet = AnimatorSet().apply {
            play(otherInfoDisappear).with(AnimatorSet().apply {
                play(button_PREV_iousDisappear).with(button_SpeakDisappear)
            })
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

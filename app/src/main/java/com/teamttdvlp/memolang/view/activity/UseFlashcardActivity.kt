package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.core.animation.addListener
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityUseFlashcardBinding
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager.SpeakerStatus.Companion.SPEAK_TEXT_AND_TRANSLATION
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager.SpeakerStatus.Companion.SPEAK_TEXT_ONLY
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager.SpeakerStatus.Companion.SPEAK_TRANSLATION_ONLY
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardView
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.view.customview.MyGestureDetector
import com.teamttdvlp.memolang.viewmodel.UseFlashcardViewModel
import javax.inject.Inject
import javax.inject.Named


class UseFlashcardActivity : BaseActivity<ActivityUseFlashcardBinding, UseFlashcardViewModel>()
                            ,UseFlashcardView {

    private var RED_TEXT_COLOR : Int = 0

    private var NORMAL_TEXT_COLOR : Int = 0


    private val on_REFLIP_FrontCardAppearAnimation = AnimatorSet ()

    private val on_REFLIP_BackCardDisappearAnimation = AnimatorSet ()

    private val on_REFLIP_Animation = AnimatorSet()


    private val on_OPEN_FrontCardDisappearAnimation = AnimatorSet ()

    private val on_OPEN_BackCardAppearAnimation = AnimatorSet ()

    private val on_OPEN_Animation = AnimatorSet()


    private val on_NEXT_CardAnimation = AnimatorSet()

    private val on_NEXT_CardBackCardDisappearAnimation = AnimatorSet()

    private val on_NEXT_CardFrontCardAppearAnimation = AnimatorSet()


    private val on_NEXT_HARD_CardAnimation = AnimatorSet()

    private val on_NEXT_HARD_BackCardDisappearAnimation = AnimatorSet()

    private val on_NEXT_HARD_FrontCardAppearThenDisappear = AnimatorSet()


    private val on_PREV_from_FRONT_SIDE_CardAnimation = AnimatorSet()

    private val on_PREV_from_FRONT_SIDE_CurrentFrontCardDisappearAnimation = AnimatorSet()

    private val on_PREV_from_FRONT_SIDE_PreviousFrontCardAppearAnimation = AnimatorSet()


    private val on_PREV_from_BACK_SIDE_CardAnimation = AnimatorSet()

    private val on_PREV_from_BACK_SIDE_CardBackCardDisappearAnimation = AnimatorSet()

    private val on_PREV_from_BACK_SIDE_CardFrontCardAppearAnimation = AnimatorSet()

    private var backButtonPressedTimes = 0

    private var textIsSpoken = true

    private var translationIsSpoken = true

    private var speakerIsOn = true

    private var canGoToPreviousCard: Boolean = false


    lateinit var viewModelProviderFactory : ViewModelProviderFactory
    @Inject set

    private lateinit var usedFlashcardSet : FlashcardSet

    companion object {
        fun requestReviewFlashcard (requestContext : Context, flashcardSet : FlashcardSet){
            val intent = Intent(requestContext, UseFlashcardActivity::class.java)
            intent.putExtra(FLASHCARD_SET_KEY, flashcardSet)
            requestContext.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_use_flashcard

    override fun takeViewModel() : UseFlashcardViewModel = getActivityViewModel(viewModelProviderFactory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dB.lifecycleOwner = this
        viewModel.setUpView(this)
        beginUsing(false)
    }

    override fun onDestroy() {
        val speakerFunction = if (textIsSpoken and translationIsSpoken) {
            SPEAK_TEXT_AND_TRANSLATION
        } else if (textIsSpoken and not(translationIsSpoken)) {
            SPEAK_TEXT_ONLY
        } else if (not(textIsSpoken) and translationIsSpoken) {
            SPEAK_TRANSLATION_ONLY
        } else throw Exception ("Speaker status unknown")

        quickLog("State: $speakerIsOn")
        viewModel.saveAllStatus(speakerFunction, speakerIsOn)
        super.onDestroy()
    }

    override fun initProperties() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RED_TEXT_COLOR = getColor(R.color.app_red)
            NORMAL_TEXT_COLOR = getColor(R.color.use_flashcard_grey_text_color)
        } else {
            RED_TEXT_COLOR = resources.getColor(R.color.app_red)
            NORMAL_TEXT_COLOR = resources.getColor(R.color.use_flashcard_grey_text_color)
        }

        dB.vwModel = viewModel

        usedFlashcardSet = getRequestedFlashcardSet()
    }

    override fun addViewControls() { dB.apply {

        txtFrontCardText.movementMethod = ScrollingMovementMethod()
    }}

    override fun addAnimationEvents() { dB.apply {

        on_NEXT_CardFrontCardAppearAnimation.addListener( onStart = {
            viewgroupFrontFlashcard.scaleX = 1.0f
            viewgroupFrontFlashcard.scaleY = 1.0f
            viewgroupFrontFlashcard.alpha = 1.0f
            doActionOnGroup_FRONT_Card { it ->
                it.goVISIBLE()
            }

            this@UseFlashcardActivity.viewModel.moveToNextCard()
            dB.executePendingBindings()
            if (textIsSpoken and speakerIsOn) {
                viewModel.speakFrontCardText(dB.txtFrontCardText.text.toString())
            }
        })

        on_PREV_from_FRONT_SIDE_CurrentFrontCardDisappearAnimation.addListener(onEnd = {
            this@UseFlashcardActivity.viewModel.moveToPreviousCard()
            dB.executePendingBindings()
            if (textIsSpoken and speakerIsOn) {
                viewModel.speakFrontCardText(dB.txtFrontCardText.text.toString())
            }
        })

        on_PREV_from_BACK_SIDE_CardBackCardDisappearAnimation.addListener(onEnd = {
            doActionOnGroup_BACK_Card { it ->
                it.goGONE()

            }
            doActionOnGroup_BACK_Card { it ->
                it.elevation = 5.dp().toFloat()
            }
            viewgroupBackFlashcard.translationX = 0f
            viewgroupBackFlashcard.translationY = 0f

            doActionOnGroup_FRONT_Card { it ->
                it.goVISIBLE()
            }
            viewgroupFrontFlashcard.scaleX = 1.0f
            viewgroupFrontFlashcard.scaleY= 1.0f
            viewgroupFrontFlashcard.alpha = 1.0f
            viewgroupFrontFlashcard.translationX = viewgroupFrontFlashcard.width.toFloat()
            this@UseFlashcardActivity.viewModel.checkIfThereIsPreviousCard()
            this@UseFlashcardActivity.viewModel.moveToPreviousCard()
            dB.executePendingBindings()
            if (textIsSpoken and speakerIsOn) {
                viewModel.speakFrontCardText(dB.txtFrontCardText.text.toString())
            }
        })

        on_OPEN_BackCardAppearAnimation.addListener(onStart = {
            doActionOnGroup_BACK_Card { it ->
                    it.goVISIBLE()

            }
            viewgroupFrontFlashcard.elevation = 0f
            if (translationIsSpoken and speakerIsOn) {
                viewModel.speakBackCardText(txtBackCardTranslation.text.toString())
            }
        })
    }}

    private fun flipBackCardUp() {
        on_OPEN_Animation.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun addViewEvents() { dB.apply {

        imgFrontSwipeZone.setOnTouchListener(object : MyGestureDetector(this@UseFlashcardActivity) {
            override fun onSwipeDown() {
                flipBackCardUp()
            }

            override fun onSwipeUp() {
                flipBackCardUp()
            }

            override fun onSwipeRight() {
                flipBackCardUp()
            }

            override fun onSwipeLeft() {
                viewModel.checkIfThereIsPreviousCard()
                if (canGoToPreviousCard) {
                    on_PREV_from_FRONT_SIDE_CardAnimation.start()
                } else {
                    showCanNotGoToPreviousCardNotification()
                }
            }
        })

        btnHard.setOnClickListener {
            this@UseFlashcardActivity.viewModel.handleHardCard()
            nextCard(false)
        }

        dialogExit.setOnHide {
            resetBackButtonPressedTimes()
        }

        btnSpeakerSetting.setOnClickListener {
            dialogSetting.show()
        }

        radioGrpSpeakerSetting.setOnCheckedChangeListener { _, checkedId ->
            quickLog("Change")
            when (checkedId) {
                checkboxSpeakTextOnly.id -> {
                    textIsSpoken = true
                    translationIsSpoken = false
                }
                checkboxSpeakTranslationOnly.id -> {
                    translationIsSpoken = true
                    textIsSpoken = false
                }
                checkboxSpeakBothTextAndTrans.id -> {
                    translationIsSpoken = true
                    textIsSpoken = true
                }
            }
        }

        switchSpeaker.setOnCheckedChangeListener  { v, isChecked ->
            speakerIsOn = isChecked
        }

        btnSpeakFrontText.setOnClickListener {
            viewModel.speakFrontCardText(txtFrontCardText.text.toString())
        }

        btnSpeakBackText.setOnClickListener {
            viewModel.speakBackCardText(txtBackCardTranslation.text.toString())
        }

        imgBackSwipeZone.setOnTouchListener(object : MyGestureDetector (this@UseFlashcardActivity) {
            override fun onSwipeUp() {
                flipFrontCardUp()
            }

            override fun onSwipeDown() {
                flipFrontCardUp()
            }

            override fun onSwipeRight() {
                this@UseFlashcardActivity.viewModel.handleEasyCard()
                nextCard(true)
            }

            override fun onSwipeLeft() {
                this@UseFlashcardActivity.viewModel.checkIfThereIsPreviousCard()
                if (canGoToPreviousCard) {
                    on_PREV_from_BACK_SIDE_CardAnimation.start()
                } else {
                    showCanNotGoToPreviousCardNotification()
                }
            }
        })
    }}

    private fun flipFrontCardUp() {
        on_REFLIP_Animation.start()
    }

    private fun showCanNotGoToPreviousCardNotification() {
        quickToast("This is the first card")
    }

    override fun onBackPressed() {
        backButtonPressedTimes ++
        if (backButtonPressedTimes == 1) {
            dB.dialogExit.show()
        } else if (backButtonPressedTimes == 2) {
            finish()
        }
    }

    // VIEW PROCESSING

    override fun onNoCardsLeft() {
        doActionOnGroup_FRONT_Card { it ->
            it.goINVISIBLE()
        }
        viewModel.stopAllTextSpeaker()
        sendHardCardListToEndActivity()
        finish()
    }

    override fun showSpeakTextError(error: String) {
        quickToast(error)
    }

    override fun lock_ShowPreviousCard_Function() {
        quickLog("Lock")
        canGoToPreviousCard = false
    }

    override fun unlock_ShowPreviousCard_Function() {
        quickLog("Unlock")
        canGoToPreviousCard = true
    }

    private fun nextCard (userRememberCurrentCard : Boolean) {
        if (viewModel.checkThereIsCardLefts()) {
            if (userRememberCurrentCard) {
                on_NEXT_CardAnimation.start()
            } else {
                on_NEXT_HARD_CardAnimation.start()
            }
        } else { // No card left
            onNoCardsLeft()
        }
    }

    fun resetBackButtonPressedTimes () {
        backButtonPressedTimes = 0
    }

    fun sendHardCardListToEndActivity () {
        val hardCardList = viewModel.getForgottenCardList()
        UseFlashcardDoneActivity.requestFinishUsingFlashcard(this, hardCardList,
            UseFlashcardDoneActivity.SendableActivity.USE_FLASHCARD_ACTIVITY.code)
    }

    private fun getRequestedFlashcardSet () : FlashcardSet {
        return intent.extras!!.getSerializable(FLASHCARD_SET_KEY) as FlashcardSet
    }

    private fun setUpSpeakerStatus () {
        speakerIsOn = viewModel.getSpeakerStatus()
        dB.switchSpeaker.isChecked = speakerIsOn
        when (viewModel.getSpeakerFunction()) {
            SPEAK_TEXT_ONLY -> {
                textIsSpoken = true
                translationIsSpoken = false
                dB.checkboxSpeakTextOnly.isChecked = true
            }

            SPEAK_TRANSLATION_ONLY -> {
                textIsSpoken = false
                translationIsSpoken = true
                dB.checkboxSpeakTranslationOnly.isChecked = true
            }

            SPEAK_TEXT_AND_TRANSLATION -> {
                textIsSpoken = true
                translationIsSpoken = true
                dB.checkboxSpeakBothTextAndTrans.isChecked = true
            }
        }
    }

    private fun beginUsing (reverseLanguage : Boolean) {
        viewModel.setData(getRequestedFlashcardSet(), reverseLanguage)
        viewModel.beginUsing()
        dB.executePendingBindings()
        setUpSpeakerStatus()
    }

    // ON OPEN

    @Inject
    fun init_ON_OPEN_CardFrontCardDisappearAnimation (
        @Named("Disappear100Percents") txtSwipeFunctionDisappear : Animator,
        @Named("FlipClose") flashcardFlipAnimator : Animator) { dB.apply {

        txtSwipeFunctionDisappear.setTarget(vwgrpSwipeFunctions)
        flashcardFlipAnimator .setTarget(viewgroupFrontFlashcard)

        on_OPEN_FrontCardDisappearAnimation.playTogether(txtSwipeFunctionDisappear, flashcardFlipAnimator)
        on_OPEN_FrontCardDisappearAnimation.addListener (
            onStart = {
                doActionOnGroup_FRONT_Card { it ->
                    it.goVISIBLE()
                }
            }, onEnd = {
                doActionOnGroup_FRONT_Card { it ->
                    it.goINVISIBLE()
                }
            })
    }}

    @Inject
    fun init_ON_OPEN_CardBackFlashcardAppearAnimation (
        @Named("FlipOpen") flashcardOPENAnimator : Animator,
        @Named("Appear100Percents") buttonEasyAppear : Animator,
        @Named("Appear100Percents") buttonHardAppear : Animator
    ) { dB.apply {

        flashcardOPENAnimator.setTarget(viewgroupBackFlashcard)
        buttonHardAppear.setTarget(btnHard)

        val navigateAppearSet= AnimatorSet().apply {
            play(buttonEasyAppear).with(buttonHardAppear)
        }

        on_OPEN_BackCardAppearAnimation.playSequentially(flashcardOPENAnimator, navigateAppearSet)
        on_OPEN_Animation.play(on_OPEN_FrontCardDisappearAnimation).before(on_OPEN_BackCardAppearAnimation)
    }}

    private fun doActionOnGroup_FRONT_Card(action: (View) -> Unit) {
        action.invoke(dB.viewgroupFrontFlashcard)
        action.invoke(dB.vwgrpSwipeFunctions)
        action.invoke(dB.btnSpeakFrontText)
        action.invoke(dB.imgFrontSwipeZone)
    }

    private fun doActionOnGroup_BACK_Card(action: (View) -> Unit) {
        action.invoke(dB.viewgroupBackFlashcard)
        action.invoke(dB.btnSpeakBackText)
        action.invoke(dB.btnHard)
        action.invoke(dB.imgBackSwipeZone)
    }

//     ON REFLIP

    @Inject
    fun init_ON_REFLIP_CardFrontCardAppearAnimation (
        @Named("Appear50Percents") txtSwipeFunctionAppear: Animator,
        @Named("FlipOpen") flashcardOpenAnimator : Animator) { dB.apply {

        txtSwipeFunctionAppear.setTarget(vwgrpSwipeFunctions)
        flashcardOpenAnimator .setTarget(viewgroupFrontFlashcard)

        on_REFLIP_FrontCardAppearAnimation.playTogether(flashcardOpenAnimator, txtSwipeFunctionAppear)
        on_REFLIP_FrontCardAppearAnimation.addListener (
            onStart = {
                doActionOnGroup_FRONT_Card { it ->
                    it.goVISIBLE()
                }
                viewgroupFrontFlashcard.elevation = 5.dp().toFloat()
            })
    }}

    @Inject
    fun init_ON_REFLIP_CardBackFlashcardDisappearAnimation (
        @Named("FlipClose") flashcardCLOSEAnimator : Animator,
        @Named("Disappear100Percents") buttonEasyDisappear : Animator,
        @Named("Disappear100Percents") buttonHardDisappear : Animator
    ) { dB.apply {

        flashcardCLOSEAnimator.setTarget(viewgroupBackFlashcard)
        buttonHardDisappear.setTarget(btnHard)

        val navigateDisappearSet= AnimatorSet().apply {
            play(buttonHardDisappear).with(buttonEasyDisappear)
        }

        on_REFLIP_BackCardDisappearAnimation.playSequentially(navigateDisappearSet, flashcardCLOSEAnimator)
        on_REFLIP_BackCardDisappearAnimation.addListener(onEnd = {
            doActionOnGroup_BACK_Card { it ->
                it.goINVISIBLE()
            }
        })
        on_REFLIP_Animation.play(on_REFLIP_BackCardDisappearAnimation).before(on_REFLIP_FrontCardAppearAnimation)
    }}

    // ON NEXT


    @Inject
    fun init_ON_NEXT_CardFrontCardAppearAnimation (
        @Named("Float") flashcardFloatAnimator : Animator,
        @Named("Appear50Percents") txtSwipeFunctionAppear : Animator,
        @Named("Appear100Percents") txtTextAppearAnimator : Animator
    ) {dB.apply {

        (flashcardFloatAnimator as ValueAnimator)

        flashcardFloatAnimator.apply {
            duration = 150
            startDelay = 100
            addUpdateListener {
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

        txtSwipeFunctionAppear.setTarget(vwgrpSwipeFunctions)

        on_NEXT_CardFrontCardAppearAnimation.play(flashcardFloatAnimator).with(txtSwipeFunctionAppear).after(AnimatorSet().apply {
            play(txtTextAppearAnimator)
        })

        on_NEXT_CardAnimation.play(on_NEXT_CardBackCardDisappearAnimation).before(on_NEXT_CardFrontCardAppearAnimation)
        on_NEXT_CardAnimation.addListener (onStart = {
            txtFrontCardText.scrollY = 0
        })
    }}


    @Inject
    fun init_ON_NEXT_EASY_CardBackCardDisappearAnimation (
        @Named("MoveRightAndFadeOutAnimtr") flashcardMoveOutAnimator : Animator,
        @Named("Disappear100Percents") buttonEasyDisappear : Animator,
        @Named("Disappear100Percents") buttonHardDisappear : Animator
    ) { dB.apply {
        flashcardMoveOutAnimator.setTarget(viewgroupBackFlashcard)
        buttonHardDisappear.setTarget(btnHard)

        val navigateDisappearSet = AnimatorSet().apply {
            play(buttonEasyDisappear).with(buttonHardDisappear)
        }

        on_NEXT_CardBackCardDisappearAnimation.playSequentially(flashcardMoveOutAnimator, navigateDisappearSet)
        on_NEXT_CardBackCardDisappearAnimation.addListener(onEnd = {
            doActionOnGroup_BACK_Card { it ->
                    it.goGONE()

            }
            viewgroupBackFlashcard.translationX = 0f
            viewgroupBackFlashcard.translationY = 0f

            txtFrontCardText.alpha = 0f
        })
    }}



    @Inject
    fun init_ON_NEXT_HARD_CardBackCardDisappearAnimation (
        @Named("Disappear100Percents") buttonEasyDisappear : Animator,
        @Named("Disappear100Percents") buttonHardDisappear : Animator,

        @Named("ScaleBiggerAndFadeOut") textHighlight : Animator,
        @Named("FlipClose") backCardClose : Animator,

        @Named("FlipOpen") frontCardOpen : Animator,
        @Named("ScaleBiggerAndFadeOut") translationHighlight: Animator,
        @Named("MoveRightAndFadeOutAnimtr") frontCardMoveOut : Animator

    ) { dB.apply {

        buttonHardDisappear.setTarget(btnHard)

        translationHighlight.setTarget(txtBackHightlightCardTranslation)
        backCardClose.apply {
            startDelay = 300
            setTarget(viewgroupBackFlashcard)
        }

        frontCardOpen.setTarget(viewgroupBackReplicaFrontFlashcard)
        textHighlight.setTarget(txtBackReplicaHighlightFrontCardText)

        frontCardMoveOut.apply {
            startDelay = 350
            setTarget(viewgroupBackReplicaFrontFlashcard)
        }

        val navigateDisappearSet = AnimatorSet().apply {
            play(buttonEasyDisappear).with(buttonHardDisappear)
        }

        
        on_NEXT_HARD_BackCardDisappearAnimation.playSequentially(navigateDisappearSet,
            translationHighlight, backCardClose)
        on_NEXT_HARD_BackCardDisappearAnimation.addListener(
            onStart = {
                txtBackCardTranslation.setTextColor(RED_TEXT_COLOR)
            },
            onEnd = {
                doActionOnGroup_BACK_Card { it ->
                    it.goGONE()
                }
                viewgroupBackReplicaFrontFlashcard.goVISIBLE()
                txtBackCardTranslation.setTextColor(NORMAL_TEXT_COLOR)
            })

            on_NEXT_HARD_FrontCardAppearThenDisappear.playSequentially(frontCardOpen, textHighlight, frontCardMoveOut)
            on_NEXT_HARD_FrontCardAppearThenDisappear.addListener(onEnd = {
                viewgroupBackReplicaFrontFlashcard.goGONE()
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


    // ON PREV FROM BACK
    @Inject
    fun init_ON_PREV_CardFrontCardAppearAnimation (
        @Named("FromRightToCentreAndFadeIn") flashcardMoveFromRightToCentre: Animator,
        @Named("Appear50Percents") txtSwipeFunctionAppear: Animator
    ) {dB.apply {

        flashcardMoveFromRightToCentre.startDelay = 150

        flashcardMoveFromRightToCentre.addListener(onStart = {
            viewgroupFrontFlashcard.elevation = 5.dp().toFloat()
        },
            onEnd = {
                viewgroupBackFlashcard.alpha = 1.0f
            })

        flashcardMoveFromRightToCentre.setTarget(dB.viewgroupFrontFlashcard)

        txtSwipeFunctionAppear.setTarget(vwgrpSwipeFunctions)

        on_PREV_from_BACK_SIDE_CardFrontCardAppearAnimation.play(txtSwipeFunctionAppear)
            .after(flashcardMoveFromRightToCentre)

    }}


    @Inject
    fun init_ON_PREV_CardBackCardDisappearAnimation (
        @Named("Sink") flashcardSink : Animator,
        @Named("Disappear100Percents") flashcardDisappear: Animator,
        @Named("Disappear100Percents") buttonEasyDisappear : Animator,
        @Named("Disappear100Percents") buttonHardDisappear : Animator
    ) { dB.apply {

        (flashcardSink as ValueAnimator).addUpdateListener {
            viewgroupBackFlashcard.elevation = it.animatedValue as Float
        }
        flashcardSink.setTarget(viewgroupBackFlashcard)
        flashcardDisappear.setTarget(viewgroupBackFlashcard)
        buttonHardDisappear.setTarget(btnHard)

        val navigateDisappearSet = AnimatorSet().apply {
            play(buttonEasyDisappear).with(buttonHardDisappear)
        }

        val flashcardDisappearSet = AnimatorSet().apply {
            play(flashcardSink).before(flashcardDisappear)
        }

        on_PREV_from_BACK_SIDE_CardBackCardDisappearAnimation.playSequentially(
            navigateDisappearSet,
            flashcardDisappearSet
        )

        on_PREV_from_BACK_SIDE_CardAnimation.play(
            on_PREV_from_BACK_SIDE_CardBackCardDisappearAnimation
        )
            .before(on_PREV_from_BACK_SIDE_CardFrontCardAppearAnimation)
    }
    }

    @Inject
    fun init_ON_PREV_FROM_FRONT_CardFrontPreviousCardAppearAnimation(
        @Named("FromRightToCentreAndFadeIn") flashcardMoveFromRightToCentre: Animator
    ) {
        dB.apply {

//        flashcardMoveFromRightToCentre.startDelay = 150
            flashcardMoveFromRightToCentre.addListener(
                onStart = {
                    viewgroupFrontFlashcard.elevation = 5.dp().toFloat()
                },
                onEnd = {
                    viewgroupBackFlashcard.alpha = 1.0f
                })

            flashcardMoveFromRightToCentre.setTarget(dB.viewgroupFrontFlashcard)

            on_PREV_from_FRONT_SIDE_PreviousFrontCardAppearAnimation.play(
                flashcardMoveFromRightToCentre
            )

    }}

    @Inject
    fun init_ON_PREV_FROM_FRONT_CardFrontCurrentCardDisappearAnimation(
        @Named("Sink") flashcardSink: Animator,
        @Named("Disappear100Percents") flashcardDisappear: Animator
    ) {
        dB.apply {

            (flashcardSink as ValueAnimator).addUpdateListener {
                viewgroupFrontFlashcard.elevation = it.animatedValue as Float
            }
            flashcardSink.addListener(onStart = {
                viewgroupFrontFlashcard.translationX = 0f
            })
            flashcardSink.setTarget(viewgroupFrontFlashcard)
            flashcardDisappear.setTarget(viewgroupFrontFlashcard)

            on_PREV_from_FRONT_SIDE_CurrentFrontCardDisappearAnimation
                .play(flashcardSink)
                .before(flashcardDisappear)


            on_PREV_from_FRONT_SIDE_CardAnimation
                .play(on_PREV_from_FRONT_SIDE_CurrentFrontCardDisappearAnimation)
                .before(on_PREV_from_FRONT_SIDE_PreviousFrontCardAppearAnimation)
        }
    }

}

package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.addListener
import androidx.core.view.doOnPreDraw
import androidx.core.view.marginStart
import androidx.core.view.setPadding
import androidx.lifecycle.Observer
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.*
import com.teamttdvlp.memolang.databinding.ActivityUseFlashcardBinding
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager.SpeakerStatus.Companion.SPEAK_TEXT_AND_TRANSLATION
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager.SpeakerStatus.Companion.SPEAK_TEXT_ONLY
import com.teamttdvlp.memolang.model.UseFCActivity_StatusManager.SpeakerStatus.Companion.SPEAK_TRANSLATION_ONLY
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardView
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.customview.MyGestureDetector
import com.teamttdvlp.memolang.view.customview.interpolator.NormalOutExtraSlowIn
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.UseFlashcardViewModel
import javax.inject.Inject
import javax.inject.Named


class UseFlashcardActivity : BaseActivity<ActivityUseFlashcardBinding, UseFlashcardViewModel>()
                            ,UseFlashcardView {

    private var RED_TEXT_COLOR: Int = 0

    private var NORMAL_TEXT_COLOR: Int = 0

    private val COMMON_PROGRESS_BAR_VIEW_DURATION: Long = 100

    private val DARK_BLUE = "#176E80"

    private val COVERED_BY_BLACK_DARK_BLUE = "#0C1343"

    @field: Named("RotateForever")
    @Inject
    lateinit var rotateForeverAnimation: Animation

    private val on_REFLIP_FrontCardAppearAnimation = AnimatorSet()

    private val on_REFLIP_BackCardDisappearAnimation = AnimatorSet()

    private val on_REFLIP_Anim = AnimatorSet()


    private val on_OPEN_FrontCardDisappearAnimation = AnimatorSet()

    private val on_OPEN_BackCardAppearAnimation = AnimatorSet ()

    private val on_OPEN_Anim = AnimatorSet()


    private val on_NEXT_REMEMBER_CARD_Anim = AnimatorSet()

    private val on_NEXT_REMEMBER_CARD_BackCard_DisappearAnim = AnimatorSet()

    private val on_NEXT_REMEMBER_CARD_FrontCard_AppearAnim = AnimatorSet()


    private val on_NEXT_FORGET_CARD_Anim = AnimatorSet()

    private val on_NEXT_FORGET_CARD_FrontCard_AppearAnim = AnimatorSet()

    private val on_NEXT_FORGET_CARD_BackCard_DisappearAnim = AnimatorSet()


    private val on_PREV_from_FRONT_SIDE_CardAnimation = AnimatorSet()

    private val on_PREV_from_FRONT_SIDE_CurrentFrontCardDisappearAnimation = AnimatorSet()

    private val on_PREV_from_FRONT_SIDE_PreviousFrontCardAppearAnimation = AnimatorSet()


    private val on_PREV_from_BACK_SIDE_CardAnimation = AnimatorSet()

    private val on_PREV_from_BACK_SIDE_Card_BackCardDisappearAnimation = AnimatorSet()

    private val on_PREV_from_BACK_SIDE_CardFrontCardAppearAnimation = AnimatorSet()

    private var backButtonPressedTimes = 0

    private var prevForgottenCardCount = 0

    private var prevPassedCardCount = 0

    private var textIsSpoken = true

    private var translationIsSpoken = true

    private var speakerIsOn = true

    private var canGoToPreviousCard: Boolean = false

    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    @Inject set

    var CARD_WIDTH: Int = 0

    companion object {
        fun requestReviewFlashcard(
            requestContext: Context,
            deck: Deck,
            reverseCardTextAndTranslation: Boolean
        ) {
            val intent = Intent(requestContext, UseFlashcardActivity::class.java)
            intent.putExtra(FLASHCARD_SET_KEY, deck)
            intent.putExtra(REVERSE_CARD_TEXT_AND_TRANSLATION, reverseCardTextAndTranslation)
            requestContext.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_use_flashcard

    override fun takeViewModel() : UseFlashcardViewModel = getActivityViewModel(viewModelProviderFactory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(Color.parseColor(DARK_BLUE))

        dB.lifecycleOwner = this

        viewModel.setUpView(this)

        setUpData()

        dB.txtTotalCardCount.text = viewModel.getDeckSize().toString()

        viewModel.currentCard.observe(this, Observer { currentFlashcard ->
            recalculateFlashcard_DimensAndProperty(currentFlashcard)
        })
    }

    private fun setUpData() {
        viewModel.setUpData(getRequestedFlashcardSet(), getIsReverseTextAndTranslation())
        dB.executePendingBindings()
        setUpSpeakerStatus()
    }

    override fun onDestroy() {
        val speakerFunction = if (textIsSpoken and translationIsSpoken) {
            SPEAK_TEXT_AND_TRANSLATION
        } else if (textIsSpoken and not(translationIsSpoken)) {
            SPEAK_TEXT_ONLY
        } else if (not(textIsSpoken) and translationIsSpoken) {
            SPEAK_TRANSLATION_ONLY
        } else throw Exception("Speaker status unknown")

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

        CARD_WIDTH =
            ScreenDimension.screenWidth -
                    (dB.viewgroupFrontFlashcard.layoutParams as ConstraintLayout.LayoutParams).marginStart * 2
    }

    private var UNINITIAL_VALUE = -5
    private var previousTxtFrontTextHeightReference_Height = UNINITIAL_VALUE
    private var previousTxtBackTranslationHeightReference_Height = UNINITIAL_VALUE

    override fun addViewSettings() {
        dB.apply {

            txtFrontTextHeightReference.viewTreeObserver.addOnGlobalLayoutListener {

                var totalHeight = txtFrontTextHeightReference.height

                var otherInformationsTotalHeight = 0

                if (txtFrontCardType.isGone().not()) {
                    otherInformationsTotalHeight += txtFrontCardType.height
                }

                if (txtFrontCardPronunciation.isGone().not()) {
                    otherInformationsTotalHeight += txtFrontCardPronunciation.height
                }

                totalHeight += otherInformationsTotalHeight

                val currentCalculatedHeight: Int

                if (totalHeight > vwgrpFrontTexts.height) {
                    currentCalculatedHeight = vwgrpFrontTexts.height - otherInformationsTotalHeight
                } else {
                    currentCalculatedHeight = txtFrontTextHeightReference.height
                }

                if (txtFrontTextHeightReference.height != previousTxtFrontTextHeightReference_Height) {
                    previousTxtFrontTextHeightReference_Height = txtFrontTextHeightReference.height
                    txtFrontCardText.layoutParams.height = currentCalculatedHeight
                    txtFrontCardText.requestLayout()
                }

            }

            txtBackTranslationHeightReference.viewTreeObserver.addOnGlobalLayoutListener {

                var totalHeight = txtBackTranslationHeightReference.height
                var otherInformationsTotalHeight = 0

                if (txtBackCardExample.isGone().not()) {
                    otherInformationsTotalHeight += txtBackCardExample.height
                }

                if (txtBackCardMeanOfExample.isGone().not()) {
                    otherInformationsTotalHeight += txtBackCardMeanOfExample.height
                }

                totalHeight += otherInformationsTotalHeight

                val currentCalculatedHeight: Int

                if (totalHeight > vwgrpBackTexts.height) {
                    currentCalculatedHeight = vwgrpBackTexts.height - otherInformationsTotalHeight
                } else {
                    currentCalculatedHeight = txtBackTranslationHeightReference.height
                }


                // Because at the beginning vwgrpBackTexts.height = 0 due to its GONE visibility
                // So we have to check if the condition (txtBackCardTranslation.height == 0)
                // The condition (txtBackTranslationHeightReference.height != previousTxtBackTranslationHeightReference_Height) would be true
                // because of ViewTreeObserver runs many times caused by changes inside Activity
                // but txtBackCardTranslation.height is not set to the real height, it still keeps the 0 height
                if ((txtBackCardTranslation.height == 0) or (txtBackTranslationHeightReference.height != previousTxtBackTranslationHeightReference_Height)) {
                    previousTxtBackTranslationHeightReference_Height =
                        txtBackTranslationHeightReference.height
                    txtBackCardTranslation.layoutParams.height = currentCalculatedHeight
                    txtBackCardTranslation.requestLayout()
                }
            }
        }}

    override fun addAnimationEvents() { dB.apply {

        on_NEXT_REMEMBER_CARD_FrontCard_AppearAnim.addListener(onStart = {
            viewgroupFrontFlashcard.scaleX = 1.0f
            viewgroupFrontFlashcard.scaleY = 1.0f
            doActionOnGroup_FRONT_Card { view ->
                view.goVISIBLE()
            }

            if (viewModel.hasNext()) {
                this@UseFlashcardActivity.viewModel.nextCard()

                dB.executePendingBindings()
                if (textIsSpoken and speakerIsOn) {
                    viewModel.speakFrontCardText(dB.txtFrontCardText.text.toString())
                }
            } else {
                onEndReviewing()
                on_NEXT_REMEMBER_CARD_Anim.cancel()
            }
        })

        on_NEXT_FORGET_CARD_FrontCard_AppearAnim.addListener(onStart = {
            viewgroupFrontFlashcard.scaleX = 1.0f
            viewgroupFrontFlashcard.scaleY = 1.0f
            doActionOnGroup_FRONT_Card { frontView ->
                frontView.goVISIBLE()
            }

            if (viewModel.hasNext()) {
                this@UseFlashcardActivity.viewModel.nextCard()
                dB.executePendingBindings()
                if (textIsSpoken and speakerIsOn) {
                    viewModel.speakFrontCardText(dB.txtFrontCardText.text.toString())
                }
            } else {
                onEndReviewing()
                on_NEXT_FORGET_CARD_Anim.cancel()
            }
        })


        on_PREV_from_FRONT_SIDE_CurrentFrontCardDisappearAnimation.addListener(onEnd = {
            this@UseFlashcardActivity.viewModel.previousCard()
            dB.executePendingBindings()
            if (textIsSpoken and speakerIsOn) {
                viewModel.speakFrontCardText(dB.txtFrontCardText.text.toString())
            }
        })

        on_PREV_from_BACK_SIDE_Card_BackCardDisappearAnimation.addListener(onEnd = {
            doActionOnGroup_BACK_Card { view ->
                view.goGONE()

            }
            doActionOnGroup_BACK_Card { view ->
                view.elevation = 5.dp().toFloat()
            }
            viewgroupBackFlashcard.translationX = 0f
            viewgroupBackFlashcard.translationY = 0f

            doActionOnGroup_FRONT_Card { view ->
                view.goVISIBLE()
            }
            viewgroupFrontFlashcard.scaleX = 1.0f
            viewgroupFrontFlashcard.scaleY = 1.0f
            viewgroupFrontFlashcard.alpha = 1.0f
            viewgroupFrontFlashcard.translationX = CARD_WIDTH.toFloat()
            this@UseFlashcardActivity.viewModel.checkIfThereIsPreviousCard()
            this@UseFlashcardActivity.viewModel.previousCard()
            dB.executePendingBindings()
            if (textIsSpoken and speakerIsOn) {
                viewModel.speakFrontCardText(dB.txtFrontCardText.text.toString())
            }
        })

        on_OPEN_BackCardAppearAnimation.addListener(onStart = {
            doActionOnGroup_BACK_Card { view ->
                view.goVISIBLE()

            }
            viewgroupFrontFlashcard.elevation = 0f
            if (translationIsSpoken and speakerIsOn) {
                viewModel.speakBackCardText(txtBackCardTranslation.text.toString())
            }
        })
    }
    }

    private fun recalculate_Flashcard_Dimens(heightRate: Float) {
        dB.apply {
            viewgroupFrontFlashcard.apply {
                layoutParams.height = (heightRate * CARD_WIDTH / 2).toInt()
                requestLayout()
            }

            viewgroupBackFlashcard.apply {
                layoutParams.height = (heightRate * CARD_WIDTH / 2).toInt()
                requestLayout()
            }
        }
    }

    private fun recalculateFlashcard_DimensAndProperty(currentCard: Flashcard) {

        if (currentCard.cardProperty.heightOption == HEIGHT_NORMAL) {
            recalculate_Flashcard_Dimens(1f)

        } else if (currentCard.cardProperty.heightOption == HEIGHT_WIDE) {
            recalculate_Flashcard_Dimens(FLASHCARD_WIDE_HEIGHT_RATE)
        }

        if (currentCard.frontIllustrationPictureName != null) {
            systemOutLogging("Not null")
            showFront_DemoIllustration(currentCard.cardProperty)
        } else {
            showFrontSide_TextOnly()
        }

        if (currentCard.backIllustrationPictureName != null) {
            showBack_DemoIllustration(currentCard.cardProperty)
        } else {
            showBackSide_TextOnly()
        }
    }

    private fun showFront_DemoIllustration(currentCardProperty: CardProperty) {
        dB.apply {
            if (currentCardProperty.frontSideHasImage) {
                if (currentCardProperty.frontSideHasText) {
                    showFrontSide_BothTextAndImage()
                } else {
                    showFrontSide_ImageOnly()
                }
            }
        }
    }

    private fun showFrontSide_TextOnly() {
        dB.apply {
            (imgFrontIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).endToEnd =
                ConstraintSet.GONE
            setFront_IllustrationPictureWidth(0)
            vwgrpFrontTexts.goVISIBLE()
        }
    }

    private fun setFront_IllustrationPictureWidth(width: Int) {
        dB.apply {
            imgFrontIllustrationPicture.layoutParams.width = width
            imgFrontIllustrationPicture.requestLayout()
        }
    }

    private fun showFrontSide_ImageOnly() {
        dB.apply {
            vwgrpFrontTexts.goGONE()
            (imgFrontIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).apply {
                endToEnd = ConstraintSet.PARENT_ID
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            imgFrontIllustrationPicture.requestLayout()
        }
    }

    private fun showFrontSide_BothTextAndImage() {
        dB.apply {
            vwgrpFrontTexts.goVISIBLE()
            (imgFrontIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).endToEnd =
                ConstraintSet.GONE
            setFront_IllustrationPictureWidth((CARD_WIDTH - 2 * imgFrontIllustrationPicture.marginStart) / 2)
            imgFrontIllustrationPicture.requestLayout()
        }
    }

    // Back

    private fun showBack_DemoIllustration(currentCardProperty: CardProperty) {
        dB.apply {
            if (currentCardProperty.backSideHasImage) {
                if (currentCardProperty.backSideHasText) {
                    showBackSide_BothTextAndImage()
                } else {
                    showBackSide_ImageOnly()
                }
            }
        }
    }

    private fun showBackSide_TextOnly() {
        dB.apply {
            (imgBackIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).endToEnd =
                ConstraintSet.GONE
            setBack_IllustrationPictureWidth(0)
            vwgrpBackTexts.goVISIBLE()
        }
    }

    private fun showBackSide_ImageOnly() {
        dB.apply {
            vwgrpBackTexts.goGONE()
            (imgBackIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).endToEnd =
                ConstraintSet.PARENT_ID
            (imgBackIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).width =
                ViewGroup.LayoutParams.MATCH_PARENT
            imgBackIllustrationPicture.requestLayout()
        }
    }

    private fun showBackSide_BothTextAndImage() {
        dB.apply {
            vwgrpBackTexts.goVISIBLE()
            (imgBackIllustrationPicture.layoutParams as ConstraintLayout.LayoutParams).endToEnd =
                ConstraintSet.GONE
            setBack_IllustrationPictureWidth((CARD_WIDTH - 2 * imgFrontIllustrationPicture.marginStart) / 2)
            imgBackIllustrationPicture.requestLayout()
        }
    }

    private fun setBack_IllustrationPictureWidth(width: Int) {
        dB.apply {
            imgBackIllustrationPicture.layoutParams.width = width
            imgBackIllustrationPicture.requestLayout()
        }
    }

    private fun flipBackCardUp() {
        on_OPEN_Anim.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun addViewEvents() {
        dB.apply {

            imgFrontSwipeZone.setOnTouchListener(object :
                MyGestureDetector(this@UseFlashcardActivity) {
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
            this@UseFlashcardActivity.viewModel.handleUserForgetCard()
            nextCard(false)
        }

        dialogExit.setOnHide {
            resetBackButtonPressedTimes()
            setStatusBarColor(Color.parseColor(DARK_BLUE))
        }

        dialogExit.setOnStartHide {
            turnStatusBarToLighterColor(dialogExit.getAnimDuration())
        }

        dialogExit.setOnShow {
            setStatusBarColor(Color.parseColor(COVERED_BY_BLACK_DARK_BLUE))
        }

        btnSpeakerSetting.setOnClickListener {
            dialogSetting.show()
        }

        btnFlipFlashcardSet.setOnClickListener {
            requestReviewFlashcard(
                this@UseFlashcardActivity, viewModel.getOriginalFlashcardSet(),
                reverseCardTextAndTranslation = viewModel.isReversedTextAndTranslation
            )
            finish()
        }

        radioGrpSpeakerSetting.setOnCheckedChangeListener { _, checkedId ->
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
                this@UseFlashcardActivity.viewModel.handleUserRememberCard()
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
        on_REFLIP_Anim.start()
    }

    private fun showCanNotGoToPreviousCardNotification() {
        quickToast("This is the first card")
    }

    override fun onBackPressed() {
        backButtonPressedTimes++
        if (backButtonPressedTimes == 1) {
            showExitDialog()
        } else if (backButtonPressedTimes == 2) {
            finish()
        }
    }

    private fun showExitDialog() {
        dB.dialogExit.show()
        turnStatusBarToDarkerColor(dB.dialogExit.getAnimDuration())
    }

    private fun turnStatusBarToDarkerColor(duration: Long) {
        val darkenAnim = ValueAnimator.ofFloat(0f, 1f)
        darkenAnim.apply {
            this.duration = duration
            addUpdateListener {
                turnStatusBarToDarkerColor(it.animatedFraction)
            }
            setTarget(View(this@UseFlashcardActivity))
            start()
        }
    }

    private fun turnStatusBarToLighterColor(duration: Long) {
        val lighterAnim = ValueAnimator.ofFloat(0f, 1f)
        lighterAnim.apply {
            this.duration = duration
            addUpdateListener {
                turnStatusBarToDarkerColor(1f - it.animatedFraction)
            }
            setTarget(View(this@UseFlashcardActivity))
            start()
        }
    }


    val redOffset = 12 - 22 // -6
    val greenOffset = 89 - 159 // -46
    val blueOffset = 102 - 186 // -65

    private fun turnStatusBarToDarkerColor(level: Float) {
        val r = 22 + redOffset * level
        val g = 159 + greenOffset * level
        val b = 186 + blueOffset * level

        setStatusBarColor(Color.rgb(r.toInt(), g.toInt(), b.toInt()))
    }


    // VIEW PROCESSING

    override fun onPassACard(passedCardCount: Int, forgottenCardCount: Int) {
        dB.txtPassedCardCount.text = passedCardCount.toString()
        dB.txtForgottenCardCount.text = forgottenCardCount.toString()

        val userFinishTest = (passedCardCount + forgottenCardCount) == viewModel.getDeckSize()
        if (userFinishTest) {
            dB.txtTotalCardCount.animate().alpha(0f).duration = 100
        }

        if ((passedCardCount + forgottenCardCount) == viewModel.getDeckSize()) {
            dB.txtForgottenCardCount.animate().alpha(0f).duration =
                COMMON_PROGRESS_BAR_VIEW_DURATION
        }

        val userRememberCard = passedCardCount > prevPassedCardCount

        val userForgetCard = userRememberCard.not() &&
                (forgottenCardCount > prevForgottenCardCount)

        val userRelearnCard = (userRememberCard) && (forgottenCardCount < prevForgottenCardCount)

        if (userRememberCard) {
            increasePassedCardProgressBar(passedCardCount)
            if (forgottenCardCount > 0) {
                updateForgottenCardProgressBar(passedCardCount, forgottenCardCount)
            }

        } else if (userForgetCard) {
            updateForgottenCardProgressBar(passedCardCount, forgottenCardCount)

        } else if (userRelearnCard) {
            increasePassedCardProgressBar(passedCardCount)
        }

        prevPassedCardCount = passedCardCount
        prevForgottenCardCount = forgottenCardCount
    }

    override fun onGetFrontIllustration(illustration: Bitmap) {
        setFront_IllustrationAndAdjustRatio(illustration)
    }

    override fun onGetBackIllustration(illustration: Bitmap) {
        setBack_IllustrationAndAdjustRatio(illustration)
    }

    override fun onLoadAllIllustrationStart() {
        rotateForeverAnimation.duration = 1000
        dB.progressBarLoadingImage.startAnimation(rotateForeverAnimation)
    }

    override fun onLoadAllIllustrationFinish() {
        hideLoadIllustrationProgressBar()
    }

    private fun hideLoadIllustrationProgressBar () {
        dB.apply {
            vwgrpLoadImageProgressBar.animate().alpha(0f)
                .setDuration(100).setInterpolator(NormalOutExtraSlowIn())
                .setLiteListener(onEnd = {
                    vwgrpLoadImageProgressBar.goGONE()
                    progressBarLoadingImage.animation.cancel()
                })
        }
    }

    private fun setFront_IllustrationAndAdjustRatio(picture: Bitmap) {

        if (isFrontTextEmpty().not()) {
            val rate = picture.width.toFloat() / picture.height.toFloat()
            if (rate < 1) {
                recalculateFront_IllustrationPictureWidth_ByHeightRate(rate)
            } else {
                recalculateFront_IllustrationPictureWidth_ByHeightRate(1f)
            }
        }

        dB.imgFrontIllustrationPicture.setPadding(0)
        dB.imgFrontIllustrationPicture.setImageBitmap(picture)
    }

    private fun isFrontTextEmpty(): Boolean {
        dB.apply {
            return viewModel.currentCard.value!!.text.isEmpty()
                    && viewModel.currentCard.value!!.type.isEmpty()
                    && viewModel.currentCard.value!!.pronunciation.isEmpty()
        }
    }

    private fun recalculateFront_IllustrationPictureWidth_ByHeightRate(heightToWidthRate: Float) {
        dB.apply {
            imgFrontIllustrationPicture.doOnPreDraw {
                imgFrontIllustrationPicture.layoutParams.width =
                    (imgFrontIllustrationPicture.height * heightToWidthRate).toInt()
                imgFrontIllustrationPicture.requestLayout()
            }
        }
    }

    private fun setBack_IllustrationAndAdjustRatio(picture: Bitmap) {

        if (isBackTextEmpty().not()) {
            val rate = picture.width.toFloat() / picture.height.toFloat()
            if (rate < 1) {
                recalculateBack_IllustrationPictureWidth_ByHeightRate(rate)
            } else {
                recalculateBack_IllustrationPictureWidth_ByHeightRate(1f)
            }
        }

        dB.imgBackIllustrationPicture.setPadding(0)
        dB.imgBackIllustrationPicture.setImageBitmap(picture)
    }

    private fun recalculateBack_IllustrationPictureWidth_ByHeightRate(heightToWidthRate: Float) {
        dB.apply {
            imgBackIllustrationPicture.doOnPreDraw {
                imgBackIllustrationPicture.layoutParams.width =
                    (imgBackIllustrationPicture.height * heightToWidthRate).toInt()
                imgBackIllustrationPicture.requestLayout()
            }
        }
    }

    private fun isBackTextEmpty(): Boolean {
        dB.apply {
            return viewModel.currentCard.value!!.translation.isEmpty()
                    && viewModel.currentCard.value!!.example.isEmpty()
                    && viewModel.currentCard.value!!.meanOfExample.isEmpty()
        }
    }

    private fun updateForgottenCardProgressBar(passedCardCount: Int, forgottenCardCount: Int) {
        dB.apply {
            if (txtForgottenCardProgressBar.alpha == 0f) {
                txtForgottenCardProgressBar.animate().alpha(1f).duration =
                    COMMON_PROGRESS_BAR_VIEW_DURATION
                txtForgottenCardCount.animate().alpha(1f).duration =
                    COMMON_PROGRESS_BAR_VIEW_DURATION
            }

            val aPartWidth = txtTotalCardProgressBar.width / viewModel.getDeckSize()

            val progrBarCurrentWidth = txtForgottenCardProgressBar.width
            val progrBarTargetWidth = (passedCardCount + forgottenCardCount) * aPartWidth
            val increaseAnim =
                ValueAnimator.ofInt(progrBarCurrentWidth, progrBarTargetWidth).apply {
                    duration = 500
                    interpolator = OvershootInterpolator(1f)
                    addUpdateListener {
                        txtForgottenCardProgressBar.layoutParams.width = it.animatedValue as Int
                        txtForgottenCardProgressBar.requestLayout()
                    }
                    setTarget(txtForgottenCardProgressBar)
                }
            increaseAnim.start()
        }
    }

    private fun increasePassedCardProgressBar(currentCount: Int) {
        dB.apply {
            val progrBarCurrentWidth = txtPassedCardProgressBar.width
            val aPartWidth = txtTotalCardProgressBar.width / viewModel.getDeckSize()
            val progrBarTargetWidth = aPartWidth * currentCount
            val increaseAnim = ValueAnimator.ofInt(progrBarCurrentWidth, progrBarTargetWidth)
            increaseAnim.duration = 500
            increaseAnim.interpolator = OvershootInterpolator(2f)
            increaseAnim.setTarget(txtPassedCardProgressBar)

            increaseAnim.addUpdateListener {
                txtPassedCardProgressBar.layoutParams.width = it.animatedValue as Int
                txtPassedCardProgressBar.requestLayout()
            }
            increaseAnim.start()
        }
    }


    override fun onEndReviewing() {
        doActionOnGroup_FRONT_Card { it ->
            it.goINVISIBLE()
        }
        viewModel.stopAllTextSpeaker()
        on_NEXT_REMEMBER_CARD_Anim.cancel()
        sendCardSetInfoTo_ResultReportActivity()
        finish()
    }

    override fun showSpeakTextError(error: String) {
        quickToast(error)
    }

    override fun lock_ShowPreviousCard_Function() {
        canGoToPreviousCard = false
    }

    override fun unlock_ShowPreviousCard_Function() {
        canGoToPreviousCard = true
    }

    private fun nextCard (userRememberCurrentCard : Boolean) {
        if (userRememberCurrentCard) {
            on_NEXT_REMEMBER_CARD_Anim.start()
        } else {
            on_NEXT_FORGET_CARD_Anim.start()
        }
    }

    private fun resetBackButtonPressedTimes() {
        backButtonPressedTimes = 0
    }

    private fun sendCardSetInfoTo_ResultReportActivity() {
        val hardCardList = viewModel.getForgottenCardList()
        val flashcardSet = viewModel.getOriginalFlashcardSet()
        ResultReportActivity.requestFinishUsingFlashcard(
            this, flashcardSet, hardCardList,
            ResultReportActivity.FlashcardSendableActivity.USE_FLASHCARD_ACTIVITY.code
        )
    }

    override fun getRequestedFlashcardSet(): Deck {
        return intent.extras!!.getSerializable(FLASHCARD_SET_KEY) as Deck
    }

    override  fun getIsReverseTextAndTranslation(): Boolean {
        return intent.extras!!.getBoolean(REVERSE_CARD_TEXT_AND_TRANSLATION, false)
    }


    private fun setUpSpeakerStatus() {
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

    override fun overrideEnterAnim() {
        overridePendingTransition(R.anim.appear, R.anim.nothing)
    }

    override fun overrideExitAnim() {
        overridePendingTransition(R.anim.nothing, R.anim.disappear)
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
                doActionOnGroup_FRONT_Card { view ->
                    view.goVISIBLE()
                }
            }, onEnd = {
                doActionOnGroup_FRONT_Card { view ->
                    view.goINVISIBLE()
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
        on_OPEN_Anim.play(on_OPEN_FrontCardDisappearAnimation)
            .before(on_OPEN_BackCardAppearAnimation)
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
        on_REFLIP_Anim.play(on_REFLIP_BackCardDisappearAnimation)
            .before(on_REFLIP_FrontCardAppearAnimation)
    }}

    // ON NEXT REMEMBER

    @Inject
    fun init_ON_NEXT_REMEBER_CardFrontCardAppearAnimation(
        @Named("Float") flashcardFloatAnimator: Animator,
        @Named("Appear50Percents") txtSwipeFunctionAppear: Animator,
        @Named("Appear100Percents") frontCardAppearAnimator: Animator
    ) {
        dB.apply {

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

            frontCardAppearAnimator.apply {
                startDelay = 30
                duration = 100
            }

            frontCardAppearAnimator.setTarget(dB.viewgroupFrontFlashcard)

            flashcardFloatAnimator.setTarget(dB.viewgroupFrontFlashcard)

            txtSwipeFunctionAppear.setTarget(vwgrpSwipeFunctions)

            on_NEXT_REMEMBER_CARD_FrontCard_AppearAnim
                .play(flashcardFloatAnimator).with(txtSwipeFunctionAppear)
                .after(frontCardAppearAnimator)

            on_NEXT_REMEMBER_CARD_Anim.play(on_NEXT_REMEMBER_CARD_BackCard_DisappearAnim)
                .before(on_NEXT_REMEMBER_CARD_FrontCard_AppearAnim)

        }}


    @Inject
    fun init_ON_NEXT_REMEBER_Card_BackCardDisappearAnimation(
        @Named("MoveRightAndFadeOutAnimtr") flashcardMoveOutAnimator: Animator,
        @Named("Disappear100Percents") buttonEasyDisappear: Animator,
        @Named("Disappear100Percents") buttonHardDisappear: Animator
    ) {
        dB.apply {
            flashcardMoveOutAnimator.setTarget(viewgroupBackFlashcard)
            buttonHardDisappear.setTarget(btnHard)

            val navigateDisappearSet = AnimatorSet().apply {
                play(buttonEasyDisappear).with(buttonHardDisappear)
            }

            on_NEXT_REMEMBER_CARD_BackCard_DisappearAnim.playSequentially(
                flashcardMoveOutAnimator,
                navigateDisappearSet
            )
            on_NEXT_REMEMBER_CARD_BackCard_DisappearAnim.addListener(onEnd = {
                doActionOnGroup_BACK_Card { it ->
                    it.goGONE()
                }

                viewgroupBackFlashcard.translationX = 0f
                viewgroupBackFlashcard.translationY = 0f

                viewgroupFrontFlashcard.alpha = 0f
            })

    }}


    // ON FORGET A CARD

    @Inject
    fun init_ON_NEXT_FORGET_CardFrontCardAppearAnimation(
        @Named("Float") flashcardFloatAnimator: Animator,
        @Named("Appear50Percents") txtSwipeFunctionAppear: Animator,
        @Named("Appear100Percents") frontCardAppearAnimator: Animator
    ) {
        dB.apply {

            (flashcardFloatAnimator as ValueAnimator)

            flashcardFloatAnimator.apply {
                startDelay = 100
                duration = 150
                addUpdateListener {
                    viewgroupFrontFlashcard.elevation = it.animatedValue as Float
                }

                addListener(
                    onEnd = {
                        viewgroupBackFlashcard.alpha = 1.0f
                    })
            }

            frontCardAppearAnimator.apply {
                startDelay = 100
                duration = 150
            }

            frontCardAppearAnimator.setTarget(dB.viewgroupFrontFlashcard)

            flashcardFloatAnimator.setTarget(dB.viewgroupFrontFlashcard)

            txtSwipeFunctionAppear.setTarget(vwgrpSwipeFunctions)

            on_NEXT_FORGET_CARD_FrontCard_AppearAnim
                .play(flashcardFloatAnimator).with(txtSwipeFunctionAppear)
                .after(frontCardAppearAnimator)

            on_NEXT_FORGET_CARD_Anim
                .play(on_NEXT_FORGET_CARD_BackCard_DisappearAnim)
                .before(on_NEXT_FORGET_CARD_FrontCard_AppearAnim)

            on_NEXT_FORGET_CARD_Anim.addListener(onEnd = {
                viewgroupBackFlashcard.scaleX = 1f
                viewgroupBackFlashcard.scaleY = 1f
            })
        }
    }

    @Inject
    fun init_ON_NEXT_FORGET_Card_BackCardDisappearAnimation(
        @Named("Disappear100Percents") backCardDisappear: Animator
        , @Named("ZoomFrom1xto3x") backCardScaleUp: Animator
//        ,@Named("Disappear100Percents") buttonHardDisappear : Animator
    ) {
        dB.apply {

            val textColorChangeAnim: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                textColorChangeAnim.duration = 1000
                textColorChangeAnim.interpolator = LinearInterpolator()
                textColorChangeAnim.setTarget(txtBackCardTranslation)
                textColorChangeAnim.addUpdateListener { valueAnim ->
                    txtBackCardTranslation.setTextColor(
                        Color.rgb((valueAnim.animatedFraction * 255).toInt(), 3, 0)
                    )
                }
            }

            backCardDisappear.apply {
                duration = 300
                startDelay = 500
                interpolator =
                    NormalOutExtraSlowIn()
                setTarget(viewgroupBackFlashcard)
            }

            backCardScaleUp.apply {
                duration = 300
                startDelay = 500
                interpolator =
                    NormalOutExtraSlowIn()
                setTarget(viewgroupBackFlashcard)
            }

            on_NEXT_FORGET_CARD_BackCard_DisappearAnim
                .play(textColorChangeAnim)
                .before(AnimatorSet().apply {
                    playTogether(backCardScaleUp, backCardDisappear)
                })

            on_NEXT_FORGET_CARD_BackCard_DisappearAnim.addListener(onEnd = {
                doActionOnGroup_BACK_Card { backView ->
                    backView.goGONE()
                }
                txtBackCardTranslation.setTextColor(Color.BLACK)
                viewgroupBackFlashcard.translationX = 0f
                viewgroupBackFlashcard.translationY = 0f

                viewgroupFrontFlashcard.alpha = 0f
            })

            on_NEXT_FORGET_CARD_Anim
                .play(on_NEXT_FORGET_CARD_BackCard_DisappearAnim)
                .before(on_NEXT_FORGET_CARD_FrontCard_AppearAnim)
        }
    }


    // ON PREV FROM BACK
    @Inject
    fun init_ON_PREV_FROM_BACK_CardFrontCardAppearAnimation(
        @Named("FromRightToCentreAndFadeIn") flashcardMoveFromRightToCentre: Animator,
        @Named("Appear50Percents") txtSwipeFunctionAppear: Animator
    ) {
        dB.apply {

            flashcardMoveFromRightToCentre.startDelay = 150

            flashcardMoveFromRightToCentre.addListener(
                onStart = {
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
    fun init_ON_PREV_FROM_BACK_Card_BackCardDisappearAnimation(
        @Named("Sink") flashcardSink: Animator,
        @Named("Disappear100Percents") flashcardDisappear: Animator,
        @Named("Disappear100Percents") buttonEasyDisappear: Animator,
        @Named("Disappear100Percents") buttonHardDisappear: Animator
    ) {
        dB.apply {

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

            on_PREV_from_BACK_SIDE_Card_BackCardDisappearAnimation.playSequentially(
                navigateDisappearSet,
                flashcardDisappearSet
            )

            on_PREV_from_BACK_SIDE_CardAnimation.play(
                on_PREV_from_BACK_SIDE_Card_BackCardDisappearAnimation
            )
                .before(on_PREV_from_BACK_SIDE_CardFrontCardAppearAnimation)
        }
    }


    // ON PREV FROM BACK

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



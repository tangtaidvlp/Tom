package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.databinding.ActivityReviewFlashcardBinding
import com.teamttdvlp.memolang.model.ReviewActivitiesSpeakerStatusManager
import com.teamttdvlp.memolang.model.findTextFormInAnother
import com.teamttdvlp.memolang.view.activity.iview.WritingFlashcardView
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.customview.interpolator.NormalOutExtraSlowIn
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.WritingFlashcardViewModel
import javax.inject.Inject
import javax.inject.Named


const val FLASHCARD_SET_KEY = "flashcard_set"

const val REVERSE_CARD_TEXT_AND_TRANSLATION = "reverse"

const val VIEW_LIST_REQUEST_CODE = 118

class WritingFlashcardActivity : BaseActivity<ActivityReviewFlashcardBinding, WritingFlashcardViewModel>(),
                                                WritingFlashcardView {

    private val MAX_SHOW_HINT_TIMES: Int = 2

    private val APP_RED = "#F65546"

    private val LITTLE_DARK_GREEN = "#01A40C"

    private val NEXT_CARD_ANIMS_DURATION = 400L

    private val COMMON_PROGRESS_BAR_VIEW_DURATION = 100L

    @field: Named("RotateForever")
    @Inject
    lateinit var rotateForeverAnimation: Animation

    @field: Named("QuickAppearThenDisappearAnimation")
    @Inject
    lateinit var imgCorAnsAppearAnim : Animation

    @field: Named("QuickAppearThenDisappearAnimation")
    @Inject
    lateinit var imgIncorAnsAppearAnim : Animation

    @field: Named("QuickAppearThenDisappearAnimation")
    @Inject
    lateinit var imgVeryGoodAnsAppearAnim : Animation

    @field: Named("QuickAppearThenDisappearAnimation")
    @Inject
    lateinit var imgBadAnswersAppearAnim : Animation

    @field: Named("VibrateAnim")
    @Inject
    lateinit var vwgrpVibrateAnim : Animation

    lateinit var nextCardAnimtor: Animator

    lateinit var viewModelProviderFactory : ViewModelProviderFactory
    @Inject set


    private var answerIsSpoken = true

    private var questionIsSpoken = true

    private var speakerIsOn = true

    private var prevForgottenCardCount = 0

    private var prevPassedCardCount = 0

    private var currentPressShowHintButtonTime = 0

    var nextCardAnimtrSet: AnimatorSet = AnimatorSet()

    var resetHintAnimtrSet: AnimatorSet = AnimatorSet()

    var reverseCardTextAndTrans = false

    companion object {
        fun requestReviewFlashcard(
            requestContext: Context,
            deck: Deck,
            reverseCardTextAndTranslation: Boolean
        ) {
            val intent = Intent(requestContext, WritingFlashcardActivity::class.java)
            intent.putExtra(FLASHCARD_SET_KEY, deck)
            intent.putExtra(REVERSE_CARD_TEXT_AND_TRANSLATION, reverseCardTextAndTranslation)
            requestContext.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_review_flashcard

    override fun takeViewModel(): WritingFlashcardViewModel {
        return getActivityViewModel(viewModelProviderFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(resources.getColor(R.color.app_writing_dark_pink))
        viewModel.setUpView(this)
        dB.vwModel = viewModel
        setUpData()
        dB.txtTotalCardCount.text = viewModel.getDeckSize().toString()
    }

    override fun onDestroy() {
        val speakerFunction = if (answerIsSpoken and questionIsSpoken) {
            ReviewActivitiesSpeakerStatusManager.SpeakerStatus.SPEAK_QUESTION_AND_ANSWER
        } else if (answerIsSpoken and not(questionIsSpoken)) {
            ReviewActivitiesSpeakerStatusManager.SpeakerStatus.SPEAK_ANSWER_ONLY
        } else if (not(answerIsSpoken) and questionIsSpoken) {
            ReviewActivitiesSpeakerStatusManager.SpeakerStatus.SPEAK_QUESTION_ONLY
        } else throw Exception ("Speaker status unknown")

        viewModel.saveAllStatus(speakerFunction, speakerIsOn)
        super.onDestroy()
    }

    override fun addAnimationEvents() {
        nextCardAnimtor.addListener (onEnd = {
            dB.vwgrpTestSubject.translationX = 0f
            dB.vwgrpTestSubject.alpha = 1f
            nextCardAnimtrSet.start()

            goToNextCard()
        })
    }

    private fun goToNextCard () {
        // Change new subject
        viewModel.nextCard()
    }

    override fun addViewSettings() {
        dB.apply {

        }
    }

    override fun addViewEvents() { dB.apply {

        val mashmallowAnim = AnimationUtils.loadAnimation(
            this@WritingFlashcardActivity,
            R.anim.mashmallow_effect
        ).apply {
            interpolator =
                NormalOutExtraSlowIn()
        }

        btnClearAnswer.setOnClickListener {
            edtInputAnswer.setText("")
            btnClearAnswer.startAnimation(mashmallowAnim)
        }

        btnSubmit.setOnClickListener {
            viewModel.submitAnswer(edtInputAnswer.text.toString())
            btnSubmit.startAnimation(mashmallowAnim)
        }

        edtInputAnswer.addTextChangeListener { userAnswer, _ , _ , _ ->
            viewModel.checkAnswer(userAnswer)
        }

        edtInputAnswer.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_GO) {
                viewModel.submitAnswer(edtInputAnswer.text.toString())
            }

            return@setOnEditorActionListener true
        }

        vwgrpBtnShowHint.setOnClickListener {
            if (currentPressShowHintButtonTime < MAX_SHOW_HINT_TIMES) {
                performShowAnswerHintAnimations()
                currentPressShowHintButtonTime++

                vwgrpBtnShowHint.startAnimation(mashmallowAnim)

                if (currentPressShowHintButtonTime == MAX_SHOW_HINT_TIMES) {
                    performHintOverAnimations()
                }
            }
        }

        btnFlipDeck.setOnClickListener {
            flipWholeFlashcardSet()
        }

        btnSetting.setOnClickListener {
            dialogSetting.show()
        }

        switchSpeaker.setOnCheckedChangeListener { v, isChecked ->
            speakerIsOn = isChecked
        }

        radioGrpSpeakerSetting.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                checkboxSpeakAnswerOnly.id -> {
                    answerIsSpoken = true
                    questionIsSpoken = false
                }

                checkboxSpeakQuestionOnly.id -> {
                    questionIsSpoken = true
                    answerIsSpoken = false
                }
                checkboxSpeakBothQuestionAndAnswer.id -> {
                    questionIsSpoken = true
                    answerIsSpoken = true
                }
            }
        }

    }
    }

    private fun performHintOverAnimations() {
        dB.imgShowHint.animate().alpha(0.3f).setDuration(100).setInterpolator(NormalOutExtraSlowIn())
        dB.imgShowHint.isClickable = false
    }

    private fun performResetHintButtonsAnimations() {
        dB.imgShowHint.animate().alpha(1f).setDuration(100).setInterpolator(NormalOutExtraSlowIn())
        dB.imgShowHint.isClickable = true
    }

    private fun performShowAnswerHintAnimations() { dB.apply {

        edtInputAnswer.setText("")
        val currentAnswerHint = viewModel.getCurrentAnswerHint()
        val showHintAnimation = ValueAnimator.ofInt(0, 100).apply {
            duration = 150
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener (object : ValueAnimator.AnimatorUpdateListener {
                var currentPos = -1
                var interval = 1f/currentAnswerHint.length.toFloat()
                override fun onAnimationUpdate(animation: ValueAnimator?) {
                    val currentProgress = animation!!.animatedFraction
                    val currentProgressPos = (currentProgress/interval).toInt()
                    if (currentProgressPos > currentPos) {
                        currentPos = currentProgressPos
                        val currentShowedHint = currentAnswerHint.substring(0, currentPos)
                        edtInputAnswer.hint = currentShowedHint
                    }
                }
            })
            setTarget(edtInputAnswer)
        }

        showHintAnimation.start()
    }}

    private fun playMinimizeTitleBarAnimations (startDelay: Long) { dB.apply {
        val DURATION = 150L
        vwgrpTitle.animate().setStartDelay(startDelay).alpha(0f).setDuration(DURATION).setInterpolator(FastOutSlowInInterpolator())
        imgLowerLayerMinimumTitleBackground.animate().setStartDelay(startDelay).alpha(1f).setDuration(DURATION).setInterpolator(FastOutSlowInInterpolator())
        vwgrpMinimumTitle.animate().setStartDelay(startDelay).alpha(1f).setDuration(DURATION).setInterpolator(FastOutSlowInInterpolator())

        val minimizeAnim = ValueAnimator.ofInt(vwgrpTitle.height, 30.dp())
        minimizeAnim.apply {
            duration = DURATION
            interpolator = NormalOutExtraSlowIn()
            addUpdateListener { anim ->
                val height = anim.animatedValue
                vwgrpTitle.layoutParams.height = height as Int
                vwgrpTitle.requestLayout()
            }
            setTarget(vwgrpTitle)
            setStartDelay(startDelay)
            start()
        }

    }}

    private fun setUpData() {
        viewModel.setUpData(getRequestedFlashcardSet(), getIsReverseTextAndTranslation())
        this.reverseCardTextAndTrans = viewModel.isDeckReversed
        setUpSpeakerStatus()
        dB.edtInputAnswer.requestFocus()
        showVirtualKeyboard()
    }

    private fun flipWholeFlashcardSet() {
        requestReviewFlashcard(
            this@WritingFlashcardActivity, viewModel.getOriginalFlashcardSet(),
            viewModel.isDeckReversed.not()
        )
        finish()
    }

    private fun setUpSpeakerStatus() {
        speakerIsOn = viewModel.getSpeakerStatus()
        dB.switchSpeaker.isChecked = speakerIsOn

        when (viewModel.getSpeakerFunction()) {
            ReviewActivitiesSpeakerStatusManager.SpeakerStatus.SPEAK_ANSWER_ONLY -> {
                answerIsSpoken = true
                questionIsSpoken = false
                dB.checkboxSpeakAnswerOnly.isChecked = true
            }

            ReviewActivitiesSpeakerStatusManager.SpeakerStatus.SPEAK_QUESTION_ONLY -> {
                answerIsSpoken = false
                questionIsSpoken = true
                dB.checkboxSpeakQuestionOnly.isChecked = true
            }

            ReviewActivitiesSpeakerStatusManager.SpeakerStatus.SPEAK_QUESTION_AND_ANSWER -> {
                answerIsSpoken = true
                questionIsSpoken = true
                dB.checkboxSpeakBothQuestionAndAnswer.isChecked = true
            }
        }
    }

    override fun overrideEnterAnim() {
        overridePendingTransition(R.anim.appear, R.anim.nothing)
    }

    override fun overrideExitAnim() {
        overridePendingTransition(R.anim.nothing, R.anim.disappear)
    }

    // VIEW FUNCTION ===========================================

    override fun showValidAnsBehaviours() {
        dB.apply {

        }
    }

    override fun showInvalidAnsBehaviours() {
        dB.apply {

        }
    }

    override fun onEndReviewing() {
        sendHardCardListToEndActivity()
        finish()
    }

    override fun showSpeakTextError(error: String) {
        systemOutLogging(error)
    }

    override fun onLoadAllIllustrationStart() {
        rotateForeverAnimation.duration = 1000
        dB.progressBarLoadingImage.startAnimation(rotateForeverAnimation)
    }

    override fun onLoadAllIllustrationFinish() {
        hideLoadIllustrationProgressBar()
        playMinimizeTitleBarAnimations(4000)
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

    private fun updateForgottenCardProgressBar(passedCardCount: Int, forgottenCardCount: Int) {
        dB.apply {
            if (txtForgottenCardProgressBar.alpha == 0f) {
                txtForgottenCardProgressBar.animate().alpha(1f).duration =
                    COMMON_PROGRESS_BAR_VIEW_DURATION
//                txtForgottenCardCount.animate().alpha(1f).duration = COMMON_PROGRESS_BAR_VIEW_DURATION
            }
            systemOutLogging("Update forgotten: " + forgottenCardCount)
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

    override fun showGoodAnswerAnimation() { dB.apply {
//        imgCorrectAnswer.goVISIBLE()
//        imgCorrectAnswer.startAnimation(imgCorAnsAppearAnim)
        performShowAnswerAnimations(true)
    }}

    override fun showExcellentAnswerAnimation() { dB.apply {
//        imgVeryGoodAnswer.goVISIBLE()
//        imgVeryGoodAnswer.startAnimation(imgVeryGoodAnsAppearAnim)
        performShowAnswerAnimations(true)
    }}

    override fun showWrongAnswerAnimation() { dB.apply {
//        imgIncorrectAnswer.goVISIBLE()
//        imgIncorrectAnswer.startAnimation(imgIncorAnsAppearAnim)
        vwgrpTestSubject.startAnimation(vwgrpVibrateAnim)
    }}

    override fun showNotPassAnswerAnimation() { dB.apply {
//        imgBadAnswers.goVISIBLE()
//        imgBadAnswers.startAnimation(imgBadAnswersAppearAnim)
        performShowAnswerAnimations(false)
    }}

    override fun onPassACard(passedCardCount: Int, forgottenCardCount: Int) {
        dB.txtPassedCardCount.text = passedCardCount.toString()

        val userFinishTest = (passedCardCount + forgottenCardCount) == viewModel.getDeckSize()
        if (userFinishTest) {
            dB.txtTotalCardCount.animate().alpha(0f).duration = 100
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

    private fun speakAnswer(text: String, onSpeakDone: () -> Unit) {
        viewModel.speakAnswer(text, onSpeakDone)
    }

    private fun allowSpeakAnswer(): Boolean {
        return (speakerIsOn and answerIsSpoken)
    }

    private fun speakQuestion(text: String) {
        viewModel.speakQuestion(text)
    }

    private fun allowSpeakQuestion(): Boolean {
        return (speakerIsOn and questionIsSpoken)
    }

    override fun highlightHintOption() {
//        dB.imgLightedLight.alpha = 1f
//        dB.imgLightedLight.goVISIBLE()
//        dB.imgGreyLight.goGONE()
    }

    override fun resetHintOptionState() {
//        dB.imgGreyLight.goVISIBLE()
//        dB.imgLightedLight.goGONE()
//        dB.txtHint.alpha = 0f
    }

    override fun nextCard (startDelay : Long) {
        if (viewModel.hasNext().not()) {
            onEndReviewing()
            return
        }

        performResetHintButtonsAnimations()
        dB.edtInputAnswer.hint = ""
        currentPressShowHintButtonTime = 0

        nextCardAnimtor.startDelay = startDelay
        nextCardAnimtor.start()
        resetHintAnimtrSet.startDelay = startDelay
        resetHintAnimtrSet.start()
        dB.edtInputAnswer.setText("")
    }

    override fun onGetTestSubject(card: Flashcard, illustration : Bitmap?, load_illustrationException: Exception?, useExampleForTestSubject : Boolean) { dB.apply {
        val answer = card.text
        txtTextAnswer.text = answer

        if (illustration != null) {
            imgTestSubjectIllustration.goVISIBLE()
            imgTestSubjectIllustration.setImageBitmap(illustration)
            systemOutLogging("Got illustration: " + imgTestSubjectIllustration)
        } else { // empty illustration
            if (load_illustrationException != null) {
                quickToast("Error happens. Can not load illustration for this card")
                load_illustrationException.printStackTrace()
            }
            systemOutLogging("Not get illustration")
            imgTestSubjectIllustration.goGONE()
        }

        if (useExampleForTestSubject) {
            setUpExampleTestSubject(card)
            if (allowSpeakQuestion())
                speakQuestion(card.meanOfExample)
        } else {
            setUpTranslationTestSubject(card)
            if (allowSpeakQuestion())
                speakQuestion(card.translation)
        }
    }}

    private fun setUpExampleTestSubject (card : Flashcard) { dB.apply {
        val answer = card.text
        val positiveHighlightedText = highlightAnswerInExample(card.example, answer, LITTLE_DARK_GREEN)
        txtExamplePositiveHighlight.setText(positiveHighlightedText, TextView.BufferType.SPANNABLE)
        txtExamplePositiveHighlight.alpha = 0f

        val negativeHighlightedText = highlightAnswerInExample(card.example, answer, APP_RED)
        txtExampleNegativeHighlight.setText(negativeHighlightedText, TextView.BufferType.SPANNABLE)
        txtExampleNegativeHighlight.alpha = 0f

        val usingWithHiddenAnswer = processHideAnswerInExample(card.example, answer)
        txtTestSubjectExample.setText(usingWithHiddenAnswer, TextView.BufferType.SPANNABLE)
        // Add a dot to make it's easier for user to regconize that blank space is at the end of the example
        // Avoiding make them confused

        txtExampleTranslation.text = card.meanOfExample
        showExampleTestSubjectComponents()
        txtTranslation.alpha = 0f
        txtTextAnswer.alpha = 0f
    }}

    private fun setUpTranslationTestSubject (card : Flashcard) { dB.apply {
        txtTranslation.text = card.translation
        hideExampleTestSubjectComponents()
        txtTranslation.alpha = 1f
        txtTextAnswer.alpha = 0f
    }}

    private fun showExampleTestSubjectComponents () { dB.apply {
        txtExampleNegativeHighlight.alpha = 0f
        txtExamplePositiveHighlight.alpha = 0f
        txtTestSubjectExample.alpha = 1f
        txtExampleTranslation.alpha = 1f
        horizontalLine.alpha = 1f
    }}

    private fun hideExampleTestSubjectComponents () {dB.apply {
        txtExamplePositiveHighlight.alpha = 0f
        txtExampleNegativeHighlight.alpha = 0f
        txtTestSubjectExample.alpha = 0f
        txtExampleTranslation.alpha = 0f
        horizontalLine.alpha = 0f
    }}

    private fun performShowAnswerAnimations (userPasses : Boolean) { dB.apply {
        val isExampleAppearOnScreen = (txtTestSubjectExample.alpha == 1f)
        if (isExampleAppearOnScreen) {
            if (userPasses) {
                txtExamplePositiveHighlight.animate().setDuration(200).alpha(1f)
            } else {
                txtExampleNegativeHighlight.animate().setDuration(200).alpha(1f)
            }

            if (allowSpeakAnswer()) {
                speakAnswer(txtExamplePositiveHighlight.text.toString(), onSpeakDone = {
                    runOnUiThread {
                        nextCard(100)
                    }
                })
            } else {
                txtExamplePositiveHighlight.animate().setDuration(200).alpha(1f)
                    .setLiteListener(onEnd = {
                        nextCard(500)
                    })
            }
        } else { // Is showing TEXT test subject
            txtTranslation.animate().alpha(0f)
                .setDuration(200).setInterpolator(FastOutSlowInInterpolator())
                .setLiteListener (onEnd = {
                    txtTranslation.translationY = 0f
                })

            if (allowSpeakAnswer()) {
                speakAnswer(txtTextAnswer.text.toString(), onSpeakDone = {
                    runOnUiThread {
                        nextCard(100)
                    }
                })

                txtTextAnswer.animate().alpha(1f)
                    .setDuration(200).interpolator = FastOutSlowInInterpolator()

            } else { // Speaker is off
                txtTextAnswer.animate().alpha(1f)
                    .setDuration(200).setInterpolator(FastOutSlowInInterpolator())
                    .setLiteListener(onEnd = {
                        nextCard(500)
                    })
            }
        }
    }
    }

    override fun getRequestedFlashcardSet(): Deck {
        return intent.extras!!.getSerializable(FLASHCARD_SET_KEY) as Deck
    }

    override fun getIsReverseTextAndTranslation(): Boolean {
        return intent.extras!!.getBoolean(REVERSE_CARD_TEXT_AND_TRANSLATION, false)
    }

    private fun sendHardCardListToEndActivity() {
        val forgottenCardList = viewModel.getForgottenCardList()
        val fullCardList = viewModel.getOriginalFlashcardSet()
        ResultReportActivity.requestFinishUsingFlashcard(
            this, fullCardList, forgottenCardList,
            ResultReportActivity.FlashcardSendableActivity.REVIEW_FLASHCARD_ACTIVITY.code
        )
    }



    // INJECTED FUNCTION ==================================================================


    @Inject
    fun initNextCardAnimations (
        @Named("MoveRight120%AndFadeOut") moveRightAndFadeOut : Animator,
        @Named("Float") vwgrpNewSubAppear : Animator) { dB.apply {
        vwgrpNewSubAppear.setTarget(vwgrpTestSubject)
        (vwgrpNewSubAppear as ValueAnimator).addUpdateListener {
            vwgrpTestSubject.elevation = it.animatedValue as Float
        }
        vwgrpNewSubAppear.duration = NEXT_CARD_ANIMS_DURATION / 2
        vwgrpNewSubAppear.interpolator = FastOutSlowInInterpolator()
        vwgrpNewSubAppear.addListener (onStart = {
            vwgrpTestSubject.elevation = 0f
        })

        moveRightAndFadeOut.interpolator = FastOutSlowInInterpolator()

        nextCardAnimtor = moveRightAndFadeOut
        nextCardAnimtor.setTarget(vwgrpTestSubject)
        nextCardAnimtrSet.play(vwgrpNewSubAppear)

    }}

    private fun highlightAnswerInExample (example : String, answer : String, colorCode : String) : CharSequence {
        val answerInExample = findTextFormInAnother(answer, example)
        if (example.endsWith(answerInExample)) {
            return Html.fromHtml((example + ".").replace(answerInExample, "<font color='$colorCode'>$answerInExample</font>"))
        } else {
            return Html.fromHtml(example.replace(answerInExample, "<font color='$colorCode'>$answerInExample</font>"))
        }
    }

    private fun processHideAnswerInExample (example : String, answer : String) : SpannableString {
        val answerInExample = findTextFormInAnother(answer, example)

        val isAnswerAtTheEnd = example.endsWith(answerInExample, false)
        val hiddenAnswerString = if (isAnswerAtTheEnd) {
            SpannableString(example + ".")
        } else {
            SpannableString(example)
        }

        val startPos = example.indexOf(answerInExample)
        val endPos = startPos + answerInExample.length
        hiddenAnswerString.setSpan(ForegroundColorSpan(Color.TRANSPARENT), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return hiddenAnswerString
    }

    @Inject
    fun initResetHintAnimations (
        @Named("ZoomTo0.5XAndFadeOut") zoomSmallerAndFadeOut : Animator,
        @Named("ZoomFrom0.5XTo1XAndFadeIn") zoomToNormalSizeAndFadeIn : Animator) { dB.apply {
//        zoomSmallerAndFadeOut.setTarget(vwgrpHint)
        zoomSmallerAndFadeOut.duration = NEXT_CARD_ANIMS_DURATION
        zoomSmallerAndFadeOut.interpolator = FastOutSlowInInterpolator()

//        zoomToNormalSizeAndFadeIn.setTarget(vwgrpHint)
        zoomToNormalSizeAndFadeIn.duration = NEXT_CARD_ANIMS_DURATION
        zoomToNormalSizeAndFadeIn.interpolator = FastOutSlowInInterpolator()
        zoomToNormalSizeAndFadeIn.addListener (onStart = {
            resetHintOptionState()
        })
        resetHintAnimtrSet.play(zoomSmallerAndFadeOut).before(zoomToNormalSizeAndFadeIn)
    }}

}

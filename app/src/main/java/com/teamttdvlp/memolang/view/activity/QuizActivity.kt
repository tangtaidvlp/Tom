package com.teamttdvlp.memolang.view.activity

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
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.databinding.ActivityQuizBinding
import com.teamttdvlp.memolang.model.findTextFormInAnother
import com.teamttdvlp.memolang.view.activity.iview.QuizView
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.customview.interpolator.NormalOutExtraSlowIn

import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.QuizActivityViewModel
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named

class QuizActivity : BaseActivity<ActivityQuizBinding, QuizActivityViewModel>(), QuizView {

    private val COMMON_PROGRESS_BAR_VIEW_DURATION: Long = 100L

    private var prevForgottenCardCount: Int = 0

    private var prevPassedCardCount: Int = 0

    private var prevFamiliarCardCount: Int = 0

    private val APP_RED = "#F65546"

    private val LITTLE_DARK_GREEN = "#01A40C"


    @field: Named("RotateForever")
    @Inject
    lateinit var rotateForeverAnimation: Animation

    lateinit var viewModelFactory: ViewModelProviderFactory
    @Inject set

    private var currentMode : QuizActivityViewModel.AnswerMode = QuizActivityViewModel.AnswerMode.QUIZ

    private var speakerIsOn = true

    private var answerIsSpoken = true

    private var questionIsSpoken = true

    private lateinit var currentChosenAnswerTextView : TextView

    private val btnQuizAnswersList : ArrayList<TextView> = ArrayList(4)

    companion object {
        fun requestReviewFlashcard(
            requestContext: Context,
            deck: Deck,
            reverseCardTextAndTranslation: Boolean
        ) {

            val intent = Intent(requestContext, QuizActivity::class.java)
            intent.putExtra(FLASHCARD_SET_KEY, deck)
            intent.putExtra(REVERSE_CARD_TEXT_AND_TRANSLATION, reverseCardTextAndTranslation)
            requestContext.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_quiz
    }

    override fun takeViewModel(): QuizActivityViewModel {
        return getActivityViewModel(viewModelFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(resources.getColor(R.color.app_quiz_dark_green))
        viewModel.setUpView(this)
        setUpData()
    }

    private fun setUpData() {
        viewModel.setUpData(getRequestedFlashcardSet(), getIsReverseTextAndTranslation())
//        this.reverseCardTextAndTrans = viewModel.isDeckReversed
        setUpSpeakerStatus()
    }

    private fun setUpSpeakerStatus() {
        //
    }

    override fun addViewSettings() { dB.apply {
        btnQuizAnswersList.add(txtAnswerA)
        btnQuizAnswersList.add(txtAnswerB)
        btnQuizAnswersList.add(txtAnswerC)
        btnQuizAnswersList.add(txtAnswerD)

        btnClearAnswer.doOnPreDraw {
            btnClearAnswer.pivotX = btnClearAnswer.width / 2f
            btnClearAnswer.pivotY = btnClearAnswer.height / 2f
        }

        btnSubmit.doOnPreDraw {
            btnSubmit.pivotX = btnSubmit.width / 2f
            btnSubmit.pivotY = btnSubmit.height / 2f
        }

        edtInputAnswer.doOnNextLayout {
            edtInputAnswer.pivotX = edtInputAnswer.width / 2f
        }
    }}

    override fun addViewEvents() { dB.apply {

        txtAnswerA.setOnClickListener {
            playMinimizeTitleBarAnimations_If_HasNot()
            viewModel.submitAnswer(txtAnswerA.text.toString())
        }

        txtAnswerB.setOnClickListener {
            playMinimizeTitleBarAnimations_If_HasNot()
            viewModel.submitAnswer(txtAnswerB.text.toString())
        }

        txtAnswerC.setOnClickListener {
            playMinimizeTitleBarAnimations_If_HasNot()
            viewModel.submitAnswer(txtAnswerC.text.toString())
        }

        txtAnswerD.setOnClickListener {
            playMinimizeTitleBarAnimations_If_HasNot()
            viewModel.submitAnswer(txtAnswerD.text.toString())
        }

        btnSubmit.setOnClickListener {
            playMinimizeTitleBarAnimations_If_HasNot()
            viewModel.submitAnswer(edtInputAnswer.text.toString())
        }

    }}

    override fun hideVirtualKeyboard () {
        imm.hideSoftInputFromWindow(dB.edtInputAnswer.windowToken, 0)
    }

    override fun extendedOnPassACard (
        passedCardCount: Int,
        familiarCardCount: Int,
        forgottenCardCount: Int
    ) {
        if (passedCardCount != prevPassedCardCount && passedCardCount != 0)
            updatePassedCardProgressBar(passedCardCount)

        // We have to pass the familiarCardCount instead of passedCardCount
        // because in this mode (QuizActivity). A card can be only passed after
        // user get familiar to it. So, familiarCardCount is always >= passedCardCount
        // So the forgottenCardProgressBar better constraint to it than passedCardCount
        onPassACard (familiarCardCount, forgottenCardCount)
    }


    /**
     * Read comments above carefully, this functions does not work like
     * the original intent
     */
    override fun onPassACard (familiarCardCount: Int, forgottenCardCount: Int) {
        dB.txtFamilarCardCount.text = familiarCardCount.toString()

        val userFinishTest = (familiarCardCount + forgottenCardCount) == viewModel.getDeckSize()

        if (userFinishTest) {
            dB.txtTotalCardCount.animate().alpha(0f).duration = 100
        }

        if (familiarCardCount != 0 && familiarCardCount != prevFamiliarCardCount) {
            updateFamiliarCardProgressBar (familiarCardCount)
        }

        if ((familiarCardCount != prevFamiliarCardCount)  or  (forgottenCardCount != prevForgottenCardCount)) {
            updateForgottenCardProgressBar(familiarCardCount, forgottenCardCount)
        }

        prevFamiliarCardCount = familiarCardCount
        prevForgottenCardCount = forgottenCardCount

    }

    private fun updateForgottenCardProgressBar(familarCardCount: Int, forgottenCardCount: Int) {
        dB.apply {
            if (txtForgottenCardProgressBar.alpha == 0f) {
                txtForgottenCardProgressBar.animate().alpha(1f).duration = COMMON_PROGRESS_BAR_VIEW_DURATION
//                txtForgottenCardCount.animate().alpha(1f).duration = COMMON_PROGRESS_BAR_VIEW_DURATION
            }


            val totalPart = familarCardCount + forgottenCardCount
            val progrBarTargetWidth : Int
            val progrBarCurrentWidth = txtForgottenCardProgressBar.width

            if (totalPart < viewModel.getDeckSize()) {
                val aPartWidth = txtTotalCardProgressBar.width / viewModel.getDeckSize()
                progrBarTargetWidth = totalPart * aPartWidth
            } else {
                progrBarTargetWidth = txtTotalCardProgressBar.width // Full bar
            }

            val noNeedAnimation = (forgottenCardCount == 0) and (familarCardCount > prevFamiliarCardCount)
            if (noNeedAnimation) {
                txtForgottenCardProgressBar.goINVISIBLE()
                txtForgottenCardProgressBar.layoutParams.width = progrBarTargetWidth
                txtForgottenCardProgressBar.requestLayout()
                return
            }

            txtForgottenCardProgressBar.goVISIBLE()
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

    private fun updatePassedCardProgressBar(currentCount: Int) {
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

    private fun updateFamiliarCardProgressBar(familiarCardCount: Int) {
        dB.apply {
            val progrBarCurrentWidth = txtFamiliarCardProgressBar.width

            val progrBarTargetWidth = if (familiarCardCount < viewModel.getDeckSize())  {
                val aPartWidth = txtTotalCardProgressBar.width / viewModel.getDeckSize()
                aPartWidth * familiarCardCount
            } else {
                txtTotalCardProgressBar.width
            }

            val updateAnimation = ValueAnimator.ofInt(progrBarCurrentWidth, progrBarTargetWidth)

            updateAnimation.duration = 500
            updateAnimation.interpolator = OvershootInterpolator(2f)
            updateAnimation.setTarget(txtFamiliarCardProgressBar)

            updateAnimation.addUpdateListener {
                txtFamiliarCardProgressBar.layoutParams.width = it.animatedValue as Int
                txtFamiliarCardProgressBar.requestLayout()
            }
            updateAnimation.start()
            prevFamiliarCardCount = familiarCardCount
        }
    }


    private var hasBeenMinimized = false
    private fun playMinimizeTitleBarAnimations_If_HasNot () { dB.apply {
        if (hasBeenMinimized) {
            return
        }

        val DURATION = 150L
        vwgrpTitle.animate().alpha(0f).setDuration(DURATION).setInterpolator(FastOutSlowInInterpolator())
        imgLowerLayerMinimumTitleBackground.animate().alpha(1f).setDuration(DURATION).setInterpolator(FastOutSlowInInterpolator())
        vwgrpMinimumTitle.animate().alpha(1f).setDuration(DURATION).setInterpolator(FastOutSlowInInterpolator())

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
            start()
        }

        hasBeenMinimized = true

    }}

    override fun onLoadAllIllustrationStart() {
        rotateForeverAnimation.duration = 1000
        dB.progressBarLoadingImage.startAnimation(rotateForeverAnimation)
    }

    override fun onLoadAllIllustrationFinish() {
        hideLoadIllustrationProgressBar()
    }

    private fun hideLoadIllustrationProgressBar () {
        dB.apply {
            vwgrpLoadImageProgressBar.animate().reset().alpha(0f)
                .setDuration(100).setInterpolator(NormalOutExtraSlowIn())
                .setLiteListener(onEnd = {
                    vwgrpLoadImageProgressBar.goGONE()
                    progressBarLoadingImage.animation.cancel()
                })
        }
    }

    private var firstCardPlayed = true
    override fun onGetTestSubject(
        testSubject: Flashcard,
        illustration: Bitmap?,
        load_illustrationException: Exception?,
        useExampleForTestSubject: Boolean,
        answerMode: QuizActivityViewModel.AnswerMode,
        answerSet: ArrayList<String>?
    ) {

        if (firstCardPlayed) {
            setUpTestSubject(testSubject, illustration, load_illustrationException, useExampleForTestSubject)
            playRefreshQuizOptionsAnims(answerSet!!, 100)
            firstCardPlayed = false
            return
        }

        if (answerMode == QuizActivityViewModel.AnswerMode.QUIZ) {
            if (currentMode == QuizActivityViewModel.AnswerMode.WRITING) {
                handleWritingToQuiz (testSubject, illustration, load_illustrationException, useExampleForTestSubject, answerSet)
            } else {
                handleNextQuiz (testSubject, illustration, load_illustrationException, useExampleForTestSubject, answerSet)
            }
            currentMode = QuizActivityViewModel.AnswerMode.QUIZ

        } else if (answerMode == QuizActivityViewModel.AnswerMode.WRITING) {
            if (currentMode == QuizActivityViewModel.AnswerMode.QUIZ) {
                handleQuizToWriting (testSubject, illustration, load_illustrationException, useExampleForTestSubject)
            } else {
                handleNextWriting (testSubject, illustration, load_illustrationException, useExampleForTestSubject)
            }
            currentMode = QuizActivityViewModel.AnswerMode.WRITING
        }
    }

    private fun handleNextWriting (testSubject: Flashcard, illustration: Bitmap?, load_Illustrationexception: Exception?, useExampleForTestSubject: Boolean) { dB.apply {
        vwgrpTestSubject.animate().reset().translationX(ScreenDimension.screenWidth.toFloat() * -1).setDuration(ANIM_DURATION * 3).setInterpolator(FastOutSlowInInterpolator())
            .setLiteListener(onEnd = {
                // Set up test subject
                setUpTestSubject(testSubject, illustration, load_Illustrationexception, useExampleForTestSubject)

                vwgrpTestSubject.translationX = ScreenDimension.screenWidth.toFloat()
                vwgrpTestSubject.animate().reset().translationX(0f).setDuration(ANIM_DURATION * 3).setInterpolator(FastOutSlowInInterpolator()).clearListeners()
            })

        edtInputAnswer.animate().reset().scaleY(0.88f).scaleX(0.93f).alpha(0.55f).setDuration(ANIM_DURATION * 2).setInterpolator(NormalOutExtraSlowIn())
            .setLiteListener(onEnd = {
                edtInputAnswer.setText("")
                edtInputAnswer.animate().reset().setStartDelay(0).scaleX(1f).scaleY(1f).alpha(1f).setDuration(ANIM_DURATION * 2).setInterpolator(NormalOutExtraSlowIn()).clearListeners()
            })

    }}

    private fun handleNextQuiz (testSubject: Flashcard, illustration: Bitmap?, load_Illustrationexception: Exception?, useExampleForTestSubject: Boolean,
                                                   answerSet: java.util.ArrayList<String>?) { dB.apply {

        vwgrpTestSubject.animate().reset().translationX(ScreenDimension.screenWidth.toFloat() * -1).setDuration(ANIM_DURATION * 3).setInterpolator(FastOutSlowInInterpolator())
            .setLiteListener(onEnd = {
                // Set up test subject
                setUpTestSubject(testSubject, illustration, load_Illustrationexception, useExampleForTestSubject)

                vwgrpTestSubject.translationX = ScreenDimension.screenWidth.toFloat()
                vwgrpTestSubject.animate().reset().translationX(0f).setDuration(ANIM_DURATION * 3).setInterpolator(FastOutSlowInInterpolator()).clearListeners()
            })


        playRefreshQuizOptionsAnims(answerSet!!, dB.vwgrpTestSubject.animate().duration)

    }}

    private fun handleQuizToWriting(testSubject: Flashcard, illustration: Bitmap?, load_Illustrationexception: Exception?, useExampleForTestSubject: Boolean) { dB.apply {

        edtInputAnswer.setText("")
        vwgrpTestSubject.animate().reset()
            .alpha(0f).translationX(ScreenDimension.screenWidth.toFloat() * -1)
            .setDuration(ANIM_DURATION * 3).setInterpolator(FastOutSlowInInterpolator())
            .setLiteListener(onEnd = {
                // Set up the testSubject when the panel disappear or before appearing
                setUpTestSubject(testSubject, illustration, load_Illustrationexception, useExampleForTestSubject)

                vwgrpTestSubject.translationX = ScreenDimension.screenWidth.toFloat()
                vwgrpTestSubject.alpha = 1f
                vwgrpTestSubject.animate().reset()
                    .translationX(0f)
                    .setDuration(ANIM_DURATION * 3).setInterpolator(FastOutSlowInInterpolator()).clearListeners()
            })

        hideQuizOptions(0, onFinish = {
            showVirtualKeyboard()
            showWritingOptions(startDelay = 200, onFinish = {
                edtInputAnswer.requestFocus()
            })
        })

    }}

    private fun handleWritingToQuiz (testSubject: Flashcard, illustration: Bitmap?,
                                                              load_Illustrationexception: Exception?, useExampleForTestSubject: Boolean, answerSet: java.util.ArrayList<String>?) { dB.apply {

        vwgrpTestSubject.animate().reset().alpha(0f).translationX(ScreenDimension.screenWidth.toFloat() * -1).setDuration(ANIM_DURATION * 3).setInterpolator(FastOutSlowInInterpolator())
            .setLiteListener(onEnd = {
                // Set up quiz data
                setUpTestSubject(testSubject, illustration, load_Illustrationexception, useExampleForTestSubject)
                setUpQuizOptions(answerSet)

                hideVirtualKeyboard()
                vwgrpTestSubject.translationX = ScreenDimension.screenWidth.toFloat()
                vwgrpTestSubject.alpha = 1f
                vwgrpTestSubject.animate().reset().translationX(0f).setDuration(ANIM_DURATION * 3)
                    .setInterpolator(FastOutSlowInInterpolator()).clearListeners()
            })

        hideWritingOptions(startDelay = 200, onFinish = {
            showQuizOptions()
        })
    }}

    private fun setUpQuizOptions (answerSet: ArrayList<String>?) {
        dB.apply {
            txtAnswerA.text = answerSet!!.get(0)
            txtAnswerB.text = answerSet.get(1)
            txtAnswerC.text = answerSet.get(2)
            txtAnswerD.text = answerSet.get(3)
        }
    }

    private fun playNextCardAnims (flashcard: Flashcard, illustration: Bitmap?, load_illustrationException: Exception?, useExampleForTestSubject: Boolean) { dB.apply {



    }}

    private fun playRefreshQuizOptionsAnims (answerSet: ArrayList<String>, startDelay: Long = 0) { dB.apply {
        val text1 = answerSet.get(0)
        val text2 = answerSet.get(1)
        val text3 = answerSet.get(2)
        val text4 = answerSet.get(3)
        vwgrpAnswerA.animate().reset().scaleY(0.88f).scaleX(0.93f).alpha(0.55f).setDuration(ANIM_DURATION).setInterpolator(NormalOutExtraSlowIn())
            .setStartDelay (startDelay + DELAY_DURATION * 0)
            .setLiteListener(onEnd = {
                txtAnswerA.text = text1
                vwgrpAnswerA.animate().reset().setStartDelay(0).scaleX(1f).scaleY(1f).alpha(1f).setDuration(ANIM_DURATION).setInterpolator(NormalOutExtraSlowIn()).clearListeners()
            })

        vwgrpAnswerB.animate().reset().scaleY(0.88f).scaleX(0.93f).alpha(0.55f).setDuration(ANIM_DURATION).setInterpolator(NormalOutExtraSlowIn())
            .setStartDelay (startDelay + DELAY_DURATION * 1)
            .setLiteListener(onEnd = {
                txtAnswerB.text = text2
                vwgrpAnswerB.animate().reset().setStartDelay(0).scaleX(1f).scaleY(1f).alpha(1f).setDuration(ANIM_DURATION).setInterpolator(NormalOutExtraSlowIn()).clearListeners()
            })

        vwgrpAnswerC.animate().reset().scaleY(0.88f).scaleX(0.93f).alpha(0.55f).setDuration(ANIM_DURATION).setInterpolator(NormalOutExtraSlowIn())
            .setStartDelay (startDelay + DELAY_DURATION * 2)
            .setLiteListener(onEnd = {
                txtAnswerC.text = text3
                vwgrpAnswerC.animate().reset().setStartDelay(0).scaleX(1f).scaleY(1f).alpha(1f).setDuration(ANIM_DURATION).setInterpolator(NormalOutExtraSlowIn()).clearListeners()
            })

        vwgrpAnswerD.animate().reset().scaleY(0.88f).scaleX(0.93f).alpha(0.55f).setDuration(ANIM_DURATION).setInterpolator(NormalOutExtraSlowIn())
            .setStartDelay(startDelay + DELAY_DURATION * 3)
            .setLiteListener(onEnd = {
                txtAnswerD.text = text4
                vwgrpAnswerD.animate().reset().setStartDelay(0).scaleX(1f).scaleY(1f).alpha(1f).setDuration(ANIM_DURATION + ANIM_DURATION / 2).clearListeners()
            })
    }}


    private fun setUpTestSubject (card: Flashcard, illustration : Bitmap?, load_illustrationException: Exception?, useExampleForTestSubject : Boolean) { dB.apply {
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

    private fun speakAnswer(text: String, onSpeakDone: () -> Unit) {
//        viewModel.speakAnswer(text, onSpeakDone)
    }

    private fun allowSpeakAnswer(): Boolean {
        return (speakerIsOn and answerIsSpoken)
    }

    private fun speakQuestion(text: String) {
        // TODO
//        viewModel.speakQuestion(text)
    }

    private fun allowSpeakQuestion(): Boolean {
        return (speakerIsOn and questionIsSpoken)
    }

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

//
//    private fun handleQuizMode() { dB.apply {
//        if (currentMode == QuizActivityViewModel.AnswerMode.WRITING) {
//
//        }
//    }}

//    private fun handleWritingMode() { dB.apply {
//        if (currentMode == QuizActivityViewModel.AnswerMode.QUIZ) {
//
//            vwgrpTestSubject.animate().reset().setStartDelay(0).alpha(0f).translationX(ScreenDimension.screenWidth.toFloat() * -1).setDuration(ANIM_DURATION * 3).setInterpolator(FastOutSlowInInterpolator())
//                .setLiteListener(onEnd = {
//                    vwgrpTestSubject.translationX = ScreenDimension.screenWidth.toFloat()
//                    vwgrpTestSubject.alpha = 1f
//                    vwgrpTestSubject.animate().reset().translationX(0f).setDuration(ANIM_DURATION * 3).setInterpolator(FastOutSlowInInterpolator())
//                })
//
//            hideQuizOptions(0, onFinish = {
//                showVirtualKeyboard()
//                showWritingOptions(startDelay = 200, onFinish = {
//                    edtInputAnswer.requestFocus()
//                })
//            })
//            currentMode = QuizActivityViewModel.AnswerMode.WRITING
//
//        }
//    }}

    private val ANIM_DURATION = 100L
    private val DELAY_DURATION = 35L

    private fun showQuizOptions(startDelay : Long = 0, onFinish: (() -> Unit)? = null ) { dB.apply {
        grpQuizOptions.goVISIBLE()
        imgQuizComponentsPadLayer.goVISIBLE()
        val animDuration = ANIM_DURATION * 3
        imgQuizComponentsPadLayer.animate().reset().alpha(0.2f).setDuration(animDuration)
            .startDelay = startDelay + DELAY_DURATION * 0

        imgQuizComponentsCoverLayer.goVISIBLE()
        imgQuizComponentsCoverLayer.animate().reset().alpha(0.2f).setDuration(animDuration)
            .startDelay = startDelay + DELAY_DURATION * 0

        vwgrpAnswerA.animate().reset().alpha(1f).scaleX(1f).scaleY(1f).setDuration(animDuration).setInterpolator(OvershootInterpolator(0.7f))
            .startDelay = startDelay + DELAY_DURATION * 1

        vwgrpAnswerB.animate().reset().alpha(1f).scaleX(1f).scaleY(1f).setDuration(animDuration).setInterpolator(OvershootInterpolator(0.7f))
            .startDelay = startDelay + DELAY_DURATION * 2

        vwgrpAnswerC.animate().reset().alpha(1f).scaleX(1f).scaleY(1f).setDuration(animDuration).setInterpolator(OvershootInterpolator(0.7f))
            .startDelay = startDelay + DELAY_DURATION * 3

        vwgrpAnswerD.animate().reset().alpha(1f).scaleX(1f).scaleY(1f).setDuration(animDuration).setInterpolator(OvershootInterpolator(0.7f))
            .setStartDelay(startDelay + DELAY_DURATION * 4)
            .setLiteListener (onEnd = {
                txtAnswerD.animate().reset().setLiteListener {  }
                onFinish?.invoke()
            })
    }}

    private fun hideQuizOptions (startDelay : Long = 0, onFinish: (() -> Unit)? = null) { dB.apply {
        val animDuration = ANIM_DURATION * 3

        vwgrpAnswerA.animate().reset().scaleY(0.75f).scaleX(0.75f).alpha(0f).
        setDuration(animDuration).setInterpolator(OvershootInterpolator(0.7f))
            .startDelay = startDelay + 0

        vwgrpAnswerB.animate().reset().scaleY(0.75f).scaleX(0.75f).alpha(0f).
        setDuration(animDuration).setInterpolator(OvershootInterpolator(0.7f))
            .startDelay = startDelay + 50

        vwgrpAnswerC.animate().reset().scaleY(0.75f).scaleX(0.75f).alpha(0f).
        setDuration(animDuration).setInterpolator(OvershootInterpolator(0.7f))
            .startDelay = startDelay + 50 + DELAY_DURATION

        vwgrpAnswerD.animate().reset().scaleY(0.75f).scaleX(0.75f).alpha(0f).
        setDuration(animDuration).setInterpolator(OvershootInterpolator(0.7f))
            .startDelay = startDelay + 50 + DELAY_DURATION * 2

        imgQuizComponentsPadLayer.animate().reset().alpha(0f)
            .setDuration(ANIM_DURATION)
            .setStartDelay(startDelay + 50 + DELAY_DURATION * 3)
            .setLiteListener (onEnd = {
                grpQuizOptions.goGONE()
                imgQuizComponentsPadLayer.animate().reset().setLiteListener { }
                onFinish?.invoke()
            })

        imgQuizComponentsCoverLayer.animate().reset().alpha(0f)
            .setDuration(ANIM_DURATION)
            .setStartDelay(startDelay + ANIM_DURATION + 50 + DELAY_DURATION * 3)

    }}

    private fun showWritingOptions (startDelay : Long = 0, onFinish: (() -> Unit)? = null) { dB.apply {
        grpWritingComponents.goVISIBLE()
        val animDuration = ANIM_DURATION * 2
        btnSubmit.animate().reset().scaleY(1f).scaleX(1f).setDuration(animDuration).setInterpolator(OvershootInterpolator(0.7f))
            .startDelay = startDelay + DELAY_DURATION * 0

        viewLineUnderEdtInput.animate().reset().scaleX(1f).setDuration(animDuration * 2).setInterpolator(OvershootInterpolator(0.7f))
            .startDelay = startDelay +  DELAY_DURATION * 1

        btnClearAnswer.animate().reset().scaleY(1f).scaleX(1f).setDuration(animDuration).setInterpolator(OvershootInterpolator(0.7f))
            .setStartDelay(startDelay + DELAY_DURATION * 2)
            .setLiteListener ( onEnd = {
                btnClearAnswer.animate().reset().setLiteListener {  }
                onFinish?.invoke()
            })

    }}

    private fun hideWritingOptions (startDelay : Long = 0, onFinish: (() -> Unit)? = null) { dB.apply {
        val animDuration = ANIM_DURATION * 2

        btnSubmit.animate().reset().scaleY(0f).scaleX(0f).setDuration(animDuration).setInterpolator(OvershootInterpolator(0.7f))

        viewLineUnderEdtInput.animate().reset().scaleX(0f).setDuration(animDuration).setInterpolator(OvershootInterpolator(0.7f))
            .startDelay = startDelay + DELAY_DURATION

        btnClearAnswer.animate().reset().scaleY(0f).scaleX(0f).setDuration(animDuration).setInterpolator(OvershootInterpolator(0.7f))
            .setStartDelay(startDelay + DELAY_DURATION * 2)
            .setLiteListener ( onEnd = {
                grpWritingComponents.goGONE()
                btnClearAnswer.animate().reset().setLiteListener {  }
                onFinish?.invoke()
            })
    }}

    override fun onEndReviewing() {
        sendHardCardListToEndActivity()
        finish()
    }

    fun sendHardCardListToEndActivity() {
        val forgottenCardList = viewModel.getForgottenCardList()
        val fullCardList = getRequestedFlashcardSet()
        ResultReportActivity.requestFinishUsingFlashcard(
            this, fullCardList, forgottenCardList,
            ResultReportActivity.FlashcardSendableActivity.REVIEW_FLASHCARD_ACTIVITY.code
        )
    }

    override fun showSpeakTextError(s: String) {
        quickToast(s)
    }

    override fun getRequestedFlashcardSet(): Deck {
        return intent.extras!!.getSerializable(FLASHCARD_SET_KEY) as Deck
    }

    override fun getIsReverseTextAndTranslation(): Boolean {
        return intent.extras!!.getBoolean(REVERSE_CARD_TEXT_AND_TRANSLATION, false)
    }
}
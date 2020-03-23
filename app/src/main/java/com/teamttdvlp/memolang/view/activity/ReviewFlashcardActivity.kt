package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.animation.Animation
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityReviewFlashcardBinding
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.findTextFormInAnother
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardView
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.ReviewFlashcardViewModel
import javax.inject.Inject
import javax.inject.Named


class ReviewFlashcardActivity : BaseActivity<ActivityReviewFlashcardBinding, ReviewFlashcardViewModel>(),
                                                ReviewFlashcardView {

    private val APP_RED = "#F65546"

    private val LITTLE_DARK_GREEN = "#01A40C"

    private val NEXT_CARD_ANIMS_DURATION = 400L

    private lateinit var RED_BORDER_BACKGROUND : Drawable

    private lateinit var NORMAL_BORDER_BACKGROUND : Drawable


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

    var nextCardAnimtrSet: AnimatorSet = AnimatorSet()

    var resetHintAnimtrSet: AnimatorSet = AnimatorSet()

    private var speakerIsOn = true

    override fun getLayoutId(): Int = R.layout.activity_review_flashcard

    override fun takeViewModel(): ReviewFlashcardViewModel {
        return getActivityViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        dB.vwModel = viewModel
        viewModel.setUpInfo(getFlashcardList())
    }

    override fun onStart() { dB.apply {
        super.onStart()
        edtInputAnswer.requestFocus()
    }}

    override fun initProperties() {
        RED_BORDER_BACKGROUND = getDrawable(R.drawable.round_3dp_black_border)!!
        NORMAL_BORDER_BACKGROUND = getDrawable(R.drawable.round_3dp_black_border)!!
    }

    override fun addAnimationEvents() {
        nextCardAnimtor.addListener(onEnd = {
            dB.vwgrpTestSubject.translationX = 0f
            dB.vwgrpTestSubject.alpha = 1f
            // Change new subject
            nextCardAnimtrSet.start()
            viewModel.nextCard()
        })

    }

    override fun addViewControls() {
        dB.txtHint.movementMethod = ScrollingMovementMethod()

    }

    override fun addViewEvents() { dB.apply {

        btnClearAnswer.setOnClickListener {
            edtInputAnswer.setText("")
        }

        btnOk.setOnClickListener {
            viewModel.submitAnswer(edtInputAnswer.text.toString())
        }

        btnNext.setOnClickListener {
            nextCard()
            swithFromNextToOKButton()
        }

        edtInputAnswer.addTextChangeListener { userAnswer, _ , _ , _ ->
            viewModel.checkAnswer(userAnswer)
        }

        imgLightedLight.setOnClickListener {
            imgGreyLight.disappear()
            imgLightedLight.animate().alpha(0f).setDuration(300).setLiteListener {
                imgLightedLight.disappear()
            }
            txtHint.animate().alpha(1f).apply {
                duration = 300
            }
        }

        btnSetting.setOnClickListener {
            dialogSetting.show()
        }
    }}

    fun switchFromOKToNextButton () { dB.apply {
        imgUpArrow.rotation = 0f
        imgUpArrow.animate().setDuration(200).setInterpolator(FastOutSlowInInterpolator()).rotation(90f)
                                                .setLiteListener (onStart = {
                                                    vwgrpArrow.appear()
                                                }, onEnd = {
                                                    vwgrpArrow.disappear()
                                                    btnNext.appear()
                                                })
    }}

    fun swithFromNextToOKButton () { dB.apply {
        imgUpArrow.rotation = 90f
        imgUpArrow.animate().setDuration(400).setInterpolator(FastOutSlowInInterpolator()).rotation(360f)
                                                .setLiteListener ( onStart = {
                                                    vwgrpArrow.appear()
                                                    },
                                                    onEnd = {
                                                        vwgrpArrow.disappear()
                                                        btnNext.disappear()
                                                    })
    }}

    // VIEW FUNCTION ===========================================


    override fun showValidAnsBehaviours () { dB.apply {

    }}

    override fun showInvalidAnsBehaviours () { dB.apply {

    }}

    override fun endReviewing() {
        sendHardCardListToEndActivity()
        finish()
    }

    override fun showSpeakTextError(error: String) {
        quickLog(error)
    }

    override fun showGoodAnswerAnimation() { dB.apply {
        imgCorrectAnswer.appear()
        imgCorrectAnswer.startAnimation(imgCorAnsAppearAnim)
        performShowAnswerAnimations(true)
        if (speakerIsOn) {
            viewModel.speakAnswer(txtTextAnswer.text.toString())
        }
    }}

    override fun showExcelentAnswerAnimation() { dB.apply {
        imgVeryGoodAnswer.appear()
        imgVeryGoodAnswer.startAnimation(imgVeryGoodAnsAppearAnim)
        performShowAnswerAnimations(true)
        if (speakerIsOn) {
            viewModel.speakAnswer(txtTextAnswer.text.toString())
        }
    }}

    override fun showWrongAnswerAnimation() { dB.apply {
        imgIncorrectAnswer.appear()
        imgIncorrectAnswer.startAnimation(imgIncorAnsAppearAnim)
        vwgrpTestSubject.startAnimation(vwgrpVibrateAnim)
    }}

    override fun showNotPassAnswerAnimation() { dB.apply {
        imgBadAnswers.appear()
        imgBadAnswers.startAnimation(imgBadAnswersAppearAnim)
        performShowAnswerAnimations(false)
        if (speakerIsOn) {
            viewModel.speakAnswer(txtTextAnswer.text.toString())
        }
    }}

    override fun highlightHintOption() {
        dB.imgLightedLight.alpha = 1f
        dB.imgLightedLight.appear()
        dB.imgGreyLight.disappear()
    }

    override fun resetHintOptionState() {
        if (dB.vwgrpHint.background != NORMAL_BORDER_BACKGROUND) {
            dB.vwgrpHint.background = NORMAL_BORDER_BACKGROUND
        }
        dB.imgGreyLight.appear()
        dB.imgLightedLight.disappear()
        dB.txtHint.alpha = 0f
    }

    override fun nextCard (startDelay : Long) {
        nextCardAnimtor.startDelay = startDelay
        nextCardAnimtor.start()
        resetHintAnimtrSet.startDelay = startDelay
        resetHintAnimtrSet.start()
        dB.edtInputAnswer.setText("")
    }

    override fun showTestSubjectOnScreen(card: Flashcard, useExampleForTestSubject : Boolean) { dB.apply {
        val answer = card.text
        txtTextAnswer.text = answer
        if (useExampleForTestSubject) {
            setUpExampleTestSubject(card)
        } else {
            setUpTranslationTestSubject(card)
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
        txtExampleTranslation.text = card.exampleMean
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
                txtExamplePositiveHighlight.animate().setDuration(200).alpha(1f).setLiteListener (onEnd = {
                    nextCard(500)
                })
            } else {
                txtExampleNegativeHighlight.animate().setDuration(200).alpha(1f)
                switchFromOKToNextButton()
            }
        } else {
            if (not(userPasses)) {
                switchFromOKToNextButton()
            }
            txtTranslation.animate().alpha(0f)
                .setDuration(200).setInterpolator(FastOutSlowInInterpolator())
                .setLiteListener (onEnd = {
                    txtTranslation.translationY = 0f
                })
            txtTextAnswer.animate().alpha(1f)
                .setDuration(200).setInterpolator(FastOutSlowInInterpolator())
                .setLiteListener (onEnd = {
                    quickLog("???")
                    if (userPasses) {
                        nextCard(500)
                    }
            })
        }
    }}

    private fun getFlashcardList () : ArrayList<Flashcard> {
        val flashcardList = intent.extras!!.getSerializable(FLASHCARD_LIST_KEY) as ArrayList<Flashcard>
        return flashcardList
    }

    fun sendHardCardListToEndActivity () {
        val hardCardList = viewModel.getFoggotenCardList()
        val intent = Intent(this@ReviewFlashcardActivity, UseFlashcardDoneActivity::class.java)
        intent.putExtra(FORGOTTEN_FLASHCARDS_LIST, hardCardList)
        startActivity(intent)
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

    private fun highlightAnswerInExample (using : String, answer : String, colorCode : String) : CharSequence {
        val answerInExample = findTextFormInAnother(answer, using)
        //                                                                                                              LITLE DARK GREEN
        return Html.fromHtml(using.replace(answerInExample, "<font color='$colorCode'>$answerInExample</font>"))
    }

    private fun processHideAnswerInExample (example : String, answer : String) : SpannableString {
        val answerInExample = findTextFormInAnother(answer, example)
        val hiddenAnswerString = SpannableString(example)
        val startPos = example.indexOf(answerInExample)
        val endPos = startPos + answerInExample.length
        hiddenAnswerString.setSpan(ForegroundColorSpan(Color.TRANSPARENT), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return hiddenAnswerString
    }

    @Inject
    fun initResetHintAnimations (
        @Named("ZoomTo0.5XAndFadeOut") zoomSmallerAndFadeOut : Animator,
        @Named("ZoomFrom0.5XTo1XAndFadeIn") zoomToNormalSizeAndFadeIn : Animator) { dB.apply {
        zoomSmallerAndFadeOut.setTarget(vwgrpHint)
        zoomSmallerAndFadeOut.duration = NEXT_CARD_ANIMS_DURATION
        zoomSmallerAndFadeOut.interpolator = FastOutSlowInInterpolator()

        zoomToNormalSizeAndFadeIn.setTarget(vwgrpHint)
        zoomToNormalSizeAndFadeIn.duration = NEXT_CARD_ANIMS_DURATION
        zoomToNormalSizeAndFadeIn.interpolator = FastOutSlowInInterpolator()
        zoomToNormalSizeAndFadeIn.addListener (onStart = {
            resetHintOptionState()
        })
        resetHintAnimtrSet.play(zoomSmallerAndFadeOut).before(zoomToNormalSizeAndFadeIn)
    }}

}

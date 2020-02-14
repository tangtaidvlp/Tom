package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
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

    private val LITTLE_DARK_GREEN = "#00A90B"

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

    lateinit var oldSubjectDisaprAnim: Animator

    var nextCardAnimtrSet: AnimatorSet = AnimatorSet()

    var resetHintAnimtrSet: AnimatorSet = AnimatorSet()

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
        RED_BORDER_BACKGROUND = getDrawable(R.drawable.round_3dp_white_background_with_red_border)!!
        NORMAL_BORDER_BACKGROUND = getDrawable(R.drawable.round_3dp_white_background_with_border)!!
    }

    override fun addViewEvents() { dB.apply {

        btnClearAnswer.setOnClickListener {
            edtInputAnswer.setText("")
        }

        btnOk.setOnClickListener {
            viewModel.submitAnswer(edtInputAnswer.text.toString())
        }

        btnNext.setOnClickListener {
            showNextCardAnims()
            swithFromNextToOKButton()
        }

        edtInputAnswer.addTextChangeListener { userAnswer, _ , _ , _ ->
            viewModel.checkAnswer(userAnswer)
        }

        imgRedLight.setOnClickListener {
            imgRedLight.animate().alpha(0f).apply {
                duration = 300
            }
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
                                                    quickLog("?????")
                                                    vwgrpArrow.appear()
                                                    },
                                                    onEnd = {
                                                        vwgrpArrow.disappear()
                                                        btnNext.disappear()
                                                    })
    }}

    // VIEW FUNCTION ===========================================


    override fun showValidAnsBehaviours () { dB.apply {
        imgValidAnswerBorder.appear()
    }}

    override fun showInvalidAnsBehaviours () { dB.apply {
        imgValidAnswerBorder.disappear()
    }}

    override fun endReviewing() {

    }

    override fun showGoodAnswerAnimation() { dB.apply {
        imgCorrectAnswer.appear()
        imgCorrectAnswer.startAnimation(imgCorAnsAppearAnim)
        showAnswerAnimations(true)
    }}

    override fun showExcelentAnswerAnimation() { dB.apply {
        imgVeryGoodAnswer.appear()
        imgVeryGoodAnswer.startAnimation(imgVeryGoodAnsAppearAnim)
        showAnswerAnimations(true)
    }}

    override fun showWrongAnswerAnimation() { dB.apply {
        imgIncorrectAnswer.appear()
        imgIncorrectAnswer.startAnimation(imgIncorAnsAppearAnim)
        vwgrpTestSubject.startAnimation(vwgrpVibrateAnim)
    }}

    override fun showNotPassAnswerAnimation() { dB.apply {
        imgBadAnswers.appear()
        imgBadAnswers.startAnimation(imgBadAnswersAppearAnim)
        showAnswerAnimations(false)
    }}

    override fun highlightHintOption() {
        if (dB.vwgrpHint.background != RED_BORDER_BACKGROUND) {
            dB.vwgrpHint.background = RED_BORDER_BACKGROUND
        }

        dB.imgRedLight.alpha = 1f
        dB.imgRedLight.appear()
        dB.imgGreyLight.disappear()
    }

    override fun resetHintOptionState() {
        if (dB.vwgrpHint.background != NORMAL_BORDER_BACKGROUND) {
            dB.vwgrpHint.background = NORMAL_BORDER_BACKGROUND
        }
        dB.imgGreyLight.appear()
        dB.imgRedLight.disappear()
    }

    override fun showNextCardAnims () {
        oldSubjectDisaprAnim.start()
        resetHintAnimtrSet.start()
        dB.edtInputAnswer.setText("")
    }

    override fun showTestSubjectOnScreen(card: Flashcard, useUsingForTestSubject : Boolean) { dB.apply {
        val answer = card.text
        txtTextAnswer.text = answer
        if (useUsingForTestSubject) {
            setUpUsingTestSubject(card)
        } else {
            setUpTranslationTestSubject(card)
        }
    }}

    private fun setUpUsingTestSubject (card : Flashcard) { dB.apply {
        val answer = card.text
        val positiveHighlightedText = highlightAnswerInUsing(card.using, answer, LITTLE_DARK_GREEN)
        txtUsingPositiveHighlight.setText(positiveHighlightedText, TextView.BufferType.SPANNABLE)
        txtUsingPositiveHighlight.alpha = 0f

        val negativeHighlightedText = highlightAnswerInUsing(card.using, answer, APP_RED)
        txtUsingNegativeHighlight.setText(negativeHighlightedText, TextView.BufferType.SPANNABLE)
        txtUsingNegativeHighlight.alpha = 0f

        val usingWithHiddenAnswer = processHideAnswerInUsing(card.using, answer)
        txtTestSubjectUsing.setText(usingWithHiddenAnswer, TextView.BufferType.SPANNABLE)
        txtUsingTranslation.text = card.usingTranslation
        showUsingTestSubjectComponents()
        txtTranslation.alpha = 0f
        txtTextAnswer.alpha = 0f
    }}

    private fun setUpTranslationTestSubject (card : Flashcard) { dB.apply {
        txtTranslation.text = card.translation
        hideUsingTestSubjectComponents()
        txtTranslation.alpha = 1f
        txtTextAnswer.alpha = 0f
    }}

    private fun showUsingTestSubjectComponents () { dB.apply {
        txtUsingNegativeHighlight.alpha = 0f
        txtUsingPositiveHighlight.alpha = 0f
        txtTestSubjectUsing.alpha = 1f
        txtUsingTranslation.alpha = 1f
        horizontalLine.alpha = 1f
    }}

    private fun hideUsingTestSubjectComponents () {dB.apply {
        txtUsingPositiveHighlight.alpha = 0f
        txtUsingNegativeHighlight.alpha = 0f
        txtTestSubjectUsing.alpha = 0f
        txtUsingTranslation.alpha = 0f
        horizontalLine.alpha = 0f
    }}

    private fun showAnswerAnimations (userPasses : Boolean) { dB.apply {
        val isUsingAppearOnScreen = (txtTestSubjectUsing.alpha == 1f)
        if (isUsingAppearOnScreen) {
            if (userPasses) {
                txtUsingPositiveHighlight.animate().setDuration(200).alpha(1f)
            } else {
                txtUsingNegativeHighlight.animate().setDuration(200).alpha(1f)
            }
        } else {
            txtTranslation.animate().setDuration(200).setInterpolator(FastOutSlowInInterpolator())
                .translationY(100f).alpha(0f)
                .setLiteListener (onEnd = {
                    txtTranslation.translationY = 0f
                })
            txtTextAnswer.animate().setDuration(200).setInterpolator(FastOutSlowInInterpolator()).alpha(1f)
        }
        switchFromOKToNextButton()
    }}

    private fun getFlashcardList () : ArrayList<Flashcard> {
        val flashcardList = intent.extras!!.getSerializable(FLASHCARD_LIST_KEY) as ArrayList<Flashcard>
        return flashcardList
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

        oldSubjectDisaprAnim = moveRightAndFadeOut
        oldSubjectDisaprAnim.addListener(onEnd = {
            dB.vwgrpTestSubject.translationX = 0f
            dB.vwgrpTestSubject.alpha = 1f
            nextCardAnimtrSet.start()
            viewModel.nextCard()
        })

        oldSubjectDisaprAnim.setTarget(vwgrpTestSubject)
        nextCardAnimtrSet.play(vwgrpNewSubAppear)

    }}

    private fun highlightAnswerInUsing (using : String, answer : String, colorCode : String) : CharSequence {
        val answerInUsing = findTextFormInAnother(answer, using)
        //                                                                                                              LITLE DARK GREEN
        return Html.fromHtml(using.replace(answerInUsing, "(<font color='$colorCode'>$answerInUsing</font>)"))
    }

    private fun processHideAnswerInUsing (using : String, answer : String) : CharSequence {
        val answerInUsing = findTextFormInAnother(answer, using)
        return Html.fromHtml(using.replace(answerInUsing, "(<font color='#ffffff'>$answerInUsing</font>)"))
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

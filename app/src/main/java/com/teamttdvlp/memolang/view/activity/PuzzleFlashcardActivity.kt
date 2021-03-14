package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.view.doOnPreDraw
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.core.widget.addTextChangedListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.databinding.ActivityReviewFlashcardEasyBinding
import com.teamttdvlp.memolang.model.ReviewActivitiesSpeakerStatusManager
import com.teamttdvlp.memolang.model.findTextFormInAnother
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardEasyView
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardEasyView.ListOfCellType.CHARACTER_LIST
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardEasyView.ListOfCellType.WORD_LIST
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.customview.Cell
import com.teamttdvlp.memolang.view.customview.Cell.Companion.DIRECTION_DOWN
import com.teamttdvlp.memolang.view.customview.Cell.Companion.DIRECTION_UP
import com.teamttdvlp.memolang.view.customview.Cell.Companion.INPUT_CELL
import com.teamttdvlp.memolang.view.customview.Cell.Companion.OUTPUT_CELL
import com.teamttdvlp.memolang.view.customview.interpolator.NormalOutExtraSlowIn
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.PuzzleFlashcardViewModel
import javax.inject.Inject
import javax.inject.Named

class PuzzleFlashcardActivity : BaseActivity<ActivityReviewFlashcardEasyBinding, PuzzleFlashcardViewModel>(),
    ReviewFlashcardEasyView, TextToSpeech.OnUtteranceCompletedListener {

    private val MIN_INPUT_ROW_COUNT: Int = 3

    private val NEXT_CARD_ANIMS_DURATION = 400L

    private val COMMON_PROGRESS_BAR_VIEW_DURATION = 100L


    // MAX_CELL_PER_ROW
    private val DEFAULT_MAX_CELL_PER_ROW = 4

    private var MAX_CELL_PER_ROW = DEFAULT_MAX_CELL_PER_ROW

    private val DEFAULT_MAX_ROW = 3

    private var MAX_ROW = DEFAULT_MAX_ROW

    private val DARK_RED = "#FF2C00"

    private val LITTLE_DARK_GREEN = "#01A40C"

    private var DARK_RED_COLOR : Int = 0

    private var LITTLE_DARD_GREEN_COLOR : Int = 0

    private var GREY_TEXT_COLOR : Int = 0

    private val CELL_MOVING_INVISIBLY_TIME = 0L

    private val INTERVAL_BETWEEN_TWO_DISAPPEAR = 50L

    private val STAY_BEFORE_NEXT_CARD_INTERVAL = 800L

    // The gradient part of Input and Output panels.
    // Go to see them in .xml file, you will see each panel
    // has 2 matte layer cover on them create gradient effect
    val IO_Gradient_Part_Size = 6.dp()

    private lateinit var RED_BORDER_BACKGROUND : Drawable

    private lateinit var NORMAL_BORDER_BACKGROUND : Drawable

    private lateinit var HIGHLIGHTED_BACKGROUND : Drawable


    var nextCardAnimtrSet: AnimatorSet = AnimatorSet()

    var resetHintAnimtrSet: AnimatorSet = AnimatorSet()

    lateinit var oldSubjectDisaprAnim_RunNextCardOnFinish: Animator


    private var curOutChCellPos = 0

    private var curOutChCellRowOrder = 0

    // CELL DIMENSIONS
    // INPUT
    private var InCellWidth = 0

    private var inCellHeight = 0

    private var inputSpaceBetweenCells = 0

    private var inTopMargin = 0

    private var inBottomMargin = 0

    private var inStartMargin = 0

    private var inEndMargin = 0

    private var inAddtnalMarginStart = 0

    // OUTPUT
    private var outCellWidth = 0

    private var outCellHeight = 0

    private var outputSpaceBetweenCells = 0

    private var outTopMargin= 0

    private var outStartMargin= 0

    private var outBottomMargin= 0

    private var outEndMargin= 0


    private var outAddtnalMarginStart = 0


    // WORD VARIALBLES
    // INPUT
    var curInputWCells_MarginStart = 0

    var curInputWCells_MarginEnd = 0

    var curInputWCells_MarginTop = 0

    // Current output panel word cell
    var curOutWCells_MarginStart = 0

    var curOutWCells_MarginTop = 0

    var curOutWCells_RowOrder = 0

    // Panel dimensions

    var testSubjectPanelHeight = 0

    var maxIOPanelHeight = 0


    private lateinit var inputPanel : View

    private lateinit var outputPanel : View

    private lateinit var cellParent : ConstraintLayout

    private var outputCellList = ArrayList<Cell>()

    private var inputCellList = ArrayList<Cell>()

    private var backButtonPressedTimes = 0

    private var prevForgottenCardCount = 0

    private var prevPassedCardCount = 0

    @field: Named("RotateForever")
    @Inject
    lateinit var rotateForeverAnimation: Animation

    lateinit var viewModelProviderFactory : ViewModelProviderFactory
    @Inject set

    private var answerIsSpoken = true
    private var questionIsSpoken = true
    private var speakerIsOn : Boolean = true

    companion object {
        fun requestReviewFlashcard(
            requestContext: Context,
            deck: Deck,
            reverseCardTextAndTranslation: Boolean
        ) {

            val intent = Intent(requestContext, PuzzleFlashcardActivity::class.java)
            intent.putExtra(FLASHCARD_SET_KEY, deck)
            intent.putExtra(REVERSE_CARD_TEXT_AND_TRANSLATION, reverseCardTextAndTranslation)
            requestContext.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_review_flashcard_easy

    override fun takeViewModel(): PuzzleFlashcardViewModel {
        return getActivityViewModel(viewModelProviderFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(Color.parseColor("#716235"))
        viewModel.setUpView(this)
        dB.vwModel = viewModel
        calculateWidgetsDimensions (onCalculationsFinish = {
            setUpData()
        })
    }

    private fun setUpData() {
        viewModel.setUpData(getRequestedFlashcardSet(), getIsReverseTextAndTranslation())
        dB.executePendingBindings()
        setUpSpeakerStatus()
    }

    private fun calculateWidgetsDimensions (onCalculationsFinish : (() -> Unit)? = null) { dB.apply {
        inputPanel = vwgrpInputCells
        outputPanel = vwgrpOutputCells
        cellParent = vwgrpPuzzlePart

        root.doOnPreDraw {
            testSubjectPanelHeight = root.height / 4
            txtInputAnswer.doOnPreDraw {
                val outputPanelParams : ConstraintLayout.LayoutParams = vwgrpOutputCells.layoutParams as ConstraintLayout.LayoutParams
                val inputPanelParams : ConstraintLayout.LayoutParams = vwgrpInputCells.layoutParams as ConstraintLayout.LayoutParams
                val txtInputAnswerParams : ConstraintLayout.LayoutParams = txtInputAnswer.layoutParams as ConstraintLayout.LayoutParams
                val testSubjectParams : ConstraintLayout.LayoutParams = vwgrpTestSubject.layoutParams as ConstraintLayout.LayoutParams
                val outputPanelPositionY = vwgrpPuzzlePart.y + vwgrpTestSubject.y + testSubjectPanelHeight
                val dimensionsThatIsNot_IO_Panel_Height =  outputPanelParams.topMargin + txtInputAnswerParams.topMargin + txtInputAnswer.height + inputPanelParams.topMargin + inputPanelParams.bottomMargin
                maxIOPanelHeight = (((root.height - outputPanelPositionY) - dimensionsThatIsNot_IO_Panel_Height) / 2).toInt()
                vwgrpInputCells.layoutParams.height = maxIOPanelHeight
                vwgrpInputCells.requestLayout()
                vwgrpOutputCells.layoutParams.height = maxIOPanelHeight
                vwgrpInputCells.requestLayout()
                vwgrpTestSubject.layoutParams.height = testSubjectPanelHeight
                vwgrpTestSubject.requestLayout()
                calculateInputCellsDimens()
                calculateOutputCellsDimens()
                txtTotalCardCount.text = getRequestedFlashcardSet().flashcards.size.toString()
                onCalculationsFinish?.invoke()
            }
        }
    }}

    override fun initProperties () {

        HIGHLIGHTED_BACKGROUND = resources.getDrawable(R.drawable.round_3dp_white_background_with_dark_brown_border)
        RED_BORDER_BACKGROUND = resources.getDrawable(R.drawable.round_3dp_white_background_with_red_border)
        NORMAL_BORDER_BACKGROUND = resources.getDrawable(R.drawable.round_3dp_review_answer_bar_background_review_easy_activity)

        DARK_RED_COLOR = resources.getColor(R.color.dark_red)
        LITTLE_DARD_GREEN_COLOR = resources.getColor(R.color.dark_green)
        GREY_TEXT_COLOR = resources.getColor(R.color.use_flashcard_grey_text_color)

    }

    override fun addViewSettings() { dB.apply {
//        layoutChooseLangFlow.txtQuestionLanguage.text = getRequestedDeck().backLanguage
//        layoutChooseLangFlow.txtAnswerLanguage.text = getRequestedDeck().frontLanguage
    }}

    override fun addViewEvents() { dB.apply {

        txtInputAnswer.addTextChangedListener(onTextChanged = { text,_,_,_ ->
            viewModel.submitAnswer(text.toString().trim())
        })

        btnGiveUp.setOnClickListener {
            viewModel.handleUserForgetCard()
        }

        dialogExit.setOnHide {
            resetBackButtonPressedTimes()
        }

        btnSetting.setOnClickListener {
            dialogSetting.show()
        }

        btnDeleteACell.setOnClickListener {
            val cell = outputCellList.lastOrNull()
            if (cell == null) return@setOnClickListener
            cell.performClick()
        }

        switchSpeaker.setOnCheckedChangeListener { view, isChecked ->
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

        btnFlipDeck.setOnClickListener {
            flipWholeDeck()
        }

    }}

    private fun flipWholeDeck() {
        requestReviewFlashcard(
            this,
            viewModel.getOriginalDeck(),
            viewModel.isDeckReversed.not()
        )
        finish()
    }

    override fun addAnimationEvents() {
        oldSubjectDisaprAnim_RunNextCardOnFinish.addListener(onEnd = {
            dB.vwgrpTestSubject.translationX = 0f
            dB.vwgrpTestSubject.alpha = 1f
            nextCardAnimtrSet.start()

            viewModel.nextCard()
        })
    }


    override fun onPassACard(passedCardCount: Int, forgottenCardCount: Int) {
        dB.txtPassedCardCount.text = passedCardCount.toString()
//        dB.txtForgottenCardCount.text = forgottenCardCount.toString()

//        if ((passedCardCount + forgottenCardCount) == viewModel.getCardListSize()) {
//            dB.txtForgottenCardCount.animate().alpha(0f).duration = COMMON_PROGRESS_BAR_VIEW_DURATION
//        }

        val userFinishTest = (passedCardCount + forgottenCardCount) == viewModel.getDeckSize()
        if (userFinishTest) {
            dB.txtTotalCardCount.animate().alpha(0f).duration = 100
        }

        val userRememberCard = passedCardCount > prevPassedCardCount
        systemOutLogging("User remember card: " + passedCardCount + " and " + prevPassedCardCount)
        systemOutLogging("* User forget card: " + forgottenCardCount + " and " + prevForgottenCardCount)
        val userForgetCard = userRememberCard.not() &&
                (forgottenCardCount > prevForgottenCardCount)

        val userRelearnCard = (userRememberCard) && (forgottenCardCount < prevForgottenCardCount)

        systemOutLogging("User for get: " + userForgetCard)

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


    override  fun onLoadAllIllustrationStart() {
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


    override fun getIsReverseTextAndTranslation(): Boolean {
        return intent.extras!!.getBoolean(REVERSE_CARD_TEXT_AND_TRANSLATION, false)
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

    override fun onBackPressed() {
        backButtonPressedTimes ++
        if (backButtonPressedTimes == 1) {
            dB.dialogExit.show()
        } else if (backButtonPressedTimes == 2) {
            finish()
        }
    }

    override fun onDestroy() {
        saveSpeakerStatus()
        super.onDestroy()
    }

    private fun saveSpeakerStatus () {
        val speakerFunction = when {
            answerIsSpoken and questionIsSpoken -> {
                ReviewActivitiesSpeakerStatusManager.SpeakerStatus.SPEAK_QUESTION_AND_ANSWER
            }
            answerIsSpoken and not(questionIsSpoken) -> {
                ReviewActivitiesSpeakerStatusManager.SpeakerStatus.SPEAK_ANSWER_ONLY
            }
            not(answerIsSpoken) and questionIsSpoken -> {
                ReviewActivitiesSpeakerStatusManager.SpeakerStatus.SPEAK_QUESTION_ONLY
            }
            else -> throw Exception("Speaker status unknown")
        }

        viewModel.saveAllStatus(speakerFunction, speakerIsOn)
    }

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

    private fun showAnswerAnimations (userIsTrue : Boolean, onEnd: () -> Unit = {}) { dB.apply {
        val isExampleAppearOnScreen = (txtTestSubjectExample.alpha == 1f)
        if (isExampleAppearOnScreen) {
            if (userIsTrue) {
                txtExamplePositiveHighlight.animate().setDuration(200).alpha(1f).setLiteListener (onEnd = {
                    onEnd()
                })
            } else {
                txtExampleNegativeHighlight.animate().setDuration(200).alpha(1f).setLiteListener (onEnd = {
                    onEnd()
                })
            }
        } else {
            txtTranslation.animate().setDuration(200).setInterpolator(FastOutSlowInInterpolator())
                .translationY(100f).alpha(0f)
                .setLiteListener (onEnd = {
                    txtTranslation.translationY = 0f
                    onEnd()
                })

            txtTextAnswer.animate().setDuration(200).setInterpolator(FastOutSlowInInterpolator())
                .alpha(1f)
                .setLiteListener (onStart = {
                    if (userIsTrue) {
                        txtTextAnswer.setTextColor(Color.BLACK)
                    } else {
                        txtTextAnswer.setTextColor(DARK_RED_COLOR)
                    }
                })
        }
    }}

    private fun resetBackButtonPressedTimes() {
        backButtonPressedTimes = 0
    }

    // ======================== VIEW OVERRIDE FUNCTION =================================

    override fun nextCard () {
        if (viewModel.hasNext()) {
            oldSubjectDisaprAnim_RunNextCardOnFinish.start()
            resetHintAnimtrSet.start()
            setAnswer("")
        } else {
            onEndReviewing()
        }
    }

    override fun onEndReviewing() {
        sendHardCardListToEndActivity()
        finish()
    }

    override fun showSpeakTextError(error: String) {
        quickToast(error)
    }


    override fun onGetTestSubject (testSubject: Flashcard, illustration : Bitmap?, load_illustrationException : Exception?,
                                   useExampleForTestSubject: Boolean,
                                   answerElements: Array<String>,
                                   listType : ReviewFlashcardEasyView.ListOfCellType) { dB.apply {

        val answer = testSubject.text
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
            setUpExampleTestSubject(testSubject)
            speakQuestion_IfAllowed(testSubject.meanOfExample)
        } else {
            setUpTranslationTestSubject(testSubject)
            speakQuestion_IfAllowed(testSubject.translation)
        }

        if (listType == CHARACTER_LIST) {
            systemOutLogging("Clgt")
            recalculate_CellDimens_AdaptingScreen(answerElements)
            answerElements.forEach {
                systemOutLogging(it)
            }
            createChar_InputCell_LIST(answerElements)
            recalculatePanelsHeight(answerElements.size)
        } else if (listType == WORD_LIST) {
            systemOutLogging("What the fuck")
            createWord_InputCell_LIST(answerElements, onLayoutInputCell_Finishing = { rowCount ->
                recalculatePanelsHeight_BasedOnRowCount(rowCount)
            })
        }

    }}

    private fun recalculate_CellDimens_AdaptingScreen(answerElements: Array<String>) {
        val maxCellOnPanel = DEFAULT_MAX_CELL_PER_ROW * DEFAULT_MAX_ROW
        val cellCountIs_BiggerThan_MaxCellCount = answerElements.size > maxCellOnPanel
        if (cellCountIs_BiggerThan_MaxCellCount) {
            val elementCount_Is_Nice = (answerElements.size % DEFAULT_MAX_ROW) == 0
            if (elementCount_Is_Nice) {
                MAX_CELL_PER_ROW = answerElements.size / DEFAULT_MAX_ROW
            } else {
                MAX_CELL_PER_ROW = (answerElements.size / DEFAULT_MAX_ROW) + 1
            }
        } else { // cell count is less or equal than max cell count
            MAX_CELL_PER_ROW = DEFAULT_MAX_CELL_PER_ROW
        }

        calculateInputCellsDimens()
        calculateOutputCellsDimens()
    }

    private fun setUpTranslationTestSubject (card : Flashcard) { dB.apply {
        txtTranslation.text = card.translation
        hideExampleTestSubjectComponents()
        txtTranslation.alpha = 1f
        txtTextAnswer.alpha = 0f
    }}

    private fun setUpExampleTestSubject (card : Flashcard) { dB.apply {
        val answer = card.text
        val positiveHighlightedText = highlightAnswerInExample(card.example, answer, LITTLE_DARK_GREEN)
        txtExamplePositiveHighlight.setText(positiveHighlightedText, TextView.BufferType.SPANNABLE)
        txtExamplePositiveHighlight.alpha = 0f

        val negativeHighlightedText = highlightAnswerInExample(card.example, answer, DARK_RED)
        txtExampleNegativeHighlight.setText(negativeHighlightedText, TextView.BufferType.SPANNABLE)
        txtExampleNegativeHighlight.alpha = 0f

        val exampleWithHiddenAnswer = processHideAnswerInExample(card.example, answer)
        txtTestSubjectExample.setText(exampleWithHiddenAnswer, TextView.BufferType.SPANNABLE)

        txtExampleTranslation.text = card.meanOfExample
        showExampleTestSubjectComponents()
        txtTranslation.alpha = 0f
        txtTextAnswer.alpha = 0f
    }
    }

    private val ORANGE_HIGHLIGHT_COLOR: Int = Color.parseColor("#F44F11")

    override fun perform_CorrectAnswerElementsOrderBehaviours() {
        dB.apply {
            if (txtInputAnswer.textColors != ColorStateList.valueOf(Color.BLACK)) {
                txtInputAnswer.setTextColor(Color.BLACK)
            }
        }
    }

    override fun perform_INcorrectAnsElemtsOrderAnims() {
        dB.apply {
            if (txtInputAnswer.textColors != ColorStateList.valueOf(ORANGE_HIGHLIGHT_COLOR)) {
                txtInputAnswer.setTextColor(ORANGE_HIGHLIGHT_COLOR)
            }
        }
    }

    override fun performPassBehaviours() {
        disableClickingAllOutputCells()
        highlightAllOutputCells()
        showAnswerAnimations(true)
        if (speakerIsOn and answerIsSpoken) {
            viewModel.speakAnswer(dB.txtTextAnswer.text.toString(), onSpeakDone = {
                clearAllOnScreenCells(
                    delayTime = STAY_BEFORE_NEXT_CARD_INTERVAL - 250L,
                    onEnd = {
                        nextCard()
                    })
            })
        } else {
            clearAllOnScreenCells (delayTime = STAY_BEFORE_NEXT_CARD_INTERVAL, onEnd = {
                nextCard()
            })
        }
    }


    override fun performNotPassBehaviours() {
        disableClickingAllOutputCells()
        disableClickingAllInputCells()
        showAnswerAnimations(false)
        if (speakerIsOn and answerIsSpoken) {
            speakAnswer_IfAllowed(dB.txtTextAnswer.text.toString()) {
                clearAllOnScreenCells (delayTime = (STAY_BEFORE_NEXT_CARD_INTERVAL * 1.5).toLong()  - 250L, onEnd = {
                    nextCard()
                })
            }
        } else {
            clearAllOnScreenCells (delayTime = (STAY_BEFORE_NEXT_CARD_INTERVAL * 1.5).toLong(), onEnd = {
                nextCard()
            })
        }
    }

    private fun speakAnswer_IfAllowed (text : String, onSpeakDone : () -> Unit) {
        if (speakerIsOn and answerIsSpoken) {
            viewModel.speakAnswer(text, onSpeakDone)
        }
    }

    private fun speakQuestion_IfAllowed (text : String) {
        if (speakerIsOn and questionIsSpoken) {
            viewModel.speakQuestion(text)
        }
    }

    // UTTERANCE PROGRESS LISTENER OVERRIDE
    override fun onUtteranceCompleted(utteranceId: String?) {
        systemOutLogging("Complete")
    }
    // ======================== CALCULATE FUNCTION =================================


    private fun calculateInputCellsDimens () {
        inStartMargin = inputPanel.paddingStart
        inTopMargin = inputPanel.paddingTop + IO_Gradient_Part_Size
        inEndMargin = inputPanel.paddingEnd
        inBottomMargin = inputPanel.paddingBottom

        curInputWCells_MarginStart = inStartMargin
        curInputWCells_MarginEnd = inEndMargin
        curInputWCells_MarginTop = inTopMargin

        val expectedPanelWidth = (inputPanel.width - inStartMargin * 2)
        inputSpaceBetweenCells = inStartMargin

        InCellWidth = (expectedPanelWidth - (MAX_CELL_PER_ROW - 1) * inputSpaceBetweenCells) / MAX_CELL_PER_ROW
//         * 2 / 3 <=> / 1.5
        inCellHeight = (maxIOPanelHeight - (MAX_ROW - 1) * inputSpaceBetweenCells - inTopMargin - inBottomMargin) / MAX_ROW
//         Try to layout cells in the middle of panel
//         Because there is calcError when convert from float to integer
//         Sometimes there is a little space at the right of panel
        val actualPanelWidth = (InCellWidth * MAX_CELL_PER_ROW + inputSpaceBetweenCells * (MAX_CELL_PER_ROW - 1))
        val calcError = expectedPanelWidth - actualPanelWidth
        val addtnalCellWidth = calcError / MAX_CELL_PER_ROW
        inAddtnalMarginStart =
            (expectedPanelWidth - (actualPanelWidth + addtnalCellWidth * MAX_CELL_PER_ROW)) / 2
        InCellWidth += addtnalCellWidth

        systemOutLogging("Calculate input")
    }

    private fun calculateOutputCellsDimens () {
        outStartMargin = outputPanel.paddingStart
        outTopMargin = outputPanel.paddingTop
        outEndMargin = outputPanel.paddingEnd
        outBottomMargin = outputPanel.paddingBottom + IO_Gradient_Part_Size

        curOutWCells_MarginStart = outStartMargin
        curOutWCells_MarginTop = outTopMargin

        val expectedPanelWidth = (outputPanel.width - outStartMargin * 2)
        outputSpaceBetweenCells = outStartMargin

        outCellWidth = (expectedPanelWidth - (MAX_CELL_PER_ROW - 1) * outputSpaceBetweenCells) / MAX_CELL_PER_ROW
//         * 2 / 3 <=> / 1.5
        outCellHeight = (maxIOPanelHeight - (MAX_ROW - 1) * outputSpaceBetweenCells - outTopMargin - outBottomMargin) / MAX_ROW
//         Try to layout cells in the middle of panel
//         Because there is calcError when convert from float to integer
//         Sometimes there is a little space at the right of panel
        val actualPanelWidth = (outCellWidth * MAX_CELL_PER_ROW + outputSpaceBetweenCells * (MAX_CELL_PER_ROW - 1))
        val calcError = expectedPanelWidth - actualPanelWidth
        val addtnalCellWidth = calcError / MAX_CELL_PER_ROW
        outAddtnalMarginStart =
            (expectedPanelWidth - (actualPanelWidth + addtnalCellWidth * MAX_CELL_PER_ROW)) / 2
        outCellWidth += addtnalCellWidth
    }


    // ======================== CHARACTERS FUNCTION =================================


    private fun createChar_InputCell_LIST(ansElements: Array<String>) {
        dB.apply {
            for ((position, element) in ansElements.withIndex()) {
                systemOutLogging("Create input cells: " + element)
                createChar_InputCell(element, position, true)
            }
        }
    }

    private fun createChar_InputCell (cellContent: String, position : Int, isFirstTimesCreated : Boolean) {
        val inCharCell = Cell(this, INPUT_CELL)
        inCharCell.text = cellContent

        // Create Layout params
        val constraint = ConstraintLayout.LayoutParams(InCellWidth, inCellHeight)
        constraint.topToTop = inputPanel.id
        constraint.startToStart = inputPanel.id
        val positionInRow = position % MAX_CELL_PER_ROW
        constraint.marginStart = (positionInRow) * (InCellWidth + inputSpaceBetweenCells) + inStartMargin
        constraint.topMargin = (position / MAX_CELL_PER_ROW) * (inCellHeight + inputSpaceBetweenCells) + inTopMargin
        if ((positionInRow == 0) or (positionInRow == MAX_CELL_PER_ROW - 1)) {
            constraint.marginStart += inAddtnalMarginStart
        }
        inCharCell.layoutParams = constraint
        cellParent.addView(inCharCell)

        inputCellList.add(inCharCell)

        // SetListener
        inCharCell.setOnClickListener {
            inCharCell.isClickable = false
            inputCellList.remove(inCharCell)
            inCharCell.performDestroyAnimate(DIRECTION_UP).setStartDelay(0)
                .setLiteListener (onEnd = {
                    createChar_OutputCell(cellContent, position)
                    cellParent.removeView(inCharCell)
                })
        }

        // Run isFirstTimesCreated
        if (isFirstTimesCreated) {
            inCharCell.performCreateAnimate(DIRECTION_UP).startDelay =
                position * INTERVAL_BETWEEN_TWO_DISAPPEAR
        } else {
            inCharCell.performCreateAnimate(DIRECTION_DOWN).startDelay = CELL_MOVING_INVISIBLY_TIME
        }

    }

    private fun createChar_OutputCell (cellContent : String, inputCellPos : Int) { dB.apply {
        val cell = Cell(this@PuzzleFlashcardActivity, OUTPUT_CELL)
        cell.text = cellContent
        val constraint = ConstraintLayout.LayoutParams(outCellWidth, outCellHeight)
        constraint.topToTop = outputPanel.id
        constraint.leftToLeft = outputPanel.id
        constraint.marginStart = (outCellWidth +  outputSpaceBetweenCells) * (curOutChCellPos % MAX_CELL_PER_ROW) + outStartMargin
        constraint.topMargin = vwgrpOutputCells.paddingTop + (curOutChCellRowOrder) * (outCellHeight + outputSpaceBetweenCells)
        cell.layoutParams = constraint
        cellParent.addView(cell)

        val currentOutputCellPosition = curOutChCellPos

        cell.setOnRestore { delayTime ->
                cell.performDestroyAnimate(DIRECTION_DOWN)
                      .setStartDelay(delayTime)
                      .setLiteListener(onEnd =  {
                            createChar_InputCell(cellContent, inputCellPos, false)
                            cellParent.removeView(cell)
            })
        }

        // Set Listener
        cell.setOnClickListener {
            cell.isClickable = false
            cell.performOnRestore(0)
            dB.txtInputAnswer.text = dB.txtInputAnswer.text.removeRange(currentOutputCellPosition, dB.txtInputAnswer.text.length)
            curOutChCellPos -= (outputCellList.size - currentOutputCellPosition)
            restoreOtherRightSideOutputCells(currentOutputCellPosition)
            removeOtherRightSideOutputCells(currentOutputCellPosition)
            curOutChCellRowOrder = curOutChCellPos / MAX_CELL_PER_ROW
        }

        outputCellList.add(cell)

        // Run Animation
        cell.performCreateAnimate(DIRECTION_UP).setStartDelay(CELL_MOVING_INVISIBLY_TIME).setLiteListener (onEnd = {
            setAnswer(dB.txtInputAnswer.text.toString() + cellContent)
        })

        // Process row and position status
        curOutChCellPos++
        curOutChCellRowOrder = curOutChCellPos / MAX_CELL_PER_ROW
    }}

    private fun setAnswer (answer : String) {
        dB.txtInputAnswer.text = answer
    }

    private fun recalculatePanelsHeight (elementCount : Int) {
//        systemOutLogging("recalculatePanelsHeight()")
//        dB.apply {
//            var rowCount = elementCount / MAX_CELL_PER_ROW
//            val notFullCellInLastRow = (elementCount % MAX_CELL_PER_ROW != 0)
//            if (notFullCellInLastRow) rowCount++
//
//            recalculatePanelsHeight_BasedOnRowCount(rowCount)
//        }
    }

    private fun recalculatePanelsHeight_BasedOnRowCount(rowCount: Int) {
//        dB.apply {
//            val expectedInputGroupHeight: Int
//            if (rowCount > MIN_INPUT_ROW_COUNT) {
//                expectedInputGroupHeight =
//                    rowCount * (inCellHeight + spaceBetInputCells) + InTopMargin * 2
//            } else {
//                expectedInputGroupHeight =
//                    MIN_INPUT_ROW_COUNT * (inCellHeight + spaceBetInputCells) + InTopMargin * 2
//            }
//            val expectedOutputGroupHeight =
//                rowCount * (outCellHeight + outSpaceBetCells) + outTopMargin * 2
//
//            if (vwgrpInputCells.layoutParams.height != expectedInputGroupHeight) {
//                vwgrpInputCells.layoutParams.height = expectedInputGroupHeight
//                vwgrpInputCells.requestLayout()
//            }
//
//            if (vwgrpOutputCells.layoutParams.height != expectedOutputGroupHeight) {
//                vwgrpOutputCells.layoutParams.height = expectedOutputGroupHeight
//                vwgrpOutputCells.requestLayout()
//            }
//        }
    }

// ======================== WORDS FUNCTION =================================


    private fun createWord_InputCell_LIST(
        ansElements: Array<String>,
        onLayoutInputCell_Finishing: (rowCount: Int) -> Unit
    ) {

        for ((position, element) in ansElements.withIndex()) {
            // Create the cell
            val inputCell = Cell(this, cellType = INPUT_CELL)
            inputCell.text = element
            val constraint = ConstraintLayout.LayoutParams(WRAP_CONTENT, inCellHeight)
            constraint.topToTop = inputPanel.id
            constraint.leftToLeft = inputPanel.id
            inputCell.setPadding(15.dp(), 0, 15.dp(), 0)
            inputCell.layoutParams = constraint
            cellParent.addView(inputCell)
            inputCellList.add(inputCell)

            // SetListener
            inputCell.doOnPreDraw {
                inputCell.setOnClickListener {
                    inputCell.isClickable = false
                    inputCellList.remove(inputCell)
                    inputCell.performDestroyAnimate(DIRECTION_UP)
                        .setStartDelay(0)
                        .setLiteListener (onEnd = {
                            createWord_OutputCell(element, inputCell.measuredWidth, inputCell.measuredHeight, inputCell.marginStart, inputCell.marginTop)
                            cellParent.removeView(inputCell)
                        })
                }
            }

            if (position == ansElements.lastIndex) {
                inputCell.doOnPreDraw {
                    val rowCount = layoutInput_WordCellsOnScreen()
                    onLayoutInputCell_Finishing.invoke(rowCount)
                }
            }

            inputCell.performCreateAnimate(DIRECTION_UP).startDelay =
                position * INTERVAL_BETWEEN_TWO_DISAPPEAR
        }
    }

    private fun createWord_InputCell (cellContent : String, cellWidth : Int, cellHeight : Int, marginStart : Int, marginTop : Int) {
        val inputCell = Cell(this, INPUT_CELL)
        inputCell.text = cellContent
        val constraint = ConstraintLayout.LayoutParams (cellWidth, cellHeight)
        constraint.topToTop = inputPanel.id
        constraint.startToStart = inputPanel.id
        constraint.topMargin = marginTop
        constraint.marginStart = marginStart
        inputCell.layoutParams = constraint
        inputCellList.add(inputCell)
        cellParent.addView(inputCell)

        inputCell.setOnClickListener {
            inputCell.isClickable = false
            inputCell.performDestroyAnimate(DIRECTION_UP).setStartDelay(0)
                .setLiteListener (onEnd = {
                    createWord_OutputCell(
                        cellContent,
                        cellWidth,
                        cellHeight,
                        marginStart,
                        marginTop
                    )
                    cellParent.removeView(inputCell)
                })
            inputCellList.remove(inputCell)
        }

        // Run animate
        inputCell.performCreateAnimate(DIRECTION_DOWN).startDelay = CELL_MOVING_INVISIBLY_TIME
    }

    private fun layoutInput_WordCellsOnScreen(): Int {
        systemOutLogging("layoutInput_WordCellsOnScreen()")
        var rowCount = 1
        for (cell in inputCellList) {
            val nextTotalCellsWidth_InLastRow = curInputWCells_MarginStart + cell.width

            val goToNextRow = nextTotalCellsWidth_InLastRow + curInputWCells_MarginEnd > inputPanel.width
            var cellMarginStart: Int
            val cellMarginTop: Int

            if (goToNextRow) {
                cellMarginStart = inStartMargin
                // Reset Start Margin to the left of Input Panel
                curInputWCells_MarginStart = inStartMargin + cell.width + inputSpaceBetweenCells
                curInputWCells_MarginTop += (cell.height + inputSpaceBetweenCells)
                rowCount++
            } else {
                cellMarginStart = curInputWCells_MarginStart
                curInputWCells_MarginStart = nextTotalCellsWidth_InLastRow + inputSpaceBetweenCells
            }

            cellMarginTop = curInputWCells_MarginTop
            val newLayoutParams = ConstraintLayout.LayoutParams(cell.layoutParams)
            newLayoutParams.marginStart = cellMarginStart
            newLayoutParams.topMargin = cellMarginTop
            newLayoutParams.topToTop = inputPanel.id
            newLayoutParams.startToStart= inputPanel.id
            cell.layoutParams = newLayoutParams
            cell.requestLayout()

        }
        return rowCount
    }


    private fun createWord_OutputCell (cellContent: String, cellWidth : Int, cellHeight : Int, inputCellMarginStart : Int, inputCellMarginTop : Int) {
        val outputCell = Cell(this, OUTPUT_CELL)
        val constraint = ConstraintLayout.LayoutParams(cellWidth, cellHeight)

        val nextTotalCellsWidth_InLastRow = curOutWCells_MarginStart + cellWidth
        val cellNotFitInRow = (nextTotalCellsWidth_InLastRow > outputPanel.width)

        val cellMarginStart : Int
        // Go to next row
        if (cellNotFitInRow) {
            cellMarginStart = outStartMargin
            // Reset Start Margin to the left of Input Panel
            curOutWCells_MarginStart = outStartMargin + cellWidth + outputSpaceBetweenCells
            curOutWCells_MarginTop += (cellHeight + outputSpaceBetweenCells)
        } else {
            cellMarginStart = curOutWCells_MarginStart
            curOutWCells_MarginStart = nextTotalCellsWidth_InLastRow + outputSpaceBetweenCells
        }

        val cellMarginTop = curOutWCells_MarginTop

        constraint.marginStart = cellMarginStart
        constraint.topMargin = cellMarginTop
        constraint.topToTop = outputPanel.id
        constraint.startToStart= outputPanel.id

        outputCell.layoutParams = constraint
        outputCell.text = cellContent

        // Set Listener
        outputCell.setOnRestore { delayTime ->
            outputCell.performDestroyAnimate(DIRECTION_DOWN)
                .setStartDelay(delayTime)
                .setLiteListener(onEnd =  {
                    createWord_InputCell(cellContent, cellWidth, cellHeight, inputCellMarginStart, inputCellMarginTop)
                    cellParent.removeView(outputCell)
                })
        }

        outputCell.setOnClickListener {
            outputCell.isClickable = false
            outputCell.performOnRestore(0)
            val pickedPos = outputCellList.indexOf(outputCell)
            var intergratedText = ""
            for (i in pickedPos..outputCellList.size - 1) {
                intergratedText += outputCellList[i].text.toString() + " "
            }
            dB.txtInputAnswer.text = dB.txtInputAnswer.text.removeSuffix(intergratedText)
            restoreOtherRightSideOutputCells(pickedPos)
            removeOtherRightSideOutputCells(pickedPos)

            if (outputCellList.size > 0) {
                // Reset to last cell position curOutWCells_MarginStart
                curOutWCells_MarginStart -= (cellWidth + outputSpaceBetweenCells)
                val noCellInLastRow = curOutWCells_MarginStart == outStartMargin
                if (noCellInLastRow) {
                    if (curOutWCells_RowOrder > 0) {
                        val lastCell = outputCellList.last()
                        curOutWCells_MarginStart = lastCell.marginStart + lastCell.width + outputSpaceBetweenCells
                        curOutWCells_RowOrder -= 1
                    }
                } else {
                    val lastCell = outputCellList.last()
                    curOutWCells_MarginStart = lastCell.marginStart + lastCell.width + outputSpaceBetweenCells
                    curOutWCells_MarginTop = lastCell.marginTop
                }
            } else {
                curOutWCells_MarginStart = outStartMargin
                curOutWCells_MarginTop = outTopMargin
            }

        }

        outputCellList.add(outputCell)
        cellParent.addView(outputCell)

        // Run animations
        outputCell.performCreateAnimate(DIRECTION_UP).setStartDelay(CELL_MOVING_INVISIBLY_TIME).setLiteListener (onEnd = {
            setAnswer(dB.txtInputAnswer.text.toString() + cellContent + " ")
        })
    }

    private fun restoreOtherRightSideOutputCells (pickedPos : Int) {
        for (pos in pickedPos + 1..outputCellList.size - 1) {
            val loopTimes = (pos - (pickedPos + 1)) + 1
            outputCellList.get(pos).performOnRestore(loopTimes * INTERVAL_BETWEEN_TWO_DISAPPEAR)
        }
    }

    private fun removeOtherRightSideOutputCells (pickedPos : Int) {
        for (pos in pickedPos until outputCellList.size) {
            outputCellList.removeAt(outputCellList.size - 1)
        }
    }

    private fun clearAllOnScreenCells(delayTime : Long, onEnd : () -> Unit = {}) {
        val outListLongerThanInList = outputCellList.size >= inputCellList.size
        val outputCellListSize = outputCellList.size
        val inputCellListSize = inputCellList.size

        if (outputCellListSize == inputCellListSize) {
            onEnd()
        }

        for ((i, cell) in outputCellList.withIndex()) {
            cell.performDestroyAnimate(DIRECTION_UP).setStartDelay(delayTime + i * INTERVAL_BETWEEN_TWO_DISAPPEAR).setLiteListener (onEnd = {
                if (outListLongerThanInList && (i == outputCellListSize - 1)) {
                    onEnd()
                }
                cellParent.removeView(cell)
            })
        }

        for ((i, cell) in inputCellList.withIndex()) {
            cell.performDestroyAnimate(DIRECTION_UP).setStartDelay(delayTime + i * INTERVAL_BETWEEN_TWO_DISAPPEAR).setLiteListener (onEnd = {
                if (not(outListLongerThanInList) && (i == inputCellListSize - 1)) {
                    onEnd()
                }
                cellParent.removeView(cell)
            })

        }

        inputCellList.clear()
        outputCellList.clear()
        curOutChCellPos = 0
        curOutChCellRowOrder = 0

        curInputWCells_MarginTop  = inTopMargin
        curInputWCells_MarginStart = inStartMargin

        curOutWCells_MarginTop  = outTopMargin
        curOutWCells_MarginStart = outStartMargin
        curOutWCells_RowOrder = 0
    }

    private fun highlightAllOutputCells () {
        for (cell in outputCellList) {
            cell.background = HIGHLIGHTED_BACKGROUND
        }
    }

    private fun disableClickingAllOutputCells () {
        for (cell in outputCellList) {
            cell.isClickable = false
        }
    }

    private fun disableClickingAllInputCells() {
        for (cell in inputCellList) {
            cell.isClickable = false
        }
    }

    override fun getRequestedFlashcardSet(): Deck {
        return intent.extras!!.getSerializable(FLASHCARD_SET_KEY) as Deck
    }

    private fun highlightAnswerInExample(
        example: String,
        answer: String,
        colorCode: String
    ): CharSequence {
        val answerInExample = findTextFormInAnother(answer, example)
        return Html.fromHtml(
            example.replace(
                answerInExample,
                "<font color='$colorCode'>$answerInExample</font>"
            )
        )
    }

    private fun processHideAnswerInExample(example: String, answer: String): SpannableString {
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

    fun sendHardCardListToEndActivity() {
        val missedCardList = viewModel.getMissedCardsList()
        val deck = viewModel.getOriginalDeck()
        ResultReportActivity.requestFinishUsingFlashcard(
            this, deck, missedCardList,
            ResultReportActivity.FlashcardSendableActivity.REVIEW_FLASHCARD_EASY_ACTIVITY.code
        )
    }

    override fun overrideEnterAnim() {
        overridePendingTransition(R.anim.appear, R.anim.nothing)
    }

    override fun overrideExitAnim() {
        overridePendingTransition(R.anim.nothing, R.anim.disappear)
    }

    // ==================================INJECTED METHODS============================================

    @Inject
    fun initNextCardAnimations(
        @Named("MoveRight120%AndFadeOut") moveRightAndFadeOut: Animator,
        @Named("Float") vwgrpNewSubAppear: Animator
    ) {
        dB.apply {
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

        oldSubjectDisaprAnim_RunNextCardOnFinish = moveRightAndFadeOut

        oldSubjectDisaprAnim_RunNextCardOnFinish.setTarget(vwgrpTestSubject)
        nextCardAnimtrSet.play(vwgrpNewSubAppear)

    }}
}




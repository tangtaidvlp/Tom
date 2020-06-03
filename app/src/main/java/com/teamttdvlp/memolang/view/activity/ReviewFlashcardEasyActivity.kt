package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
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
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.view.doOnPreDraw
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.core.widget.addTextChangedListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
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
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.ReviewFlashcardEasyViewModel
import javax.inject.Inject
import javax.inject.Named

class ReviewFlashcardEasyActivity : BaseActivity<ActivityReviewFlashcardEasyBinding, ReviewFlashcardEasyViewModel>(),
    ReviewFlashcardEasyView, TextToSpeech.OnUtteranceCompletedListener {

    private val MIN_INPUT_ROW_COUNT: Int = 3

    private val NEXT_CARD_ANIMS_DURATION = 400L


    // MAX_CELL_PER_ROW
    private val MAX_CPR = 6

    private val DARK_RED = "#FF2C00"

    private val LITTLE_DARK_GREEN = "#01A40C"

    private var DARK_RED_COLOR : Int = 0

    private var LITTLE_DARD_GREEN_COLOR : Int = 0

    private var GREY_TEXT_COLOR : Int = 0

    private val CELL_MOVING_INVISIBLY_TIME = 0L

    private val INTERVAL_BETWEEN_TWO_DISAPPEAR = 50L

    private val STAY_BEFORE_NEXT_CARD_INTERVAL = 800L

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

    private var spaceBetInputCells = 0

    private var InTopMargin = 0

    private var InStartMargin = 0

    private var InAddtnalMarginStart = 0

    // OUTPUT
    private var outCellWidth = 0

    private var outCellHeight = 0

    private var outSpaceBetCells = 0

    private var outTopMargin= 0

    private var outStartMargin= 0

    private var outAddtnalMarginStart = 0


    // WORD VARIALBLES
    // INPUT
    var curInputWCells_MarginStart = 0

    var curInputWCells_MarginTop = 0

    // Current output panel word cell
    var curOutWCells_MarginStart = 0

    var curOutWCells_MarginTop = 0

    var curOutWCells_RowOrder = 0


    private lateinit var inputPanel : View

    private lateinit var outputPanel : View

    private lateinit var cellParent : ConstraintLayout

    private var outputCellList = ArrayList<Cell>()

    private var inputCellList = ArrayList<Cell>()

    private var backButtonPressedTimes = 0

    lateinit var viewModelProviderFactory : ViewModelProviderFactory
    @Inject set

    private var answerIsSpoken = true
    private var questionIsSpoken = true
    private var speakerIsOn : Boolean = true

    companion object {
        fun requestReviewFlashcard (requestContext : Context, flashcardSet : FlashcardSet){
            val intent = Intent(requestContext, ReviewFlashcardEasyActivity::class.java)
            intent.putExtra(FLASHCARD_SET_KEY, flashcardSet)
            requestContext.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_review_flashcard_easy

    override fun takeViewModel(): ReviewFlashcardEasyViewModel {
        return getActivityViewModel(viewModelProviderFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        dB.vwModel = viewModel

        inputPanel = dB.vwgrpInputCells
        outputPanel = dB.vwgrpOutputCells
        cellParent = dB.root as ConstraintLayout

        dB.vwgrpInputCells.doOnPreDraw {
            dB.vwgrpOutputCells.doOnPreDraw {
                calculatelInputCellsDimens()
                calculateOutputCellsDimens()
            }
        }
    }

    override fun initProperties() {
        HIGHLIGHTED_BACKGROUND = resources.getDrawable(R.drawable.round_3dp_white_background_with_dark_brown_border)
        RED_BORDER_BACKGROUND = resources.getDrawable(R.drawable.round_3dp_white_background_with_red_border)
        NORMAL_BORDER_BACKGROUND = resources.getDrawable(R.drawable.round_3dp_review_answer_bar_background_review_easy_activity)

        DARK_RED_COLOR = resources.getColor(R.color.dark_red)
        LITTLE_DARD_GREEN_COLOR = resources.getColor(R.color.dark_green)
        GREY_TEXT_COLOR = resources.getColor(R.color.use_flashcard_grey_text_color)
    }

    override fun addViewControls() { dB.apply {
        layoutChooseLangFlow.txtQuestionLanguage.text = getRequestedFlashcardSet().backLanguage
        layoutChooseLangFlow.txtAnswerLanguage.text = getRequestedFlashcardSet().frontLanguage
    }}

    override fun addViewEvents() { dB.apply {
        txtInputAnswer.addTextChangedListener(onTextChanged = { text,_,_,_ ->
            viewModel.checkAnswer(text.toString().trim())
        })

        btnGiveUp.setOnClickListener {
            viewModel.processForgottenCard()
            performNotPassBehaviours()
        }

        dialogExit.setOnHide {
            resetBackButtonPressedTimes()
        }

        btnSetting.setOnClickListener {
            dialogSetting.show()
        }

        layoutChooseLangFlow.btnReverseLangage.setOnClickListener {
            beginUsing(true)
        }

        layoutChooseLangFlow.btnDoesNotReverseLanguage.setOnClickListener {
            beginUsing(false)
        }

        btnTurnOffSpeaker.setOnClickListener {
            speakerIsOn = false
            btnTurnOnSpeaker.goVISIBLE()
        }

        btnTurnOnSpeaker.setOnClickListener {
            speakerIsOn = true
            btnTurnOnSpeaker.goGONE()
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

    }}

    override fun addAnimationEvents() {
        oldSubjectDisaprAnim_RunNextCardOnFinish.addListener( onEnd = {
            dB.vwgrpTestSubject.translationX = 0f
            dB.vwgrpTestSubject.alpha = 1f
            nextCardAnimtrSet.start()

            viewModel.nextCard()
        })
    }

    private fun beginUsing (reverseLanguageFlow : Boolean) {
        viewModel.setUp(getRequestedFlashcardSet(), reverseLanguageFlow)
        dB.layoutChooseLangFlow.root.goGONE()
        setUpSpeakerStatus()
    }

    private fun setUpSpeakerStatus () {
        speakerIsOn = viewModel.getSpeakerStatus()
        if (speakerIsOn) {
            dB.btnTurnOnSpeaker.goGONE()
        } else {
            dB.btnTurnOnSpeaker.goVISIBLE()
        }

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
        val hasNotSetUpLanguages = (dB.layoutChooseLangFlow.root.isVisible())
        if (hasNotSetUpLanguages) {
            finish()
        }

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
        val speakerFunction = if (answerIsSpoken and questionIsSpoken) {
            ReviewActivitiesSpeakerStatusManager.SpeakerStatus.SPEAK_QUESTION_AND_ANSWER
        } else if (answerIsSpoken and not(questionIsSpoken)) {
            ReviewActivitiesSpeakerStatusManager.SpeakerStatus.SPEAK_ANSWER_ONLY
        } else if (not(answerIsSpoken) and questionIsSpoken) {
            ReviewActivitiesSpeakerStatusManager.SpeakerStatus.SPEAK_QUESTION_ONLY
        } else throw Exception ("Speaker status unknown")

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


    fun resetBackButtonPressedTimes () {
        backButtonPressedTimes = 0
    }

    // ======================== VIEW OVERRIDE FUNCTION =================================

    override fun nextCard () {
        if (viewModel.checkIsThereCardLeft()) {
            oldSubjectDisaprAnim_RunNextCardOnFinish.start()
            resetHintAnimtrSet.start()
            setAnswer("")
        } else {
            endReviewing()
        }
    }

    override fun endReviewing() {
        sendHardCardListToEndActivity()
        finish()
    }



    override fun showSpeakTextError(error: String) {
        quickToast(error)
    }


    override fun onGetTestSubject (testSubject: Flashcard,
                                  useExampleForTestSubject: Boolean,
                                  ansElements: Array<String>,
                                  listType : ReviewFlashcardEasyView.ListOfCellType) { dB.apply {

        val answer = testSubject.text
        txtTextAnswer.text = answer

        if (useExampleForTestSubject) {
            setUpExampleTestSubject(testSubject)
            speakQuestion_IfAllowed(testSubject.meanOfExample)
        } else {
            setUpTranslationTestSubject(testSubject)
            speakQuestion_IfAllowed(testSubject.translation)
        }

        if (listType == CHARACTER_LIST) {
            quickLog("Char")
            createChar_InputCells(ansElements)
        } else if (listType == WORD_LIST) {
            quickLog("Word")
            createWord_InputCells(ansElements)
        }

        recalculatePanelsHeight(ansElements.size)
    }}

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
    }}


    override fun performCorrectAnsElemtsOrderAnims() { dB.apply {
        if (imgTxtInputAnswerBackground.background != NORMAL_BORDER_BACKGROUND) {
            imgTxtInputAnswerBackground.background = NORMAL_BORDER_BACKGROUND
        }
    }}

    override fun performIncorrectAnsElemtsOrderAnims() { dB.apply {
        if (imgTxtInputAnswerBackground.background != RED_BORDER_BACKGROUND) {
            imgTxtInputAnswerBackground.background = RED_BORDER_BACKGROUND
        }
    }}



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
        quickLog("Complete")
    }
    // ======================== CALCULATE FUNCTION =================================


    private fun calculatelInputCellsDimens () {
        InStartMargin = inputPanel.paddingStart
        InTopMargin = inputPanel.paddingTop

        curInputWCells_MarginStart = InStartMargin
        curInputWCells_MarginTop = InTopMargin

        val expectedPanelWidth = (inputPanel.width - InStartMargin * 2)
        spaceBetInputCells = expectedPanelWidth / 41
        InCellWidth = expectedPanelWidth * MAX_CPR / 41
//         * 2 / 3 <=> / 1.5
        inCellHeight = InCellWidth * 2 / 3
//         Try to layout cells in the middle of panel
//         Because there is calcError when convert from float to integer
//         Sometimes there is a little space at the right of panel
        val actualPanelWidth = (InCellWidth * MAX_CPR + spaceBetInputCells * (MAX_CPR - 1))
        val calcError = expectedPanelWidth - actualPanelWidth
        val addtnalCellWidth = calcError / MAX_CPR
        InAddtnalMarginStart = (expectedPanelWidth - (actualPanelWidth + addtnalCellWidth * MAX_CPR))/2
        InCellWidth += addtnalCellWidth
    }

    private fun calculateOutputCellsDimens () {
        outStartMargin= outputPanel.paddingStart
        outTopMargin= outputPanel.paddingTop
        curOutWCells_MarginStart = outStartMargin
        curOutWCells_MarginTop = outTopMargin

        val expectedPanelWidth = (outputPanel.width - outStartMargin* 2)
        outSpaceBetCells = expectedPanelWidth / 41
        outCellWidth = expectedPanelWidth * MAX_CPR / 41

//         * 2 / 3 <=> / 1.5
//            I want its width synchronize with Input Cell
        outCellHeight = inCellHeight
//         Try to layout cells in the middle of panel
//         Because there is calcError when convert from float to integer
//         Sometimes there is a little space at the right of panel
        val actualPanelWidth = (outCellWidth * MAX_CPR + outSpaceBetCells * (MAX_CPR - 1))
        val calcError = expectedPanelWidth - actualPanelWidth
        val addtnalCellWidth = calcError / MAX_CPR
        outAddtnalMarginStart = (expectedPanelWidth - (actualPanelWidth + addtnalCellWidth * MAX_CPR))/2
        outCellWidth += addtnalCellWidth
    }


    // ======================== CHARACTERS FUNCTION =================================


    private fun createChar_InputCells (ansElements: Array<String>) { dB.apply {
        for ((position, element) in ansElements.withIndex()) {
            createChar_InputCell(element, position, true)
        }
    }}

    private fun createChar_InputCell (cellContent: String, position : Int, isFirstTimesCreated : Boolean) {
        val inCharCell = Cell(this, INPUT_CELL)
        inCharCell.text = cellContent

        // Create Layout params
        val constraint = ConstraintLayout.LayoutParams(InCellWidth, inCellHeight)
        constraint.topToTop = inputPanel.id
        constraint.startToStart = inputPanel.id
        val positionInRow = position % MAX_CPR
        constraint.marginStart = (positionInRow) * (InCellWidth + spaceBetInputCells) + InStartMargin
        constraint.topMargin = (position / MAX_CPR) * (inCellHeight + spaceBetInputCells) + InTopMargin
        if ((positionInRow == 0) or (positionInRow == MAX_CPR - 1)) {
            constraint.marginStart += InAddtnalMarginStart
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
        val cell = Cell(this@ReviewFlashcardEasyActivity, OUTPUT_CELL)
        cell.text = cellContent
        val constraint = ConstraintLayout.LayoutParams(outCellWidth, outCellHeight)
        constraint.topToTop = outputPanel.id
        constraint.leftToLeft = outputPanel.id
        constraint.marginStart = (outCellWidth +  outSpaceBetCells) * (curOutChCellPos % MAX_CPR) + outStartMargin
        constraint.topMargin = vwgrpOutputCells.paddingTop + (curOutChCellRowOrder) * (outCellHeight + outSpaceBetCells)
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
            curOutChCellRowOrder = curOutChCellPos / MAX_CPR
        }

        outputCellList.add(cell)

        // Run Animation
        cell.performCreateAnimate(DIRECTION_UP).setStartDelay(CELL_MOVING_INVISIBLY_TIME).setLiteListener (onEnd = {
            setAnswer(dB.txtInputAnswer.text.toString() + cellContent)
        })

        // Process row and position status
        curOutChCellPos++
        curOutChCellRowOrder = curOutChCellPos / MAX_CPR
    }}

    private fun setAnswer (answer : String) {
        dB.txtInputAnswer.text = answer
    }

    private fun recalculatePanelsHeight (elementCount : Int) {
        dB.apply {
            var rowCount = elementCount / MAX_CPR
            val notFullCellInLastRow = (elementCount % MAX_CPR != 0)
            if (notFullCellInLastRow) rowCount ++

            val expectedInputGroupHeight : Int
            if (rowCount > MIN_INPUT_ROW_COUNT) {
                expectedInputGroupHeight = rowCount * (inCellHeight + spaceBetInputCells) + InTopMargin * 2
            } else {
                expectedInputGroupHeight = MIN_INPUT_ROW_COUNT * (inCellHeight + spaceBetInputCells) + InTopMargin * 2
            }
            val expectedOutputGroupHeight = rowCount * (outCellHeight + outSpaceBetCells) + outTopMargin * 2

            if (vwgrpInputCells.layoutParams.height != expectedInputGroupHeight) {
                vwgrpInputCells.layoutParams.height = expectedInputGroupHeight
                vwgrpInputCells.requestLayout()
            }

            if (vwgrpOutputCells.layoutParams.height != expectedOutputGroupHeight) {
                vwgrpOutputCells.layoutParams.height = expectedOutputGroupHeight
                vwgrpOutputCells.requestLayout()
            }

        }
    }


// ======================== WORDS FUNCTION =================================


    private fun createWord_InputCells (ansElements: Array<String>) {
        for ((position, element) in ansElements.withIndex()) {
            // Create the cell
            val inputCell = Cell(this, cellType = INPUT_CELL)
            inputCell.text = element
            val constraint = ConstraintLayout.LayoutParams(WRAP_CONTENT, inCellHeight)
            constraint.topToTop = inputPanel.id
            constraint.leftToLeft  = inputPanel.id
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
                    layoutInput_WordCellsOnScreen()
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
                    createWord_OutputCell(cellContent, cellWidth, cellHeight, marginStart, marginTop)
                    cellParent.removeView(inputCell)
                })
            inputCellList.remove(inputCell)
        }

        // Run animate
        inputCell.performCreateAnimate(DIRECTION_DOWN).startDelay = CELL_MOVING_INVISIBLY_TIME
    }

    private fun layoutInput_WordCellsOnScreen () {
        for (cell in inputCellList) {
            val nextTotalCellsWidth_InLastRow = curInputWCells_MarginStart + cell.width

            val goToNextRow = nextTotalCellsWidth_InLastRow > inputPanel.width
            var cellMarginStart : Int
            val cellMarginTop : Int

            if (goToNextRow) {
                cellMarginStart = InStartMargin
                // Reset Start Margin to the left of Input Panel
                curInputWCells_MarginStart = InStartMargin + cell.width + spaceBetInputCells
                curInputWCells_MarginTop += (cell.height + spaceBetInputCells)
            } else {
                cellMarginStart = curInputWCells_MarginStart
                curInputWCells_MarginStart = nextTotalCellsWidth_InLastRow + spaceBetInputCells
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
            curOutWCells_MarginStart = outStartMargin + cellWidth + outSpaceBetCells
            curOutWCells_MarginTop += (cellHeight + outSpaceBetCells)
        } else {
            cellMarginStart = curOutWCells_MarginStart
            curOutWCells_MarginStart = nextTotalCellsWidth_InLastRow + outSpaceBetCells
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
                curOutWCells_MarginStart -= (cellWidth + outSpaceBetCells)
                val noCellInLastRow = curOutWCells_MarginStart == outStartMargin
                if (noCellInLastRow) {
                    if (curOutWCells_RowOrder > 0) {
                        val lastCell = outputCellList.last()
                        curOutWCells_MarginStart = lastCell.marginStart + lastCell.width + outSpaceBetCells
                        curOutWCells_RowOrder -= 1
                    }
                } else {
                    val lastCell = outputCellList.last()
                    curOutWCells_MarginStart = lastCell.marginStart + lastCell.width + outSpaceBetCells
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
        for (pos in pickedPos..outputCellList.size - 1) {
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

        curInputWCells_MarginTop  = InTopMargin
        curInputWCells_MarginStart = InStartMargin

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

    private fun disableClickingAllInputCells () {
        for (cell in outputCellList) {
            cell.isClickable = false
        }
    }

    private fun getRequestedFlashcardSet () : FlashcardSet {
        return intent.extras!!.getSerializable(FLASHCARD_SET_KEY) as FlashcardSet
    }

    private fun highlightAnswerInExample (example : String, answer : String, colorCode : String) : CharSequence {
        val answerInExample = findTextFormInAnother(answer, example)
        return Html.fromHtml(example.replace(answerInExample, "<font color='$colorCode'>$answerInExample</font>"))
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

    fun sendHardCardListToEndActivity () {
        val missedCardList = viewModel.getMissedCardsList()
        val flashcardSet = viewModel.getFlashcardSet()
        UseFlashcardDoneActivity.requestFinishUsingFlashcard(
            this, flashcardSet, missedCardList,
            UseFlashcardDoneActivity.FlashcardSendableActivity.REVIEW_FLASHCARD_EASY_ACTIVITY.code
        )
    }

    // ==================================INJECTED METHODS============================================

    @Inject
    fun initNextCardAnimations (
        @Named("MoveRight120%AndFadeOut") moveRightAndFadeOut : Animator,
        @Named("Float") vwgrpNewSubAppear : Animator
    ) { dB.apply {
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




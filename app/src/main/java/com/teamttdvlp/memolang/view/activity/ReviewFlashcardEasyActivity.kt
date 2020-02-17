package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Html
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
import com.teamttdvlp.memolang.databinding.ActivityReviewFlashcardEasyBinding
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.findTextFormInAnother
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardEasyView
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardEasyView.Companion.CHARACTER_LIST
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardEasyView.Companion.WORD_LIST
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
    ReviewFlashcardEasyView {

    private val NEXT_CARD_ANIMS_DURATION = 400L

    var nextCardAnimtrSet: AnimatorSet = AnimatorSet()

    var resetHintAnimtrSet: AnimatorSet = AnimatorSet()

    lateinit var oldSubjectDisaprAnim: Animator

    // MAX_CELL_PER_ROW
    private val MAX_CPR = 6

    private val APP_RED = "#FMAX_CELL_PER_ROW554MAX_CELL_PER_ROW"

    private val LITTLE_DARK_GREEN = "#00A90B"

    private var currentOutCellPosition = 0

    private var curOutRowOrder = 0


    private var InCellWidth = 0

    private var InCellHeight = 0

    private var SpaceBetInputCells = 0

    private var InTopMargin = 0

    private var InStartMargin = 0

    private var InAddtnalMarginStart = 0

    private lateinit var inputPanel : View

    private lateinit var cellParent : ConstraintLayout

    private var OutCellWidth = 0

    private var OutCellHeight = 0

    private var OutSpaceBetCells = 0

    private var OutTopMargin= 0

    private var OutStartMargin= 0

    private var OutAddtnalMarginStart = 0

    private lateinit var outputPanel : View

    private var outputCellList = ArrayList<Cell>()

    private var inputCellList = ArrayList<Cell>()

    private val CELL_MOVING_INVISIBLY_TIME = 0L

    private val INTERVAL_BETWEEN_TWO_DISAPPEAR = 50L

    override fun getLayoutId(): Int = R.layout.activity_review_flashcard_easy

    override fun takeViewModel(): ReviewFlashcardEasyViewModel {
        return getActivityViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)

        inputPanel = dB.vwgrpInputCells
        outputPanel = dB.vwgrpOutputCells
        cellParent = dB.root as ConstraintLayout

        dB.vwgrpInputCells.doOnPreDraw {
            dB.vwgrpOutputCells.doOnPreDraw {
                calculatelInputCellsDimens()
                calculateOutputCellsDimens()

                viewModel.setUp(TestEverything.mockFlashCardList())
            }
        }
    }

    override fun addViewEvents() { dB.apply {
        txtInputAnswer.addTextChangedListener(onTextChanged = { text,_,_,_ ->
            viewModel.checkAnswer(text.toString().trim())
        })

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

    override fun endReviewing() {

    }

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

    private fun showAnswerAnimations (userIsTrue : Boolean, onEnd: () -> Unit = {}) { dB.apply {
        val isUsingAppearOnScreen = (txtTestSubjectUsing.alpha == 1f)
        if (isUsingAppearOnScreen) {
            if (userIsTrue) {
                txtUsingPositiveHighlight.animate().setDuration(200).alpha(1f).setLiteListener (onEnd = {
                    onEnd()
                })
            } else {
                txtUsingNegativeHighlight.animate().setDuration(200).alpha(1f).setLiteListener (onEnd = {
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
            txtTextAnswer.animate().setDuration(200).setInterpolator(FastOutSlowInInterpolator()).alpha(1f)
        }
    }}

    private fun setAnswer (answer : String) {
        dB.txtInputAnswer.setText(answer)
    }

    // ======================== VIEW OVERRIDE FUNCTION =================================


    override fun showTestSubjectOnScreen( testSubject: Flashcard,
                                          useUsingForTestSubject: Boolean,
                                          ansElements: Array<String>,
                                          listType : Int) { dB.apply {
        val answer = testSubject.text
        txtTextAnswer.text = answer
        if (useUsingForTestSubject) {
            setUpUsingTestSubject(testSubject)
        } else {
            setUpTranslationTestSubject(testSubject)
        }

        if (listType == CHARACTER_LIST) {
            createChar_InputCells(ansElements)
        } else if (listType == WORD_LIST) {
            createWord_InputCells(ansElements)
        }
        recalculatePanelsHeight(ansElements.size)
    }}

    override fun performCorrectAnsElemtsOrderAnims() {
        quickToast("Correct")
    }

    override fun performIncorrectAnsElemtsOrderAnims() {
        quickToast("Incorrect")
    }

    override fun performPassBehaviours() {
        showAnswerAnimations(true, onEnd = {
            performClearAllOnScreenCells ( onEnd = {
                showNextCardAnims()
            })
        })
    }

    override fun showNextCardAnims () {
        oldSubjectDisaprAnim.start()
        resetHintAnimtrSet.start()
        setAnswer("")
    }

    override fun performNotPassBehaviours() {
//        showAnswerAnimations(false) {
//            quickLog("Not pass behaviours")
//        }
        performClearAllOnScreenCells() {
            viewModel.nextCard()
        }
        quickToast("Not pass")
    }


    // ======================== CALCULATE FUNCTION =================================


    private fun calculatelInputCellsDimens () {
        InStartMargin = inputPanel.paddingStart
        InTopMargin = inputPanel.paddingTop

        curInput_WordCells_MarginStart = InStartMargin
        curInput_WordCells_MarginTop = InTopMargin

        val expectedPanelWidth = (inputPanel.width - InStartMargin * 2)
        SpaceBetInputCells = expectedPanelWidth / 41
        InCellWidth = expectedPanelWidth * MAX_CPR / 41
//         * 2 / 3 <=> / 1.5
        InCellHeight = InCellWidth * 2 / 3
//         Try to layout cells in the middle of panel
//         Because there is calcError when convert from float to integer
//         Sometimes there is a little space at the right of panel
        val actualPanelWidth = (InCellWidth * MAX_CPR + SpaceBetInputCells * (MAX_CPR - 1))
        val calcError = expectedPanelWidth - actualPanelWidth
        val addtnalCellWidth = calcError / MAX_CPR
        InAddtnalMarginStart = (expectedPanelWidth - (actualPanelWidth + addtnalCellWidth * MAX_CPR))/2
        InCellWidth += addtnalCellWidth
    }

    private fun calculateOutputCellsDimens () {
        OutStartMargin= outputPanel.paddingStart
        OutTopMargin= outputPanel.paddingTop
        curOutWCells_MarginStart = OutStartMargin
        curOutWCells_MarginTop = OutTopMargin

        val expectedPanelWidth = (outputPanel.width - OutStartMargin* 2)
        OutSpaceBetCells = expectedPanelWidth / 41
        OutCellWidth = expectedPanelWidth * MAX_CPR / 41

//         * 2 / 3 <=> / 1.5
//            I want its width synchronize with Input Cell
        OutCellHeight = InCellHeight
//         Try to layout cells in the middle of panel
//         Because there is calcError when convert from float to integer
//         Sometimes there is a little space at the right of panel
        val actualPanelWidth = (OutCellWidth * MAX_CPR + OutSpaceBetCells * (MAX_CPR - 1))
        val calcError = expectedPanelWidth - actualPanelWidth
        val addtnalCellWidth = calcError / MAX_CPR
        OutAddtnalMarginStart = (expectedPanelWidth - (actualPanelWidth + addtnalCellWidth * MAX_CPR))/2
        OutCellWidth += addtnalCellWidth
    }


    // ======================== CHARACTERS FUNCTION =================================


    private fun createChar_InputCells (ansElements: Array<String>) { dB.apply {
        for ((position, element) in ansElements.withIndex()) {
            createChar_InputCell(element, position, true)
        }
    }}

    private fun createChar_InputCell (cellContent: String, position : Int, isFirstTimesCreated : Boolean) {
        val cell = Cell(this, INPUT_CELL)
        cell.setText(cellContent)

        // Create Layout params
        val constraint = ConstraintLayout.LayoutParams(InCellWidth, InCellHeight)
        constraint.topToTop = inputPanel.id
        constraint.startToStart = inputPanel.id
        val positionInRow = position % MAX_CPR
        constraint.marginStart = (positionInRow) * (InCellWidth + SpaceBetInputCells) + InStartMargin
        constraint.topMargin = (position / MAX_CPR) * (InCellHeight + SpaceBetInputCells) + InTopMargin
        if ((positionInRow == 0) or (positionInRow == MAX_CPR - 1)) {
            constraint.marginStart += InAddtnalMarginStart
        }
        cell.layoutParams = constraint
        cellParent.addView(cell)

        inputCellList.add(cell)
        // SetListener
        cell.setOnClickListener {
            cell.isClickable = false
            cell.performDestroyAnimate(DIRECTION_UP)
                .setLiteListener (onEnd = {
                    createChar_OutputCell(cellContent, position)
                    cellParent.removeView(cell)
                })
        }

        // Run isFirstTimesCreated
        if (isFirstTimesCreated) {
            cell.performCreateAnimate(DIRECTION_UP).setStartDelay(position * INTERVAL_BETWEEN_TWO_DISAPPEAR)
        } else {
            cell.performCreateAnimate(DIRECTION_DOWN).setStartDelay(CELL_MOVING_INVISIBLY_TIME)
        }

    }

    private fun createChar_OutputCell (cellContent : String, inputCellPos : Int) { dB.apply {
        val cell = Cell(this@ReviewFlashcardEasyActivity, OUTPUT_CELL)
        cell.setText(cellContent)
        val constraint = ConstraintLayout.LayoutParams(OutCellWidth, OutCellHeight)
        constraint.topToTop = outputPanel.id
        constraint.leftToLeft = outputPanel.id
        constraint.marginStart = (OutCellWidth +  OutSpaceBetCells) * (currentOutCellPosition % MAX_CPR) + OutStartMargin
        constraint.topMargin = vwgrpOutputCells.paddingTop + (curOutRowOrder) * (OutCellHeight + OutSpaceBetCells)
        cell.layoutParams = constraint
        cellParent.addView(cell)

        val currentOutputCellPosition = currentOutCellPosition

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
            currentOutCellPosition -= (outputCellList.size - currentOutputCellPosition)
            restoreOtherRightSideOutputCells(currentOutputCellPosition)
            removeOtherRightSideOutputCells(currentOutputCellPosition)
            curOutRowOrder = currentOutCellPosition / MAX_CPR
        }

        outputCellList.add(cell)

        // Run Animation
        cell.performCreateAnimate(DIRECTION_UP).setStartDelay(CELL_MOVING_INVISIBLY_TIME).setLiteListener (onEnd = {
            setAnswer(dB.txtInputAnswer.text.toString() + cellContent)
        })

        // Process row and position status
        currentOutCellPosition++
        curOutRowOrder = currentOutCellPosition / MAX_CPR
    }}

    private fun recalculatePanelsHeight (elementCount : Int) {
        dB.apply {
            var rowCount = elementCount / MAX_CPR
            val notFullCellInLastRow = (elementCount % MAX_CPR != 0)
            if (notFullCellInLastRow) rowCount ++
            val expectedInputGroupHeight = 4 * (InCellHeight + SpaceBetInputCells) + InTopMargin * 2
            val expectedOutputGroupHeight = rowCount * (OutCellHeight + OutSpaceBetCells) + OutTopMargin * 2

            vwgrpInputCells.layoutParams.height = expectedInputGroupHeight
            vwgrpInputCells.requestLayout()

            vwgrpOutputCells.layoutParams.height = expectedOutputGroupHeight
            vwgrpOutputCells.requestLayout()
        }
    }


// ======================== WORDS FUNCTION =================================


    private fun createWord_InputCells (ansElements: Array<String>) {
        for ((position, element) in ansElements.withIndex()) {
            // Create the cell
            val inputCell = Cell(this, INPUT_CELL)
            inputCell.setText(element)
            val constraint = ConstraintLayout.LayoutParams(WRAP_CONTENT, InCellHeight)
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
                    inputCell.performDestroyAnimate(DIRECTION_UP)
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

            inputCell.performCreateAnimate(DIRECTION_UP).setStartDelay(position * INTERVAL_BETWEEN_TWO_DISAPPEAR)
        }
    }

    private fun createWord_InputCell (cellContent : String, cellWidth : Int, cellHeight : Int, marginStart : Int, marginTop : Int) {
        val inputCell = Cell(this, INPUT_CELL)
        inputCell.setText(cellContent)
        val constraint = ConstraintLayout.LayoutParams (cellWidth, cellHeight)
        constraint.topToTop = inputPanel.id
        constraint.startToStart = inputPanel.id
        constraint.topMargin = marginTop
        constraint.marginStart = marginStart
        inputCell.layoutParams = constraint
        cellParent.addView(inputCell)

        inputCell.setOnClickListener {
            inputCell.isClickable = false
            inputCell.performDestroyAnimate(DIRECTION_UP)
                .setLiteListener (onEnd = {
                    createWord_OutputCell(cellContent, cellWidth, cellHeight, marginStart, marginTop)
                    cellParent.removeView(inputCell)
                })
            dB.txtInputAnswer.text = dB.txtInputAnswer.text.toString() + cellContent + " "
        }

        // Run animate
        inputCell.performCreateAnimate(DIRECTION_DOWN).setStartDelay(CELL_MOVING_INVISIBLY_TIME)
    }


    var curInput_WordCells_MarginStart = 0

    var curInput_WordCells_MarginTop = 0

    private fun layoutInput_WordCellsOnScreen () {
        for ((i, cell) in inputCellList.withIndex()){
            val nextTotalCellsWidth_InLastRow = curInput_WordCells_MarginStart + cell.width

            val goToNextRow = nextTotalCellsWidth_InLastRow > inputPanel.width
            var cellMarginStart : Int
            val cellMarginTop : Int

            if (goToNextRow) {
                cellMarginStart = InStartMargin
                // Reset Start Margin to the left of Input Panel
                curInput_WordCells_MarginStart = InStartMargin + cell.width + SpaceBetInputCells
                curInput_WordCells_MarginTop += (cell.height + SpaceBetInputCells)
            } else {
                cellMarginStart = curInput_WordCells_MarginStart
                curInput_WordCells_MarginStart = nextTotalCellsWidth_InLastRow + SpaceBetInputCells
            }

            cellMarginTop = curInput_WordCells_MarginTop
            val newLayoutParams = ConstraintLayout.LayoutParams(cell.layoutParams)
            newLayoutParams.marginStart = cellMarginStart
            newLayoutParams.topMargin = cellMarginTop
            newLayoutParams.topToTop = inputPanel.id
            newLayoutParams.startToStart= inputPanel.id
            cell.layoutParams = newLayoutParams
            cell.requestLayout()
        }
    }


    // Current output panel word cell
    var curOutWCells_MarginStart = 0

    var curOutWCells_MarginTop = 0

    var curOutWCells_RowOrder = 0

    private fun createWord_OutputCell (cellContent: String, cellWidth : Int, cellHeight : Int, inputCellMarginStart : Int, inputCellMarginTop : Int) {
        val outputCell = Cell(this, OUTPUT_CELL)
        val constraint = ConstraintLayout.LayoutParams(cellWidth, cellHeight)

        val nextTotalCellsWidth_InLastRow = curOutWCells_MarginStart + cellWidth
        val cellNotFitInRow = (nextTotalCellsWidth_InLastRow > outputPanel.width)

        val cellMarginStart : Int
        // Go to next row
        if (cellNotFitInRow) {
            cellMarginStart = OutStartMargin
            // Reset Start Margin to the left of Input Panel
            curOutWCells_MarginStart = OutStartMargin + cellWidth + OutSpaceBetCells
            curOutWCells_MarginTop += (cellHeight + OutSpaceBetCells)
        } else {
            cellMarginStart = curOutWCells_MarginStart
            curOutWCells_MarginStart = nextTotalCellsWidth_InLastRow + OutSpaceBetCells
        }

        val cellMarginTop = curOutWCells_MarginTop

        constraint.marginStart = cellMarginStart
        constraint.topMargin = cellMarginTop
        constraint.topToTop = outputPanel.id
        constraint.startToStart= outputPanel.id

        outputCell.layoutParams = constraint
        outputCell.setText(cellContent)

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
                curOutWCells_MarginStart -= (cellWidth + OutSpaceBetCells)
                val noCellInLastRow = curOutWCells_MarginStart == OutStartMargin
                if (noCellInLastRow) {
                    if (curOutWCells_RowOrder > 0) {
                        val lastCell = outputCellList.last()
                        curOutWCells_MarginStart = lastCell.marginStart + lastCell.width + OutSpaceBetCells
                        curOutWCells_RowOrder -= 1
                    }
                } else {
                    val lastCell = outputCellList.last()
                    curOutWCells_MarginStart = lastCell.marginStart + lastCell.width + OutSpaceBetCells
                    curOutWCells_MarginTop = lastCell.marginTop
                }
            } else {
                curOutWCells_MarginStart = OutStartMargin
                curOutWCells_MarginTop = OutTopMargin
            }

        }

        outputCellList.add(outputCell)
        cellParent.addView(outputCell)

        // Run animations
        outputCell.performCreateAnimate(DIRECTION_UP).setStartDelay(CELL_MOVING_INVISIBLY_TIME).setLiteListener (onEnd = {
            setAnswer(dB.txtInputAnswer.text.toString() + cellContent + " ")
        })
    }

    fun restoreOtherRightSideOutputCells (pickedPos : Int) {
        for (pos in pickedPos + 1..outputCellList.size - 1) {
            val loopTimes = (pos - (pickedPos + 1)) + 1
            outputCellList.get(pos).performOnRestore(loopTimes * INTERVAL_BETWEEN_TWO_DISAPPEAR)
        }
    }

    fun removeOtherRightSideOutputCells (pickedPos : Int) {
        for (pos in pickedPos..outputCellList.size - 1) {
            outputCellList.removeAt(outputCellList.size - 1)
        }
    }

    fun performClearAllOnScreenCells(onEnd : () -> Unit = {}) {
        val outListLongerThanInList = outputCellList.size >= inputCellList.size
        val outputCellListSize = outputCellList.size
        val inputCellListSize = inputCellList.size
        for ((i, cell) in outputCellList.withIndex()) {
            cell.performDestroyAnimate(DIRECTION_UP).setStartDelay(i * 50L).setLiteListener (onEnd = {
                if (outListLongerThanInList && (i == outputCellListSize - 1)) {
                    onEnd()
                }
                cellParent.removeView(cell)
            })
        }

        for ((i, cell) in inputCellList.withIndex()) {
            cell.performDestroyAnimate(DIRECTION_UP).setStartDelay(i * 50L).setLiteListener (onEnd = {
                if (not(outListLongerThanInList) && (i == inputCellListSize - 1)) {
                    onEnd()
                }
                cellParent.removeView(cell)
            })

        }

        inputCellList.clear()
        outputCellList.clear()
        currentOutCellPosition = 0
        curOutRowOrder = 0
    }

    private fun getFlashcardList () : ArrayList<Flashcard> {
//        val flashcardList = intent.extras!!.getSerializable(FLASHCARD_LIST_KEY) as ArrayList<Flashcard>
//        return flashcardList
        return TestEverything.mockFlashCardList()
    }

    private fun highlightAnswerInUsing (using : String, answer : String, colorCode : String) : CharSequence {
        val answerInUsing = findTextFormInAnother(answer, using)
        //                                                                                                              LITLE DARK GREEN
        return Html.fromHtml(using.replace(answerInUsing, "(<font color='$colorCode'>$answerInUsing</font>)"))
    }

    private fun processHideAnswerInUsing (using : String, answer : String) : CharSequence {
        val answerInUsing = findTextFormInAnother(answer, using)
        return Html.fromHtml(using.replace(answerInUsing, "(<font color='#ffffff'>$answerInUsing</font>)"))
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
}




package com.teamttdvlp.memolang.view.activity

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityReviewFlashcardEasyBinding
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.findTextFormInAnother
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardEasyView
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.customview.CharacterCell
import com.teamttdvlp.memolang.view.customview.CharacterCell.Companion.DIRECTION_DOWN
import com.teamttdvlp.memolang.view.customview.CharacterCell.Companion.DIRECTION_UP
import com.teamttdvlp.memolang.view.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.helper.setLiteListener
import com.teamttdvlp.memolang.viewmodel.ReviewFlashcardEasyViewModel

class ReviewFlashcardEasyActivity : BaseActivity<ActivityReviewFlashcardEasyBinding, ReviewFlashcardEasyViewModel>(),
    ReviewFlashcardEasyView {

    // MAX_CELL_PER_ROW
    private val MAX_CPR = 6

    private val APP_RED = "#FMAX_CELL_PER_ROW554MAX_CELL_PER_ROW"

    private val LITTLE_DARK_GREEN = "#00A90B"

    private var currentOutCellPosition = 0

    private var curOutRowOrder = 0


    private var ICellWidth = 0

    private var ICellHeight = 0

    private var ISpaceBetCells = 0

    private var IVerticalMargin = 0

    private var IHorizontalMargin = 0

    private var IAddtnalMarginStart = 0

    private lateinit var inputPanel : View

    private lateinit var cellParent : ConstraintLayout

    private var OCellWidth = 0

    private var OCellHeight = 0

    private var OSpaceBetCells = 0

    private var OVerticalMargin= 0

    private var OHorizontalMargin= 0

    private var OAddtnalMarginStart = 0

    private lateinit var outputPanel : View

    private var outputCellList = ArrayList<CharacterCell>()

    private val CELL_MOVING_INVISIBLY_TIME = 0L

    private val INTERVAL_BETWEEN_TWO_DISAPPEAR = 50L

    override fun getLayoutId(): Int = R.layout.activity_review_flashcard_easy

    override fun takeViewModel(): ReviewFlashcardEasyViewModel {
        return getActivityViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)

        val mock = "Abacterial"
        inputPanel = dB.vwgrpInputCells
        outputPanel = dB.vwgrpOutputCells
        cellParent = dB.root as ConstraintLayout
        calculatelInputCellsDimens()
        calculateIOutputCellsDimens()
        createInputCells(Array<String>(mock.length) {
            mock[it].toString()
        })
    }

    override fun showTestSubjectOnScreen( testSubject: Flashcard,
                                          ansElements: Array<String>,
                                          useUsingForTestSubject: Boolean  ) { dB.apply {
        val answer = testSubject.text
        txtTextAnswer.text = answer
        if (useUsingForTestSubject) {
            setUpUsingTestSubject(testSubject)
        } else {
            setUpTranslationTestSubject(testSubject)
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

    private fun showPassAnimation () { dB.apply {
        val isUsingAppearOnScreen = (txtTestSubjectUsing.alpha == 1f)
        if (isUsingAppearOnScreen) {
            txtUsingPositiveHighlight.animate().setDuration(200).alpha(1f)
        } else {
            txtTranslation.animate().setDuration(200).setInterpolator(FastOutSlowInInterpolator())
                .translationY(100f).alpha(0f)
                .setLiteListener (onEnd = {
                    txtTranslation.translationY = 0f
                })
            txtTextAnswer.animate().setDuration(200).setInterpolator(FastOutSlowInInterpolator()).alpha(1f)
        }
    }}

    private fun showAnswerAnimations (userIsTrue : Boolean) { dB.apply {
        val isUsingAppearOnScreen = (txtTestSubjectUsing.alpha == 1f)
        if (isUsingAppearOnScreen) {
            if (userIsTrue) {
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

    override fun performCorrectAnsElemtsOrderAnims() {

    }

    override fun performIncorrectAnsElemtsOrderAnims() {

    }

    override fun performPassAnims() {

    }

    override fun performNotPassAnim() {

    }

    private fun calculatelInputCellsDimens () {
        inputPanel.doOnPreDraw {
            IHorizontalMargin = inputPanel.paddingStart
            IVerticalMargin = inputPanel.paddingTop
            val expectedPanelWidth = (inputPanel.width - IHorizontalMargin * 2)
            ISpaceBetCells = expectedPanelWidth / 41
            ICellWidth = expectedPanelWidth * MAX_CPR / 41
//         * 2 / 3 <=> / 1.5
            ICellHeight = ICellWidth * 2 / 3
//         Try to layout cells in the middle of panel
//         Because there is calcError when convert from float to integer
//         Sometimes there is a little space at the right of panel
            val actualPanelWidth = (ICellWidth * MAX_CPR + ISpaceBetCells * (MAX_CPR - 1))
            val calcError = expectedPanelWidth - actualPanelWidth
            val addtnalCellWidth = calcError / MAX_CPR
            IAddtnalMarginStart = (expectedPanelWidth - (actualPanelWidth + addtnalCellWidth * MAX_CPR))/2
            ICellWidth += addtnalCellWidth
        }
    }

    private fun calculateIOutputCellsDimens () { dB.apply {
        vwgrpInputCells.doOnPreDraw {
            OHorizontalMargin= outputPanel.paddingStart
            OVerticalMargin= outputPanel.paddingTop
            val expectedPanelWidth = (outputPanel.width - OHorizontalMargin* 2)
            OSpaceBetCells = expectedPanelWidth / 41
            OCellWidth = expectedPanelWidth * MAX_CPR / 41

//         * 2 / 3 <=> / 1.5
//            I want its width synchronize with Input Cell
            OCellHeight = ICellHeight
//         Try to layout cells in the middle of panel
//         Because there is calcError when convert from float to integer
//         Sometimes there is a little space at the right of panel
            val actualPanelWidth = (OCellWidth * MAX_CPR + OSpaceBetCells * (MAX_CPR - 1))
            val calcError = expectedPanelWidth - actualPanelWidth
            val addtnalCellWidth = calcError / MAX_CPR
            OAddtnalMarginStart = (expectedPanelWidth - (actualPanelWidth + addtnalCellWidth * MAX_CPR))/2
            OCellWidth += addtnalCellWidth
        }
    }}

    private fun createInputCells (ansElements: Array<String>) { dB.apply {
        vwgrpInputCells.doOnPreDraw {
            for ((position, element) in ansElements.withIndex()) {
                createInputCell(element, position)
            }
        }
    }}

    private fun createOutputCell (cellContent : String, inputCellPos : Int) { dB.apply {
        val cell = CharacterCell(this@ReviewFlashcardEasyActivity)
        cell.setText(cellContent)
        val constraint = ConstraintLayout.LayoutParams(OCellWidth, OCellHeight)
        constraint.topToTop = outputPanel.id
        constraint.leftToLeft = outputPanel.id
        constraint.marginStart = (OCellWidth +  OSpaceBetCells) * (currentOutCellPosition % MAX_CPR) + OHorizontalMargin
        constraint.topMargin = vwgrpOutputCells.paddingTop + (curOutRowOrder) * (OCellHeight + OSpaceBetCells)
        cell.layoutParams = constraint
        cellParent.addView(cell)

        val currentOutputCellPosition = currentOutCellPosition

        cell.setOnRestore { delayTime ->
                cell.performDestroyAnimate(DIRECTION_DOWN)
                .setStartDelay(delayTime)
                .setLiteListener(onEnd =  {
                createInputCell(cellContent, inputCellPos, true)
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
            curOutRowOrder = currentOutCellPosition / MAX_CPR
        }

        outputCellList.add(cell)

        // Run Animation
        cell.performCreateAnimate(DIRECTION_UP).setStartDelay(CELL_MOVING_INVISIBLY_TIME)
        // Process row and position status
        currentOutCellPosition++
        curOutRowOrder = currentOutCellPosition / MAX_CPR
    }}

    private fun createInputCell (cellContent: String, position : Int, animate : Boolean = false) {
        val cell = CharacterCell(this)
        cell.setText(cellContent)

        // Create Layout params
        val constraint = ConstraintLayout.LayoutParams(ICellWidth, ICellHeight)
        constraint.topToTop = inputPanel.id
        constraint.startToStart = inputPanel.id
        val positionInRow = position % MAX_CPR
        constraint.marginStart = (positionInRow) * (ICellWidth + ISpaceBetCells) + IHorizontalMargin
        constraint.topMargin = (position / MAX_CPR) * (ICellHeight + ISpaceBetCells) + IVerticalMargin
        if ((positionInRow == 0) or (positionInRow == MAX_CPR - 1)) {
            constraint.marginStart += IAddtnalMarginStart
        }
        cell.layoutParams = constraint
        cellParent.addView(cell)

        // SetListener
        cell.setOnClickListener {
            cell.isClickable = false
            cell.performDestroyAnimate(DIRECTION_UP)
                                   .setLiteListener (onEnd = {
                                       createOutputCell(cellContent, position)
                                       cellParent.removeView(cell)
                                   })
            dB.txtInputAnswer.text = dB.txtInputAnswer.text.toString() + cellContent
        }

        // Run animate
        if (animate) {
            cell.performCreateAnimate(DIRECTION_DOWN).setStartDelay(CELL_MOVING_INVISIBLY_TIME)
        }

    }

    fun restoreOtherRightSideOutputCells (pickedPos : Int) {
        for (pos in pickedPos + 1..outputCellList.size - 1) {
            val loopTimes = (pos - (pickedPos + 1)) + 1
            outputCellList.get(pos).performOnRestore(loopTimes * INTERVAL_BETWEEN_TWO_DISAPPEAR)
        }


        for (pos in pickedPos..outputCellList.size - 1) {
            outputCellList.removeAt(outputCellList.size - 1)
        }

    }

    fun clearAllInputCells() {

    }

}




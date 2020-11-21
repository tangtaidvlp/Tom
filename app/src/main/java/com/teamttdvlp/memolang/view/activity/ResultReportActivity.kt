package com.teamttdvlp.memolang.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.databinding.ActivityUseFlashcardDoneBinding
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardDoneView
import com.teamttdvlp.memolang.view.adapter.RCVRecent_Search_FlashcardAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.customview.NormalOutExtraSlowIn
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.UseFlashcardDoneViewModel
import kotlin.math.roundToInt


const val FORGOTTEN_FLASHCARDS_LIST = "fgl"
const val FULL_FLASHCARD_SET = "fullfcl"
const val SENDER_CODE = "sdrc"

class ResultReportActivity :
    BaseActivity<ActivityUseFlashcardDoneBinding, UseFlashcardDoneViewModel>(),
    UseFlashcardDoneView {

    enum class FlashcardSendableActivity(val code: Int) {
        USE_FLASHCARD_ACTIVITY(1),
        REVIEW_FLASHCARD_ACTIVITY(2),
        REVIEW_FLASHCARD_EASY_ACTIVITY(3);

        companion object {
            fun getTypeByCode(code: Int): FlashcardSendableActivity {
                return when (code) {
                    USE_FLASHCARD_ACTIVITY.code -> USE_FLASHCARD_ACTIVITY
                    REVIEW_FLASHCARD_ACTIVITY.code -> REVIEW_FLASHCARD_ACTIVITY
                    REVIEW_FLASHCARD_EASY_ACTIVITY.code -> REVIEW_FLASHCARD_EASY_ACTIVITY
                    else -> throw Exception("Unknown code. Your activity must implement FlashcardSendableActivity interface")
                }
            }
        }
    }

    lateinit var rcvForgottenCardAdapter: RCVRecent_Search_FlashcardAdapter

    private lateinit var activitySender: FlashcardSendableActivity

    companion object {
        fun requestFinishUsingFlashcard(
            requestContext: Context,
            deck: Deck,
            forgottenCardList: ArrayList<Flashcard>,
            senderCode: Int
        ) {
            val intent = Intent(requestContext, ResultReportActivity::class.java)
            intent.putExtra(FORGOTTEN_FLASHCARDS_LIST, forgottenCardList)
            intent.putExtra(FULL_FLASHCARD_SET, deck)
            intent.putExtra(SENDER_CODE, senderCode)
            requestContext.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_use_flashcard_done

    override fun takeViewModel(): UseFlashcardDoneViewModel = getActivityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        activitySender = getActivitySender()
        viewModel.setData(getFullFlashcardSet(), getMissedCardsList())

        setUpViewByActivitySender(activitySender)

        dB.txtMaxScore.text = viewModel.getFullCardListSize().toString()
        dB.txtMissedCardCount.text = viewModel.getMissedCardsListSize().toString()

        runCircleScoreReport()
    }

    private fun setUpViewByActivitySender(activitySender: FlashcardSendableActivity) {
        dB.apply {
            when (activitySender) {
                FlashcardSendableActivity.USE_FLASHCARD_ACTIVITY -> {
                    btnCenterRestart.setImageResource(R.drawable.image_button_blue_restart)
                    btnRestart.setImageResource(R.drawable.image_button_blue_restart)
                }

                FlashcardSendableActivity.REVIEW_FLASHCARD_ACTIVITY -> {
                    btnCenterRestart.setImageResource(R.drawable.image_button_red_restart)
                    btnRestart.setImageResource(R.drawable.image_button_red_restart)

                    btnCenterGoToWritting.goGONE()
                    btnCenterGoToUseFlashcard.goVISIBLE()

                    btnGoToWritting.goGONE()
                    btnGoToUseFlashcard.goVISIBLE()
                }

                FlashcardSendableActivity.REVIEW_FLASHCARD_EASY_ACTIVITY -> {
                    btnCenterRestart.setImageResource(R.drawable.image_button_violet_restart)
                    btnRestart.setImageResource(R.drawable.image_button_violet_restart)

                    btnCenterGoToPuzzle.goGONE()
                    btnCenterGoToUseFlashcard.goVISIBLE()

                    btnGoToPuzzle.goGONE()
                    btnGoToUseFlashcard.goVISIBLE()
                }
            }
        }
    }

    private fun updateScoreOnScreen(score: Int) {
        dB.txtUserScore.text = score.toString()
    }

    private fun runCircleScoreReport() {
        val missRatio = viewModel.getMissCardRatio()
        val passRatio = viewModel.getPassCardRatio()
        dB.circleScoreReport.setProgressesValue(passRatio, missRatio)
        dB.circleScoreReport.addOnPositiveProgressChangeListener { progress ->
            val progressScore: Float = (progress * (viewModel.getPassedCardsCount()))
            if (progressScore.isNaN().not()) {
                val intProgressScore = progressScore.roundToInt()
                updateScoreOnScreen(intProgressScore)
            }
        }

        if (viewModel.userGetMaxScore().not()) {
            dB.vwgrpMissedCard.animate()
                .alpha(1f)
                .setDuration(250)
                .setStartDelay(300)
                .setInterpolator(FastOutSlowInInterpolator())
                .setLiteListener(onEnd = {
                    dB.rcvForgottenCardsList.goVISIBLE()
                })
        } else {
            (dB.circleScoreReport.layoutParams as ConstraintLayout.LayoutParams).apply {
                verticalBias = 0.45f
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            }

            (dB.txtTitleScore.layoutParams as ConstraintLayout.LayoutParams).apply {
                bottomToTop = dB.circleScoreReport.id
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                horizontalBias = 0.5f
                verticalBias = 1.0f
                bottomMargin = 15.dp()
                marginStart = 0.dp()
            }

        }

        dB.circleScoreReport.addOnDrawAlmostEndListener {
            onFinishDrawCircleReport()
        }

        dB.circleScoreReport.startDrawing(100)

    }

    private fun onFinishDrawCircleReport() {
        val userGetMaxScore = viewModel.userGetMaxScore()
        if (userGetMaxScore) {
            highlightUserScore()
            showCenterButtons(startDelay = 1100)
            playGetMaxScoreAnimation(startDelay = 100, duration = 375)
        } else {
            showBottomRightButtons(startDelay = 200)
        }
    }

    private fun playGetMaxScoreAnimation(startDelay: Long = 0, duration: Long = 150) {
        dB.imageCircleGreenBehindCircleScoreReport.animate().alpha(0f).scaleX(1.32f).scaleY(1.32f)
            .setDuration(duration).setStartDelay(startDelay).interpolator = NormalOutExtraSlowIn()
    }

    private fun showCenterButtons(startDelay: Long = 0) {
        dB.vwgrpCenterButtons.goVISIBLE()
        dB.vwgrpCenterButtons.animate().alpha(1f)
            .setDuration(100)
            .setStartDelay(startDelay).interpolator = LinearInterpolator()

        dB.viewCoverScoreReport.goVISIBLE()
        dB.viewCoverScoreReport.animate().alpha(1f)
            .setStartDelay(startDelay)
            .setDuration(200).interpolator = LinearInterpolator()

        dB.txtNiceWork.goVISIBLE()
        dB.txtNiceWork.animate().alpha(1f)
            .setStartDelay(startDelay)
            .setDuration(200).interpolator = LinearInterpolator()
    }

    private fun showBottomRightButtons(startDelay: Long = 0) {
        dB.vwgrpBottomRightButtons.goVISIBLE()
        dB.vwgrpBottomRightButtons.animate().alpha(1f)
            .setDuration(100)
            .setStartDelay(startDelay).interpolator = LinearInterpolator()
    }

    fun highlightUserScore() {
        dB.txtUserScore.setTextColor(resources.getColor(R.color.use_flashcard_done_green))
    }

    override fun addViewSettings() {
        dB.apply {
            rcvForgottenCardAdapter = RCVRecent_Search_FlashcardAdapter(this@ResultReportActivity)
            rcvForgottenCardAdapter.setData(getMissedCardsList())

            rcvForgottenCardsList.adapter = rcvForgottenCardAdapter
            rcvForgottenCardsList.layoutManager =
                LinearLayoutManager(this@ResultReportActivity, RecyclerView.VERTICAL, false)
        }
    }

    override fun addViewEvents() { dB.apply {

        btnQuit.setOnClickListener {
            finish()
        }

        btnRestart.setOnClickListener {
            handleRestart()
        }

        btnGoToUseFlashcard.setOnClickListener {
            goToUseFlashcardActivity()
        }

        btnGoToWritting.setOnClickListener {
            goToWritingFlashcardActivity()
        }

        btnGoToPuzzle.setOnClickListener {
            goToPuzzleActivity()
        }

        // CENTER

        btnCenterQuit.setOnClickListener {
            btnQuit.performClick()
        }

        btnCenterRestart.setOnClickListener {
            btnRestart.performClick()
        }

        btnCenterGoToUseFlashcard.setOnClickListener {
            btnGoToUseFlashcard.performClick()
        }

        btnCenterGoToWritting.setOnClickListener {
            btnGoToWritting.performClick()
        }

        btnCenterGoToPuzzle.setOnClickListener {
            btnGoToPuzzle.performClick()
        }
    }
    }

    private fun handleRestart() {

        var deck: Deck
        if (viewModel.getMissedCardsListSize() > 0) {
            deck = viewModel.getFlashcardSetWithMissedCardList()
        } else {
            deck = viewModel.getFlashcardSet()
        }

        when (activitySender) {
            FlashcardSendableActivity.USE_FLASHCARD_ACTIVITY -> {
                UseFlashcardActivity.requestReviewFlashcard(
                    this@ResultReportActivity,
                    deck,
                    reverseCardTextAndTranslation = false
                )
            }

            FlashcardSendableActivity.REVIEW_FLASHCARD_ACTIVITY -> {
                ReviewFlashcardActivity.requestReviewFlashcard(
                    this@ResultReportActivity,
                    deck,
                    reverseCardTextAndTranslation = false
                )
            }

            FlashcardSendableActivity.REVIEW_FLASHCARD_EASY_ACTIVITY -> {
                ReviewFlashcardEasyActivity.requestReviewFlashcard(
                    this@ResultReportActivity,
                    deck,
                    reverseCardTextAndTranslation = false
                )
            }
        }
        finish()
    }

    private fun goToPuzzleActivity() {
        ReviewFlashcardEasyActivity.requestReviewFlashcard(
            this@ResultReportActivity,
            getFullFlashcardSet(),
            reverseCardTextAndTranslation = false
        )
        finish()
    }

    private fun goToWritingFlashcardActivity() {
        ReviewFlashcardActivity.requestReviewFlashcard(
            this@ResultReportActivity,
            getFullFlashcardSet(),
            reverseCardTextAndTranslation = false
        )
        finish()
    }


    private fun goToUseFlashcardActivity() {
        UseFlashcardActivity.requestReviewFlashcard(
            this@ResultReportActivity,
            getFullFlashcardSet(),
            reverseCardTextAndTranslation = false
        )
        finish()
    }

    private fun getMissedCardsList(): ArrayList<Flashcard> {
        return intent.extras!!.getSerializable(FORGOTTEN_FLASHCARDS_LIST) as (ArrayList<Flashcard>)
    }

    private fun getActivitySender(): FlashcardSendableActivity {
        return FlashcardSendableActivity.getTypeByCode(intent.extras!!.getInt(SENDER_CODE))
    }

    private fun getFullFlashcardSet(): Deck {
        return intent.extras!!.getSerializable(FULL_FLASHCARD_SET) as Deck
    }
}

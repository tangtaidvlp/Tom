package com.teamttdvlp.memolang.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityUseFlashcardDoneBinding
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardDoneView
import com.teamttdvlp.memolang.view.adapter.RCVViewFlashcardAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.goVISIBLE
import com.teamttdvlp.memolang.view.helper.getActivityViewModel
import com.teamttdvlp.memolang.viewmodel.UseFlashcardDoneViewModel


const val FORGOTTEN_FLASHCARDS_LIST = "fgl"
const val SENDER_CODE = "sdrc"

class UseFlashcardDoneActivity : BaseActivity<ActivityUseFlashcardDoneBinding, UseFlashcardDoneViewModel>(), UseFlashcardDoneView {

    enum class SendableActivity (val code : Int) {
        USE_FLASHCARD_ACTIVITY(1),
        REVIEW_FLASHCARD_ACTIVITY(2),
        REVIEW_FLASHCARD_EASY_ACTIVITY(3);

        companion object {
            fun getTypeByCode (code : Int) : SendableActivity{
                return when (code) {
                    USE_FLASHCARD_ACTIVITY.code -> USE_FLASHCARD_ACTIVITY
                    REVIEW_FLASHCARD_ACTIVITY.code -> REVIEW_FLASHCARD_ACTIVITY
                    REVIEW_FLASHCARD_EASY_ACTIVITY.code -> REVIEW_FLASHCARD_EASY_ACTIVITY
                    else -> throw Exception("Unknown code")
                }
            }
        }
    }

    lateinit var rcvForgottenCardAdapter : RCVViewFlashcardAdapter

    private var forgotenCardList : ArrayList<Flashcard>? = null

    private lateinit var activitySender : SendableActivity

    companion object {
        fun requestFinishUsingFlashcard (requestContext : Context, hardCardList: ArrayList<Flashcard>, senderCode : Int) {
            val intent = Intent(requestContext, UseFlashcardDoneActivity::class.java)
            intent.putExtra(FORGOTTEN_FLASHCARDS_LIST, hardCardList)
            intent.putExtra(SENDER_CODE, senderCode)
            requestContext.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_use_flashcard_done

    override fun takeViewModel(): UseFlashcardDoneViewModel = getActivityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        viewModel.checkUserWellDone(getForgottenCardList())
    }

    override fun addViewControls() { dB.apply {
        rcvForgottenCardAdapter = RCVViewFlashcardAdapter(this@UseFlashcardDoneActivity, getForgottenCardList())
        rcvForgottenCardsList.adapter = rcvForgottenCardAdapter
        rcvForgottenCardsList.layoutManager =
            LinearLayoutManager(this@UseFlashcardDoneActivity, RecyclerView.VERTICAL, false)
    }}

    override fun addViewEvents() { dB.apply {

        btnQuit.setOnClickListener {
            finish()
        }

        btnRestart.setOnClickListener {
            val someCard = forgotenCardList!!.first()
            val flashcardSet = FlashcardSet(someCard.setOwner, someCard.frontLanguage, someCard.backLanguage)
            flashcardSet.flashcards = forgotenCardList!!

            when (activitySender) {
                SendableActivity.USE_FLASHCARD_ACTIVITY -> {
                    UseFlashcardActivity.requestReviewFlashcard(this@UseFlashcardDoneActivity, flashcardSet)
                }

                SendableActivity.REVIEW_FLASHCARD_ACTIVITY -> {
                    ReviewFlashcardActivity.requestReviewFlashcard(this@UseFlashcardDoneActivity, flashcardSet)
                }

                SendableActivity.REVIEW_FLASHCARD_EASY_ACTIVITY -> {
                    ReviewFlashcardEasyActivity.requestReviewFlashcard(this@UseFlashcardDoneActivity, flashcardSet)
                }
            }

            finish()
        }

    }}

    override fun showCongratulationText() { dB.apply {
        txtCongratulation.goVISIBLE()
    }}

    override fun showFoggotenCardListAndNavigations() { dB.apply {
        rcvForgottenCardsList.goVISIBLE()
        btnQuit.goVISIBLE()
        btnRestart.goVISIBLE()
        txtTitleForgottenCards.goVISIBLE()
    }}

    override fun waitForAWhileAndFinish () {
        Thread (object : Runnable {
            override fun run() {
                Thread.sleep(1500)
                runOnUiThread(Runnable {
                    finish()
                })
            }
        }).start()
    }

    private fun getForgottenCardList () : ArrayList<Flashcard> {
        if (forgotenCardList == null) {
            forgotenCardList = intent.extras!!.getSerializable(FORGOTTEN_FLASHCARDS_LIST) as (ArrayList<Flashcard>)
            activitySender = SendableActivity.getTypeByCode(intent.extras!!.getInt(SENDER_CODE))
        }
        return forgotenCardList!!
    }
}

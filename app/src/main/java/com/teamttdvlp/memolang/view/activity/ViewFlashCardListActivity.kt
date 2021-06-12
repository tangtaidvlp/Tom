package com.teamttdvlp.memolang.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.PopupWindow
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.CardQuizInfor
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.databinding.ActivityViewFlashCardListBinding
import com.teamttdvlp.memolang.databinding.PopupEditFlashcardListMenuBinding
import com.teamttdvlp.memolang.view.activity.iview.ViewFlashcardListView
import com.teamttdvlp.memolang.view.adapter.ChooseTargetSetRCVAdapter
import com.teamttdvlp.memolang.view.adapter.RCVViewFlashcardAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.ViewFlashCardListViewModel
import javax.inject.Inject

const val FLASHCARD_KEY = "memocard"

const val EDIT_FLASHCARD_REQUEST_CODE = 1801

const val FLASHCARD_SET = "flashcard_set"

const val UPDATED_FLASHCARD_SET  = "updaed_flcard_set"

const val ALL_FLASHCARD_SET_NAME = "all_flashcard_set_name"

class ViewFlashCardListActivity : BaseActivity<ActivityViewFlashCardListBinding, ViewFlashCardListViewModel>()
                                 ,ViewFlashcardListView {

    lateinit var viewModelProviderFactory: ViewModelProviderFactory
        @Inject set

    lateinit var rcvFlashcardListAdapter: RCVViewFlashcardAdapter

    lateinit var rcvMoveTargetSetAdapter: ChooseTargetSetRCVAdapter

    private var hasAChangeInList = false

    private lateinit var currentViewedDeck: Deck

    private lateinit var editFlashcardPopUpMenu : PopupWindow


    override fun getLayoutId(): Int = R.layout.activity_view_flash_card_list

    override fun takeViewModel(): ViewFlashCardListViewModel =
        getActivityViewModel(viewModelProviderFactory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(resources.getColor(R.color.app_blue))
        viewModel.setUpView(this)
        viewModel.setUpData(currentViewedDeck)
        dB.vwModel = viewModel
    }

    override fun onStart() {
        super.onStart()
        // OnStart() runs when user back from EditFlashcardActivity to this Activity
        stopPreventUserFromClickingItem()
    }

    override fun addViewSettings() { dB.apply {

        val flashcardSetName = getAll_FlashcardSetName()
        flashcardSetName.remove(getViewedFlashcardSet().name)
        rcvMoveTargetSetAdapter = ChooseTargetSetRCVAdapter(this@ViewFlashCardListActivity, flashcardSetName)
        rcvChooseMoveSetTarget.adapter = rcvMoveTargetSetAdapter

        txtMoveTargetSet.text = rcvMoveTargetSetAdapter.dataList.get(0)

        createEditFlashcardPopUpMenu()

        try {
            currentViewedDeck = getViewedFlashcardSet()

        } catch (ex: Exception) {
            ex.printStackTrace()
            currentViewedDeck = Deck("", "", "")
        }

    }}

    override fun onLoadDataSuccess(
        quizDataList: ArrayList<CardQuizInfor>
    ) { dB.apply {
        // Reverse here to make flashcard last in first out
        setUpFlashcardList(quizDataList)
    }}

    private fun setUpFlashcardList (quizDataList: ArrayList<CardQuizInfor>) { dB.apply {

        rcvFlashcardListAdapter =
            RCVViewFlashcardAdapter (this@ViewFlashCardListActivity,
                currentViewedDeck.flashcards, quizDataList
            )

        rcvFlashcardList.adapter = rcvFlashcardListAdapter

        rcvFlashcardListAdapter.setOnItemClickListener { card ->
            preventUserFromClickingOtherItem()
            RetrofitAddFlashcardActivity.requestEditFlashcard(
                this@ViewFlashCardListActivity,
                card,
                currentViewedDeck
            )
        }

    }

    }

    private lateinit var editCardsPopUpDB : PopupEditFlashcardListMenuBinding

    private fun createEditFlashcardPopUpMenu () {
        editCardsPopUpDB = PopupEditFlashcardListMenuBinding.inflate(layoutInflater)
        editFlashcardPopUpMenu = PopupWindow(editCardsPopUpDB.root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
    }


    override fun addViewEvents() { dB.apply {

        btnStartEditAnswersQuiz.setOnClickListener {
            rcvFlashcardListAdapter.turnOnEditAnswerQuizMode()
            turnOnEdit_CardQuizMode()
        }

        btnFinishEditAnswerQuiz.setOnClickListener {
            rcvFlashcardListAdapter.turnOffEditAnswerQuizMode()
            turnOffEdit_CardQuizMode()
        }

        btnDeleteSelectedCards.setOnClickListener {
            try {
                this@ViewFlashCardListActivity.viewModel.deleteCards(rcvFlashcardListAdapter.getSelectedFlashcardList())
                hasAChangeInList = true
                rcvFlashcardListAdapter.deleteSelectedFlashcards()
                val thereIsNoCardLeft = (rcvFlashcardListAdapter.list.size == 0)
                if (thereIsNoCardLeft) {
                    returnUpdatedFlashcardSet()
                    finish()
                } else {
                    rcvFlashcardListAdapter.turnOffDeleteMode()
                    btnDeleteSelectedCards.goGone_ByFadeOut()
                    btnStartEditAnswersQuiz.goVisible_ByFadeIn()
                    this@ViewFlashCardListActivity.viewModel.getFlashcardCount().set(rcvFlashcardListAdapter.list.size)
                }
            } catch (ex : Exception) {
                ex.printStackTrace()
            }
        }

        rcvMoveTargetSetAdapter.setOnItemClickListener { view, setName ->
            txtMoveTargetSet.text = setName
            rcvChooseMoveSetTarget.goGone_ByFadeOut()
        }

        txtMoveTargetSet.setOnClickListener {
            rcvChooseMoveSetTarget.goVisible_ByFadeIn()
        }

        btnMove.setOnClickListener {
            viewModel.moveSelectedCardToTargetSet(rcvFlashcardListAdapter.getSelectedFlashcardList(), txtMoveTargetSet.text.toString())
            turnOffMoveCardMode()
        }

        vwgrpMeetball.setOnClickListener {
            editFlashcardPopUpMenu.showAsDropDown(btnMeetball)
        }

        editCardsPopUpDB.apply {

            vwgrpDeleteSet.setOnClickListener {
                turnOnDeleteMode()
                editFlashcardPopUpMenu.dismiss()
            }

            vwgrpMoveCard.setOnClickListener {
                turnOnMoveCardMode()
                editFlashcardPopUpMenu.dismiss()
            }
        }
    }}

    private fun turnOnMoveCardMode() { dB.apply {
        rcvFlashcardListAdapter.turnOnMoveCardsMode()
        vwgrpMoveCardOption.goVisible_ByFadeIn()
        btnStartEditAnswersQuiz.goGone_ByFadeOut()
    }}


    private fun turnOffMoveCardMode() { dB.apply {
        rcvChooseMoveSetTarget.goGone_ByFadeOut()
        rcvFlashcardListAdapter.turnOffMoveCardMode()
        vwgrpMoveCardOption.goGone_ByFadeOut()
        btnStartEditAnswersQuiz.goVisible_ByFadeIn()
    }}

    private fun turnOnDeleteMode() {
        dB.apply {
            rcvFlashcardListAdapter.turnOnDeleteMode()
            btnDeleteSelectedCards.goVisible_ByFadeIn()
            btnStartEditAnswersQuiz.goGone_ByFadeOut()
        }
    }

    private fun turnOffDeleteMode() {
        dB.apply {
            rcvFlashcardListAdapter.turnOffDeleteMode()
            btnDeleteSelectedCards.goGone_ByFadeOut()
            btnStartEditAnswersQuiz.goVisible_ByFadeIn()
        }
    }

    val ANIM_INTERVAL = 100L
    private fun turnOnEdit_CardQuizMode() { dB.apply {

        btnStartEditAnswersQuiz.animate().reset().scaleX(0f).scaleY(0f)
            .setDuration(ANIM_INTERVAL).setLiteListener(onEnd = {
                btnStartEditAnswersQuiz.goGONE()
            })

        btnBtnAddNewQuiz.goVISIBLE()
        btnBtnAddNewQuiz.animate().reset().scaleX(1f).scaleY(1f).setDuration(ANIM_INTERVAL)

        btnFinishEditAnswerQuiz.goVISIBLE()
        btnFinishEditAnswerQuiz.animate().reset().scaleX(1f).scaleY(1f).setDuration(ANIM_INTERVAL)

    }}

    private fun turnOffEdit_CardQuizMode() { dB.apply {

        btnStartEditAnswersQuiz.goVisible_ByFadeIn()
        btnFinishEditAnswerQuiz.goGone_ByFadeOut()
        btnBtnAddNewQuiz.goGone_ByFadeOut()
    }}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == EDIT_FLASHCARD_REQUEST_CODE) and (resultCode == Activity.RESULT_OK)) {
//            hasAChangeInList = true
//            val newUpdatedFlashcard = data!!.getSerializableExtra(UPDATED_FLASHCARD) as Flashcard
//            updateCurrentViewedFlashcardList(newUpdatedFlashcard)
            // TODO(Do some update thing)
        }
    }

    private fun updateCurrentViewedFlashcardList (updatedFlashcard : Flashcard) {
        var updatedPosition = -1
        for (card in rcvFlashcardListAdapter.list) {
            if (card.id == updatedFlashcard.id) {
                updatedPosition = rcvFlashcardListAdapter.list.indexOf(card)
                break
            }
        }
        rcvFlashcardListAdapter.list[updatedPosition] = updatedFlashcard
        rcvFlashcardListAdapter.notifyItemChanged(updatedPosition)
    }

    private fun getViewedFlashcardSet(): Deck {
        return intent.getSerializableExtra(FLASHCARD_SET_KEY) as Deck
    }

    private fun getAll_FlashcardSetName(): ArrayList<String> {
        return intent.getSerializableExtra(ALL_FLASHCARD_SET_NAME) as ArrayList<String>
    }


    override fun onBackPressed() {
        if (rcvFlashcardListAdapter.isIn_Delete_Mode) {
            turnOffDeleteMode()
        } else if (rcvFlashcardListAdapter.isIn_MoveCard_Mode) {
            turnOffMoveCardMode()
        } else {
            if (hasAChangeInList) {
                returnUpdatedFlashcardSet()
            }
            super.onBackPressed()
        }
    }

    private fun returnUpdatedFlashcardSet() {
        val intent = Intent()
        currentViewedDeck.flashcards = rcvFlashcardListAdapter.list
        intent.putExtra(UPDATED_FLASHCARD_SET, currentViewedDeck)
        setResult(Activity.RESULT_OK, intent)
    }

    private fun preventUserFromClickingOtherItem () {
        dB.imgPreventUserMultiItemClick.goVISIBLE()
    }

    private fun stopPreventUserFromClickingItem() {
        dB.imgPreventUserMultiItemClick.goGONE()
    }

}

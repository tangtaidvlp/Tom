package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.animation.addListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.databinding.ActivityViewFlashCardListBinding
import com.teamttdvlp.memolang.view.activity.iview.ViewFlashcardListView
import com.teamttdvlp.memolang.view.adapter.RCVViewFlashcardAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.ViewFlashCardListViewModel
import javax.inject.Inject
import javax.inject.Named

const val FLASHCARD_KEY = "memocard"

const val EDIT_FLASHCARD_REQUEST_CODE = 1801

const val FLASHCARD_SET = "flashcard_set"

const val UPDATED_FLASHCARD_SET  = "updaed_flcard_set"

class ViewFlashCardListActivity : BaseActivity<ActivityViewFlashCardListBinding, ViewFlashCardListViewModel>()
                                 ,ViewFlashcardListView {

    lateinit var viewModelProviderFactory: ViewModelProviderFactory
        @Inject set

    private lateinit var buttonDeleteAppearAnimator: Animator

    private lateinit var buttonDeleteDisappearAnimator: Animator

    lateinit var viewFlashcardAdapter: RCVViewFlashcardAdapter

    private var hasAChangeInList = false

    private lateinit var currentViewedDeck: Deck

    override fun getLayoutId(): Int = R.layout.activity_view_flash_card_list

    override fun takeViewModel(): ViewFlashCardListViewModel =
        getActivityViewModel(viewModelProviderFactory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(resources.getColor(R.color.app_blue))
        viewModel.setUpView(this)
        dB.vwModel = viewModel
    }

    override fun onStart() {
        super.onStart()
        // OnStart() runs when user back from EditFlashcardActivity to this Activity
        stopPreventUserFromClickingItem()
    }

    override fun addViewSettings() {
        dB.apply {
            try {
                currentViewedDeck = getViewedFlashcardSet()
            } catch (ex: Exception) {
                ex.printStackTrace()
                currentViewedDeck = Deck("", "", "")
            }

            // Reverse here to make flashcard last in first out
            viewFlashcardAdapter =
                RCVViewFlashcardAdapter(
                    this@ViewFlashCardListActivity,
                    currentViewedDeck.flashcards
                )
        rcvFlashcardList.adapter = viewFlashcardAdapter
        rcvFlashcardList.layoutManager = LinearLayoutManager(this@ViewFlashCardListActivity, RecyclerView.VERTICAL, false)
        viewModel.setFlashcardSet(currentViewedDeck)
    }}


    override fun addViewEvents() { dB.apply {
        viewFlashcardAdapter.setOnItemClickListener { card ->
            preventUserFromClickingOtherItem()
            systemOutLogging("Chosen card: ${card.cardProperty}")
            RetrofitAddFlashcardActivity.requestEditFlashcard(
                this@ViewFlashCardListActivity,
                card,
                currentViewedDeck
            )
        }

        viewFlashcardAdapter.setOnDeleteButtonClickListener {
            viewFlashcardAdapter.turnOnDeleteMode()
            buttonDeleteAppearAnimator.start()
        }

        btnDeleteSelectedCards.setOnClickListener {
            try {
                this@ViewFlashCardListActivity.viewModel.deleteCards(viewFlashcardAdapter.getSelectedFlashcardList())
                hasAChangeInList = true
                viewFlashcardAdapter.deleteSelectedFlashcards()
                val thereIsNoCardLeft = (viewFlashcardAdapter.list.size == 0)
                if (thereIsNoCardLeft) {
                    returnUpdatedFlashcardSet()
                    finish()
                } else {
                    viewFlashcardAdapter.turnOffDeleteMode()
                    buttonDeleteDisappearAnimator.start()
                    this@ViewFlashCardListActivity.viewModel.getFlashcardCount().set(viewFlashcardAdapter.list.size)
                }
            } catch (ex : Exception) {
                ex.printStackTrace()
            }
        }
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
        for (card in viewFlashcardAdapter.list) {
            if (card.id == updatedFlashcard.id) {
                updatedPosition = viewFlashcardAdapter.list.indexOf(card)
                break
            }
        }
        viewFlashcardAdapter.list[updatedPosition] = updatedFlashcard
        viewFlashcardAdapter.notifyItemChanged(updatedPosition)
    }

    private fun getViewedFlashcardSet(): Deck {
        return intent.getSerializableExtra(FLASHCARD_SET_KEY) as Deck
    }


    override fun onBackPressed() {
        if (viewFlashcardAdapter.isInDeleteMode) {
            viewFlashcardAdapter.turnOffDeleteMode()
            buttonDeleteDisappearAnimator.start()
        } else {
            if (hasAChangeInList) {
                returnUpdatedFlashcardSet()
            }
            super.onBackPressed()
        }
    }

    private fun returnUpdatedFlashcardSet() {
        val intent = Intent()
        currentViewedDeck.flashcards = viewFlashcardAdapter.list
        intent.putExtra(UPDATED_FLASHCARD_SET, currentViewedDeck)
        setResult(Activity.RESULT_OK, intent)
    }

    private fun preventUserFromClickingOtherItem () {
        dB.imgPreventUserMultiItemClick.goVISIBLE()
    }

    private fun stopPreventUserFromClickingItem() {
        dB.imgPreventUserMultiItemClick.goGONE()
    }


    // INJECT ANIMATIONS

    @Inject
    fun initButtonBinDeleteAnimations (
        @Named("FromNormalSizeToNothing") fromNormalSizeToNothing : Animator,
        @Named("FromNothingToNormalSize") fromNothingToNormalSize: Animator) {
        buttonDeleteAppearAnimator = fromNothingToNormalSize
        buttonDeleteDisappearAnimator = fromNormalSizeToNothing

        buttonDeleteAppearAnimator.addListener (onStart = {
            dB.btnDeleteSelectedCards.goVISIBLE()
        })

        buttonDeleteDisappearAnimator.addListener (onEnd = {
            dB.btnDeleteSelectedCards.goGONE()
        })

        buttonDeleteAppearAnimator.setTarget(dB.btnDeleteSelectedCards)
        buttonDeleteDisappearAnimator.setTarget(dB.btnDeleteSelectedCards)
    }


}

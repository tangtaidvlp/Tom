package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.animation.addListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.base.BaseActivity
import kotlinx.android.synthetic.main.activity_view_flash_card_list.*
import com.teamttdvlp.memolang.databinding.ActivityViewFlashCardListBinding
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.view.adapter.RCVViewFlashcardAdapter
import com.teamttdvlp.memolang.view.helper.getActivityViewModel
import com.teamttdvlp.memolang.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.view.activity.iview.ViewFlashcardListView
import com.teamttdvlp.memolang.view.helper.appear
import com.teamttdvlp.memolang.view.helper.disappear
import com.teamttdvlp.memolang.viewmodel.ViewFlashCardListViewModel
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named

const val FLASHCARD_KEY = "memocard"

const val EDIT_FLASHCARD_REQUEST_CODE = 1801

const val UPDATED_FLASHCARD_LIST_KEY = "card_list"

const val FLASHCARD_SET_NAME = "flashcard_type"

class ViewFlashCardListActivity : BaseActivity<ActivityViewFlashCardListBinding, ViewFlashCardListViewModel>()
                                 ,ViewFlashcardListView {

    private var hasAChangeInList = false

    private var flashcardSetName : String? = null

    lateinit var flashcardRepository : FlashcardRepository
    @Inject set

    private lateinit var buttonDeleteAppearAnimator : Animator

    private lateinit var buttonDeleteDisappearAnimator : Animator

    override fun getLayoutId(): Int = R.layout.activity_view_flash_card_list

    override fun takeViewModel(): ViewFlashCardListViewModel = getActivityViewModel() {
        ViewFlashCardListViewModel(flashcardRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        dB.viewModel = viewModel
    }

    lateinit var viewFlashcardAdapter : RCVViewFlashcardAdapter

    override fun addViewControls() { dB.apply {
        var flashcardSet : FlashcardSet
        try {
            flashcardSet = intent.getSerializableExtra(FLASHCARD_SET_KEY) as FlashcardSet
            flashcardSetName = flashcardSet.name
        } catch (ex : Exception) {
            ex.printStackTrace()
            flashcardSet = FlashcardSet("null")
        }
        // Reverse here to make flashcard last in first out
        val updatedList = ArrayList<Flashcard>().apply {
            addAll(flashcardSet.flashcards.reversed())
        }
        viewFlashcardAdapter = RCVViewFlashcardAdapter(this@ViewFlashCardListActivity,updatedList)
        rcv_flashcard_list.adapter = viewFlashcardAdapter
        rcv_flashcard_list.layoutManager = LinearLayoutManager(this@ViewFlashCardListActivity, RecyclerView.VERTICAL, false)
        this@ViewFlashCardListActivity.viewModel.setFlashcardSet(flashcardSet)
    }}


    override fun addViewEvents() { dB.apply {
        viewFlashcardAdapter.setOnItemClickListener { card ->
            val intent = Intent(this@ViewFlashCardListActivity, EditFlashcardActivity::class.java)
            intent.putExtra(FLASHCARD_KEY, card)
            startActivityForResult(intent, EDIT_FLASHCARD_REQUEST_CODE)
        }

        viewFlashcardAdapter.setOnDeleteButtonClickListener {
            viewFlashcardAdapter.turnOnDeleteMode()
            buttonDeleteAppearAnimator.start()
        }

        viewFlashcardAdapter.setOnEndDeleteModeListener {

        }

        btnDeleteSelectedCards.setOnClickListener {
            try {
                this@ViewFlashCardListActivity.viewModel.deleteCards(viewFlashcardAdapter.getSelectedFlashcardList())
                hasAChangeInList = true
                viewFlashcardAdapter.deleteSelectedFlashcards()
                val thereIsNoCardLeft = (viewFlashcardAdapter.list.size == 0)
                if (thereIsNoCardLeft) {
                    returnUpdatedData()
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
            hasAChangeInList = true
            val newFlashcard = data!!.getSerializableExtra(UPDATED_FLASHCARD) as Flashcard
            val i = viewFlashcardAdapter.list.indexOf(newFlashcard)
            viewFlashcardAdapter.list[i] = newFlashcard
            viewFlashcardAdapter.notifyItemChanged(i)
        }
    }

    override fun onBackPressed() {
        if (viewFlashcardAdapter.isInDeleteMode) {
            viewFlashcardAdapter.turnOffDeleteMode()
            buttonDeleteDisappearAnimator.start()
        } else {
            if (hasAChangeInList) {
                returnUpdatedData()
            }
            super.onBackPressed()
        }
    }

    private fun returnUpdatedData() {
        val intent = Intent()
        val resultList = ArrayList<Flashcard>()
        for (item in viewFlashcardAdapter.list.reversed()) {
            resultList.add(item)
        }
        intent.putExtra(UPDATED_FLASHCARD_LIST_KEY, resultList)
        intent.putExtra(FLASHCARD_SET_NAME, flashcardSetName)
        setResult(Activity.RESULT_OK, intent)
    }


    // INJECT ANIMATIONS

    @Inject
    fun initButtonBinDeleteAnimations (
        @Named("FromNormalSizeToNothing") fromNormalSizeToNothing : Animator,
        @Named("FromNothingToNormalSize") fromNothingToNormalSize: Animator) {
        buttonDeleteAppearAnimator = fromNothingToNormalSize
        buttonDeleteDisappearAnimator = fromNormalSizeToNothing

        buttonDeleteAppearAnimator.addListener (onStart = {
            dB.btnDeleteSelectedCards.appear()
        })

        buttonDeleteDisappearAnimator.addListener (onEnd = {
            dB.btnDeleteSelectedCards.disappear()
        })

        buttonDeleteAppearAnimator.setTarget(dB.btnDeleteSelectedCards)
        buttonDeleteDisappearAnimator.setTarget(dB.btnDeleteSelectedCards)
    }
}

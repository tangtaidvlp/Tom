package com.teamttdvlp.memolang.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.databinding.ActivityViewFlashcardSetBinding
import com.teamttdvlp.memolang.view.activity.iview.ViewFlashcardSetView
import com.teamttdvlp.memolang.view.adapter.RCV_FlashcardSetAdapter
import com.teamttdvlp.memolang.view.adapter.RCV_FlashcardSetsBackground_Adapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.ViewFlashcardSetViewModel
import javax.inject.Inject

class
ViewFlashcardSetActivity : BaseActivity<ActivityViewFlashcardSetBinding, ViewFlashcardSetViewModel>()
                        ,ViewFlashcardSetView {

    lateinit var flashcardSetAdapter : RCV_FlashcardSetAdapter
    @Inject set

    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    @Inject set

    override fun getLayoutId(): Int {
        return R.layout.activity_view_flashcard_set
    }

    override fun takeViewModel(): ViewFlashcardSetViewModel {
        return getActivityViewModel(viewModelProviderFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        loadFlashcard()
    }

    override fun onRestart() {
        super.onRestart()
        loadFlashcard()
    }

    override fun addViewSettings() {
        dB.apply {
            rcvLanguageList.adapter = flashcardSetAdapter
            rcvLanguageList.layoutManager =
                LinearLayoutManager(this@ViewFlashcardSetActivity, RecyclerView.VERTICAL, false)
            rcvBackground.adapter =
                RCV_FlashcardSetsBackground_Adapter(this@ViewFlashcardSetActivity)
        }
    }

    private fun loadFlashcard () {
//        viewModel.getAllFlashcardSets {
//            flashcardSetAdapter.setData_But_OmitEmptySet(it!!)
//            if (flashcardSetAdapter.itemCount > 0) {
//                hideSuggesstionBackground()
//            }
//        }
    }

    private fun hideSuggesstionBackground () {
        dB.vwgrpSuggestion.goGONE()
        dB.rcvBackground.goGONE()
    }

    private fun showSuggestionBackground () {
        dB.vwgrpSuggestion.goVISIBLE()
        dB.rcvBackground.goVISIBLE()
    }

    override fun addViewEvents() { dB.apply {
        flashcardSetAdapter.setOnBtnViewListClickListener {
            val intent = Intent(this@ViewFlashcardSetActivity, ViewFlashCardListActivity::class.java)
            intent.putExtra(FLASHCARD_SET_KEY, it)
            startActivityForResult(intent, VIEW_LIST_REQUEST_CODE)
        }

        flashcardSetAdapter.setOnBtnAddClickListener { flashcardSet ->
            AddFlashcardActivity.requestAddLanguage(this@ViewFlashcardSetActivity, flashcardSet)
        }

        flashcardSetAdapter.setOnBtnUseFlashcardClickListener { flashcardSet ->
            UseFlashcardActivity.requestReviewFlashcard(
                this@ViewFlashcardSetActivity,
                flashcardSet,
                reverseCardTextAndTranslation = false
            )
        }

        flashcardSetAdapter.setOnBtn_GoToWritingActivity_ClickListener { flashcardSet ->
            WritingFlashcardActivity.requestReviewFlashcard(
                this@ViewFlashcardSetActivity,
                flashcardSet,
                reverseCardTextAndTranslation = false
            )
        }

        flashcardSetAdapter.setOnBtn_GoToPuzzleActivity_ClickListener { flashcardSet ->
            PuzzleFlashcardActivity.requestReviewFlashcard(
                this@ViewFlashcardSetActivity,
                flashcardSet,
                false
            )
        }

        btnCreateNewFlashcard.setOnClickListener {
            quickStartActivity(AddFlashcardActivity::class.java)
            finish()
        }
    }}

    override fun overrideEnterAnim () {
        overridePendingTransition(R.anim.appear, R.anim.nothing)
    }

    override fun overrideExitAnim () {
        overridePendingTransition(R.anim.nothing,R.anim.disappear)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == VIEW_LIST_REQUEST_CODE) and (resultCode == Activity.RESULT_OK)) {
            val dataBundle = data!!.extras!!
            val updatedFlashcardSet = dataBundle.getSerializable(UPDATED_FLASHCARD_SET) as Deck
            val listIsAllCleared = (updatedFlashcardSet.flashcards.size == 0)
            if (listIsAllCleared) {
                for ((pos, set) in flashcardSetAdapter.list.withIndex()) {
                    if (set.name == updatedFlashcardSet.name) {
                        flashcardSetAdapter.list.remove(set)
                        flashcardSetAdapter.notifyItemRemoved(pos)
                        break
                    }
                }

                val thereIsNoFlashcardSetOnScreen = (flashcardSetAdapter.list.size == 0)
                if (thereIsNoFlashcardSetOnScreen) {
                    showSuggestionBackground()
                }
            } else {
                for ((pos, set) in flashcardSetAdapter.list.withIndex()) {
                    val findOutCorrespondingList = (set.flashcards.first().setOwner == updatedFlashcardSet.name)
                    if (findOutCorrespondingList) {
                        flashcardSetAdapter.list[pos] = updatedFlashcardSet
                        flashcardSetAdapter.notifyItemChanged(pos)
                        break
                    }
                }
            }
        }
    }

}

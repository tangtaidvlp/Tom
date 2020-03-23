package com.teamttdvlp.memolang.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.database.sql.repository.UserRepository
import com.teamttdvlp.memolang.view.adapter.RCV_FlashcardSetAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.viewmodel.ViewFlashcardSetViewModel
import com.teamttdvlp.memolang.databinding.ActivityViewFlashcardSetBinding
import com.teamttdvlp.memolang.model.entity.Language.Companion.LANG_DIVIDER
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.entity.Language.Companion.SOURCE_LANGUAGE
import com.teamttdvlp.memolang.model.entity.Language.Companion.TARGET_LANGUAGE
import com.teamttdvlp.memolang.view.activity.iview.ViewFlashcardSetView
import com.teamttdvlp.memolang.view.adapter.RCV_FlashcardSetsBackground_Adapter
import com.teamttdvlp.memolang.view.helper.disappear
import com.teamttdvlp.memolang.view.helper.quickStartActivity
import javax.inject.Inject


const val FLASHCARD_SET_KEY = "flashcard_set"

const val FLASHCARD_LIST_KEY = "flashcard_list"

const val LANGUAGE_REQUEST_CODE = 118

class ViewFlashcardSetActivity : BaseActivity<ActivityViewFlashcardSetBinding, ViewFlashcardSetViewModel>()
                        ,ViewFlashcardSetView {

    lateinit var flashcardSetAdapter : RCV_FlashcardSetAdapter
    @Inject set

    lateinit var flashcardRepository: FlashcardRepository
    @Inject set

    lateinit var userRepository: UserRepository
    @Inject set


    override fun getLayoutId(): Int {
        return R.layout.activity_view_flashcard_set
    }

    override fun takeViewModel(): ViewFlashcardSetViewModel {
        return ViewFlashcardSetViewModel(application, flashcardRepository, userRepository)
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

    override fun addViewControls() { dB.apply {
        rcvLanguageList.adapter = flashcardSetAdapter
        rcvLanguageList.layoutManager = LinearLayoutManager(this@ViewFlashcardSetActivity, RecyclerView.VERTICAL, false)
        rcvBackground.adapter = RCV_FlashcardSetsBackground_Adapter(this@ViewFlashcardSetActivity)
    }}

    fun loadFlashcard () {
        viewModel.triggerGetAllSetOfCard {
            flashcardSetAdapter.setData(it)
            setBackgroundState()
        }
    }

    fun setBackgroundState () {
        if (flashcardSetAdapter.itemCount > 0) {
            dB.vwgrpSuggestion.disappear()
            dB.rcvBackground.disappear()
        }
    }

    override fun addViewEvents() { dB.apply {
        flashcardSetAdapter.setOnBtnViewListClickListener {
            val intent = Intent(this@ViewFlashcardSetActivity, ViewFlashCardListActivity::class.java)
            intent.putExtra(FLASHCARD_SET_KEY, it)
            startActivityForResult(intent, LANGUAGE_REQUEST_CODE)
        }

        flashcardSetAdapter.setOnBtnAddClickListener { set ->
            val intent = Intent(this@ViewFlashcardSetActivity, AddFlashcardActivity::class.java)
            intent.putExtra(AddFlashcardActivity.SRC_LANG_KEY, set.flashcards.first().languagePair.split(LANG_DIVIDER).get(SOURCE_LANGUAGE))
            intent.putExtra(AddFlashcardActivity.TGT_LANG_KEY, set.flashcards.first().languagePair.split(LANG_DIVIDER).get(TARGET_LANGUAGE))
            startActivity(intent)
        }

        flashcardSetAdapter.setOnBtnUseFlashcardClickListener {
            sendCardListTo(it.flashcards, UseFlashcardActivity::class.java)
        }

        flashcardSetAdapter.setOnBtnReviewFlashcardHardClickListener {
            sendCardListTo(it.flashcards, ReviewFlashcardActivity::class.java)
        }

        flashcardSetAdapter.setOnBtnReviewFlashcardEasyClickListener {
            sendCardListTo(it.flashcards, ReviewFlashcardEasyActivity::class.java)
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

        if ((requestCode == LANGUAGE_REQUEST_CODE) and (resultCode == Activity.RESULT_OK)) {
            val dataBundle = data!!.extras!!
            val updatedCardList = dataBundle.getSerializable(UPDATED_FLASHCARD_LIST_KEY) as ArrayList<Flashcard>
            val updatedCardSetName = dataBundle.getString(FLASHCARD_SET_NAME, "")

            val listIsAllCleared = (updatedCardList.size == 0)
            if (listIsAllCleared) {
                for ((pos, set) in flashcardSetAdapter.list.withIndex()) {
                    if (set.name == updatedCardSetName) {
                        flashcardSetAdapter.list.remove(set)
                        flashcardSetAdapter.notifyItemRemoved(pos)
                        viewModel.removeUserFlashcardSet(set.name)
                        break
                    }
                }
            }

            for ((pos, set) in flashcardSetAdapter.list.withIndex()) {
                val findOutCorresList = (set.flashcards.first().languagePair == updatedCardSetName)
                if (findOutCorresList) {
                    set.flashcards = updatedCardList
                    flashcardSetAdapter.notifyItemChanged(pos)
                    break
                }
            }
        }
    }

    private fun sendCardListTo (flashcardList : ArrayList<Flashcard>, targetActivity : Class<*>) {
        val intent = Intent(this@ViewFlashcardSetActivity, targetActivity)
        intent.putExtra(FLASHCARD_LIST_KEY, flashcardList)
        startActivity(intent)
    }

}

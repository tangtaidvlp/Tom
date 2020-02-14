package com.teamttdvlp.memolang.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.adapter.RCV_LanguageAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.getActivityViewModel
import com.teamttdvlp.memolang.viewmodel.LanguageViewModel
import com.teamttdvlp.memolang.databinding.ActivityLanguageBinding
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.entity.Language.Companion.SOURCE_LANGUAGE
import com.teamttdvlp.memolang.model.entity.Language.Companion.TARGET_LANGUAGE
import com.teamttdvlp.memolang.view.activity.iview.LanguageView
import javax.inject.Inject


const val FLASHCARD_SET_KEY = "flashcard_set"

const val FLASHCARD_LIST_KEY = "flashcard_list"

const val LANGUAGE_REQUEST_CODE = 118

class LanguageActivity : BaseActivity<ActivityLanguageBinding, LanguageViewModel>()
                        ,LanguageView {

    lateinit var languageAdapter : RCV_LanguageAdapter
    @Inject set

    override fun getLayoutId(): Int {
        return R.layout.activity_language
    }

    override fun takeViewModel(): LanguageViewModel {
        return getActivityViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
    }

    override fun addViewControls() { dB.apply {
        viewModel.triggerGetAllSetOfCard {
            languageAdapter.setData(it)
            rcvLanguageList.adapter = languageAdapter
            rcvLanguageList.layoutManager = LinearLayoutManager(this@LanguageActivity, RecyclerView.VERTICAL, false)
            languageAdapter.setOnBtnViewListClickListener {
                val intent = Intent(this@LanguageActivity, ViewFlashCardListActivity::class.java)
                intent.putExtra(FLASHCARD_SET_KEY, it)
                startActivityForResult(intent, LANGUAGE_REQUEST_CODE)
            }

            languageAdapter.setOnBtnAddClickListener { set ->
                val intent = Intent(this@LanguageActivity, AddFlashcardActivity::class.java)
                intent.putExtra(AddFlashcardActivity.SRC_LANG_KEY, set.flashcards.first().languagePair.split("-")[SOURCE_LANGUAGE])
                intent.putExtra(AddFlashcardActivity.TGT_LANG_KEY, set.flashcards.first().languagePair.split("-")[TARGET_LANGUAGE])
                startActivity(intent)
                finish()
            }
        }

        languageAdapter.setOnBtnUseFlashcardClickListener {
            sendCardListTo(it.flashcards, UseFlashcardActivity::class.java)
        }

        languageAdapter.setOnBtnReviewFlashcardHardClickListener {
            sendCardListTo(it.flashcards, ReviewFlashcardActivity::class.java)
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
                for ((pos, set) in languageAdapter.list.withIndex()) {
                    if (set.name == updatedCardSetName) {
                        languageAdapter.list.remove(set)
                        languageAdapter.notifyItemRemoved(pos)
                        break
                    }
                }
            }

            for ((pos, set) in languageAdapter.list.withIndex()) {
                val findOutCorresList = (set.flashcards.first().languagePair == updatedCardSetName)
                if (findOutCorresList) {
                    set.flashcards = updatedCardList
                    languageAdapter.notifyItemChanged(pos)
                    break
                }
            }
        }
    }

    private fun sendCardListTo (flashcardList : ArrayList<Flashcard>, targetActivity : Class<*>) {
        val intent = Intent(this@LanguageActivity, targetActivity)
        intent.putExtra(FLASHCARD_LIST_KEY, flashcardList)
        startActivity(intent)
    }

}

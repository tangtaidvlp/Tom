package com.teamttdvlp.memolang.view.activity

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.adapter.LanguageRCVAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.getActivityViewModel
import com.teamttdvlp.memolang.viewmodel.language.LanguageViewModel
import com.teamttdvlp.memolang.databinding.ActivityLanguageBinding
import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.view.helper.quickStartActivity
import javax.inject.Inject


const val FLASHCARD_SET_KEY = "flashcard_set"

const val FLASHCARD_LIST_KEY = "flashcard_list"

class LanguageActivity : BaseActivity<ActivityLanguageBinding, LanguageViewModel>() {

    lateinit var adapterLanguage : LanguageRCVAdapter
    @Inject set

    override fun getLayoutId(): Int {
        return R.layout.activity_language
    }

    override fun takeViewModel(): LanguageViewModel {
        return getActivityViewModel()
    }

    override fun addViewControls() { dataBinding.apply {
        viewModel.triggerGetAllSetOfCard {
            adapterLanguage.setData(it)
            rcvLanguageList.adapter = adapterLanguage
            rcvLanguageList.layoutManager = LinearLayoutManager(this@LanguageActivity, RecyclerView.VERTICAL, false)
            adapterLanguage.setOnBtnViewListClickListener {
                val intent = Intent(this@LanguageActivity, ViewFlashCardListActivity::class.java)
                intent.putExtra(FLASHCARD_SET_KEY, it)
                startActivity(intent)
            }

        }

        adapterLanguage.setOnItemClickListener {
            sendFlashcardListToUseFlashcardActivity(it.flashcards)
        }
    }}

    override fun overrideEnterAnim() {
        overridePendingTransition(R.anim.appear, R.anim.nothing)
    }

    override fun overrideExitAnim() {
        overridePendingTransition(R.anim.nothing,R.anim.disappear)
    }

    fun sendFlashcardListToUseFlashcardActivity (flashcardList : ArrayList<Flashcard>) {
        val intent = Intent(this@LanguageActivity, UseFlashcardActivity::class.java)
        intent.putExtra(FLASHCARD_LIST_KEY, flashcardList)
        startActivity(intent)
    }

}

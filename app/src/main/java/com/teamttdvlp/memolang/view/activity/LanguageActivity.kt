package com.teamttdvlp.memolang.view.activity

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.activity.adapter.LanguageRCVAdapter
import com.teamttdvlp.memolang.view.activity.base.BaseActivity
import com.teamttdvlp.memolang.view.activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.viewmodel.language.LanguageViewModel
import com.teamttdvlp.memolang.databinding.ActivityLanguageBinding
import com.teamttdvlp.memolang.view.activity.helper.quickLog
import com.teamttdvlp.memolang.view.activity.helper.quickStartActivity
import javax.inject.Inject


const val FLASHCARD_SET_KEY = "flashcard_set"

class LanguageActivity : BaseActivity<ActivityLanguageBinding, LanguageViewModel>() {

    lateinit var adapter : LanguageRCVAdapter
    @Inject set

    override fun getLayoutId(): Int {
        return R.layout.activity_language
    }

    override fun takeViewModel(): LanguageViewModel {
        return getActivityViewModel()
    }

    override fun addViewControls() {
        viewModel.triggerGetAllSetOfCard {
            quickLog(it.size.toString())
            adapter.setData(it)
            dataBinding.apply {
                rcvLanguageList.adapter = adapter
                rcvLanguageList.layoutManager = LinearLayoutManager(this@LanguageActivity, RecyclerView.VERTICAL, false)
                adapter.setOnBtnViewListClickListener {
                    val intent = Intent(this@LanguageActivity, ViewFlashCardListActivity::class.java)
                    intent.putExtra(FLASHCARD_SET_KEY, it)
                    startActivity(intent)
                }
            }
        }

        adapter.setOnItemClickListener {
            quickStartActivity(UseFlashcardActivity::class.java)
        }
    }

    override fun overrideEnterAnim() {
        overridePendingTransition(R.anim.appear, R.anim.nothing)
    }

    override fun overrideExitAnim() {
        overridePendingTransition(R.anim.nothing,R.anim.disappear)
    }

}

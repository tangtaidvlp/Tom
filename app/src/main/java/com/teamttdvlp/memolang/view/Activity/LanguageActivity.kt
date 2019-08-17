package com.teamttdvlp.memolang.view.Activity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.Activity.adapter.LanguageRCVAdapter
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.helper.quickStartActivity
import com.teamttdvlp.memolang.view.Activity.mockmodel.FlashcardSet
import com.teamttdvlp.memolang.view.Activity.viewmodel.language.LanguageViewModel
import com.teamttdvlp.memolang.databinding.ActivityLanguageBinding
import com.teamttdvlp.memolang.model.sqlite.entity.MemoCardEntity
import com.teamttdvlp.memolang.view.Activity.helper.TestEverything

class LanguageActivity : BaseActivity<ActivityLanguageBinding, LanguageViewModel>() {

    lateinit var adapter : LanguageRCVAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_language
    }

    override fun takeViewModel(): LanguageViewModel {
        return getActivityViewModel()
    }

    override fun addViewControls() {
        val mockList = ArrayList<FlashcardSet>()
        mockList.add(FlashcardSet("English", "Vietnamese", TestEverything().mockList()))
        mockList.add(FlashcardSet("China", "Vietnamese", TestEverything().mockList()))
        adapter = LanguageRCVAdapter(this@LanguageActivity, mockList)
        dataBinding.apply {
            rcvLanguageList.adapter = adapter
            rcvLanguageList.layoutManager = LinearLayoutManager(this@LanguageActivity, RecyclerView.VERTICAL, false)
            adapter.setOnItemClickListener {
                quickStartActivity(ViewFlashCardListActivity::class.java)
            }
        }
    }

}

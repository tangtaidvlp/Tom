package com.teamttdvlp.memolang.view.Activity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.Activity.adapter.LanguageRCVAdapter
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.helper.quickStartActivity
import com.teamttdvlp.memolang.view.Activity.mockmodel.Flashcard
import com.teamttdvlp.memolang.view.Activity.mockmodel.FlashcardSet
import com.teamttdvlp.memolang.view.Activity.viewmodel.language.LanguageViewModel
import com.teamttdvlp.memolang.databinding.ActivityLanguageBinding

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
        mockList.add(FlashcardSet("English", "Vietnamese", getMockList()))
        mockList.add(FlashcardSet("China", "Vietnamese", getMockList()))
        adapter = LanguageRCVAdapter(this@LanguageActivity, mockList)
        dataBinding.apply {
            rcvLanguageList.adapter = adapter
            rcvLanguageList.layoutManager = LinearLayoutManager(this@LanguageActivity, RecyclerView.VERTICAL, false)
            adapter.setOnItemClickListener {
                quickStartActivity(ViewFlashCardListActivity::class.java)
            }
        }
    }

    fun getMockList(): ArrayList<Flashcard> {
        val flashcard = Flashcard(1,"Negotiate", "Đàm phán", "We negotiate for intergrate two force")
        val flashcard1 = Flashcard(1,"Something", "Cái gì đó", "We always do something to figure out the solution")
        val flashcard2 = Flashcard(1,"Figure out", "Tìm ra", "We always do something to figure out the solution")
        val flashcard3 = Flashcard(1,"Until", "Cho đến khi", "Fight until the end")
        val flashcard4 = Flashcard(1,"Glorious", "Vẻ Vang", "We're worthy to have that glorious victory")
        val flashcard5 = Flashcard(1,"Curious", "Tò mò", "Successful people is always curious")
        val flashcard6 = Flashcard(1,"Give in", "Buông xuôi", "Give in laziness make you down")
        val flashcard7 = Flashcard(1,
            "Procastinate",
            "Trì hoãn",
            "Procastinating something when and only if you can do it better then"
        )
        val mockDataList = ArrayList<Flashcard>()
        mockDataList.add(flashcard)
        mockDataList.add(flashcard1)
        mockDataList.add(flashcard2)
        mockDataList.add(flashcard3)
        mockDataList.add(flashcard4)
        mockDataList.add(flashcard5)
        mockDataList.add(flashcard6)
        mockDataList.add(flashcard7)
        mockDataList.add(flashcard)
        mockDataList.add(flashcard1)
        mockDataList.add(flashcard2)
        mockDataList.add(flashcard3)
        mockDataList.add(flashcard4)
        mockDataList.add(flashcard5)
        mockDataList.add(flashcard6)
        mockDataList.add(flashcard7)
        return mockDataList
    }
}

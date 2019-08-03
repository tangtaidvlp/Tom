package com.teamttdvlp.memolang.view.Activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.Activity.adapter.FlashcardRCVAdapter
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.quickStartActivity
import com.teamttdvlp.memolang.view.Activity.mockmodel.Flashcard
import kotlinx.android.synthetic.main.activity_view_flash_card_list.*
import com.teamttdvlp.memolang.databinding.ActivityViewFlashCardListBinding
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.viewmodel.ViewFlashCardViewModel

class ViewFlashCardListActivity : BaseActivity<ActivityViewFlashCardListBinding, ViewFlashCardViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_view_flash_card_list

    override fun takeViewModel(): ViewFlashCardViewModel = getActivityViewModel()

    lateinit var adapter : FlashcardRCVAdapter

    override fun addViewControls() {
        val mockList = mockList()
        adapter = FlashcardRCVAdapter(this@ViewFlashCardListActivity, mockList)
        rcv_flashcard_list.adapter = adapter
        rcv_flashcard_list.layoutManager = LinearLayoutManager(this@ViewFlashCardListActivity, RecyclerView.VERTICAL, false)
    }

    override fun addViewEvents() {
        adapter.setOnItemClickListener {
            quickStartActivity(EditFlashcardActivity::class.java)
        }
    }

    fun mockList () : ArrayList<Flashcard> {
        val flashcard = Flashcard("Negotiate", "Đàm phán", "We negotiate for intergrate two force")
        val flashcard1 = Flashcard("Something", "Cái gì đó", "We always do something to figure out the solution")
        val flashcard2 = Flashcard("Figure out", "Tìm ra", "We always do something to figure out the solution")
        val flashcard3 = Flashcard("Until", "Cho đến khi", "Fight until the end")
        val flashcard4 = Flashcard("Glorious", "Vẻ Vang", "We're worthy to have that glorious victory")
        val flashcard5 = Flashcard("Curious", "Tò mò", "Successful people is always curious")
        val flashcard6 = Flashcard("Give in", "Buông xuôi", "Give in laziness make you down")
        val flashcard7 = Flashcard("Procastinate", "Trì hoãn", "Procastinating something when and only if you can do it better then")
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

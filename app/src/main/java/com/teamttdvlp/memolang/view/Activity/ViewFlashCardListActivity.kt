package com.teamttdvlp.memolang.view.Activity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.quickStartActivity
import kotlinx.android.synthetic.main.activity_view_flash_card_list.*
import com.teamttdvlp.memolang.databinding.ActivityViewFlashCardListBinding
import com.teamttdvlp.memolang.model.sqlite.entity.MemoCardEntity
import com.teamttdvlp.memolang.view.Activity.adapter.MemoCardRCVAdapter
import com.teamttdvlp.memolang.view.Activity.helper.TestEverything
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.viewmodel.view_flashcard.ViewFlashCardViewModel

class ViewFlashCardListActivity : BaseActivity<ActivityViewFlashCardListBinding, ViewFlashCardViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_view_flash_card_list

    override fun takeViewModel(): ViewFlashCardViewModel = getActivityViewModel()

    lateinit var flashcardAdapter : MemoCardRCVAdapter

    override fun addViewControls() {
        val mockList = TestEverything().mockList()
        flashcardAdapter = MemoCardRCVAdapter(this@ViewFlashCardListActivity, mockList)
        rcv_flashcard_list.adapter = flashcardAdapter
        rcv_flashcard_list.layoutManager = LinearLayoutManager(this@ViewFlashCardListActivity, RecyclerView.VERTICAL, false)
    }

    override fun addViewEvents() {
        flashcardAdapter.setOnItemClickListener {
            quickStartActivity(EditFlashcardActivity::class.java)
        }
    }



}

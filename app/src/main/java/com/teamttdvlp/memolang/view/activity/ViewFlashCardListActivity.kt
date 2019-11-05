package com.teamttdvlp.memolang.view.activity

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.base.BaseActivity
import kotlinx.android.synthetic.main.activity_view_flash_card_list.*
import com.teamttdvlp.memolang.databinding.ActivityViewFlashCardListBinding
import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.view.adapter.FlashcardRCVAdapter
import com.teamttdvlp.memolang.view.helper.getActivityViewModel
import com.teamttdvlp.memolang.model.model.FlashcardSet
import com.teamttdvlp.memolang.viewmodel.view_flashcard.ViewFlashCardViewModel
import java.lang.Exception

const val FLASHCARD_KEY = "memocard"

const val EDIT_FLASHCARD_REQUEST_CODE = 1801

class ViewFlashCardListActivity : BaseActivity<ActivityViewFlashCardListBinding, ViewFlashCardViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_view_flash_card_list

    override fun takeViewModel(): ViewFlashCardViewModel = getActivityViewModel()

    lateinit var flashcardAdapter : FlashcardRCVAdapter

    override fun addViewControls() { dataBinding.apply {
        var flashcardSet : FlashcardSet
        try {
            flashcardSet = intent.getSerializableExtra(FLASHCARD_SET_KEY) as FlashcardSet
        } catch (ex : Exception) {
            flashcardSet = FlashcardSet("null")
        }
        flashcardAdapter = FlashcardRCVAdapter(this@ViewFlashCardListActivity, flashcardSet.flashcards)
        rcv_flashcard_list.adapter = flashcardAdapter
        rcv_flashcard_list.layoutManager = LinearLayoutManager(this@ViewFlashCardListActivity, RecyclerView.VERTICAL, false)
    }}

    override fun addViewEvents() { dataBinding.apply {
        flashcardAdapter.setOnEditButtonClickListener { card ->
            val intent = Intent(this@ViewFlashCardListActivity, EditFlashcardActivity::class.java)
            intent.putExtra(FLASHCARD_KEY, card)
            startActivityForResult(intent, EDIT_FLASHCARD_REQUEST_CODE)
        }
    }}


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_FLASHCARD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val newFlashcard = data!!.getSerializableExtra(UPDATED_FLASHCARD) as Flashcard
                for ((index, card)  in flashcardAdapter.list.withIndex()) {
                    if (card.id == newFlashcard.id) {
                        flashcardAdapter.list[index] = newFlashcard
                        flashcardAdapter.notifyItemChanged(index)
                    }
                }
            }
        }
    }

}

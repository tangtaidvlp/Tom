package com.teamttdvlp.memolang.view.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityUseFlashcardDoneBinding
import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.view.activity.iview.UseFlashcardDoneView
import com.teamttdvlp.memolang.view.adapter.ForgottenFlashcardRCVAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.appear
import com.teamttdvlp.memolang.view.helper.getActivityViewModel
import com.teamttdvlp.memolang.viewmodel.use_flashcard_done.UseFlashcardDoneAcivityViewModel

class UseFlashcardDoneActivity : BaseActivity<ActivityUseFlashcardDoneBinding, UseFlashcardDoneAcivityViewModel>(), UseFlashcardDoneView {

    lateinit var rcvForgottenCardAdapter : ForgottenFlashcardRCVAdapter

    private var forgotenCardList : ArrayList<Flashcard>? = null

    override fun getLayoutId(): Int = R.layout.activity_use_flashcard_done

    override fun takeViewModel(): UseFlashcardDoneAcivityViewModel = getActivityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        viewModel.checkUserWellDone(getForgottenCardList())
    }

    override fun addViewControls() { dataBinding.apply {
        rcvForgottenCardAdapter = ForgottenFlashcardRCVAdapter(this@UseFlashcardDoneActivity, getForgottenCardList())
        rcvFogottenCardsList.adapter = rcvForgottenCardAdapter
        rcvFogottenCardsList.layoutManager = LinearLayoutManager(this@UseFlashcardDoneActivity, RecyclerView.VERTICAL, false)
    }}

    override fun addViewEvents() { dataBinding.apply {

        btnGotIt.setOnClickListener {
            finish()
        }

    }}


    override fun showCongratulationText() { dataBinding.apply {
        txtCongratulation.appear()
    }}

    override fun showFoggotenCardListAndNavigations() { dataBinding.apply {
        rcvFogottenCardsList.appear()
        btnGotIt.appear()
        titleBar.appear()
    }}

    override fun waitForAWhileAndFinish () {
        Thread (object : Runnable {
            override fun run() {
                Thread.sleep(1500)
                runOnUiThread(Runnable {
                    finish()
                })
            }
        }).start()
    }

    fun getForgottenCardList () : ArrayList<Flashcard> {
        if (forgotenCardList == null) {
            forgotenCardList = intent.extras!!.getSerializable(FORGOTTEN_FLASHCARDS_LIST) as (ArrayList<Flashcard>)
        }
        return forgotenCardList!!
    }
}

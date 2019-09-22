package com.teamttdvlp.memolang.view.activity

import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityUseFlashcardBinding
import com.teamttdvlp.memolang.view.activity.base.BaseActivity
import com.teamttdvlp.memolang.view.activity.helper.appear
import com.teamttdvlp.memolang.view.activity.helper.disappear
import com.teamttdvlp.memolang.view.activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.viewmodel.use_flashcard_activity.UseFlashcardActivityViewModel

class UseFlashcardActivity : BaseActivity<ActivityUseFlashcardBinding, UseFlashcardActivityViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_use_flashcard

    override fun takeViewModel() : UseFlashcardActivityViewModel = getActivityViewModel()

    override fun addViewEvents() { dataBinding.apply {
        btnSwipeUp.setOnClickListener {
            groupBeforeShowCard.disappear()
            groupAfterShowCard.appear()
        }
    }}

}

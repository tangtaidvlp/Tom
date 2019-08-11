package com.teamttdvlp.memolang.view.Activity

import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.viewmodel.add_flashcard.AddFlashCardActivityViewModel
import com.teamttdvlp.memolang.databinding.ActivityAddFlashcardBinding

class AddFlashcardActivity : BaseActivity<ActivityAddFlashcardBinding, AddFlashCardActivityViewModel>() {

    override fun getLayoutId(): Int {
        return R.layout.activity_add_flashcard
    }

    override fun takeViewModel(): AddFlashCardActivityViewModel {
        return getActivityViewModel()
    }

}


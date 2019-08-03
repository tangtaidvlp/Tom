package com.teamttdvlp.memolang.view.Activity

import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityEditFlashcardBinding
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.viewmodel.EditFlashcardActivityViewModel

class EditFlashcardActivity : BaseActivity<ActivityEditFlashcardBinding, EditFlashcardActivityViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_edit_flashcard

    override fun takeViewModel(): EditFlashcardActivityViewModel = getActivityViewModel()

    override fun addViewEvents() {

    }
}

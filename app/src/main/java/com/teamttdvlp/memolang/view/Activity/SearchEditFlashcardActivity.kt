package com.teamttdvlp.memolang.view.Activity

import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.viewmodel.SearchEditFlashcardActivityViewModel
import com.teamttdvlp.memolang.databinding.ActivitySearchEditFlashcardBinding

class SearchEditFlashcardActivity : BaseActivity<ActivitySearchEditFlashcardBinding, SearchEditFlashcardActivityViewModel>() {

    override fun getLayoutId(): Int {
        return R.layout.activity_search_edit_flashcard
    }

    override fun takeViewModel(): SearchEditFlashcardActivityViewModel {
        return getActivityViewModel()
    }

}

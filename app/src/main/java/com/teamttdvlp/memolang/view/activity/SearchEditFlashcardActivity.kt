package com.teamttdvlp.memolang.view.activity

import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.view.activity.base.BaseActivity
import com.teamttdvlp.memolang.view.activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.viewmodel.search_edit_flashcard.SearchEditFlashcardActivityViewModel
import com.teamttdvlp.memolang.databinding.ActivitySearchEditFlashcardBinding

class SearchEditFlashcardActivity : BaseActivity<ActivitySearchEditFlashcardBinding, SearchEditFlashcardActivityViewModel>() {

    override fun getLayoutId(): Int {
        return R.layout.activity_search_edit_flashcard
    }

    override fun takeViewModel(): SearchEditFlashcardActivityViewModel {
        return getActivityViewModel()
    }

}

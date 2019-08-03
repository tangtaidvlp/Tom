package com.teamttdvlp.memolang.view.Activity

import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityMenuBinding
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.helper.quickStartActivity
import com.teamttdvlp.memolang.view.Activity.viewmodel.MenuActivityViewModel
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : BaseActivity<ActivityMenuBinding, MenuActivityViewModel>() {

    override fun getLayoutId(): Int {
        return R.layout.activity_menu
    }

    override fun takeViewModel(): MenuActivityViewModel {
        return getActivityViewModel()
    }

    override fun addViewControls() {
        btn_add_flashcard.setOnClickListener {
            quickStartActivity(AddFlashcardActivity::class.java)
        }

        btn_learn_flashcard.setOnClickListener {
            quickStartActivity(LanguageActivity::class.java)
        }

        btn_search.setOnClickListener {

        }
    }
}

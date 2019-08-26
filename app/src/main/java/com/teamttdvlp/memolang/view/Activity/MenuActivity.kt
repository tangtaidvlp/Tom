package com.teamttdvlp.memolang.view.Activity

import android.view.animation.Animation
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityMenuBinding
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.helper.quickStartActivity
import com.teamttdvlp.memolang.view.Activity.viewmodel.menu.MenuActivityViewModel
import kotlinx.android.synthetic.main.activity_menu.*
import javax.inject.Inject
import javax.inject.Named

class MenuActivity : BaseActivity<ActivityMenuBinding, MenuActivityViewModel>() {

//    @Named("AppearThenDisappearAnimation")
    var showAnimation : Animation? = null
//    @Inject set

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

    override fun onStart() { dataBinding.apply {
        super.onStart()
        txtOfflineModeNotification.startAnimation(showAnimation)
    }}

    @Inject
    fun init(@Named("AppearThenDisappearAnimation")
              showAnimation : Animation) {
        this.showAnimation = showAnimation
    }
}

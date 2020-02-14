package com.teamttdvlp.memolang.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import androidx.core.app.ActivityOptionsCompat
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityMenuBinding
import com.teamttdvlp.memolang.database.sql.repository.UserRepository
import com.teamttdvlp.memolang.view.activity.iview.MenuView
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.MenuActivityViewModel
import kotlinx.android.synthetic.main.activity_menu.*
import javax.inject.Inject
import javax.inject.Named

class MenuActivity : BaseActivity<ActivityMenuBinding, MenuActivityViewModel>(), MenuView {

    @field: Named("AppearThenDisappearAnimation")
    @Inject
    lateinit var showAnimation : Animation

    lateinit var userRepository : UserRepository
    @Inject set

    override fun getLayoutId(): Int {
        return R.layout.activity_menu
    }

    override fun takeViewModel(): MenuActivityViewModel {
        return getActivityViewModel() {
            MenuActivityViewModel(this@MenuActivity.application)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
    }

    override fun addViewEvents() { dB.apply {
        btn_add_flashcard.setOnClickListener {
            quickStartActivity(AddFlashcardActivity::class.java)
        }

        btn_learn_flashcard.setOnClickListener {
            quickStartActivity(LanguageActivity::class.java)
        }

        btn_search.setOnClickListener {
            quickStartActivity(SearchVocabularyActivity::class.java)
        }

        edtEngViDictionary.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val activityOptionCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@MenuActivity, dB.edtEngViDictionary, "Search_Edittext")
                val intent = Intent(this@MenuActivity, SearchEngVNDictionaryActivity::class.java)
                startActivity(intent, activityOptionCompat.toBundle())
            }
        }

    }}

    override fun overrideEnterAnim() {
        overridePendingTransition(R.anim.from_right_to_centre, R.anim.from_centre_to_left)
    }

    override fun onStart() { dB.apply {
        super.onStart()
//        if (ConnectivityManagerCompat.isActiveNetworkMetered((getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager))) {
//
//        }
    }}

}

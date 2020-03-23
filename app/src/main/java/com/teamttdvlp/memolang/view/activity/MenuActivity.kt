package com.teamttdvlp.memolang.view.activity

import android.os.Bundle
import android.view.animation.Animation
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityMenuBinding
import com.teamttdvlp.memolang.database.sql.repository.UserRepository
import com.teamttdvlp.memolang.model.RecentAddedFlashcardManager
import com.teamttdvlp.memolang.view.activity.iview.MenuView
import com.teamttdvlp.memolang.view.adapter.RCVRecent_USE_FlashcardAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.customview.MenuBackgroundListViewAdapter
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.MenuActivityViewModel
import javax.inject.Inject
import javax.inject.Named

class MenuActivity : BaseActivity<ActivityMenuBinding, MenuActivityViewModel>(), MenuView {

    @field: Named("AppearThenDisappearAnimation")
    @Inject
    lateinit var showAnimation : Animation

    lateinit var userRepository : UserRepository
    @Inject set

    lateinit var backgroundListAdapter : MenuBackgroundListViewAdapter
    @Inject set

    lateinit var rcvRecentUseFCAdapter : RCVRecent_USE_FlashcardAdapter
    @Inject set

    lateinit var recentAddedFlashcardManager : RecentAddedFlashcardManager
    @Inject set

    override fun getLayoutId(): Int {
        return R.layout.activity_menu
    }

    override fun takeViewModel(): MenuActivityViewModel {
        return getActivityViewModel() {
            MenuActivityViewModel(this@MenuActivity.application, recentAddedFlashcardManager)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        loadRecentAddedFlashcard()
    }

    override fun onRestart() {
        super.onRestart()
        quickLog("On Restart")
        loadRecentAddedFlashcard()
    }

    override fun addViewControls() { dB.apply {
        lvBackground.adapter = backgroundListAdapter
        rcvRecentUseFlashcards.adapter = rcvRecentUseFCAdapter
    }}

    override fun addViewEvents() { dB.apply {
        btnAddFlashcard.setOnClickListener {
            quickStartActivity(AddFlashcardActivity::class.java)
        }

        btnUseFlashcard.setOnClickListener {
            quickStartActivity(ViewFlashcardSetActivity::class.java)
        }

        btnSearchOnline.setOnClickListener {
            quickStartActivity(SearchOnlineActivity::class.java)
        }

        txtEngViDictionary.setOnClickListener() {
            quickStartActivity(SearchEngVNDictionaryActivity::class.java)
//                val activityOptionCompat =
//                    ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        this@MenuActivity, dB.edtEngViDictionary, "Search_Edittext")
//                val intent = Intent(this@MenuActivity, SearchEngVNDictionaryActivity::class.java)
//                startActivity(intent, activityOptionCompat.toBundle())
        }

        btnEnViDictionary.setOnClickListener {
            txtEngViDictionary.performClick()
        }

    }}

    override fun overrideEnterAnim() {
        overridePendingTransition(R.anim.from_right_to_centre, R.anim.from_centre_to_left)
    }

    fun loadRecentAddedFlashcard () {
        quickLog("Load")
        viewModel.getRecentAddedFlashcard {
            rcvRecentUseFCAdapter.setData(it)
            quickLog("Size: ${it.size}")
            for (card in it) {
                quickLog("Card: $card")
            }
            if (it.size != 0) {
                dB.lvBackground.hide()
                if (it.size  > 3) {
                    dB.txtAddFlashcard.disappear()
                    dB.txtEnViDictionary.disappear()
                    dB.txtQuickAdd.disappear()
                    dB.txtSearchOnline.disappear()
                    dB.txtUseFlashcard.disappear()
                }
            } else {
                dB.lvBackground.appear()
            }
        }
    }
}

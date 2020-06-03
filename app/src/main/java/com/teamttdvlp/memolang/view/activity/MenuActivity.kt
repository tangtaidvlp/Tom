package com.teamttdvlp.memolang.view.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import androidx.core.view.doOnPreDraw
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityMenuBinding
import com.teamttdvlp.memolang.databinding.LayoutFloatAddBodyBinding
import com.teamttdvlp.memolang.databinding.LayoutFloatAddIconBinding
import com.teamttdvlp.memolang.view.activity.iview.MenuView
import com.teamttdvlp.memolang.view.adapter.RCV_FlashcardSetAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.customview.floating_library.FloatingAddServiceManager
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.MenuActivityViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.abs


class MenuActivity : BaseActivity<ActivityMenuBinding, MenuActivityViewModel>(), MenuView {

    @field: Named("AppearThenDisappearAnimation")
    @Inject
    lateinit var showAnimation : Animation

    lateinit var viewModelProviderFactory : ViewModelProviderFactory
    @Inject set

    lateinit var flashcardSetAdapter : RCV_FlashcardSetAdapter
    @Inject set

    private var orginalBurgerWidth : Int? = null

    private lateinit var floatingQuickAddService : FloatingAddServiceManager

    override fun getLayoutId(): Int {
        return R.layout.activity_menu
    }

    override fun takeViewModel(): MenuActivityViewModel {
        return getActivityViewModel(viewModelProviderFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(resources.getColor(R.color.app_blue))
        viewModel.setUpView(this)
        loadFlashcardSet()
        dB.btnMenuButton.doOnPreDraw {
            if (orginalBurgerWidth == null) {
                orginalBurgerWidth = dB.btnMenuButton.width
            }
        }
    }

    override fun onStart() {
        super.onStart()
        loadFlashcardSet()
        if (dB.btnMenuButton.width != orginalBurgerWidth) {
            if (orginalBurgerWidth != null) {
                dB.btnMenuButton.layoutParams.width = orginalBurgerWidth!!
                dB.btnMenuButton.requestFocus()
                dB.btnMenuButton.goVISIBLE()
            }
        }
    }

    override fun addViewControls() { dB.apply {
        val headDB = LayoutFloatAddIconBinding.inflate(LayoutInflater.from(this@MenuActivity))
        val bodyDB = LayoutFloatAddBodyBinding.inflate(LayoutInflater.from(this@MenuActivity))

        floatingQuickAddService = FloatingAddServiceManager.createInstance(this@MenuActivity, headDB, bodyDB)!!
        floatingQuickAddService.stopService()

        rcvFlashcardSetList.adapter = flashcardSetAdapter
    }}

    override fun addViewEvents() { dB.apply {

        btnAddFlashcard.setOnClickListener {
            quickStartActivity(RetrofitAddFlashcardActivity::class.java)
        }

        btnSearchOnline.setOnClickListener {
            quickStartActivity(SearchOnlineActivity::class.java)
        }

        edtEngViDictionary.setOnClickListener {
            quickStartActivity(SeeVocabularyActivity::class.java)
        }

        btnEnViDictionary.setOnClickListener {
            edtEngViDictionary.performClick()
        }

        btnQuickAdd.setOnClickListener {
            createFloatingWidget()
        }

        flashcardSetAdapter.setOnBtnViewListClickListener {
            val intent = Intent(this@MenuActivity, ViewFlashCardListActivity::class.java)
            intent.putExtra(FLASHCARD_SET_KEY, it)
            startActivityForResult(intent, VIEW_LIST_REQUEST_CODE)
        }

        flashcardSetAdapter.setOnBtnAddClickListener { flashcardSet ->
            AddFlashcardActivity.requestAddLanguage(this@MenuActivity, flashcardSet)
        }

        flashcardSetAdapter.setOnBtnUseFlashcardClickListener { flashcardSet ->
            UseFlashcardActivity.requestReviewFlashcard(
                this@MenuActivity,
                flashcardSet,
                reverseCardTextAndTranslation = false
            )
        }

        flashcardSetAdapter.setOnBtnReviewFlashcardHardClickListener { flashcardSet ->
            ReviewFlashcardActivity.requestReviewFlashcard(
                this@MenuActivity,
                flashcardSet,
                reverseCardTextAndTranslation = false
            )
        }

        flashcardSetAdapter.setOnBtnReviewFlashcardEasyClickListener { flashcardSet ->
            ReviewFlashcardEasyActivity.requestReviewFlashcard(this@MenuActivity, flashcardSet)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rcvFlashcardSetList.setOnScrollChangeListener(object : View.OnScrollChangeListener {
                private var defaultScrollY : Float? = null
                private var lineIsShowed = false
                override fun onScrollChange( v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                    if (defaultScrollY == null) {
                        defaultScrollY = rcvFlashcardSetList.getChildAt(0).y
                    }
                    val userStartScrollDown = abs(rcvFlashcardSetList.getChildAt(0).y - defaultScrollY!!) > rcvFlashcardSetList.paddingTop / 2
                    if (userStartScrollDown and lineIsShowed.not()) {
                        lineIsShowed = true
                        imgDeviderLine.animate().alpha(1f).duration = 200
                        return
                    }

                    val userScrollToTopMost = abs(rcvFlashcardSetList.getChildAt(0).y - defaultScrollY!!) < 2.dp()
                    if (userScrollToTopMost and lineIsShowed) {
                        lineIsShowed = false
                        imgDeviderLine.animate().alpha(0f).duration = 200
                    }
                }
            })
        }
    }}

    override fun overrideEnterAnim() {
        overridePendingTransition(R.anim.from_right_to_centre, R.anim.from_centre_to_left)
    }

    private fun createFloatingWidget() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(
                this
            )
        ) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 3)
        } else
            startFloatingWidgetService()
    }

    private fun loadFlashcardSet () {
        viewModel.getAllFlashcardSets {
            flashcardSetAdapter.setData(it!!)
        }
    }


    private fun startFloatingWidgetService() {
        floatingQuickAddService.startService()
        finish()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK)
                startFloatingWidgetService() else
            quickToast("PERMISION DENIED")
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}

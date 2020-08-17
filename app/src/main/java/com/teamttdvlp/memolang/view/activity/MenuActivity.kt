package com.teamttdvlp.memolang.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.widget.TextView
import androidx.core.view.isVisible
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.databinding.ActivityMenuBinding
import com.teamttdvlp.memolang.databinding.LayoutFloatAddBodyBinding
import com.teamttdvlp.memolang.databinding.LayoutFloatAddIconBinding
import com.teamttdvlp.memolang.view.activity.iview.MenuView
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter
import com.teamttdvlp.memolang.view.adapter.RCVRecentUsedLanguageAdapter
import com.teamttdvlp.memolang.view.adapter.RCV_FlashcardSetAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.customview.AppThemeManager
import com.teamttdvlp.memolang.view.customview.NormalOutExtraSlowIn
import com.teamttdvlp.memolang.view.customview.Theme
import com.teamttdvlp.memolang.view.customview.floating_library.FloatingAddServiceManager
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.MenuActivityViewModel
import javax.inject.Inject
import javax.inject.Named


class MenuActivity : BaseActivity<ActivityMenuBinding, MenuActivityViewModel>(), MenuView {

    @field: Named("AppearThenDisappearAnimation")
    @Inject
    lateinit var showAnimation: Animation

    lateinit var viewModelProviderFactory: ViewModelProviderFactory
        @Inject set

    lateinit var flashcardSetAdapter: RCV_FlashcardSetAdapter
        @Inject set

    lateinit var rcvFrontLangChooseLanguageAdapter: RCVChooseLanguageAdapter
        @Inject set

    lateinit var rcvFrontLangRecentChosenLanguageAdapter: RCVRecentUsedLanguageAdapter
        @Inject set

    lateinit var rcvBackLangChooseLanguageAdapter: RCVChooseLanguageAdapter
        @Inject set

    lateinit var rcvBackLangRecentChosenLanguageAdapter: RCVRecentUsedLanguageAdapter
        @Inject set


    private var orginalBurgerWidth: Int? = null

    private lateinit var floatingQuickAddService: FloatingAddServiceManager

    override fun getLayoutId(): Int {
        return R.layout.activity_menu
    }

    override fun takeViewModel(): MenuActivityViewModel {
        return getActivityViewModel(viewModelProviderFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(resources.getColor(R.color.app_blue))
        window.callback.onWindowFocusChanged(true)
        viewModel.setUpView(this)
        loadFlashcardSet()
    }

    override fun onStart() {
        super.onStart()
        loadFlashcardSet()
    }

    override fun addViewControls() {
        dB.apply {
            val headDB = LayoutFloatAddIconBinding.inflate(LayoutInflater.from(this@MenuActivity))
            val bodyDB = LayoutFloatAddBodyBinding.inflate(LayoutInflater.from(this@MenuActivity))

            setUpCreateFlashcardSetDefaultInfo()

            floatingQuickAddService =
                FloatingAddServiceManager.createInstance(this@MenuActivity, headDB, bodyDB)!!
            floatingQuickAddService.stopService()

            rcvFlashcardSetList.adapter = flashcardSetAdapter

            setUpChooseLanguageRecyclerViews()

        }
    }

    private fun setUpChooseLanguageRecyclerViews() {
        dB.apply {
            // Choose LANGUAGE RecyclerView
            rcvFrontChooseLanguage.adapter = rcvFrontLangChooseLanguageAdapter
            rcvBackChooseLanguage.adapter = rcvBackLangChooseLanguageAdapter

            // Recent CHOSEN LANGUAGE RecyclerView
            rcvFrontRecentChosenLanguage.adapter = rcvFrontLangRecentChosenLanguageAdapter
            rcvBackRecentChosenLanguage.adapter = rcvBackLangRecentChosenLanguageAdapter


            rcvFrontRecentChosenLanguage.adapter = rcvFrontLangRecentChosenLanguageAdapter
            rcvBackRecentChosenLanguage.adapter = rcvBackLangRecentChosenLanguageAdapter

            viewModel.getUsedLanguageList { recentUsedLanguageList ->
                rcvFrontLangRecentChosenLanguageAdapter.setData(recentUsedLanguageList)
                rcvBackLangRecentChosenLanguageAdapter.setData(recentUsedLanguageList)
            }
        }
    }

    private fun setUpCreateFlashcardSetDefaultInfo() {
        dB.apply {
            edtNewSetFrontLanguage.setText(viewModel.getCurrentUse_FrontLanguage())
            edtNewSetBackLanguage.setText(viewModel.getCurrentUse_BackLanguage())
        }
    }

    private var currentBeingFocusedFlashcardSet: FlashcardSet? = null

    @SuppressLint("WrongConstant")
    override fun addViewEvents() {
        dB.apply {

            layoutMenuButtons.btnSetting.setOnClickListener {
                drawerContainer.openDrawer(Gravity.START)
            }

            layoutNav.apply {
                vwgrpChooseTheme.setOnClickListener {
                    hideNavMenu_And_ShowThemePicker()
                }
            }

            layoutMenuButtons.btnButtonAdd.setOnClickListener {
                quickStartActivity(RetrofitAddFlashcardActivity::class.java)
            }

            layoutMenuButtons.btnTranslateOnline.setOnClickListener {
                quickStartActivity(SearchOnlineActivity::class.java)
            }

            layoutMenuButtons.btnDictionary.setOnClickListener {
                quickStartActivity(EngVietDictionaryActivity::class.java)
            }

            layoutMenuButtons.btnBubbleSearch.setOnClickListener {
                createFloatingWidget()
            }

            flashcardSetAdapter.setOnBtnViewListClickListener {
                val intent = Intent(this@MenuActivity, ViewFlashCardListActivity::class.java)
                intent.putExtra(FLASHCARD_SET_KEY, it)
                startActivityForResult(intent, VIEW_LIST_REQUEST_CODE)
            }

            flashcardSetAdapter.setOnBtnAddClickListener { flashcardSet ->
                RetrofitAddFlashcardActivity.requestAddLanguage(this@MenuActivity, flashcardSet)
            }

            flashcardSetAdapter.setOnBtnUseFlashcardClickListener { flashcardSet ->
                UseFlashcardActivity.requestReviewFlashcard(
                    this@MenuActivity,
                    flashcardSet,
                    reverseCardTextAndTranslation = false
                )
            }

            flashcardSetAdapter.setOnBtn_GoToWritingActivity_ClickListener { flashcardSet ->
                ReviewFlashcardActivity.requestReviewFlashcard(
                    this@MenuActivity,
                    flashcardSet,
                    reverseCardTextAndTranslation = false
                )
            }

            flashcardSetAdapter.setOnBtn_GoToPuzzleActivity_ClickListener { flashcardSet ->
                ReviewFlashcardEasyActivity.requestReviewFlashcard(
                    this@MenuActivity,
                    flashcardSet,
                    false
                )
            }

            flashcardSetAdapter.setOnBtn_Edit_FlashcardClickListener { flashcardSet ->
                currentBeingFocusedFlashcardSet = flashcardSet
                edtPanelEditSetName.setText(flashcardSet.name)
                dialogEditFlashcardSetName.show()
                showVirtualKeyboard()
                edtPanelEditSetName.requestFocus()
            }

            flashcardSetAdapter.setOnBtn_Delete_FlashcardClickListener { flashcardSet ->
                currentBeingFocusedFlashcardSet = flashcardSet
                txtConfirmDeleteFlashcardSet.setText(
                    getConfirmText(flashcardSet.name),
                    TextView.BufferType.SPANNABLE
                )
                dialogDeleteFlashcardSetName.show()
                viewModel.updateHistory()
            }

            btnPanelSaveSetName.setOnClickListener {
                val newName = edtPanelEditSetName.text.toString()
                viewModel.deleteFlashcard(currentBeingFocusedFlashcardSet!!)
                viewModel.updateSetName(currentBeingFocusedFlashcardSet!!, newName)
                viewModel.updateOtherActivitiesSharePref_Info(
                    currentBeingFocusedFlashcardSet!!.name,
                    newName
                )
                dialogEditFlashcardSetName.dismiss()
                hideVirtualKeyboard()
                flashcardSetAdapter.updateFlashcardSetName(
                    currentBeingFocusedFlashcardSet!!,
                    newName
                )
            }

            btnPanelDeleteFlashcardSet.setOnClickListener {
                viewModel.deleteFlashcard(currentBeingFocusedFlashcardSet!!)
                viewModel.updateOtherActivitiesSharePref_Info(
                    currentBeingFocusedFlashcardSet!!.name,
                    ""
                )
                dialogDeleteFlashcardSetName.dismiss()
                flashcardSetAdapter.deleteFlashcardSet(currentBeingFocusedFlashcardSet!!)
                updateButtonsMenuHeight(flashcardSetAdapter.itemCount)
            }

            // Create new flashcard set
            imgTurnOffCreateNewSet.setOnClickListener {
                hideCreateNewFlashcardSetPanel()
                if (vwgrpFrontLangChooseLanguage.isVisible) {
                    hideChooseFrontLanguageList()
                }
                if (vwgrpBackLangChooseLanguage.isVisible) {
                    hideChooseBackLanguageList()
                }
            }

            layoutMenuButtons.btnButtonNewSet.setOnClickListener {
                edtNewSetName.requestFocus()
                edtNewSetFrontLanguage.setText(viewModel.getCurrentUse_FrontLanguage())
                edtNewSetBackLanguage.setText(viewModel.getCurrentUse_BackLanguage())
                showCreateSetNamePanel()
            }

            btnPanelCreateNewSet.setOnClickListener {
                val setName = edtNewSetName.text.toString()
                val frontLang = edtNewSetFrontLanguage.text.toString()
                val backLang = edtNewSetBackLanguage.text.toString()
                val newSet = viewModel.createNewFlashcardSetIfValid(setName, frontLang, backLang)
                flashcardSetAdapter.addNewSet(newSet)
                updateButtonsMenuHeight(flashcardSetAdapter.itemCount)
            }

            imgNewSetFrontLangSpinner.setOnClickListener {
                vwgrpFrontLangChooseLanguage
                    .showByScaleUpAndFadeIn(startDelay = KEYBOARD_DISAPPEAR_INTERVAL)
                hideChooseBackLanguageList()
                hideVirtualKeyboard()
            }

            imgNewSetBackLangSpinner.setOnClickListener {
                vwgrpBackLangChooseLanguage
                    .showByScaleUpAndFadeIn(startDelay = KEYBOARD_DISAPPEAR_INTERVAL)
                hideChooseFrontLanguageList()
                hideVirtualKeyboard()
            }

            rcvFrontLangChooseLanguageAdapter.setOnItemClickListener { language ->
                onChooseFrontLanguage(language)
            }

            rcvFrontLangRecentChosenLanguageAdapter.setOnItemClickListener { language ->
                onChooseFrontLanguage(language)
            }

            rcvBackLangChooseLanguageAdapter.setOnItemClickListener { language ->
                onChooseBackLanguage(language)
            }

            rcvBackLangRecentChosenLanguageAdapter.setOnItemClickListener { language ->
                onChooseBackLanguage(language)
            }

            // Choose theme color
            val onChooseThemeColorListener = View.OnClickListener { v ->
                layoutNav.themePicker.apply {
                    when (v!!.id) {
                        btnChooseBlue.id -> {
                            AppThemeManager.theme = Theme.DEFAULT_BLUE
                        }

                        btnChooseRed.id -> {
                            AppThemeManager.theme = Theme.RED
                        }

                        btnChooseGreen.id -> {
                            AppThemeManager.theme = Theme.GREEN
                        }

                        btnChooseYellow.id -> {
                            AppThemeManager.theme = Theme.YELLOW
                        }

                        btnChoosePink.id -> {
                            AppThemeManager.theme = Theme.PINK
                        }

                        btnChooseDarkViolet.id -> {
                            AppThemeManager.theme = Theme.DARK_VIOLET
                        }
                        else -> throw Exception(" What is this button ?")
                    }
                    updateTheme()
                }
            }
            layoutNav.themePicker.apply {
                btnChooseBlue.setOnClickListener(onChooseThemeColorListener)
                btnChooseRed.setOnClickListener(onChooseThemeColorListener)
                btnChooseGreen.setOnClickListener(onChooseThemeColorListener)
                btnChooseYellow.setOnClickListener(onChooseThemeColorListener)
                btnChoosePink.setOnClickListener(onChooseThemeColorListener)
                btnChooseDarkViolet.setOnClickListener(onChooseThemeColorListener)
            }
        }
    }

    private fun updateButtonsMenuHeight(flashcardSetsCount: Int) {
        dB.apply {
            val flashcardSetList_IsShort = flashcardSetsCount <= 1
            quickLog("Count: : $flashcardSetsCount")
            quickLog("Is short: $flashcardSetList_IsShort")

            if (flashcardSetList_IsShort) {
                if (layoutShrinkMenuButtons.root.isVisible()) {
                    expandButtonsMenu()
                }
            } else {
                if (layoutShrinkMenuButtons.root.isGone()) {
                    minimizeButtonsMenu()
                }

            }
        }
    }

    private fun expandButtonsMenu() {
        dB.apply {
            layoutMenuButtons.root.goVISIBLE()
            layoutShrinkMenuButtons.root.goGONE()
        }
    }

    private fun minimizeButtonsMenu() {
        dB.apply {
            layoutMenuButtons.root.goGONE()
            layoutShrinkMenuButtons.root.goVISIBLE()
        }
    }

    override fun updateTheme() {
        dB.apply {
            when (AppThemeManager.theme) {
                Theme.DEFAULT_BLUE -> {

                    setStatusBarColor(resources.getColor(R.color.app_blue))
                }
                Theme.RED -> {

                    setStatusBarColor(resources.getColor(R.color.app_theme_dark_red))
                }
                Theme.GREEN -> {

                    setStatusBarColor(resources.getColor(R.color.app_theme_dark_green))
                }
                Theme.YELLOW -> {

                    setStatusBarColor(resources.getColor(R.color.app_theme_hard_yellow))
                }
                Theme.PINK -> {

                    setStatusBarColor(resources.getColor(R.color.app_theme_pink))
                }
                Theme.DARK_VIOLET -> {

                    setStatusBarColor(resources.getColor(R.color.app_dark_violet_theme))
                }
            }

        }
    }

    private fun onChooseFrontLanguage(language: String) {
        dB.apply {
            edtNewSetFrontLanguage.setText(language)
            hideChooseFrontLanguageList()
        }
    }

    private fun hideChooseFrontLanguageList() {
        dB.apply {
            vwgrpFrontLangChooseLanguage.hideByScaleDownAndFadeOut()
        }
    }


    private fun onChooseBackLanguage(language: String) {
        dB.apply {
            edtNewSetBackLanguage.setText(language)
            hideChooseBackLanguageList()
        }
    }

    private fun hideChooseBackLanguageList() {
        dB.apply {
            vwgrpBackLangChooseLanguage.hideByScaleDownAndFadeOut()
        }
    }


    private val NAVIGATION_SECTION_TRANSITION_DURATION = 200L

    private fun hideNavMenu_And_ShowThemePicker() {
        dB.layoutNav.apply {
            vwgrpNavMenu.animate()
                .alpha(0f)
                .setDuration(NAVIGATION_SECTION_TRANSITION_DURATION)
                .setInterpolator(NormalOutExtraSlowIn())
                .setLiteListener(onEnd = { // On hide End
                    vwgrpNavMenu.goGONE()
                    showThemePicker()
                })
        }
    }

    private fun showThemePicker() {
        dB.layoutNav.apply {
            themePicker.viewContainer.goVISIBLE()
            themePicker.viewContainer.alpha = 0f
            themePicker.viewContainer.animate()
                .alpha(1f)
                .setDuration(NAVIGATION_SECTION_TRANSITION_DURATION).interpolator =
                NormalOutExtraSlowIn()
        }
    }

    private fun getConfirmText(setName: String): CharSequence {
        return Html.fromHtml("You want to delete <font color='#F44F11'>${setName}</font> set?")
    }

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
        viewModel.getAllFlashcardSets_And_CacheIt {
            flashcardSetAdapter.setData(it!!)
            var totalCardCount = 0
            for (set in it) {
                totalCardCount += set.flashcards.size
            }
            dB.layoutNav.txtTotalCardsCount.text = totalCardCount.toString()
            updateButtonsMenuHeight(it.size)
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

    private fun showCreateSetNamePanel() {
        dB.apply {
            // Blue with 50% Black color covers
            setStatusBarColor(resources.getColor(R.color.dark_covered_blue))

            vwgrpCreateNewSet.showByScaleUpAndFadeIn()
            imgTurnOffCreateNewSet.goVISIBLE()
            imgTurnOffCreateNewSet.animate().alpha(0.5f).setDuration(100).setLiteListener(onEnd = {
                showVirtualKeyboard()
            })
        }
    }

    override fun hideCreateNewFlashcardSetPanel() {
        dB.apply {
            setStatusBarColor(resources.getColor(R.color.app_blue))

            vwgrpCreateNewSet.hideByScaleDownAndFadeOut()
            imgTurnOffCreateNewSet.animate().alpha(0f).setDuration(130)
                .setLiteListener(onEnd = {
                    hideVirtualKeyboard()
                    imgTurnOffCreateNewSet.goGONE()
                })
        }
    }

    override fun showInvalidFlashcardSetError(errorMessage: String) {
        dB.txtCreateSetError.text = errorMessage
        dB.vwgrpCreateSetError.goVISIBLE()
        dB.vwgrpCreateSetError.animate().alpha(1f).duration = 200
    }


    private fun View.hideByScaleDownAndFadeOut() {
        this.animate().alpha(0f)
            .scaleX(0f).scaleY(0f)
            .setDuration(170).setInterpolator(NormalOutExtraSlowIn())
            .setLiteListener(onStart = {
                // DO NOTHING (This prevent view from running the onStart() set by View.showByScaleUpAndFadeIn()
            }, onEnd = {
                this.goGONE()
            })
    }

    private fun View.showByScaleUpAndFadeIn(startDelay: Long = 0) {
        this.animate().alpha(1f)
            .scaleX(1f).scaleY(1f)
            .setStartDelay(startDelay)
            .setDuration(150).setInterpolator(NormalOutExtraSlowIn())
            .setLiteListener(onStart = {
                this.goVISIBLE()
            }, onEnd = {
                // DO NOTHING (This prevent view from running the onEnd() set by View.hideByScaleDownAndFadeOut()
            })
    }

}

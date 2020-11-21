package com.teamttdvlp.memolang.view.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
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

    override fun addViewSettings() {
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

    private var scrollViewMenuButtonsOrginalHeight = -1

    private fun recalcMenuButtonsDimensions() {
        dB.apply {
            scrvMenuButtons.doOnPreDraw {
                // Initialize value
                if (scrollViewMenuButtonsOrginalHeight == -1) {
                    scrollViewMenuButtonsOrginalHeight = scrvMenuButtons.height
                }

                if (layoutShrinkMenuButtons.root.isVisible) {
                    (scrvMenuButtons.layoutParams as ConstraintLayout.LayoutParams).topMargin =
                        scrollViewMenuButtonsOrginalHeight - 115.dp() - 15.dp()
                } else {
                    (scrvMenuButtons.layoutParams as ConstraintLayout.LayoutParams).topMargin =
                        scrollViewMenuButtonsOrginalHeight - 225.dp() - 15.dp()
                }
                scrvMenuButtons.requestLayout()
            }
            scrvMenuButtons.isScrollContainer = false
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

    private var currentBeingFocusedDeck: Deck? = null

    @SuppressLint("WrongConstant")
    override fun addViewEvents() {
        dB.apply {

            layoutMenuButtons.btnSetting.setOnClickListener {
                drawerContainer.openDrawer(Gravity.START)
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

            addShrinkButtonsMenuEvents()

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

            dialogDeleteFlashcardSetName.setOnStartHide {
                turnStatusBarToLighterColor(dialogDeleteFlashcardSetName.getAnimDuration())
            }

            dialogEditFlashcardSetName.setOnStartHide {
                turnStatusBarToLighterColor(dialogEditFlashcardSetName.getAnimDuration())
            }

            flashcardSetAdapter.setOnBtn_Edit_FlashcardSetClickListener { flashcardSet ->
                currentBeingFocusedDeck = flashcardSet
                edtPanelEditSetName.setText(flashcardSet.name)
                edtPanelEditSetName.setSelection(flashcardSet.name.length)
                dialogEditFlashcardSetName.show()
                turnStatusBarToDarkerColor(dialogEditFlashcardSetName.getAnimDuration())
                showVirtualKeyboard()
                edtPanelEditSetName.requestFocus()
            }

            flashcardSetAdapter.setOnBtn_Delete_FlashcardSetClickListener { flashcardSet ->
                currentBeingFocusedDeck = flashcardSet
                txtConfirmDeleteFlashcardSet.setText(
                    getConfirmText(flashcardSet.name),
                    TextView.BufferType.SPANNABLE
                )
                dialogDeleteFlashcardSetName.show()
                turnStatusBarToDarkerColor(dialogDeleteFlashcardSetName.getAnimDuration())
                viewModel.updateHistory()
            }

            btnPanelSaveSetName.setOnClickListener {
                val newName = edtPanelEditSetName.text.toString()
                viewModel.deleteFlashcardSet(currentBeingFocusedDeck!!)
                viewModel.updateSetName(currentBeingFocusedDeck!!, newName)
                viewModel.update_OtherActivitiesSharePref_Info(
                    currentBeingFocusedDeck!!.name,
                    newName
                )
                dialogEditFlashcardSetName.dismiss()
                turnStatusBarToLighterColor(dialogEditFlashcardSetName.getAnimDuration())
                hideVirtualKeyboard()
                flashcardSetAdapter.updateFlashcardSetName(
                    currentBeingFocusedDeck!!,
                    newName
                )
            }

            btnPanelDeleteFlashcardSet.setOnClickListener {
                viewModel.deleteFlashcardSet(currentBeingFocusedDeck!!)
                viewModel.update_OtherActivitiesSharePref_Info(
                    currentBeingFocusedDeck!!.name,
                    ""
                )

                dialogDeleteFlashcardSetName.dismiss()
                turnStatusBarToLighterColor(dialogDeleteFlashcardSetName.getAnimDuration())

                flashcardSetAdapter.deleteFlashcardSet(currentBeingFocusedDeck!!)
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
                edtNewSetName.setText("")
                edtNewSetName.requestFocus()
                edtNewSetFrontLanguage.setText(viewModel.getCurrentUse_FrontLanguage())
                edtNewSetBackLanguage.setText(viewModel.getCurrentUse_BackLanguage())
                updateEdtNewSetHint()
                showCreateSetNamePanel()
            }

            btnPanelClearSetName.setOnClickListener {
                edtPanelEditSetName.setText("")
            }

            btnPanelClearNewSetName.setOnClickListener {
                edtNewSetName.setText("")
            }

            btnPanelCreateNewSet.setOnClickListener {
                val setName = edtNewSetName.text.toString()
                val frontLang = edtNewSetFrontLanguage.text.toString()
                val backLang = edtNewSetBackLanguage.text.toString()

                val newSet = viewModel.createNewFlashcardSetIfValid(setName, frontLang, backLang)
                val setIsValid = newSet != null
                if (setIsValid) {
                    flashcardSetAdapter.addNewSet(newSet)
                    updateButtonsMenuHeight(flashcardSetAdapter.itemCount)
                    viewModel.set_OtherActivitiesSharePref_Info(newSet.name)
                }
            }

            imgNewSetFrontLangSpinner.setOnClickListener {
                imgNewSetFrontLangSpinner.setImageDrawable(getDrawable(R.drawable.image_button_app_spinner_blue))
                vwgrpFrontLangChooseLanguage
                    .showByScaleUpAndFadeIn(startDelay = KEYBOARD_DISAPPEAR_INTERVAL)
                hideChooseBackLanguageList()
                hideVirtualKeyboard()
            }

            imgNewSetBackLangSpinner.setOnClickListener {
                imgNewSetBackLangSpinner.setImageDrawable(getDrawable(R.drawable.image_button_app_spinner_blue))
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

            onNavigationBarViewEvents()

            btnInErrorPanelGotIt.setOnClickListener {
                hideInvalidFlashcardSetError()
            }

            edtFindSet.addTextChangeListener(onTextChanged = { text, _, _, _ ->
                if (text.length != 0) {
                    flashcardSetAdapter.search(text)
                } else {
                    flashcardSetAdapter.stopSearching()
                }
            })

            vwgrpExitDialog.setOnClickListener {
                finish()
            }

            btnCancelExit.setOnClickListener {
                backPressedTimes = 0
                hideExitDialog()
            }
        }
    }

    private var backPressedTimes = 0

    override fun onBackPressed() {
        if (dB.drawerContainer.isDrawerOpen(Gravity.LEFT)) {
            dB.drawerContainer.closeDrawer(Gravity.LEFT)
        } else if (backPressedTimes == 0) {
            backPressedTimes = 1
            showExitDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun showExitDialog() {
        dB.apply {
            vwgrpExitDialog.animate().translationY(0f)
                .setDuration(230).interpolator = NormalOutExtraSlowIn()
        }
    }

    private fun hideExitDialog() {
        dB.apply {
            vwgrpExitDialog.animate().translationY(vwgrpExitDialog.height.toFloat() + 5.dp())
                .setDuration(230).interpolator = NormalOutExtraSlowIn()
        }
    }

    private fun onNavigationBarViewEvents() {
        dB.apply {
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

            val onDrawListener = object : DrawerLayout.DrawerListener {

                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        turnStatusBarToDarkerColor(slideOffset)
                    }
                }

                override fun onDrawerStateChanged(newState: Int) {
                }

                override fun onDrawerClosed(drawerView: View) {
                }

                override fun onDrawerOpened(drawerView: View) {
                }

            }
            drawerContainer.addDrawerListener(onDrawListener)

            // Night mode
            layoutNav.switchNightMode.setOnCheckedChangeListener { v, isCheck ->
                if (isCheck) {
                    turnOnNightShiftMode()
                } else {
                    turnOffNightShiftMode()
                }
                drawerContainer.closeDrawer(Gravity.LEFT)
            }

            // Theme picker
            layoutNav.vwgrpChooseTheme.setOnClickListener {
                hideNavMenu_And_ShowThemePicker()
            }

            layoutNav.themePicker.btnChooseThemeBackToNav.setOnClickListener {
                hideThemePicker_And_ShowNavMenu()
            }

        }
    }

    private fun updateEdtNewSetHint() {
        dB.apply {
            edtNewSetName.hint =
                edtNewSetFrontLanguage.text.toString() + " - " + edtNewSetBackLanguage.text.toString()
        }
    }

    private fun turnOnNightShiftMode() {
        //TODO
    }

    private fun turnOffNightShiftMode() {
        //TODO
    }

    private fun addShrinkButtonsMenuEvents() {
        dB.apply {
            layoutShrinkMenuButtons.btnSetting.setOnClickListener {
                layoutMenuButtons.btnSetting.performClick()
            }

            layoutShrinkMenuButtons.btnBubbleSearch.setOnClickListener {
                layoutMenuButtons.btnBubbleSearch.performClick()
            }

            layoutShrinkMenuButtons.btnTranslateOnline.setOnClickListener {
                layoutMenuButtons.btnTranslateOnline.performClick()
            }

            layoutShrinkMenuButtons.btnDictionary.setOnClickListener {
                layoutMenuButtons.btnDictionary.performClick()
            }

            layoutShrinkMenuButtons.btnButtonNewSet.setOnClickListener {
                layoutMenuButtons.btnButtonNewSet.performClick()
            }

            layoutShrinkMenuButtons.btnButtonAdd.setOnClickListener {
                layoutMenuButtons.btnButtonAdd.performClick()
            }
        }
    }

    private fun updateButtonsMenuHeight(flashcardSetsCount: Int) {
        dB.apply {
            val flashcardSetList_IsShort = flashcardSetsCount <= 1

            if (flashcardSetList_IsShort) {
                expandButtonsMenu()
            } else {
                minimizeButtonsMenu()
            }
        }

        recalcMenuButtonsDimensions()
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


    private fun turnStatusBarToDarkerColor(duration: Long) {
        val darkenAnim = ValueAnimator.ofFloat(0f, 1f)
        darkenAnim.apply {
            this.duration = duration
            addUpdateListener {
                turnStatusBarToDarkerColor(it.animatedFraction)
            }
            setTarget(View(this@MenuActivity))
            start()
        }
    }

    private fun turnStatusBarToLighterColor(duration: Long) {
        val lighterAnim = ValueAnimator.ofFloat(0f, 1f)
        lighterAnim.apply {
            this.duration = duration
            addUpdateListener {
                systemOutLogging("Pro: ${it.animatedFraction}")
                turnStatusBarToDarkerColor(1f - it.animatedFraction)
            }
            setTarget(View(this@MenuActivity))
            start()
        }
    }


    val redOffset = 12 - 22 // -6
    val greenOffset = 89 - 159 // -46
    val blueOffset = 102 - 186 // -65

    private fun turnStatusBarToDarkerColor(level: Float) {
        val r = 22 + redOffset * level
        val g = 159 + greenOffset * level
        val b = 186 + blueOffset * level

        setStatusBarColor(Color.rgb(r.toInt(), g.toInt(), b.toInt()))
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
            updateEdtNewSetHint()
        }
    }

    private fun hideChooseFrontLanguageList() {
        dB.apply {
            vwgrpFrontLangChooseLanguage.hideByScaleDownAndFadeOut()
            imgNewSetFrontLangSpinner.setImageDrawable(getDrawable(R.drawable.image_button_app_spinner_grey))
        }
    }


    private fun onChooseBackLanguage(language: String) {
        dB.apply {
            edtNewSetBackLanguage.setText(language)
            hideChooseBackLanguageList()
            updateEdtNewSetHint()
        }
    }

    private fun hideChooseBackLanguageList() {
        dB.apply {
            imgNewSetBackLangSpinner.setImageDrawable(getDrawable(R.drawable.image_button_app_spinner_grey))
            vwgrpBackLangChooseLanguage.hideByScaleDownAndFadeOut()
        }
    }


    private val NAVIGATION_SECTION_TRANSITION_DURATION = 200L

    private fun hideNavMenu_And_ShowThemePicker() {

        fun showThemePicker() {
            dB.layoutNav.apply {
                themePicker.viewContainer.goVISIBLE()
                themePicker.viewContainer.alpha = 0f
                themePicker.viewContainer.animate()
                    .alpha(1f)
                    .setDuration(NAVIGATION_SECTION_TRANSITION_DURATION).setInterpolator(
                        NormalOutExtraSlowIn()
                    )
                    .setLiteListener() // Clear all listener
            }
        }

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


    private fun hideThemePicker_And_ShowNavMenu() {

        fun showNavMenu() {
            dB.layoutNav.apply {
                vwgrpNavMenu.alpha = 0f
                vwgrpNavMenu.goVISIBLE()
                vwgrpNavMenu.animate()
                    .alpha(1f)
                    .setDuration(NAVIGATION_SECTION_TRANSITION_DURATION)
                    .setInterpolator(NormalOutExtraSlowIn())
                    .setLiteListener() // Clear all listener
            }
        }

        dB.layoutNav.apply {
            themePicker.viewContainer.animate()
                .alpha(0f)
                .setDuration(NAVIGATION_SECTION_TRANSITION_DURATION)
                .setInterpolator(NormalOutExtraSlowIn())
                .setLiteListener(onEnd = { // On hide End
                    themePicker.viewContainer.goGONE()
                    showNavMenu()
                })
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
        val DURATION = 100L
        dB.apply {
            vwgrpCreateNewSet.showByScaleUpAndFadeIn()
            imgTurnOffCreateNewSet.goVISIBLE()
            imgTurnOffCreateNewSet.animate().alpha(0.5f).setDuration(DURATION)
                .setLiteListener(onEnd = {
                    showVirtualKeyboard()
                })
            turnStatusBarToDarkerColor(DURATION)
        }
    }

    override fun hideCreateNewFlashcardSetPanel() {
        val DURATION = 130L
        dB.apply {
            vwgrpCreateNewSet.hideByScaleDownAndFadeOut()
            imgTurnOffCreateNewSet.animate().alpha(0f).setDuration(DURATION)
                .setLiteListener(onEnd = {
                    hideVirtualKeyboard()
                    imgTurnOffCreateNewSet.goGONE()
                })

            turnStatusBarToLighterColor(DURATION)
        }
    }

    override fun showInvalidFlashcardSetError(errorMessage: String) {
        dB.txtCreateSetError.text = errorMessage
        dB.vwgrpCreateSetError.animate().alpha(1f)
            .setDuration(200).setInterpolator(NormalOutExtraSlowIn())
            .setLiteListener(onStart = {
                dB.vwgrpCreateSetError.goVISIBLE()
            })
    }

    override fun hideInvalidFlashcardSetError() {
        dB.vwgrpCreateSetError.animate().alpha(0f)
            .setDuration(200).setInterpolator(NormalOutExtraSlowIn())
            .setLiteListener(onEnd = {
                dB.vwgrpCreateSetError.goGONE()
            })
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

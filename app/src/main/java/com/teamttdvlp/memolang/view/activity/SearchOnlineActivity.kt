package com.teamttdvlp.memolang.view.activity

import android.animation.*
import android.animation.ValueAnimator.INFINITE
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.animation.Animation
import android.widget.TextView
import androidx.core.animation.addListener
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivitySearchOnlineBinding
import com.teamttdvlp.memolang.database.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.database.sql.repository.UserRepository
import com.teamttdvlp.memolang.database.sql.repository.UserSearchHistoryRepository
import com.teamttdvlp.memolang.model.CardType
import com.teamttdvlp.memolang.model.RecentAddedFlashcardManager
import com.teamttdvlp.memolang.model.entity.Language.Companion.LANG_DIVIDER
import com.teamttdvlp.memolang.view.activity.iview.SearchVocabularyView
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter.UserSavingAssitant.Companion.USER_REPOSITORY_POS
import com.teamttdvlp.memolang.view.adapter.RCVRecent_Search_FlashcardAdapter
import com.teamttdvlp.memolang.view.adapter.RCVSimpleListAdapter2
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.SearchOnlineViewModel
import javax.inject.Inject
import javax.inject.Named

class SearchOnlineActivity : BaseActivity<ActivitySearchOnlineBinding, SearchOnlineViewModel>()
    ,SearchVocabularyView {

    private val TYPE_NONE = 131073

    private val TYPE_TEXT = 1

    private val THE_TIME_THAT_EDIT_VIEWGROUP_APPEAR = 5000L

    private var RED : Int = 0

    private var HINT_COLOR : Int = 0

    private lateinit var moveRightAndFadeOutAnim : Animation

    private lateinit var moveLeftAndFadeOutAnim : Animation

    private var startSearch_OpenTxtTranslation_AnmtrSet : AnimatorSet = AnimatorSet()

    private var endSearchingAnmtrSet : AnimatorSet = AnimatorSet()

    private var startSearch_Without_OpenTxtTranslation_AnmtrSet : AnimatorSet = AnimatorSet()

    private var endSearchWithTransAnmtrSet : AnimatorSet = AnimatorSet()

    private var addedFlashcardAnmtrSet : AnimatorSet = AnimatorSet()

    private var showTransErrorAnmtrSet  : AnimatorSet = AnimatorSet()

    private lateinit var hideTransErrorAnmtr  : Animator


    private val AnmtrSetChooseLangAppear = AnimatorSet()

    private val AnmtrSetChooseLangDisappear = AnimatorSet()


    private val AnmtrSetCardInfoAppear = AnimatorSet()

    private val AnmtrSetCardInfoDisappear = AnimatorSet()

    lateinit var btnAddAppearAnimator : Animator

    lateinit var btnAddDisappearAnimator : Animator

    private lateinit var translatingTextAnimator : ValueAnimator

    private lateinit var progressRotateTranslatingTextAnmtion : Animation

    private var selectedLanguageTextView : TextView? = null

    @field: Named("AppearThenDisappearAnimation")
    @Inject
    lateinit var showLangSelectionErrorAnmtn : Animation

    lateinit var rcvRecentSearchFlashcardAdapter: RCVRecent_Search_FlashcardAdapter
    @Inject set

    lateinit var rcvChooseLanguageAdapter: RCVChooseLanguageAdapter
    @Inject set

    lateinit var rcvRecentChosenLanguageAdapter: RCVChooseLanguageAdapter
    @Inject set

    lateinit var rcvChooseTextTypeAdapter : RCVSimpleListAdapter2
    @Inject set

    lateinit var rcvChooseSetNameAdapter : RCVSimpleListAdapter2
    @Inject set

    lateinit var databaseManager: MemoLangSqliteDataBase
    @Inject set

    lateinit var flashcardRepository : FlashcardRepository
    @Inject set

    lateinit var userRepository : UserRepository
    @Inject set

    lateinit var searchHistoryRepository: UserSearchHistoryRepository
    @Inject set

    lateinit var recentAddedFlashcardManager: RecentAddedFlashcardManager
    @Inject set

    private var isInSearchingMode = false

    private var isTxtTranslationOpened = false

    private var isInChooseLanguageMode= false

    private var isInSeeCardInfoMode = false

    private var isInAddFlashcardMode = false

    private var addButtonDoesAppear = false

    private var previousText = ""



    override fun getLayoutId(): Int  = R.layout.activity_search_online

    override fun takeViewModel(): SearchOnlineViewModel = getActivityViewModel() {
        SearchOnlineViewModel(
            this@SearchOnlineActivity.application,
            databaseManager,
            recentAddedFlashcardManager,
            userRepository,
            flashcardRepository,
            searchHistoryRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        viewModel.getAllSearchHistoryInfo {
            rcvRecentSearchFlashcardAdapter.setData(it)
        }
    }

    override fun initProperties() { dB.apply {
        dB.viewModelS = viewModel
        txtSourceLang.text = viewModel.getUser().recentSourceLanguage
        txtTargetLang.text = viewModel.getUser().recentTargetLanguage

        RED = resources.getColor(R.color.app_red)
        HINT_COLOR = resources.getColor(R.color.hint_color)
    }}

    override fun addViewControls() { dB.apply {

        txtTranslation.hint = "Your translation here (${txtTargetLang.text})"
        val recentUseFlashcardSet = viewModel.getUser().recentUseFlashcardSet
        edtPanelSetName.setText(recentUseFlashcardSet)

        // RECENT SEARCH FLASHCARDS
        rcvRecentSearchFlashcards.adapter = rcvRecentSearchFlashcardAdapter

        // CHOOSE LANGUAGE
        layoutChooseLang.rcvChooseLanguage.adapter = rcvChooseLanguageAdapter
        rcvChooseLanguageAdapter.assistant = RCVChooseLanguageAdapter.UserSavingAssitant()
        rcvChooseLanguageAdapter.assistant!!.addAssistant(USER_REPOSITORY_POS, userRepository as Object)

        // RECENT CHOSEN LANUGAES
        layoutChooseLang.rcvRecentChosenLanguage.adapter = rcvRecentChosenLanguageAdapter
        rcvRecentChosenLanguageAdapter.setData(viewModel.getUser().recentUseLanguages)


        // CHOOSE CARD TYPE
        rcvChooseTextTypeAdapter.setData(CardType.TYPE_LIST)
        rcvChooseType.adapter = rcvChooseTextTypeAdapter

        // CHOOSE SET NAME
        rcvChooseSetNameAdapter.setData(viewModel.getUser().flashcardSetNames)
        rcvChooseSetName.adapter = rcvChooseSetNameAdapter
    }}

    override fun addViewEvents() { dB.apply {
        edtText.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                startSearchingAnimations()
            }
        }

        btnDeleteAllText.setOnClickListener {
            edtText.setText("")
            if (!isInSearchingMode) {
                 startSearch_OpenTxtTranslation_AnmtrSet.start()
            }
        }

        edtText.addTextChangeListener (onTextChanged = { text, _, _, _ ->

            if (txtSourceLang.text.toString() == txtTargetLang.text.toString()) {
                txtLangSelectionErrors.startAnimation(showLangSelectionErrorAnmtn)
                return@addTextChangeListener
            }

            // Because onFocusChangeListener also calls onTextChangeListener
            // So, making this helps us not translating the same text
            // Avoiding getting some bugs as well as Saving Translation Storage
            val isTranslatingSameText = (text.trim().equals(previousText))
            if (isTranslatingSameText) return@addTextChangeListener
            previousText = text.trim()

            performTranslatingAnimations (text)

            val sourceLang = txtSourceLang.text.toString() + ""
            val targetLang = txtTargetLang.text.toString() + ""

            translate(text, sourceLang, targetLang)
        })

        txtSourceLang.setOnClickListener {
            selectedLanguageTextView = txtSourceLang
            AnmtrSetChooseLangAppear.start()
        }

        txtTargetLang.setOnClickListener {
            selectedLanguageTextView = txtTargetLang
            AnmtrSetChooseLangAppear.start()
        }

        layoutChooseLang.imgBlackBackgroundChooseLanguage.setOnClickListener {
            AnmtrSetChooseLangDisappear.start()
        }

        rcvChooseLanguageAdapter.setOnItemClickListener { language ->
            onChooseLanguage(language)
        }

        rcvRecentChosenLanguageAdapter.setOnItemClickListener{ language ->
            onChooseLanguage(language)
        }

        rcvRecentSearchFlashcardAdapter.setOnItemClickListener { card ->
            dB.layoutCardInfo.beingViewedCard = card
            hideVirtualKeyboard()
            AnmtrSetCardInfoAppear.start()
        }


        layoutCardInfo.imgBlackBackgroundSeeCardInfo.setOnClickListener {
            AnmtrSetCardInfoDisappear.start()
        }

        btnTapToSearch.setOnClickListener {
            edtText.requestFocus()
            showVirtualKeyboard()
            btnTapToSearch.disappear()
        }

        btnAdd.setOnClickListener {
            edtPanelText.setText(edtText.text)
            edtPanelTranslation.setText(txtTranslation.text)

            addFCPanelAppear.start()
            hideVirtualKeyboard()
            rcvRecentSearchFlashcards.smoothScrollToPosition(0)

            edtPanelSetName.hint = "Default (${getDefaultSetName()})"
        }

        imgBlackBgAddFlashcardPanel.setOnClickListener {
            addFCPanelDisappear.start()
            btnAddAppearAnimator.start()
            rcvChooseType.disappear()
            rcvChooseSetName.disappear()
        }

        btnPanelAdd.setOnClickListener {
            val sourceLang = txtSourceLang.text.toString()
            val targetLang = txtTargetLang.text.toString()
            val setName = edtPanelSetName.text.toString()
            val type = edtPanelType.text.toString()
            val text = edtPanelText.text.toString()
            val translation = edtPanelTranslation.text.toString()
            val example = edtPanelExample.text.toString()
            val meanOfExample = edtPanelMeanExample.text.toString()

            viewModel.addFlashcard(
                sourceLang, targetLang, setName, type,
                text, translation, example, meanOfExample, "") { newFlashcard ->
                rcvRecentSearchFlashcardAdapter.addFlashcardAtTheFirstPosition(newFlashcard)
            }

            imgBlackBgAddFlashcardPanel.performClick()
            btnTapToSearch.appear()
            rcvChooseSetNameAdapter.addToFirst(if (setName.isNotEmpty()) setName else getDefaultSetName())
        }

        btnSwapLanguage.setOnClickListener {

            txtSourceLang.startAnimation(moveRightAndFadeOutAnim)
            txtTargetLang.startAnimation(moveLeftAndFadeOutAnim)

            val text = edtText.text.toString()

            txtTranslation.hint = "Your translation here (${txtSourceLang.text})"
            quickLog(txtTranslation.hint)
            if (text != "") {
                if ((!translatingTextAnimator.isStarted) and (txtTranslation.hint == "Your translation here (${txtSourceLang.text})"))
                    translatingTextAnimator.start()
                if (dB.imgTranslatingCircleProgressBar.animation == null) {
                    showTranslatingProgressBar()
                }
            }

            // Swap
            val sourceLang = txtTargetLang.text.toString() + ""
            val targetLang = txtSourceLang.text.toString() + ""

            translate(text, sourceLang, targetLang)
        }

        btnDeleteAllText.setOnClickListener {
            edtText.setText("")
        }

        btnRetry.setOnClickListener {
            edtText.setText(edtText.text.append(""))
            showTransErrorAnmtrSet.cancel()
            hideTransErrorAnmtr.start()
        }

        imgChooseTypeSpinner.setOnClickListener {
            if (rcvChooseType.isVisible().not()) {
                rcvChooseType.appear()
                hideVirtualKeyboard()
                rcvChooseSetName.disappear()
            } else {
                rcvChooseType.disappear()
            }
        }

        rcvChooseTextTypeAdapter.setOnItemClickListener {
            edtPanelType.setText(it)
            rcvChooseType.disappear()
        }

        imgChooseSetNameSpinner.setOnClickListener {
            if (rcvChooseSetName.isVisible().not()) {
                groupChooseSetName.appear()
                hideVirtualKeyboard()
                rcvChooseType.disappear()
            } else {
                groupChooseSetName.disappear()
            }
        }

        btnGetDefaultSetName.setOnClickListener {
            edtPanelSetName.setText(getDefaultSetName())
            groupChooseSetName.disappear()
        }

        rcvChooseSetNameAdapter.setOnItemClickListener {
            edtPanelSetName.setText(it)
            groupChooseSetName.disappear()
        }

        layoutCardInfo.btnEdit.setOnClickListener {
            val intent = Intent(this@SearchOnlineActivity, EditFlashcardActivity::class.java)
            intent.putExtra(FLASHCARD_KEY, dB.layoutCardInfo.beingViewedCard)
            startActivity(intent)
        }

    }}

    private fun getDefaultSetName () : String { dB.apply {
        return "${txtSourceLang.text}$LANG_DIVIDER${txtTargetLang.text}"
    }}

    private fun translate (text : String, sourceLang : String, targetLang : String) { dB.apply {
        val needTranslatingText = text
        viewModel.translateText(needTranslatingText, sourceLang, targetLang, object : SearchOnlineViewModel.OnTranslateListener {
            override fun onSuccess() {
                if (!addButtonDoesAppear) {
                    btnAddAppearAnimator.start()
                    translatingTextAnimator.end()
                }
            }

            override fun onFailed(ex: Exception) {
                quickLog("ERROR: ${ex.message}")
                ex.printStackTrace()
                translatingTextAnimator.end()
                hideTranslatingProgressBar()
                hideVirtualKeyboard()
                showTransErrorAnmtrSet.start()
            }
        })
    }}

    //================== LIFE CYCLE OVERRIDE METHOD ===============


    override fun onBackPressed() {
        if (isInAddFlashcardMode) dB.imgBlackBgAddFlashcardPanel.performClick()
        else if (isInChooseLanguageMode) AnmtrSetChooseLangDisappear.start()
        else if (isInSeeCardInfoMode) AnmtrSetCardInfoDisappear.start()
        else if (isInSearchingMode) startEndSearchingAnimations()
        else super.onBackPressed()
    }

    private fun onChooseLanguage (language : String) { dB.apply {
        selectedLanguageTextView?.text = language
        if (selectedLanguageTextView == txtSourceLang) {
            viewModel.updateUserRecentSourceLang(language)
        } else if (selectedLanguageTextView == txtTargetLang) {
            viewModel.updateUserRecentTargetLang(language)
            txtTranslation.hint = "Your translation here (${txtTargetLang.text})"
        }

        if (txtTargetLang.text == txtSourceLang.text) {
            txtLangSelectionErrors.alpha = 1f
            txtLangSelectionErrors.startAnimation(showLangSelectionErrorAnmtn)
        } else {
            // Trigger translating
            val text = dB.edtText.text.toString()
            performTranslatingAnimations (text)

            val sourceLang = txtSourceLang.text.toString() + ""
            val targetLang = txtTargetLang.text.toString() + ""

            translate(text, sourceLang, targetLang)
        }

        AnmtrSetChooseLangDisappear.start()
        rcvRecentChosenLanguageAdapter.addLanguage(language)
    }}


    override fun hideTranslatingProgressBar () {
        dB.apply {
            imgTranslatingCircleProgressBar.animation?.cancel()
            imgBackgroundProgress.disappear()
            imgTranslatingCircleProgressBar.disappear()
        }
    }

    override fun showTranslatingProgressBar () {
        dB.apply {
            imgTranslatingCircleProgressBar.startAnimation(progressRotateTranslatingTextAnmtion)
            imgBackgroundProgress.appear()
            imgTranslatingCircleProgressBar.appear()
        }
    }

    override fun onCheckConnectionWhenSearch (hasConnection : Boolean) { dB.apply {
        if (hasConnection) {
            if (txtTranslation.hintTextColors.defaultColor  == RED) {
                txtTranslation.hint = "Your translation here (${txtTargetLang.text})"
                txtTranslation.setHintTextColor(HINT_COLOR)
            }
        } else {
            if (translatingTextAnimator.isRunning) translatingTextAnimator.end()
            txtTranslation.hint = "No internet connection. Please check again"
            txtTranslation.text = ""
            txtTranslation.setHintTextColor(RED)
            viewModel.cancelAllCurrentTranslating()
        }
    }}

    override fun onAddFlashcardSuccess() {
        addedFlashcardAnmtrSet.start()
    }


    private fun startEndSearchingAnimations () { dB.apply {
        if (edtText.text.toString() == "") {
            endSearchingAnmtrSet.start()
        } else {
            endSearchWithTransAnmtrSet.start()
        }
        hideVirtualKeyboard()
        currentFocus?.clearFocus()
    }}

    private fun startSearchingAnimations () { dB.apply {
        if  (isTxtTranslationOpened) {
            startSearch_Without_OpenTxtTranslation_AnmtrSet.start()
        } else {
            startSearch_OpenTxtTranslation_AnmtrSet.start()
        }
    }}

    private fun swapLanguage () { dB.apply {
        var langHolder = txtSourceLang.text
        txtSourceLang.text = txtTargetLang.text
        txtTargetLang.text = langHolder
        quickLog("2: " + txtTranslation.hint)
        txtTranslation.hint == "Your translation here (${txtTargetLang.text})"
    }}

    private fun makeEditTextsMultiLine () { dB.apply {
        edtText.inputType = TYPE_NONE or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        edtText.setSelection(edtText.text.toString().length)

        txtTranslation.inputType = TYPE_NONE or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }}

    private fun makeEditTextsSingleLine () { dB.apply {
        edtText.inputType = TYPE_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        edtText.setSelection(edtText.text.toString().length)

        txtTranslation.inputType = TYPE_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }}

    private fun performTranslatingAnimations (text: String) {
        if (text == "") {
            viewModel.cancelAllCurrentTranslating()
            btnAddDisappearAnimator.start()
            translatingTextAnimator.end()
        } else if (text != "") {
            if ((!translatingTextAnimator.isStarted) and (dB.txtTranslation.hint == "Your translation here (${dB.txtTargetLang.text})"))
                translatingTextAnimator.start()
            if (dB.imgTranslatingCircleProgressBar.animation == null) {
                showTranslatingProgressBar()
            }
        }
    }

    //================== INIT ANIMATIONS ========================


    @Inject
    fun initStartSearchingAnimations (
        @Named("ViewgroupLangOptionHide") vgLangOption_HideAnimator : ValueAnimator,
        @Named("AppearAnimator") txtTranslation_FadeIn_Animator  : Animator,
        @Named("TxtTranslationAppear") txtTranslation_ScaleBigger_Animator : ValueAnimator) { dB.apply {

        vgLangOption_HideAnimator.apply {
            addUpdateListener {
                val newLayoutParams = viewgroupLanguageOption.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                viewgroupLanguageOption.layoutParams = newLayoutParams
            }
            setTarget(viewgroupLanguageOption)
        }

        txtTranslation_FadeIn_Animator.setTarget(vwgrpTxtTranslation)

        txtTranslation_ScaleBigger_Animator.apply {
            addUpdateListener {
                val newLayoutParams = vwgrpTxtTranslation.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                vwgrpTxtTranslation.layoutParams = newLayoutParams
                quickLog("Val: ${it.animatedValue as Int}")
            }

            addListener (onStart = {
                txtTranslation.appear()
                vwgrpTxtTranslation.appear()
                isTxtTranslationOpened = true
            })

            setTarget(vwgrpTxtTranslation)
        }

        val txtTranslation_AppearAnmtrSet = AnimatorSet().apply {
            play(txtTranslation_FadeIn_Animator).with(txtTranslation_ScaleBigger_Animator)
        }

        startSearch_OpenTxtTranslation_AnmtrSet
            .play(vgLangOption_HideAnimator)
            .with(txtTranslation_AppearAnmtrSet)
        startSearch_OpenTxtTranslation_AnmtrSet.addListener (onStart = {
            edtText.hint = "Type your text here (${txtSourceLang.text})"
            rcvRecentSearchFlashcards.smoothScrollToPosition(0)
            btnTapToSearch.disappear()
        }, onEnd = {
            isInSearchingMode = true
            makeEditTextsSingleLine()
        })

        startSearch_Without_OpenTxtTranslation_AnmtrSet
            .play(vgLangOption_HideAnimator)

        startSearch_Without_OpenTxtTranslation_AnmtrSet.addListener (onStart = {
            edtText.hint = "Type your text here (${txtSourceLang.text})"
            rcvRecentSearchFlashcards.smoothScrollToPosition(0)
            btnTapToSearch.disappear()
        }, onEnd = {
            isInSearchingMode = true
            makeEditTextsSingleLine()
        })
    }}

    @Inject
    fun initEndSearchingAnimations (
        @Named("ViewGroupLanguageOptionShowAnimator") vgLangOption_ShowAnimator : ValueAnimator,
        @Named("DisappearAnimator") txtTranslation_Disappear_Animator : Animator,
        @Named("TxtTranslationScaleToNothing") txtTranslation_ScaleSmaller_Animator : ValueAnimator) { dB.apply {
        vgLangOption_ShowAnimator.apply {
            addUpdateListener {
                val newLayoutParams = viewgroupLanguageOption.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                viewgroupLanguageOption.layoutParams = newLayoutParams
            }
        }

        txtTranslation_Disappear_Animator.setTarget(vwgrpTxtTranslation)

        txtTranslation_ScaleSmaller_Animator.apply {
            addUpdateListener {
                val newLayoutParams = vwgrpTxtTranslation.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                vwgrpTxtTranslation.layoutParams = newLayoutParams
            }
            addListener (onEnd = {
                txtTranslation.disappear()
                isTxtTranslationOpened = false
            })
            setTarget(vwgrpTxtTranslation)
        }

        val txtTranslation_DisappearAnmtrSet = AnimatorSet().apply {
            play(txtTranslation_Disappear_Animator).with(txtTranslation_ScaleSmaller_Animator)
        }

        endSearchingAnmtrSet
            .play(vgLangOption_ShowAnimator)
            .with(txtTranslation_DisappearAnmtrSet)
        endSearchingAnmtrSet.addListener (onStart = {
            rcvRecentSearchFlashcards.smoothScrollToPosition(0)
        }, onEnd = {
            isInSearchingMode = false
            makeEditTextsMultiLine()
            edtText.hint = "Tap here to enter text"
            btnTapToSearch.appear()
        })

        endSearchWithTransAnmtrSet
            .play(vgLangOption_ShowAnimator)
        endSearchWithTransAnmtrSet.addListener (onStart = {
            rcvRecentSearchFlashcards.smoothScrollToPosition(0)
        }, onEnd = {
            isInSearchingMode = false
            makeEditTextsMultiLine()
            edtText.hint = "Tap here to enter text"
            btnTapToSearch.appear()
        })
    }}

    @Inject
    fun initAddButtonAnimations (
        @Named("FromNothingToNormalSizeAnimator") appearAnimator : Animator,
        @Named("FromNormalSizeToNothingAnimator") disappearAnimator : Animator) {

        btnAddAppearAnimator = appearAnimator.apply {
            setTarget(dB.vwgrpBtnAddAndProgressBar)
            addListener (onStart = {
                dB.vwgrpBtnAddAndProgressBar.appear()
            }, onEnd = {
                addButtonDoesAppear = true
            })
        }

        btnAddDisappearAnimator = disappearAnimator.apply {
            setTarget(dB.vwgrpBtnAddAndProgressBar)
            addListener (onEnd = {
                dB.vwgrpBtnAddAndProgressBar.disappear()
                addButtonDoesAppear = false
                hideTranslatingProgressBar()
            })
        }
    }


    @Inject
    fun initSwapAnimations (
        @Named("MoveRightAndFadeOut") moveRightAndFadeOutAnimation : Animation,
        @Named("MoveLeftAndFadeOut") moveLeftAndFadeOutAnimation : Animation) { dB.apply {
        moveRightAndFadeOutAnim = moveRightAndFadeOutAnimation.apply {
            addAnimationLister (onStart = {
            }, onEnd = {
                swapLanguage()
            })
        }
        moveLeftAndFadeOutAnim = moveLeftAndFadeOutAnimation

    }}

    @Inject
    fun initChooseLanguageAnimation (
        @Named("FromNormalSizeToNothing") rcvChooseLanguageDisappear : Animator,
        @Named("Disappear50Percents") blackBackgroundDisappear : Animator,
        @Named("FromNothingToNormalSize") rcvChooseLanguageAppear : Animator,
        @Named("Appear50Percents") blackBackgroundAppear : Animator) { dB.apply {

        rcvChooseLanguageDisappear.setTarget(layoutChooseLang.viewgroupChooseLanguage)
        blackBackgroundDisappear.setTarget(layoutChooseLang.imgBlackBackgroundChooseLanguage)
        AnmtrSetChooseLangDisappear.play(rcvChooseLanguageDisappear).before(blackBackgroundDisappear)
        AnmtrSetChooseLangDisappear.addListener (onEnd = {
            layoutChooseLang.groupChooseLanguage.visibility = View.GONE
            isInChooseLanguageMode = false
        })

        rcvChooseLanguageAppear.setTarget(layoutChooseLang.viewgroupChooseLanguage)
        blackBackgroundAppear.setTarget(layoutChooseLang.imgBlackBackgroundChooseLanguage)
        AnmtrSetChooseLangAppear.play(blackBackgroundAppear).before(rcvChooseLanguageAppear)
        AnmtrSetChooseLangAppear.addListener(onStart = {layoutChooseLang.groupChooseLanguage.appear()},
            onEnd = {
                isInChooseLanguageMode = true
            })
    }}

    @Inject
    fun initSeeCardInfoAnimation (
        @Named("Disappear100Percents") viewgroupCardInfoDisappear : Animator,
        @Named("Disappear50Percents") blackBackgroundDisappear : Animator,
        @Named("Appear100Percents") viewgroupCardInfoAppear : Animator,
        @Named("Appear50Percents") blackBackgroundAppear : Animator) { dB.apply {

        viewgroupCardInfoDisappear.setTarget(layoutCardInfo.viewgroupCardInfo)
        blackBackgroundDisappear.setTarget(layoutCardInfo.imgBlackBackgroundSeeCardInfo)
        AnmtrSetCardInfoDisappear.play(viewgroupCardInfoDisappear).before(blackBackgroundDisappear)
        AnmtrSetCardInfoDisappear.addListener (onEnd = {
            layoutCardInfo.groupCardInfo.visibility = View.GONE
            isInSeeCardInfoMode = false
        })

        viewgroupCardInfoAppear.setTarget(layoutCardInfo.viewgroupCardInfo)
        blackBackgroundAppear.setTarget(layoutCardInfo.imgBlackBackgroundSeeCardInfo)
        AnmtrSetCardInfoAppear.play(blackBackgroundAppear).before(viewgroupCardInfoAppear)
        AnmtrSetCardInfoAppear.addListener(
        onStart = {
            layoutCardInfo.groupCardInfo.appear()
        },
        onEnd = {
            isInSeeCardInfoMode = true
        })
    }}

    @Inject
    fun initTranslatingTextAnimation (
        @Named("TranslatingText") translatingTextAnim : Animator,
        @Named("RotateForever") rotateForeverAnim : Animation ) {
        translatingTextAnimator = (translatingTextAnim as ValueAnimator)
        translatingTextAnimator.apply {
            duration = 600
            setTarget(dB.txtTranslation)
            repeatCount = INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener {
                if (it.animatedFraction < 0.25f) {
                    dB.txtTranslation.hint = "Translating"
                } else if (it.animatedFraction < 0.5f) {
                    dB.txtTranslation.hint = "Translating."
                } else if (it.animatedFraction < 0.75f) {
                    dB.txtTranslation.hint = "Translating.."
                } else if (it.animatedFraction < 1) {
                    dB.txtTranslation.hint = "Translating..."
                }
            }
            addListener (onEnd = {
                dB.txtTranslation.hint = "Your translation here (${dB.txtTargetLang.text})"
            })
        }

        progressRotateTranslatingTextAnmtion = rotateForeverAnim
    }

    @Inject
    fun initTranslateErrorAnimation (
        @Named("Appear100Percents") appearAnimation : Animator,
        @Named("Disappear100Percents") disappearAnimator: Animator,
        @Named("Disappear100Percents") disappearAnimator2: Animator)
    { dB.apply {
        disappearAnimator.startDelay = 4000
        disappearAnimator.duration = 300
        disappearAnimator2.duration = 300
        appearAnimation.duration = 300
        showTransErrorAnmtrSet.play(appearAnimation).before(disappearAnimator)
        showTransErrorAnmtrSet.setTarget(vwgrpTranslatingErrorHappened)
        showTransErrorAnmtrSet.addListener (
            onStart = {
                vwgrpTranslatingErrorHappened.appear()
            },
            onEnd = {
                vwgrpTranslatingErrorHappened.disappear()
            })

        hideTransErrorAnmtr = disappearAnimator2
        hideTransErrorAnmtr.setTarget(vwgrpTranslatingErrorHappened)
        hideTransErrorAnmtr.addListener(
            onStart = {
                vwgrpTranslatingErrorHappened.appear()
            },
            onEnd = {
                vwgrpTranslatingErrorHappened.disappear()
            })
    }}

    private val addFCPanelAppear : AnimatorSet = AnimatorSet()
    private val addFCPanelDisappear : AnimatorSet = AnimatorSet()

    @Inject
    fun initAddFlashcardPanelAnimations (
        @Named("Appear50Percents") blackBackgroundAppear : Animator,
        @Named("Disappear50Percents") blackBackgroundDisappear : Animator,
        @Named("FromNothingToNormalSize") panelAppear : Animator,
        @Named("FromNormalSizeToNothing") panelDisappear: Animator
    ) { dB.apply {

        blackBackgroundAppear.setTarget(imgBlackBgAddFlashcardPanel)
        panelAppear.setTarget(panelAddFlashcard)
        addFCPanelAppear.play(blackBackgroundAppear).before(panelAppear)
        addFCPanelAppear.addListener (onStart = {
            groupAddFlashcard.appear()
            btnAdd.disappear()
            isInAddFlashcardMode = true
        })

        blackBackgroundDisappear.setTarget(imgBlackBgAddFlashcardPanel)
        panelDisappear.setTarget(panelAddFlashcard)
        addFCPanelDisappear.play(panelDisappear).before(blackBackgroundDisappear)
        addFCPanelDisappear.addListener (onEnd = {
            btnAdd.appear()
            groupAddFlashcard.disappear()
            isInAddFlashcardMode = false
        })

    }}
}

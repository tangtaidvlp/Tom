package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.animation.Animation
import android.widget.TextView
import androidx.core.animation.addListener
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.CardProperty
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.SetNameUtils
import com.teamttdvlp.memolang.databinding.ActivitySearchOnlineBinding
import com.teamttdvlp.memolang.model.CardType
import com.teamttdvlp.memolang.view.activity.iview.SearchVocabularyView
import com.teamttdvlp.memolang.view.adapter.*
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.SearchOnlineViewModel
import javax.inject.Inject
import javax.inject.Named

const val SEARCH_ONLINE_TEXT = "sot"

class SearchOnlineActivity : BaseActivity<ActivitySearchOnlineBinding, SearchOnlineViewModel>()
    , SearchVocabularyView {

    private val TYPE_NONE = 131073

    private val TYPE_TEXT = 1

    private var RED: Int = 0

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


    private val anmtrSetChooseLangAppear = AnimatorSet()

    private val anmtrSetChooseLangDisappear = AnimatorSet()


    private val anmtrSetCardInfoAppear = AnimatorSet()

    private val anmtrSetCardInfoDisappear = AnimatorSet()

    lateinit var btnAddAppearAnimator : Animator

    lateinit var btnAddDisappearAnimator : Animator

    private lateinit var translatingTextAnimator : ValueAnimator

    private lateinit var progressRotateTranslatingTextAnmtion : Animation

    private var selectedLanguageTextView : TextView? = null

    @field: Named("AppearThenDisappearAnimation")
    @Inject
    lateinit var showLangSelectionErrorAnmtn : Animation

    lateinit var rcvRecentSearchedFlashcardAdapter: RCVRecent_Search_FlashcardAdapter
    @Inject set

    lateinit var rcvChooseLanguageAdapter: RCVChooseLanguageAdapter
    @Inject set

    lateinit var rcvRecentChosenLanguageAdapter: RCVRecentUsedLanguageAdapter
    @Inject set

    lateinit var rcvChooseCardTypeAdapter : RCVSimpleListAdapter2
    @Inject set

    lateinit var rcvChooseSetNameAdapter : RCVSimpleListChooseSetNameAdapter
    @Inject set

    lateinit var viewModelProviderFactory : ViewModelProviderFactory
    @Inject set

    private var isInSearchingMode = false

    private var isTxtTranslationOpened = false

    private var isInChooseLanguageMode= false

    private var isInSeeCardInfoMode = false

    private var isInAddFlashcardMode = false

    private var addButtonDoesAppear = false

    private var previousText = ""

    private var txtTranslationHintHolder = ""

    companion object {
        fun requestSearchOnline(context: Context, text: String) {
            val intent = Intent(context, SearchOnlineActivity::class.java)
            intent.putExtra(SEARCH_ONLINE_TEXT, text)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_search_online

    override fun takeViewModel(): SearchOnlineViewModel =
        getActivityViewModel(viewModelProviderFactory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        val searchOnlineRequest = getSearchOnlineRequest()
        if (searchOnlineRequest != null) {
            autoSearch(searchOnlineRequest)
        }
    }

    private fun autoSearch(searchOnlineRequest: String) {
        dB.edtText.setText(searchOnlineRequest)
        startSearchingAnimations()
    }

    private fun getSearchOnlineRequest(): String? {
        if (intent.extras == null) return null
        return intent.extras!!.getString(SEARCH_ONLINE_TEXT, null)
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveSearchHistory()
    }

    override fun initProperties() {
        dB.apply {
            dB.vwModel = viewModel

        RED = resources.getColor(R.color.app_red)
        HINT_COLOR = resources.getColor(R.color.hint_color)
    }}

    override fun addViewControls() { dB.apply {

        // RECENT USED LANGUAGES
        txtSourceLang.text = viewModel.getCurrentSourceLanguage()
        txtTargetLang.text = viewModel.getCurrentTargetLanguage()

        // LASTED USED FLASHCARD SET
        updateTxtTranslationHintHolder(txtTargetLang.text.toString())
        txtTranslation.hint = txtTranslationHintHolder
        val lastedUsedFlashcardSet = viewModel.getLastedUsedFlashcardSet()
        edtPanelSetName.setText(lastedUsedFlashcardSet)

        // RECENT SEARCH FLASHCARDS
        rcvRecentSearchedFlashcards.adapter = rcvRecentSearchedFlashcardAdapter
        viewModel.getAllRecentOnlineSearchedFlashcard { allRecentSearchedFlashcard ->
            rcvRecentSearchedFlashcardAdapter.setData(allRecentSearchedFlashcard)
        }

        // CHOOSE LANGUAGE
        layoutChooseLang.rcvChooseLanguage.adapter = rcvChooseLanguageAdapter

        // RECENT CHOSEN LANUGAES
        layoutChooseLang.rcvRecentChosenLanguage.adapter = rcvRecentChosenLanguageAdapter
        viewModel.getUserUsedLanguageList(onGet = {
            rcvRecentChosenLanguageAdapter.setData(it)
        })

        // CHOOSE CARD TYPE
        val cardTypeList = ArrayList<String>().apply {
            addAll(CardType.TYPE_LIST)
            for (type in viewModel.getUserOwnCardTypes()) {
                if (contains(type)) {
                    remove(type)
                }
                add(0, type)
            }
        }
        rcvChooseCardTypeAdapter.setData(cardTypeList)
        rcvChooseCardType.adapter = rcvChooseCardTypeAdapter

        // CHOOSE SET NAME
        rcvChooseSetName.adapter = rcvChooseSetNameAdapter
        viewModel.getAllFlashcardSetWithNOCardList { flashcardSetList ->
            rcvChooseSetNameAdapter.setData(flashcardSetList)
        }
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
            val isTranslatingSameText = (text.trim() == previousText)
            if (isTranslatingSameText) return@addTextChangeListener
            previousText = text.trim()

            performTranslatingAnimations (text)

            val sourceLang = txtSourceLang.text.toString() + ""
            val targetLang = txtTargetLang.text.toString() + ""

            translate(text, sourceLang, targetLang)
        })

        txtSourceLang.setOnClickListener {
            selectedLanguageTextView = txtSourceLang
            anmtrSetChooseLangAppear.start()
        }

        txtTargetLang.setOnClickListener {
            selectedLanguageTextView = txtTargetLang
            anmtrSetChooseLangAppear.start()
        }

        layoutChooseLang.imgBlackBackgroundChooseLanguage.setOnClickListener {
            anmtrSetChooseLangDisappear.start()
        }

        rcvChooseLanguageAdapter.setOnItemClickListener { language ->
            onChooseLanguage(language)
        }

        rcvRecentChosenLanguageAdapter.setOnItemClickListener{ language ->
            onChooseLanguage(language)
        }

        rcvRecentSearchedFlashcardAdapter.setOnItemClickListener { card ->
            dB.layoutCardInfo.beingViewedCard = card
            hideVirtualKeyboard()
            anmtrSetCardInfoAppear.start()
        }


        layoutCardInfo.imgBlackBackgroundSeeCardInfo.setOnClickListener {
            anmtrSetCardInfoDisappear.start()
        }

        btnNavigateToSearchZone.setOnClickListener {
            edtText.requestFocus()
            showVirtualKeyboard()
            btnNavigateToSearchZone.goGONE()
        }

        btnAdd.setOnClickListener {
            edtPanelText.text = edtText.text
            edtPanelTranslation.setText(txtTranslation.text)

            addFCPanelAppear.start()
            hideVirtualKeyboard()
            rcvRecentSearchedFlashcards.smoothScrollToPosition(0)

            edtPanelSetName.hint = "Default (${getDefaultSetName()})"
            val frontLanguage = txtSourceLang.text.toString()
            val backLanguage = txtTargetLang.text.toString()
            rcvChooseSetNameAdapter.filtFlashcardSetByLanguagePair(frontLanguage, backLanguage)
        }

        imgBlackBgAddFlashcardPanel.setOnClickListener {
            addFCPanelDisappear.start()
            btnAddAppearAnimator.start()
            rcvChooseCardType.goGONE()
            rcvChooseSetName.goGONE()
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

            val newCard = Flashcard(
                0,
                setOwner = setName,
                text = text,
                translation = translation,
                frontLanguage = sourceLang,
                backLanguage = targetLang,
                example = example,
                meanOfExample = meanOfExample,
                type = type,
                cardProperty = CardProperty()
            )
            // TODO (Check property)

            viewModel.addFlashcard_And_UpdateUserInfo(newCard, onAddSuccess = { newFlashcard ->
                rcvRecentSearchedFlashcardAdapter.addFlashcardAtTheFirstPosition(newFlashcard)
            })

            imgBlackBgAddFlashcardPanel.performClick()
            btnNavigateToSearchZone.goVISIBLE()
            rcvChooseSetNameAdapter.addToFirst(Deck(setName, sourceLang, targetLang))
        }

        btnSwapLanguage.setOnClickListener {
            txtTranslation.text = ""

            // Swap
            val sourceLang = txtTargetLang.text.toString() + ""
            val targetLang = txtSourceLang.text.toString() + ""
            val text = edtText.text.toString()

            translate(text, sourceLang, targetLang)

            txtSourceLang.startAnimation(moveRightAndFadeOutAnim)
            txtTargetLang.startAnimation(moveLeftAndFadeOutAnim)
            val newTargetLang = txtSourceLang.text.toString()
            val oldHint = txtTranslationHintHolder
            updateTxtTranslationHintHolder(newTargetLang)

            if (text != "") {
                if ((translatingTextAnimator.isStarted.not()) and (txtTranslation.hint == oldHint))
                    translatingTextAnimator.start()
                if (dB.imgTranslatingCircleProgressBar.animation == null) {
                    showTranslatingProgressBar()
                }
            }

        }

        btnDeleteAllText.setOnClickListener {
            edtText.setText("")
        }

        btnRetry.setOnClickListener {
            edtText.text = edtText.text.append("")
            showTransErrorAnmtrSet.cancel()
            hideTransErrorAnmtr.start()
        }

        imgChooseTypeSpinner.setOnClickListener {
            if (rcvChooseCardType.isVisible().not()) {
                rcvChooseCardType.goVISIBLE()
                hideVirtualKeyboard()
                rcvChooseSetName.goGONE()
            } else {
                rcvChooseCardType.goGONE()
            }
        }

        rcvChooseCardTypeAdapter.setOnItemClickListener {
            edtPanelType.setText(it)
            rcvChooseCardType.goGONE()
        }

        imgChooseSetNameSpinner.setOnClickListener {
            if (rcvChooseSetName.isVisible().not()) {
                hideVirtualKeyboard()
                rcvChooseCardType.goGONE()
            }
        }

        rcvChooseSetNameAdapter.setOnItemClickListener {flashcardSet ->
            edtPanelSetName.setText(flashcardSet.name)
        }

        layoutCardInfo.btnEdit.setOnClickListener {
            val intent = Intent(this@SearchOnlineActivity, EditFlashcardActivity::class.java)
            intent.putExtra(FLASHCARD_KEY, dB.layoutCardInfo.beingViewedCard)
            startActivity(intent)
        }

    }}

    private fun getDefaultSetName () : String { dB.apply {
        return SetNameUtils.getSetNameFromLangPair(txtSourceLang.text.toString(), txtTargetLang.text.toString())
    }}

    private fun translate (text : String, sourceLang : String, targetLang : String) { dB.apply {
        viewModel.translateText(text, sourceLang, targetLang, object : SearchOnlineViewModel.OnTranslateListener {
            override fun onSuccess() {
                if (!addButtonDoesAppear) {
                    btnAddAppearAnimator.start()
                    translatingTextAnimator.end()
                }
            }

            override fun onFailed(ex: Exception) {
                systemOutLogging("ERROR: ${ex.message}")
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
        when {
            isInAddFlashcardMode -> dB.imgBlackBgAddFlashcardPanel.performClick()
            isInChooseLanguageMode -> anmtrSetChooseLangDisappear.start()
            isInSeeCardInfoMode -> anmtrSetCardInfoDisappear.start()
            isInSearchingMode -> startEndSearchingAnimations()
            else -> super.onBackPressed()
        }
    }

    private fun onChooseLanguage (language : String) { dB.apply {
        selectedLanguageTextView?.text = language
        if (selectedLanguageTextView == txtSourceLang) {
            viewModel.updateUserRecentSourceLang(language)
        } else if (selectedLanguageTextView == txtTargetLang) {
            viewModel.updateUserRecentTargetLang(language)
            updateTxtTranslationHintHolder(language)
            txtTranslation.hint = txtTranslationHintHolder
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

        anmtrSetChooseLangDisappear.start()
        addToUsedLanguageList(language)
    }}

    private fun addToUsedLanguageList (language : String) {
        rcvRecentChosenLanguageAdapter.addLanguage(language)
        viewModel.addToUsedLanguageList(language)
    }

    override fun hideTranslatingProgressBar () {
        dB.apply {
            imgTranslatingCircleProgressBar.animation?.cancel()
            imgTranslatingCircleProgressBar.goGONE()
        }
    }

    override fun showTranslatingProgressBar () {
        dB.apply {
            imgTranslatingCircleProgressBar.startAnimation(progressRotateTranslatingTextAnmtion)
            imgTranslatingCircleProgressBar.goVISIBLE()
        }
    }

    override fun onCheckConnectionWhenSearch (hasConnection : Boolean) { dB.apply {
        if (hasConnection) {
            if (txtTranslation.hintTextColors.defaultColor  == RED) {
                txtTranslation.hint = txtTranslationHintHolder
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
        val  langHolder = txtSourceLang.text
        txtSourceLang.text = txtTargetLang.text
        txtTargetLang.text = langHolder
        updateTxtTranslationHintHolder(txtTargetLang.text.toString())
        if (translatingTextAnimator.isRunning.not()) {
            txtTranslation.hint = txtTranslationHintHolder
        }
    }}

    private fun updateTxtTranslationHintHolder (language: String) {
        txtTranslationHintHolder = "Your translation here ($language)"
    }

    private fun makeEditTextsMultiLine () { dB.apply {
        edtText.inputType = TYPE_NONE or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        edtText.setSelection(edtText.text.toString().length)

        txtTranslation.inputType = TYPE_NONE or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }}


    private fun performTranslatingAnimations (text: String) {
        if (text == "") {
            viewModel.cancelAllCurrentTranslating()
            btnAddDisappearAnimator.start()
            translatingTextAnimator.end()
        } else if (text != "") {
            if ((translatingTextAnimator.isStarted.not()) and (dB.txtTranslation.hint == txtTranslationHintHolder))
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
            addListener(onEnd = {
                viewgroupLanguageOption.goGONE()
            })
            setTarget(viewgroupLanguageOption)
        }

        txtTranslation_FadeIn_Animator.setTarget(txtTranslation)

        txtTranslation_ScaleBigger_Animator.apply {
            addUpdateListener {
                val newLayoutParams = txtTranslation.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                txtTranslation.layoutParams = newLayoutParams
            }

            addListener (onStart = {
                txtTranslation.goVISIBLE()
                txtTranslation.goVISIBLE()
                isTxtTranslationOpened = true
            })

            setTarget(txtTranslation)
        }

        val txtTranslation_AppearAnmtrSet = AnimatorSet().apply {
            play(txtTranslation_FadeIn_Animator).with(txtTranslation_ScaleBigger_Animator)
        }

        startSearch_OpenTxtTranslation_AnmtrSet
            .play(vgLangOption_HideAnimator)
            .with(txtTranslation_AppearAnmtrSet)
        startSearch_OpenTxtTranslation_AnmtrSet.addListener (onStart = {
            edtText.hint = "Type your text here (${txtSourceLang.text})"
            rcvRecentSearchedFlashcards.smoothScrollToPosition(0)
            btnNavigateToSearchZone.goGONE()
        }, onEnd = {
            isInSearchingMode = true
        })

        startSearch_Without_OpenTxtTranslation_AnmtrSet
            .play(vgLangOption_HideAnimator)

        startSearch_Without_OpenTxtTranslation_AnmtrSet.addListener (onStart = {
            edtText.hint = "Type your text here (${txtSourceLang.text})"
            rcvRecentSearchedFlashcards.smoothScrollToPosition(0)
            btnNavigateToSearchZone.goGONE()
        }, onEnd = {
            isInSearchingMode = true
        })
    }}

    @Inject
    fun initEndSearchingAnimations (
        @Named("ViewGroupLanguageOptionShowAnimator") vgLangOption_ShowAnimator : ValueAnimator,
        @Named("DisappearAnimator") txtTranslation_Disappear_Animator : Animator,
        @Named("TxtTranslationScaleToNothing") txtTranslation_ScaleSmaller_Animator : ValueAnimator) { dB.apply {
        vgLangOption_ShowAnimator.apply {
            setTarget(vgLangOption_ShowAnimator)
            addUpdateListener {
                val newLayoutParams = viewgroupLanguageOption.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                viewgroupLanguageOption.layoutParams = newLayoutParams
            }
            addListener (onStart = {
                viewgroupLanguageOption.goVISIBLE()
            })
        }

        txtTranslation_Disappear_Animator.setTarget(txtTranslation)

        txtTranslation_ScaleSmaller_Animator.apply {
            addUpdateListener {
                val newLayoutParams = txtTranslation.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                txtTranslation.layoutParams = newLayoutParams
            }
            addListener (onEnd = {
                txtTranslation.goGONE()
                isTxtTranslationOpened = false
            })
            setTarget(txtTranslation)
        }

        val txtTranslation_DisappearAnmtrSet = AnimatorSet().apply {
            play(txtTranslation_Disappear_Animator).with(txtTranslation_ScaleSmaller_Animator)
        }

        endSearchingAnmtrSet
            .playTogether(vgLangOption_ShowAnimator, txtTranslation_DisappearAnmtrSet)
        endSearchingAnmtrSet.addListener (onStart = {
            rcvRecentSearchedFlashcards.smoothScrollToPosition(0)
        }, onEnd = {
            isInSearchingMode = false
            makeEditTextsMultiLine()
            edtText.hint = "Tap here to enter text"
            btnNavigateToSearchZone.goVISIBLE()
        })

        // End searching without hide txtTranslation
        endSearchWithTransAnmtrSet
            .play(vgLangOption_ShowAnimator)
        endSearchWithTransAnmtrSet.addListener (onStart = {
            rcvRecentSearchedFlashcards.smoothScrollToPosition(0)
        }, onEnd = {
            isInSearchingMode = false
            makeEditTextsMultiLine()
            edtText.hint = "Tap here to enter text"
            btnNavigateToSearchZone.goVISIBLE()
        })
    }}

    @Inject
    fun initAddButtonAnimations (
        @Named("FromNothingToNormalSizeAnimator") appearAnimator : Animator,
        @Named("FromNormalSizeToNothingAnimator") disappearAnimator : Animator) {

        btnAddAppearAnimator = appearAnimator.apply {
            setTarget(dB.vwgrpBtnAddAndProgressBar)
            addListener (onStart = {
                dB.vwgrpBtnAddAndProgressBar.goVISIBLE()
            }, onEnd = {
                addButtonDoesAppear = true
            })
        }

        btnAddDisappearAnimator = disappearAnimator.apply {
            setTarget(dB.vwgrpBtnAddAndProgressBar)
            addListener (onEnd = {
                dB.vwgrpBtnAddAndProgressBar.goGONE()
                addButtonDoesAppear = false
                hideTranslatingProgressBar()
            })
        }
    }


    @Inject
    fun initSwapAnimations (
        @Named("MoveRightAndFadeOut") moveRightAndFadeOutAnimation : Animation,
        @Named("MoveLeftAndFadeOut") moveLeftAndFadeOutAnimation : Animation) { dB.apply {
        moveRightAndFadeOutAnim = moveRightAndFadeOutAnimation
        moveRightAndFadeOutAnim.addAnimationLister(onStart = null,
            onEnd = {
                swapLanguage()
        })
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
        anmtrSetChooseLangDisappear.play(rcvChooseLanguageDisappear).before(blackBackgroundDisappear)
        anmtrSetChooseLangDisappear.addListener (onEnd = {
            layoutChooseLang.groupChooseLanguage.visibility = View.GONE
            isInChooseLanguageMode = false
        })

        rcvChooseLanguageAppear.setTarget(layoutChooseLang.viewgroupChooseLanguage)
        blackBackgroundAppear.setTarget(layoutChooseLang.imgBlackBackgroundChooseLanguage)
        anmtrSetChooseLangAppear.play(blackBackgroundAppear).before(rcvChooseLanguageAppear)
        anmtrSetChooseLangAppear.addListener(onStart = {layoutChooseLang.groupChooseLanguage.goVISIBLE()},
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
        anmtrSetCardInfoDisappear.play(viewgroupCardInfoDisappear).before(blackBackgroundDisappear)
        anmtrSetCardInfoDisappear.addListener (onEnd = {
            layoutCardInfo.groupCardInfo.visibility = View.GONE
            isInSeeCardInfoMode = false
        })

        viewgroupCardInfoAppear.setTarget(layoutCardInfo.viewgroupCardInfo)
        blackBackgroundAppear.setTarget(layoutCardInfo.imgBlackBackgroundSeeCardInfo)
        anmtrSetCardInfoAppear.play(blackBackgroundAppear).before(viewgroupCardInfoAppear)
        anmtrSetCardInfoAppear.addListener(
        onStart = {
            layoutCardInfo.groupCardInfo.goVISIBLE()
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
                when {
                    it.animatedFraction < 0.25f -> {
                        dB.txtTranslation.hint = "Translating"
                    }
                    it.animatedFraction < 0.5f -> {
                        dB.txtTranslation.hint = "Translating."
                    }
                    it.animatedFraction < 0.75f -> {
                        dB.txtTranslation.hint = "Translating.."
                    }
                    it.animatedFraction < 1 -> {
                        dB.txtTranslation.hint = "Translating..."
                    }
                }
            }
            addListener (onEnd = {
                dB.txtTranslation.hint = txtTranslationHintHolder
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
                vwgrpTranslatingErrorHappened.goVISIBLE()
            },
            onEnd = {
                vwgrpTranslatingErrorHappened.goGONE()
            })

        hideTransErrorAnmtr = disappearAnimator2
        hideTransErrorAnmtr.setTarget(vwgrpTranslatingErrorHappened)
        hideTransErrorAnmtr.addListener(
            onStart = {
                vwgrpTranslatingErrorHappened.goVISIBLE()
            },
            onEnd = {
                vwgrpTranslatingErrorHappened.goGONE()
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
            groupAddFlashcard.goVISIBLE()
            btnAdd.goGONE()
            isInAddFlashcardMode = true
        })

        blackBackgroundDisappear.setTarget(imgBlackBgAddFlashcardPanel)
        panelDisappear.setTarget(panelAddFlashcard)
        addFCPanelDisappear.play(panelDisappear).before(blackBackgroundDisappear)
        addFCPanelDisappear.addListener (onEnd = {
            btnAdd.goVISIBLE()
            groupAddFlashcard.goGONE()
            isInAddFlashcardMode = false
        })

    }}
}

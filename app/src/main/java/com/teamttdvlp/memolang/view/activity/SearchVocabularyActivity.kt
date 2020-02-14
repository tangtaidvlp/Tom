package com.teamttdvlp.memolang.view.activity

import android.animation.*
import android.animation.ValueAnimator.INFINITE
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivitySearchVocabularyBinding
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.database.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.database.sql.repository.UserRepository
import com.teamttdvlp.memolang.database.sql.repository.UserSearchHistoryRepository
import com.teamttdvlp.memolang.view.activity.iview.SearchVocabularyView
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter.UserSavingAssitant.Companion.USER_REPOSITORY_POS
import com.teamttdvlp.memolang.view.adapter.RCVRecentSearchFlashcardAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.SearchVocabularyViewModel
import java.util.*
import javax.inject.Inject
import javax.inject.Named

const val SEARCH_VOCABULARY_JUST_BEEN_ADDED_CARD_KEY = "added_card"

class SearchVocabularyActivity : BaseActivity<ActivitySearchVocabularyBinding, SearchVocabularyViewModel>()
    ,SearchVocabularyView {

    private val TYPE_NONE = 131073

    private val TYPE_TEXT = 1

    private val THE_TIME_THAT_EDIT_VIEWGROUP_APPEAR = 5000L

    private var RED : Int = 0

    private var HINT_COLOR : Int = 0

    private lateinit var moveRightAndFadeOutAnim : Animation

    private lateinit var moveLeftAndFadeOutAnim : Animation

    private var startSearchingAnmtrSet : AnimatorSet = AnimatorSet()

    private var endSearchingAnmtrSet : AnimatorSet = AnimatorSet()

    private var startSearchWithTransAnmtrSet : AnimatorSet = AnimatorSet()

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
    lateinit var showLangSelectionAnmtn : Animation

    lateinit var rcvRecentSearchFlashcardAdapter: RCVRecentSearchFlashcardAdapter
    @Inject set

    lateinit var rcvChooseLanguageAdapter: RCVChooseLanguageAdapter
    @Inject set

    lateinit var rcvRecentChosenLanguageAdapter: RCVChooseLanguageAdapter
        @Inject set

    lateinit var linearLayoutManager: LinearLayoutManager
        @Inject set

    lateinit var linearLayoutManager2: LinearLayoutManager
        @Inject set

    lateinit var linearLayoutManager3: LinearLayoutManager
        @Inject set

    lateinit var databaseManager: MemoLangSqliteDataBase
        @Inject set

    lateinit var flashcardRepository : FlashcardRepository
        @Inject set

    lateinit var userRepository : UserRepository
        @Inject set

    lateinit var searchHistoryRepository: UserSearchHistoryRepository
        @Inject set

    private var isInSearchingMode = false

    private var isInChooseLanguageMode= false

    private var isInSeeCardInfoMode = false

    private var addButtonDoesAppear = false

    private var previousText = ""

    override fun getLayoutId(): Int  = R.layout.activity_search_vocabulary

    override fun takeViewModel(): SearchVocabularyViewModel = getActivityViewModel() {
        SearchVocabularyViewModel(
            this@SearchVocabularyActivity.application
            , databaseManager, userRepository, flashcardRepository, searchHistoryRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
    }

    override fun initProperties() { dB.apply {
        dB.viewModelS = viewModel
        txtSourceLang.text = viewModel.getSingletonUser()!!.recentSourceLanguage
        txtTargetLang.text = viewModel.getSingletonUser()!!.recentTargetLanguage

        RED = resources.getColor(R.color.app_red)
        HINT_COLOR = resources.getColor(R.color.hint_color)
    }}

    override fun addViewControls() { dB.apply {

        txtTranslation.hint = "Your translation here (${txtTargetLang.text})"

        rcvRecentSearchFlashcards.adapter = rcvRecentSearchFlashcardAdapter
        rcvRecentSearchFlashcards.layoutManager = linearLayoutManager

        layoutChooseLang.rcvChooseLanguage.adapter = rcvChooseLanguageAdapter
        layoutChooseLang.rcvChooseLanguage.layoutManager = linearLayoutManager2
        rcvChooseLanguageAdapter.assistant = RCVChooseLanguageAdapter.UserSavingAssitant()
        rcvChooseLanguageAdapter.assistant!!.addAssistant(USER_REPOSITORY_POS, userRepository as Object)

        layoutChooseLang.rcvRecentChosenLanguage.adapter = rcvRecentChosenLanguageAdapter
        layoutChooseLang.rcvRecentChosenLanguage.layoutManager = linearLayoutManager3
        rcvRecentChosenLanguageAdapter.setData(viewModel.getSingletonUser()!!.recentUseLanguages)
    }}

    override fun addViewEvents() { dB.apply {
        btnEdit.setOnClickListener {
            if (isInSearchingMode) endSearchWithTransAnmtrSet.end()
            viewgroupFlashcardAddedNotification.disappear()
            val intent = Intent(this@SearchVocabularyActivity, SearchEditFlashcardActivity::class.java)
            intent.putExtra(SEARCH_VOCABULARY_JUST_BEEN_ADDED_CARD_KEY, viewModel.getjustBeenAddedCard())
            startActivity(intent)
        }

        layoutCardInfo.btnEdit.setOnClickListener {
            if (isInSearchingMode) endSearchWithTransAnmtrSet.end()
            viewgroupFlashcardAddedNotification.disappear()
            val intent = Intent(this@SearchVocabularyActivity, SearchEditFlashcardActivity::class.java)
            intent.putExtra(SEARCH_VOCABULARY_JUST_BEEN_ADDED_CARD_KEY, layoutCardInfo.beingViewedCard)
            startActivity(intent)
        }

        edtText.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                startSearchingAnimations()
            }
        }

        btnDeleteAllText.setOnClickListener {
            edtText.setText("")
            if (!isInSearchingMode) {
                 startSearchingAnmtrSet.start()
            }
        }

        edtText.addTextChangeListener (onTextChanged = { text, _, _, _ ->

            if (txtSourceLang.text.toString() == txtTargetLang.text.toString()) {
                quickToast("Your two languages are similar. Please check again")
                return@addTextChangeListener
            }

            // Because onFocusChangeListener also calls onTextChangeListener
            // So, making this helps us not translating the same text
            // Avoiding getting some bugs as well as Saving Translation Storage
            val isTranslatingSameText = (text.trim().equals(previousText))
            if (isTranslatingSameText) return@addTextChangeListener
            previousText = text.trim()

            if (text == "") {
                viewModel.cancelAllCurrentTranslating()
                btnAddDisappearAnimator.start()
                translatingTextAnimator.end()
            } else if (text != "") {
                if ((!translatingTextAnimator.isStarted) and (txtTranslation.hint == "Your translation here (${txtTargetLang.text})"))
                    translatingTextAnimator.start()
                if (dB.imgTranslatingCircleProgressBar.animation == null) {
                    showTranslatingProgressBar()
                }
            }

            val sourceLang = txtSourceLang.text.toString() + ""
            val targetLang = txtTargetLang.text.toString() + ""

            translate(text, sourceLang, targetLang)
        })

        viewgroupTxtSourceLang.setOnClickListener {
            selectedLanguageTextView = txtSourceLang
            AnmtrSetChooseLangAppear.start()
        }

        viewgroupTxtTargetLang.setOnClickListener {
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

            fun addFlashcardToHistory (cardId : Int, searchedDate : Date) {
                viewModel.addSearchedCardToHistory(cardId, searchedDate)
            }

            val newFlashcard = Flashcard(
                0,
                edtText.text.toString(),
                txtTranslation.text.toString(),
                "${txtSourceLang.text}-${txtTargetLang.text}"
            )
            viewModel.addFlashcardToOfflineDB(newFlashcard) { isSuccess, insertedCardId, ex ->
                if (isSuccess) {
                    quickLog("Add Success To Offline DB")
                    // If add to offline database successfully, we could get its unique id and pass to Online Database
                    // and Search History
                    newFlashcard.id = insertedCardId.toInt()
                    viewModel.setJustBeenAddedCard(newFlashcard)
                    addFlashcardToHistory(newFlashcard.id, newFlashcard.createdAt)
                    addedFlashcardAnmtrSet.start()
                } else {
                    quickLog("Add Failed To Offline DB")
                }
            }
//            if (isInSearchingMode) startEndSearchingAnimations()
            rcvRecentSearchFlashcardAdapter.addFlashcardAtTheFirstPosition(newFlashcard)
            btnAddDisappearAnimator.start()
            rcvRecentSearchFlashcards.smoothScrollToPosition(0)
        }

        btnSwapLanguage.setOnClickListener {

            viewgroupTxtSourceLang.startAnimation(moveRightAndFadeOutAnim)
            viewgroupTxtTargetLang.startAnimation(moveLeftAndFadeOutAnim)

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
    }}

    private fun translate (text : String, sourceLang : String, targetLang : String) { dB.apply {
        val needTranslatingText = text
        viewModel.translateText(needTranslatingText, sourceLang, targetLang, object : SearchVocabularyViewModel.OnTranslateListener {
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
                showTransErrorAnmtrSet.start()
            }
        })
    }}

    //================== LIFE CYCLE OVERRIDE METHOD ===============

    override fun onStart() {
        super.onStart()
        viewModel.getAllSearchHistoryInfo {
            rcvRecentSearchFlashcardAdapter.setData(it)
        }
    }

    override fun onBackPressed() {
        if (isInSearchingMode) startEndSearchingAnimations()
        else if (isInChooseLanguageMode) AnmtrSetChooseLangDisappear.start()
        else if (isInSeeCardInfoMode) AnmtrSetCardInfoDisappear.start()
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
            txtLangSelectionErrors.startAnimation(showLangSelectionAnmtn)
        }

        AnmtrSetChooseLangDisappear.start()
        rcvChooseLanguageAdapter.addLanguage(language)

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
        if  ((txtTranslation.text.toString().isEmpty())) {
            startSearchingAnmtrSet.start()
        } else {
            startSearchWithTransAnmtrSet.start()
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

    //================== INIT ANIMATIONS ========================


    @Inject
    fun initStartSearchingAnimations (
        @Named("EditTextTextScaleSmallerAnimator") edtTextTextScaleSmaller_Animator : ValueAnimator,
        @Named("ViewGroupLanguageOptionHideAnimator") vgLangOption_HideAnimator : ValueAnimator,
        @Named("AppearAnimator") txtTranslation_Appear_Animator  : Animator,
        @Named("ScaleBiggerAnimator") txtTranslation_ScaleBigger_Animator : ValueAnimator,
        @Named("EditTextTextScaleSmallerAnimator") txtTranslation_ScaleSmaller_Animator : ValueAnimator) { dB.apply {

        vgLangOption_HideAnimator.apply {
            addUpdateListener {
                val newLayoutParams = viewgroupLanguageOption.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                viewgroupLanguageOption.layoutParams = newLayoutParams
            }
            setTarget(viewgroupLanguageOption)
        }

        txtTranslation_Appear_Animator.setTarget(txtTranslation)

        txtTranslation_ScaleBigger_Animator.apply {

            addUpdateListener {
                val newLayoutParams = txtTranslation.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                txtTranslation.layoutParams = newLayoutParams
            }

            addListener (onStart = {
                txtTranslation.appear()
            })

            setTarget(txtTranslation)
        }

        txtTranslation_ScaleSmaller_Animator.apply {
            addUpdateListener {
                val newLayoutParams = txtTranslation.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                txtTranslation.layoutParams = newLayoutParams
            }

            addListener (onEnd = {
                txtTranslation.gravity = Gravity.CENTER_VERTICAL
                txtTranslation.setPadding(edtText.paddingLeft, 0, 0, 0)
                txtTranslation.requestLayout()
            })

            setTarget(txtTranslation)
        }

        edtTextTextScaleSmaller_Animator.apply {
            addUpdateListener {
                val newLayoutParams = edtText.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                edtText.layoutParams = newLayoutParams
            }
            addListener (onEnd = {
                edtText.gravity = Gravity.CENTER_VERTICAL
                edtText.setPadding(edtText.paddingLeft, 0, 0, 0)
                edtText.requestLayout()
            })
            setTarget(edtText)
        }

        val txtTranslation_AppearAnmtrSet = AnimatorSet().apply {
            play(txtTranslation_Appear_Animator).with(txtTranslation_ScaleBigger_Animator)
        }
        startSearchingAnmtrSet
            .play(vgLangOption_HideAnimator)
            .with(txtTranslation_AppearAnmtrSet)
            .with(edtTextTextScaleSmaller_Animator)
        startSearchingAnmtrSet.addListener (onStart = {
            edtText.hint = "Type your text here (${txtSourceLang.text})"
            rcvRecentSearchFlashcards.smoothScrollToPosition(0)
            btnTapToSearch.disappear()
        }, onEnd = {
            isInSearchingMode = true
            makeEditTextsSingleLine()
        })

        startSearchWithTransAnmtrSet
            .play(vgLangOption_HideAnimator)
            .with(edtTextTextScaleSmaller_Animator)
            .with(txtTranslation_ScaleSmaller_Animator)
        startSearchWithTransAnmtrSet.addListener (onStart = {
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
        @Named("EditTextTextScaleBiggerAnimator") edtTextTextScaleBigger_Animator : ValueAnimator,
        @Named("ViewGroupLanguageOptionShowAnimator") vgLangOption_ShowAnimator : ValueAnimator,
        @Named("DisappearAnimator") txtTranslation_Disappear_Animator : Animator,
        @Named("ScaleSmallerAnimator") txtTranslation_ScaleSmaller_Animator : ValueAnimator,
        @Named("EditTextTextScaleBiggerAnimator") txtTranslation_ScaleBigger_Animator: ValueAnimator) { dB.apply {
        vgLangOption_ShowAnimator.apply {
            addUpdateListener {
                val newLayoutParams = viewgroupLanguageOption.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                viewgroupLanguageOption.layoutParams = newLayoutParams
            }
        }

        txtTranslation_Disappear_Animator.setTarget(txtTranslation)

        txtTranslation_ScaleSmaller_Animator.apply {
            addUpdateListener {
                val newLayoutParams = txtTranslation.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                txtTranslation.layoutParams = newLayoutParams
            }
            addListener (onEnd = {
                txtTranslation.disappear()
            })
            setTarget(txtTranslation)
        }

        txtTranslation_ScaleBigger_Animator.apply {
            addUpdateListener {
                val newLayoutParams = txtTranslation.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                txtTranslation.layoutParams = newLayoutParams
            }

            addListener (onStart = {
                txtTranslation.gravity = Gravity.TOP xor Gravity.LEFT
                txtTranslation.setPadding(txtTranslation.paddingLeft, txtTranslation.paddingLeft * 16 / 20, 0, 0)
                txtTranslation.requestLayout()
            })
            setTarget(txtTranslation)
        }

        edtTextTextScaleBigger_Animator.apply {
            addUpdateListener {
                val newLayoutParams = edtText.layoutParams
                newLayoutParams.height = it.animatedValue as Int
                edtText.layoutParams = newLayoutParams
            }
            addListener (onStart = {
                edtText.gravity = Gravity.TOP xor Gravity.LEFT
                edtText.setPadding(edtText.paddingLeft, edtText.paddingLeft * 16 / 20, 0, 0)
                edtText.requestLayout()
                quickLog("Done")
            })
            setTarget(edtText)
        }

        val txtTranslation_DisappearAnmtrSet = AnimatorSet().apply {
            play(txtTranslation_Disappear_Animator).with(txtTranslation_ScaleSmaller_Animator)
        }

        endSearchingAnmtrSet
            .play(vgLangOption_ShowAnimator)
            .with(txtTranslation_DisappearAnmtrSet)
            .with(edtTextTextScaleBigger_Animator)
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
            .with(edtTextTextScaleBigger_Animator)
            .with(txtTranslation_ScaleBigger_Animator)
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
            setTarget(dB.viewgroupButtonAddAndProgressBar)
            addListener (onStart = {
                dB.viewgroupButtonAddAndProgressBar.appear()
            }, onEnd = {
                addButtonDoesAppear = true
            })
        }

        btnAddDisappearAnimator = disappearAnimator.apply {
            setTarget(dB.viewgroupButtonAddAndProgressBar)
            addListener (onEnd = {
                dB.viewgroupButtonAddAndProgressBar.disappear()
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
        @Named("FromNormalSizeToNothing_") rcvChooseLanguageDisappear : Animator,
        @Named("Disappear50percents_") blackBackgroundDisappear : Animator,
        @Named("FromNothingToNormalSize_") rcvChooseLanguageAppear : Animator,
        @Named("Appear50percents_") blackBackgroundAppear : Animator) { dB.apply {

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
        @Named("Disappear100percents_") viewgroupCardInfoDisappear : Animator,
        @Named("Disappear50percents_") blackBackgroundDisappear : Animator,
        @Named("Appear100percents_") viewgroupCardInfoAppear : Animator,
        @Named("Appear50percents_") blackBackgroundAppear : Animator) { dB.apply {

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
    fun initAddedFlashcardAnimation (
        @Named("MoveUpAndAppear") viewgroupMoveUpAndFadeIn : Animator,
        @Named("DisappearAnimator") viewgroupDisappear : Animator
    ) {
        viewgroupMoveUpAndFadeIn.setTarget(dB.viewgroupFlashcardAddedNotification)
        viewgroupMoveUpAndFadeIn.addListener(onStart = {
            quickLog("Start")
            dB.viewgroupFlashcardAddedNotification.appear()
        })
        viewgroupDisappear.setTarget(dB.viewgroupFlashcardAddedNotification)
        viewgroupDisappear.startDelay = THE_TIME_THAT_EDIT_VIEWGROUP_APPEAR
        viewgroupDisappear.addListener (onEnd = {
            dB.viewgroupFlashcardAddedNotification.disappear()
            quickLog("End")
        })
        addedFlashcardAnmtrSet.play(viewgroupMoveUpAndFadeIn).before(viewgroupDisappear)
    }

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
        showTransErrorAnmtrSet.setTarget(viewgroupErrorHappened)
        showTransErrorAnmtrSet.addListener (
            onStart = {
                viewgroupErrorHappened.appear()
            },
            onEnd = {
                viewgroupErrorHappened.disappear()
            })

        hideTransErrorAnmtr = disappearAnimator2
        hideTransErrorAnmtr.setTarget(viewgroupErrorHappened)
        hideTransErrorAnmtr.addListener(
            onStart = {
                viewgroupErrorHappened.appear()
            },
            onEnd = {
                viewgroupErrorHappened.disappear()
            })
    }}
}

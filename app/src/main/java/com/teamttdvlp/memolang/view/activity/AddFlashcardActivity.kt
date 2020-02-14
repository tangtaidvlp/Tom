package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.graphics.Color
import android.os.Bundle
import android.text.InputType.TYPE_NULL
import android.view.View
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.viewmodel.AddFlashCardViewModel
import com.teamttdvlp.memolang.databinding.ActivityAddFlashcardBinding
import android.view.animation.*
import com.teamttdvlp.memolang.R
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.model.entity.Language.Companion.SOURCE_LANGUAGE
import com.teamttdvlp.memolang.model.entity.Language.Companion.TARGET_LANGUAGE
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.database.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.database.sql.repository.UserRepository
import com.teamttdvlp.memolang.view.activity.iview.AddFlashcardView
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter.UserSavingAssitant.Companion.USER_REPOSITORY_POS
//import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager
import javax.inject.Inject
import javax.inject.Named


class AddFlashcardActivity : BaseActivity<ActivityAddFlashcardBinding, AddFlashCardViewModel>()
                            ,AddFlashcardView {

    companion object {
        val SRC_LANG_KEY = "srcLang"

        val TGT_LANG_KEY = "tgtLang"
    }

    // The image will stay on screen for a while before disappearing
    private val TIME_THAT_SAVED_FLASHCARD_IMAGE_STAY_ON_SCREEN = 400L

    private var selectedTextHighlightAnim : Animation? = null

    private val animatorSetSaving = AnimatorSet()

    private val animatorSetChooseLangAppear = AnimatorSet()

    private val animatorSetChooseLangDisappear = AnimatorSet()

    private val animatorSetChooseCardTypeAppear = AnimatorSet()

    private val animatorSetChooseCardTypeDisappear = AnimatorSet()

    private var selectedLanguageTextView : TextView? = null

    private var isInChooseLanguageMode = false

    private var isIPAKeyboardVisible = false

    lateinit var rcvChooseLanguageAdapter : RCVChooseLanguageAdapter
    @Inject set

    lateinit var rcvRecentChosenLanguageAdapter: RCVChooseLanguageAdapter
    @Inject set

    var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

    lateinit var linearLayoutManager2: LinearLayoutManager
        @Inject set

    lateinit var database : MemoLangSqliteDataBase
    @Inject set

//    lateinit var onlineFlashcardManager: OnlineFlashcardDBManager
//    @Inject set

    lateinit var flashcardRepository : FlashcardRepository
    @Inject set

    lateinit var userRepository : UserRepository
    @Inject set

    override fun getLayoutId(): Int  = R.layout.activity_add_flashcard

    override fun takeViewModel(): AddFlashCardViewModel = getActivityViewModel {
        AddFlashCardViewModel(
            database,
            userRepository,
            flashcardRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        dB.dbViewModel = viewModel
    }

    override fun addViewControls() { dB.apply {

        rcvChooseLanguage.adapter = rcvChooseLanguageAdapter
        rcvChooseLanguage.layoutManager = linearLayoutManager!!
        rcvChooseLanguageAdapter.assistant = RCVChooseLanguageAdapter.UserSavingAssitant()
        rcvChooseLanguageAdapter.assistant!!.addAssistant(USER_REPOSITORY_POS, userRepository)

        layoutAddFlashcard.apply {
            if (viewModel.getSingletonUser() != null) {
                val requestedLangPair = getRequestedLangPair()
                if (requestedLangPair == null) {
                    val sourceLang = viewModel.getSingletonUser()!!.recentSourceLanguage
                    val targetLang = viewModel.getSingletonUser()!!.recentTargetLanguage
                    /**
                     * NEED RECODING, use two way databinding to solve
                     */
                    viewModel.bindLanguageInfoToUI(sourceLang, targetLang)
                } else {
                    viewModel.bindLanguageInfoToUI(
                        requestedLangPair.get(SOURCE_LANGUAGE),
                        requestedLangPair.get(TARGET_LANGUAGE))
                }
            } else {
//                viewModel.bindLanguageInfoToUI("", "")
            }
        }

        rcvRecentChosenLanguage.adapter = rcvRecentChosenLanguageAdapter
        rcvRecentChosenLanguage.layoutManager = linearLayoutManager2
        rcvRecentChosenLanguageAdapter.assistant = RCVChooseLanguageAdapter.UserSavingAssitant()
        rcvRecentChosenLanguageAdapter.assistant!!.addAssistant(USER_REPOSITORY_POS, userRepository)
        rcvRecentChosenLanguageAdapter.setData(viewModel.getSingletonUser()!!.recentUseLanguages)

        layoutAddFlashcard.edtPronunciation.inputType = TYPE_NULL

        ipaKeyboard.setFocusedText(dB.layoutAddFlashcard.edtPronunciation)
    }}

    override fun addViewEvents() { dB.apply {

        layoutAddFlashcard.edtPronunciation.setOnClickListener {
            showIPAKeyboard()
        }

        ipaKeyboard.setOnDeviceVirtualKeyboardShow {
            hideIPAKeyboard()
        }

        ipaKeyboard.setOnBtnDoneClickListener {
            hideIPAKeyboard()
        }

        btnSave.setOnClickListener { layoutAddFlashcard.apply {
            hideIPAKeyboard()

            val text = edtText.text.toString()
            val translation = edtTranslation.text.toString()
            val using = edtUsing.text.toString()
            val type = edtType.text.toString()
            val pronunciation = edtPronunciation.text.toString()
            val sourceLang = txtSourceLang.text.toString()
            val targetLang = txtTargetLang.text.toString()
            val setName = edtSetName.text.toString()

            viewModel.addFlashcard(sourceLang, targetLang, setName, type, text, translation, using, pronunciation)
        }}

        btnCancel.setOnClickListener {
            finish()
        }

        layoutAddFlashcard.apply {
            viewgroupTxtSourceLang.setOnClickListener {
                animatorSetChooseLangAppear.start()
                selectedLanguageTextView = txtSourceLang
            }

            viewgroupTxtTargetLang.setOnClickListener {
                animatorSetChooseLangAppear.start()
                selectedLanguageTextView = txtTargetLang
            }
        }

        imgBlackBackgroundChooseLanguage.setOnClickListener {
            animatorSetChooseLangDisappear.start()
        }

        rcvChooseLanguageAdapter.setOnItemClickListener { language ->
            onChooseLanguage(language)
        }

        rcvRecentChosenLanguageAdapter.setOnItemClickListener {language ->
            onChooseLanguage(language)
        }

        layoutAddFlashcard.edtType.setOnClickListener {
            animatorSetChooseCardTypeAppear.start()
        }

        layoutChooseCardType.imgBlackBackgroundChooseCardType.setOnClickListener {
            animatorSetChooseCardTypeDisappear.start()
        }

        layoutChooseCardType.rcvChooseCardType.setOnItemClickListener { type ->
            layoutAddFlashcard.edtType.setText(type)
            animatorSetChooseCardTypeDisappear.start()
            dB.layoutAddFlashcard.edtText.requestFocus()
        }

        layoutAddFlashcard.edtText.addTextChangeListener(onTextChanged = { text,_,_,_ ->
            if ((text != "") and (layoutAddFlashcard.txtErrorText.isVisible)) {
                hideTextInputError()
            }
        })

        layoutAddFlashcard.edtTranslation.addTextChangeListener(onTextChanged = { text,_,_,_ ->
            if ((text != "") and (layoutAddFlashcard.txtErrorTranslation.isVisible)) {
                hideTranslationInputError()
            }
        })
    }}

    override fun overrideEnterAnim() {
        overridePendingTransition(R.anim.from_left_to_centre, R.anim.nothing)
    }

    override fun onBackPressed() { dB.apply {
        if (isIPAKeyboardVisible) {
            hideIPAKeyboard()
        } else {
            btnCancel.performClick()
        }
    }}

    fun getRequestedLangPair () : Array<String>? {
        var result : Array<String>? = null
        val bundle = intent.extras
        val srcLang = bundle?.getString(SRC_LANG_KEY, null)
        val tgtLang = bundle?.getString(TGT_LANG_KEY, null)
        if ((srcLang != null) and (tgtLang != null)) {
            result = Array<String>(2) { index ->
                if (index == TARGET_LANGUAGE) {
                    return@Array tgtLang + ""
                } else if (index == SOURCE_LANGUAGE) {
                    return@Array srcLang + ""
                } else {
                    throw Exception("Something wrong happen, index out of Language Info size")
                }
            }
        }
        return result
    }


    // VIEW IMPLEMENTED METHODS

    private fun hideIPAKeyboard() { dB.apply {
        isIPAKeyboardVisible = false
        ipaKeyboard.hide()
        ipaKeyboard.db.keyboardParent.isClickable = false
        layoutAddFlashcard.ipaKeyboardPlaceholder.disappear()
    }}

    private fun showIPAKeyboard() { dB.apply {
        ipaKeyboard.appear()
        ipaKeyboard.db.keyboardParent.isClickable = true
        isIPAKeyboardVisible = true
        layoutAddFlashcard.ipaKeyboardPlaceholder.appear()
        hideVirtualKeyboard()
        Thread(kotlinx.coroutines.Runnable {
            Thread.sleep(50)
            runOnUiThread {
                layoutAddFlashcard.scrollview.scrollToBottom()
            }
        }).start()
        clearAllScreenFocus()
    }}

    private fun clearAllScreenFocus () {
        dB.activityAddFlashcards.requestFocus()
    }


    override fun showTextInputError() {
        dB.layoutAddFlashcard.txtErrorText.appear()
    }

    override fun showTranslationInputError() {
        dB.layoutAddFlashcard.txtErrorTranslation.appear()
    }

    override fun onAddFlashcardSuccess() {
        animatorSetSaving.start()
        hideVirtualKeyboard()
        currentFocus?.clearFocus()
    }


    fun hideTextInputError() {
        dB.layoutAddFlashcard.txtErrorText.disappear()
    }

    fun hideTranslationInputError() {
        dB.layoutAddFlashcard.txtErrorTranslation.disappear()
    }

    fun onChooseLanguage (language : String) {
        selectedLanguageTextView?.text = language
        if (selectedLanguageTextView == dB.layoutAddFlashcard.txtSourceLang) {
            viewModel.updateUserRecentSourceLang(language)
        } else if (selectedLanguageTextView == dB.layoutAddFlashcard.txtTargetLang) {
            viewModel.updateUserRecentTargetLang(language)
        }
        animatorSetChooseLangDisappear.start()
        rcvChooseLanguageAdapter.addLanguage(language)

    }

    // INJECTED METHOD


    @Inject
    fun initSaveAnimation (
        @Named("ZoomToNothingAndRotate") groupDisappear : Animator,
        @Named("ZoomFromNothingToOversizeThenNormalSize") savedImageAppear : Animator,
        @Named("MoveRightAndFadeOutAnimtr") savedImageDisappear : Animator) { dB.apply {

        groupDisappear.apply {
            setTarget(viewgroupAddFlashcard)
            addListener(onEnd = { viewgroupAddFlashcard.disappear() })
        }

        savedImageAppear.setTarget(imgSavedFlashcard)
        savedImageDisappear.setTarget(imgSavedFlashcard)

        val appearSet = AnimatorSet().apply { play(groupDisappear).before(savedImageAppear) }
        val disappearSet = AnimatorSet().apply { play(savedImageDisappear).after(TIME_THAT_SAVED_FLASHCARD_IMAGE_STAY_ON_SCREEN)}
        disappearSet.addListener(onEnd = {
            finish()
            quickStartActivity(AddFlashcardActivity::class.java)
        })
        animatorSetSaving.play(appearSet).before(disappearSet)
    }}

    @Inject
    fun initChooseLanguageAnimation (
        @Named("FromNormalSizeToNothing") rcvChooseLanguageDisappear : Animator,
        @Named("Disappear50Percents") blackBackgroundDisappear : Animator,
        @Named("FromNothingToNormalSize") rcvChooseLanguageAppear : Animator,
        @Named("Appear50Percents") blackBackgroundAppear : Animator) { dB.apply {

        rcvChooseLanguageDisappear.setTarget(viewgroupChooseLanguage)
        blackBackgroundDisappear.setTarget(imgBlackBackgroundChooseLanguage)
        animatorSetChooseLangDisappear.play(rcvChooseLanguageDisappear).before(blackBackgroundDisappear)
        animatorSetChooseLangDisappear.addListener (onEnd = {
            groupChooseLanguage.visibility = View.GONE
            isInChooseLanguageMode = false
        })

        rcvChooseLanguageAppear.setTarget(viewgroupChooseLanguage)
        blackBackgroundAppear.setTarget(imgBlackBackgroundChooseLanguage)
        animatorSetChooseLangAppear.play(blackBackgroundAppear).before(rcvChooseLanguageAppear)
        animatorSetChooseLangAppear.addListener(onStart = {groupChooseLanguage.appear()},
            onEnd = {
                isInChooseLanguageMode = true
            })
    }}

    @Inject
    fun initChooseCardTypeAnimation (
        @Named("FromNormalSizeToNothing") rcvChooseCardTypeDisappear : Animator,
        @Named("Disappear50Percents") blackBackgroundDisappear : Animator,
        @Named("FromNothingToNormalSize") rcvChooseCardTypeAppear : Animator,
        @Named("Appear50Percents") blackBackgroundAppear : Animator) { dB.layoutChooseCardType.apply {

        rcvChooseCardTypeDisappear.setTarget(rcvChooseCardType)
        blackBackgroundDisappear.setTarget(imgBlackBackgroundChooseCardType)
        animatorSetChooseCardTypeDisappear.play(rcvChooseCardTypeDisappear).before(blackBackgroundDisappear)
        animatorSetChooseCardTypeDisappear.addListener (onEnd = {
            rcvChooseCardType.disappear()
            imgBlackBackgroundChooseCardType.disappear()
        })

        rcvChooseCardTypeAppear.setTarget(rcvChooseCardType)
        blackBackgroundAppear.setTarget(imgBlackBackgroundChooseCardType)
        animatorSetChooseCardTypeAppear.play(blackBackgroundAppear).before(rcvChooseCardTypeAppear)
        animatorSetChooseCardTypeAppear.addListener(onStart = {
            rcvChooseCardType.appear()
            imgBlackBackgroundChooseCardType.appear()
        })
    }}

    @Inject
    fun initSelectedTextAnimation (@Named("HighlightTextAnim") selectedTextHighlightAnim : Animation) { dB.apply {
        selectedTextHighlightAnim.addAnimationLister(
            onStart = {
            val darkOrangeColor = ResourcesCompat.getColor(resources, R.color.dark_orange_text, null)
            selectedLanguageTextView?.setTextColor(darkOrangeColor)
        },
            onEnd = {
            selectedLanguageTextView?.setTextColor(Color.BLACK)
        })
        this@AddFlashcardActivity.selectedTextHighlightAnim = selectedTextHighlightAnim
    }}

}


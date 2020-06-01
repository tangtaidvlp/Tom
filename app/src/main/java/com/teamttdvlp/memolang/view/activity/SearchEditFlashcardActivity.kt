//package com.teamttdvlp.memolang.view.activity
//
//import android.animation.Animator
//import android.animation.AnimatorSet
//import android.graphics.Color
//import android.os.Bundle
//import androidx.core.animation.addListener
//import androidx.core.view.isVisible
//import com.teamttdvlp.memolang.R
//import com.teamttdvlp.memolang.view.base.BaseActivity
//import com.teamttdvlp.memolang.viewmodel.SearchEditFlashcardViewModel
//import com.teamttdvlp.memolang.databinding.ActivitySearchEditFlashcardBinding
//import com.teamttdvlp.memolang.data.entity.model.flashcard.Flashcard
//import com.teamttdvlp.memolang.data.entity.other.language.Language.Companion.SOURCE_LANGUAGE
//import com.teamttdvlp.memolang.data.entity.other.language.Language.Companion.TARGET_LANGUAGE
//import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
//import com.teamttdvlp.memolang.data.entity.other.language.Language.Companion.LANG_DEVIDER
//import com.teamttdvlp.memolang.view.activity.iview.SearchEditFlashcardView
//import com.teamttdvlp.memolang.view.helper.*
////import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager
//import javax.inject.Inject
//import javax.inject.Named
//
//class SearchEditFlashcardActivity : BaseActivity<ActivitySearchEditFlashcardBinding, SearchEditFlashcardViewModel>()
//                                   ,SearchEditFlashcardView {
//
//    // The image will stay on screen for a while before disappearing
//    private val TIME_THAT_SAVED_FLASHCARD_IMAGE_STAY_ON_SCREEN = 400L
//
//    private val animatorSetSaving = AnimatorSet()
//
//    private val animatorSetChooseCardTypeAppear = AnimatorSet()
//
//    private val animatorSetChooseCardTypeDisappear = AnimatorSet()
//
//    lateinit var flashcardRepository: FlashcardRepository
//    @Inject set
//
//    private var isIPAKeyboardVisible = false
//
////    lateinit var onlineFlashcardManager: OnlineFlashcardDBManager
////    @Inject set
//
//    override fun getLayoutId(): Int {
//        return R.layout.activity_search_edit_flashcard
//    }
//
//    override fun takeViewModel(): SearchEditFlashcardViewModel = getActivityViewModel() {
//        SearchEditFlashcardViewModel(/*onlineFlashcardManager, */
//            flashcardRepository
//        )
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        viewModel.setUpView(this)
//        viewModel.setOriginalCard(getData())
//        dB.viewModel = viewModel
//    }
//
//    override fun initProperties() { dB.layoutAddFlashcard.apply {
//        val editingCard = getData()
//        val languageDetail = editingCard.languagePair.split(LANG_DEVIDER)
//        val sourceLang = languageDetail[SOURCE_LANGUAGE]
//        val targetLang = languageDetail[TARGET_LANGUAGE]
//        val text = editingCard.text
//        val translation = editingCard.translation
//        val using = editingCard.example
//
//        txtSourceLang.setText(sourceLang)
//        txtTargetLang.setText(targetLang)
//        edtText.setText(text)
//        edtTranslation.setText(translation)
//        edtExample.setText(using)
//    }}
//
//    override fun addViewControls() { dB.apply {
//        val BLUE_COLOR = Color.parseColor("#0025DC")
//        layoutAddFlashcard.edtText.setTextColor(BLUE_COLOR)
//        ipaKeyboard.setFocusedText(layoutAddFlashcard.edtPronunciation)
//    }}
//
//    override fun addViewEvents() { dB.apply {
//
//        ipaKeyboard.setOnBtnDoneClickListener {
//            hideIPAKeyboard()
//        }
//
//        ipaKeyboard.setOnDeviceVirtualKeyboardShow {
//            hideIPAKeyboard()
//        }
//
//        layoutAddFlashcard.edtPronunciation.setOnClickListener {
//            showIPAKeyboard()
//        }
//
//        layoutAddFlashcard.edtType.setOnClickListener {
//            animatorSetChooseCardTypeAppear.start()
//        }
//
//        btnSave.setOnClickListener {
//            layoutAddFlashcard.apply {
//                val sourceLang = txtSourceLang.text.toString()
//                val targetLang = txtTargetLang.text.toString()
//                val setName = edtSetName.text.toString()
//                val text = edtText.text.toString()
//                val translation = edtTranslation.text.toString()
//                val using = edtExample.text.toString()
//                val type = edtType.text.toString()
//                val pronunciation = edtPronunciation.text.toString()
//
//                this@SearchEditFlashcardActivity.viewModel.updateCard(sourceLang, targetLang, setName, type, text, translation, using, pronunciation)
//            }
//        }
//
//        btnCancel.setOnClickListener {
//            finish()
//        }
//
//        layoutAddFlashcard.edtText.addTextChangeListener(onTextChanged = { text,_,_,_ ->
//            if ((text != "") and (layoutAddFlashcard.txtErrorText.isVisible)) {
//                hideTextInputError()
//            }
//        })
//
//        layoutAddFlashcard.edtTranslation.addTextChangeListener(onTextChanged = { text,_,_,_ ->
//            if ((text != "") and (layoutAddFlashcard.txtErrorTranslation.isVisible)) {
//                hideTranslationInputError()
//            }
//        })
//
//        layoutAddFlashcard.edtType.setOnClickListener {
//            animatorSetChooseCardTypeAppear.start()
//        }
//
//        layoutChooseCardType.imgBlackBackgroundChooseCardType.setOnClickListener {
//            animatorSetChooseCardTypeDisappear.start()
//        }
//
//        layoutChooseCardType.rcvChooseCardType.setOnItemClickListener { type ->
//            layoutAddFlashcard.edtType.setText(type)
//            animatorSetChooseCardTypeDisappear.start()
//        }
//    }}
//
//    override fun onBackPressed() {
//        if (isIPAKeyboardVisible)
//            hideIPAKeyboard()
//        else
//            super.onBackPressed()
//    }
//
//    // VIEW IMPLEMENTED METHODS
//
//    private fun hideIPAKeyboard() { dB.apply {
//        isIPAKeyboardVisible = false
//        ipaKeyboard.hide()
//        ipaKeyboard.db.keyboardParent.isClickable = false
//        layoutAddFlashcard.ipaKeyboardPlaceholder.disappear()
//    }}
//
//    private fun showIPAKeyboard() { dB.apply {
//        ipaKeyboard.appear()
//        ipaKeyboard.db.keyboardParent.isClickable = true
//        isIPAKeyboardVisible = true
//        layoutAddFlashcard.ipaKeyboardPlaceholder.appear()
//        hideVirtualKeyboard()
//        Thread(kotlinx.coroutines.Runnable {
//            Thread.sleep(50)
//            runOnUiThread {
//                layoutAddFlashcard.scrollview.scrollToBottom()
//            }
//        }).start()
//        clearAllScreenFocus()
//    }}
//
//    fun clearAllScreenFocus () {
//        dB.activityAddFlashcards.requestFocus()
//    }
//
//    override fun showTextInputError() {
//        dB.layoutAddFlashcard.txtErrorText.appear()
//    }
//
//
//    override fun showTranslationInputError() {
//        dB.layoutAddFlashcard.txtErrorTranslation.appear()
//    }
//
//    override fun onUpdateFlashcardSuccess() {
//        animatorSetSaving.start()
//        hideVirtualKeyboard()
//        currentFocus?.clearFocus()
//    }
//
//    fun hideTextInputError() {
//        dB.layoutAddFlashcard.txtErrorText.disappear()
//    }
//
//    fun hideTranslationInputError() {
//        dB.layoutAddFlashcard.txtErrorTranslation.disappear()
//    }
//
//    fun getData () : Flashcard {
//        return intent.extras!!.getSerializable(SEARCH_VOCABULARY_JUST_BEEN_ADDED_CARD_KEY) as Flashcard
//    }
//
//    @Inject
//    fun initSaveAnimation (
//        @Named("ZoomToNothingAndRotate") groupDisappear : Animator,
//        @Named("ZoomFromNothingToOversizeThenNormalSize") savedImageAppear : Animator,
//        @Named("MoveRightAndFadeOutAnimtr") savedImageDisappear : Animator
//    ) { dB.apply {
//
//        groupDisappear.apply {
//            setTarget(viewgroupAddFlashcard)
//            addListener(onEnd = { viewgroupAddFlashcard.disappear() })
//        }
//
//        savedImageAppear.setTarget(imgSavedFlashcard)
//        savedImageDisappear.setTarget(imgSavedFlashcard)
//
//        val appearSet = AnimatorSet().apply { play(groupDisappear).before(savedImageAppear) }
//        val disappearSet = AnimatorSet().apply { play(savedImageDisappear).after(TIME_THAT_SAVED_FLASHCARD_IMAGE_STAY_ON_SCREEN)}
//        disappearSet.addListener(onEnd = { finish() })
//        animatorSetSaving.play(appearSet).before(disappearSet)
//    }}
//
//
//    @Inject
//    fun initChooseCardTypeAnimation (
//        @Named("FromNormalSizeToNothing") rcvChooseCardTypeDisappear : Animator,
//        @Named("Disappear50Percents") blackBackgroundDisappear : Animator,
//        @Named("FromNothingToNormalSize") rcvChooseCardTypeAppear : Animator,
//        @Named("Appear50Percents") blackBackgroundAppear : Animator) { dB.layoutChooseCardType.apply {
//
//        rcvChooseCardTypeDisappear.setTarget(rcvChooseCardType)
//        blackBackgroundDisappear.setTarget(imgBlackBackgroundChooseCardType)
//        animatorSetChooseCardTypeDisappear.play(rcvChooseCardTypeDisappear).before(blackBackgroundDisappear)
//        animatorSetChooseCardTypeDisappear.addListener (onEnd = {
//            rcvChooseCardType.disappear()
//            imgBlackBackgroundChooseCardType.disappear()
//        })
//
//        rcvChooseCardTypeAppear.setTarget(rcvChooseCardType)
//        blackBackgroundAppear.setTarget(imgBlackBackgroundChooseCardType)
//        animatorSetChooseCardTypeAppear.play(blackBackgroundAppear).before(rcvChooseCardTypeAppear)
//        animatorSetChooseCardTypeAppear.addListener(onStart = {
//            rcvChooseCardType.appear()
//            imgBlackBackgroundChooseCardType.appear()
//        })
//    }}
//
//
//}

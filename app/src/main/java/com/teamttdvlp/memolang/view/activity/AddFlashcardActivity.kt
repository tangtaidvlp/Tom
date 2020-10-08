package com.teamttdvlp.memolang.view.activity

import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.view.animation.Animation
import android.widget.TextView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.databinding.ActivityAddFlashcardBinding
import com.teamttdvlp.memolang.view.activity.iview.AddFlashcardView
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter
import com.teamttdvlp.memolang.view.adapter.RCVRecentUsedLanguageAdapter
import com.teamttdvlp.memolang.view.adapter.RCV_FlashcardSetNameAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.ViewModelProviderFactory
import com.teamttdvlp.memolang.view.helper.getActivityViewModel
import com.teamttdvlp.memolang.viewmodel.AddFlashCardViewModel
import javax.inject.Inject


class AddFlashcardActivity : BaseActivity<ActivityAddFlashcardBinding, AddFlashCardViewModel>()
    , AddFlashcardView {

    companion object {

        private val SET_NAME = "set_name"

        fun requestAddLanguage(packageContext: Context, deck: Deck) {
            val intent = Intent(packageContext, AddFlashcardActivity::class.java)
            intent.putExtra(SET_NAME, deck)
            packageContext.startActivity(intent)
        }
    }

    // The image will stay on screen for a while before disappearing
    private val TIME_THAT_SAVED_FLASHCARD_IMAGE_STAY_ON_SCREEN = 400L

    private var selectedTextHighlightAnim: Animation? = null

    private val animatorSetSaving = AnimatorSet()

    private val animatorSetChooseLangAppear = AnimatorSet()

    private val animatorSetChooseLangDisappear = AnimatorSet()

    private var currentFocusedLanguageTextView: TextView? = null

    private var isInChooseLanguageMode = false

    private var isIPAKeyboardVisible = false

    lateinit var rcvChooseLanguageAdapter: RCVChooseLanguageAdapter
        @Inject set

    lateinit var rcvRecentChosenLanguageAdapter: RCVRecentUsedLanguageAdapter
        @Inject set

    lateinit var rcvFlashcardSetNameAdapter: RCV_FlashcardSetNameAdapter
        @Inject set

    lateinit var viewModelProviderFactory: ViewModelProviderFactory
        @Inject set

    override fun getLayoutId(): Int = R.layout.activity_add_flashcard

    override fun takeViewModel(): AddFlashCardViewModel {
        return getActivityViewModel(viewModelProviderFactory)
    }

    override fun showTextInputError() {
        TODO("Not yet implemented")
    }

    override fun showTranslationInputError() {
        TODO("Not yet implemented")
    }

    override fun onAddFlashcardSuccess() {
        TODO("Not yet implemented")
    }

    override fun showInvalidFlashcardSetError(errorMessage: String) {
        TODO("Not yet implemented")
    }

    override fun hideCreateNewFlashcardSetPanel() {
        TODO("Not yet implemented")
    }

    override fun showFrontEmptyImageError() {
        TODO("Not yet implemented")
    }

    override fun showBackEmptyImageError() {
        TODO("Not yet implemented")
    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        viewModel.setUpView(this)
//        dB.dbViewModel = viewModel
//    }
//
//    override fun onPause() {
//        super.onPause()
//        viewModel.saveUsingHistory()
//    }
//
//    override fun addViewControls() {
//        dB.apply {
//            showDefault_Info_OnScreen()
//            disableEdtPronunciation()
//
//            // Choose Flashcard SET NAME Recycler View
//            layoutAddFlashcard.rcvFlashcardSetName.adapter = rcvFlashcardSetNameAdapter
//            viewModel.getAllFlashcardSetWithNoCardList { flashcardSetList ->
//                rcvFlashcardSetNameAdapter.setData(flashcardSetList)
//            }
//
//            // Choose LANGUAGE RecyclerView
//            rcvChooseLanguage.adapter = rcvChooseLanguageAdapter
//
//            // Recent CHOSEN LANGUAGE RecyclerView
//            rcvRecentChosenLanguage.adapter = rcvRecentChosenLanguageAdapter
//            viewModel.getUsedLanguageList { recentUsedLanguageList ->
//                rcvRecentChosenLanguageAdapter.setData(recentUsedLanguageList)
//            }
//
//            // Choose TYPE RecyclerView
//            rcvChooseCardType.addTypes(viewModel.getUserOwnCardTypes())
//
//            ipaKeyboard.setFocusedText(dB.layoutAddFlashcard.edtPronunciation)
//        }
//    }
//
//    private fun disableEdtPronunciation() {
//        dB.layoutAddFlashcard.edtPronunciation.inputType = TYPE_NULL
//    }
//
//    private fun showDefault_Info_OnScreen() {
//        val flashcardSet = getAddFlashcardRequest()
//        if (flashcardSet == null) {
//            setUp_CurrentChosenOptions_OnScreen()
//        } else {
////            viewModel.showToUI(flashcardSet.name, flashcardSet.frontLanguage, flashcardSet.backLanguage)
//        }
//    }
//
//    private fun setUp_CurrentChosenOptions_OnScreen() {
//        val currentBackLang = viewModel.getCurrentBackLanguage()
//        val currentFrontLang = viewModel.getCurrentFrontLanguage()
//        val currentUseSetName = viewModel.getLastedUseFlashcardSetName()
////        viewModel.showToUI(currentUseSetName, currentFrontLang, currentBackLang)
//    }
//
//    override fun addViewEvents() {
//        dB.apply {
//
//            layoutAddFlashcard.edtPronunciation.setOnClickListener {
//                showIPAKeyboard()
//            }
//
//            ipaKeyboard.setOnDeviceVirtualKeyboardShow {
//                hideIPAKeyboard()
//            }
//
//            ipaKeyboard.setOnBtnDoneClickListener {
//                hideIPAKeyboard()
//            }
//
//            layoutAddFlashcard.btnAdd.setOnClickListener {
//                layoutAddFlashcard.apply {
//                    hideIPAKeyboard()
//                    val text = edtText.text.toString()
//                    val translation = edtTranslation.text.toString()
//                    val example = edtExample.text.toString()
//                    val meanOfExample = edtMeanOfExample.text.toString()
//                    val type = edtType.text.toString()
//                    val pronunciation = edtPronunciation.text.toString()
//                    val frontLanguage = txtFrontLang.text.toString()
//                    val backLanguage = txtBackLang.text.toString()
//                    val setName = edtSetName.text.toString()
//
//                    val newCard = Flashcard(id = 0, text = text,
//                            translation = translation,
//                            example = example,
//                            meanOfExample = meanOfExample,
//                            type = type,
//                            pronunciation = pronunciation,
//                            frontLanguage = frontLanguage,
//                            backLanguage = backLanguage,
//                            setOwner = setName)
//
////                    viewModel.proceedAddFlashcard(newCard)
//
//                    rcvChooseCardType.addType(type)
//                }
//            }
//
//            layoutAddFlashcard.apply {
//                viewgroupTxtFrontLang.setOnClickListener {
//                    hideVirtualKeyboard()
//                    animatorSetChooseLangAppear.start()
//                    currentFocusedLanguageTextView = txtFrontLang
//                }
//
//                viewgroupTxtBackLang.setOnClickListener {
//                    hideVirtualKeyboard()
//                    animatorSetChooseLangAppear.start()
//                    currentFocusedLanguageTextView = txtBackLang
//                }
//            }
//
//            imgBlackBackgroundChooseLanguage.setOnClickListener {
//                animatorSetChooseLangDisappear.start()
//            }
//
//            rcvChooseLanguageAdapter.setOnItemClickListener { language ->
//                onChooseLanguage(language)
//            }
//
//            rcvRecentChosenLanguageAdapter.setOnItemClickListener { language ->
//                onChooseLanguage(language)
//            }
//
//            layoutAddFlashcard.imgChooseTypeSpinner.setOnClickListener {
//                hideVirtualKeyboard()
//                dialogChooseCardType.show()
//            }
//
//            rcvChooseCardType.setOnItemClickListener { type ->
//                layoutAddFlashcard.edtType.setText(type)
//                dialogChooseCardType.dismiss()
//            }
//
//            layoutAddFlashcard.edtText.addTextChangeListener(onTextChanged = { text, _, _, _ ->
//                if ((text != "") and (layoutAddFlashcard.txtErrorText.isVisible)) {
//                    hideTextInputError()
//                }
//            })
//
//            layoutAddFlashcard.edtTranslation.addTextChangeListener(onTextChanged = { text, _, _, _ ->
//                if ((text != "") and (layoutAddFlashcard.txtErrorTranslation.isVisible)) {
//                    hideTranslationInputError()
//                }
//            })
//
//            rcvFlashcardSetNameAdapter.setOnItemClickListener { flashcardSet ->
//                layoutAddFlashcard.edtSetName.setText(flashcardSet.name)
//                layoutAddFlashcard.txtFrontLang.text = flashcardSet.frontLanguage
//                layoutAddFlashcard.txtBackLang.text = flashcardSet.backLanguage
//                hideChooseFlashcardSetOptions()
//            }
//
//            layoutAddFlashcard.btnCreateNewSet.setOnClickListener {
//                layoutAddFlashcard.apply {
//                    edtSetName.setText("")
//                    edtSetName.requestFocus()
//                    showVirtualKeyboard()
//                    hideChooseFlashcardSetOptions()
//                }
//            }
//
//            layoutAddFlashcard.imgSetNameSpinner.setOnClickListener {
//                if (layoutAddFlashcard.btnCreateNewSet.isVisible.not()) {
//                    showChooseFlashcardSetOptions()
//                } else {
//                    hideChooseFlashcardSetOptions()
//                }
//            }
//
//            btnInErrorPanelCreateNewSet.setOnClickListener {
//                dialogInvalidFlashcardSet.dismiss()
//                layoutAddFlashcard.edtSetName.setText("")
//                layoutAddFlashcard.edtSetName.requestFocus()
//                showVirtualKeyboard()
//            }
//
//            btnInErrorPanelEditLanguageInfo.setOnClickListener {
//                dB.dialogInvalidFlashcardSet.dismiss()
//            }
//
//        }
//    }
//
//    override fun hideCreateNewFlashcardSetPanel() {
//
//    }
//
//    override fun overrideEnterAnim() {
//        overridePendingTransition(R.anim.appear, R.anim.nothing)
//    }
//
//    override fun onBackPressed() {
//        dB.apply {
//            if (isIPAKeyboardVisible) {
//                hideIPAKeyboard()
//            } else {
//                super.onBackPressed()
//            }
//        }
//    }
//
//
//    private fun showChooseFlashcardSetOptions() {
//        if (rcvFlashcardSetNameAdapter.itemCount != 0) {
//            dB.layoutAddFlashcard.rcvFlashcardSetName.animate().alpha(1f).setDuration(100)
//                .setLiteListener(onEnd = {
//                    dB.layoutAddFlashcard.rcvFlashcardSetName.goVISIBLE()
//                })
//        }
//
//        dB.layoutAddFlashcard.btnCreateNewSet.animate().alpha(1f).setDuration(100)
//            .setLiteListener(onEnd = {
//                dB.layoutAddFlashcard.btnCreateNewSet.goVISIBLE()
//            })
//    }
//
//    private fun hideChooseFlashcardSetOptions() {
//        if (rcvFlashcardSetNameAdapter.itemCount != 0) {
//            dB.layoutAddFlashcard.rcvFlashcardSetName.animate().alpha(0f).setDuration(100)
//                .setLiteListener(onEnd = {
//                    dB.layoutAddFlashcard.rcvFlashcardSetName.goGONE()
//                })
//        }
//
//        dB.layoutAddFlashcard.btnCreateNewSet.animate().alpha(0f).setDuration(100)
//            .setLiteListener(onEnd = {
//                dB.layoutAddFlashcard.btnCreateNewSet.goGONE()
//            })
//    }
//
//    private fun getAddFlashcardRequest(): FlashcardSet? {
//        val bundle = intent.extras
//
//        if (bundle == null) {
//            return null
//        }
//
//        val flashcardSet = bundle.getSerializable(SET_NAME) as FlashcardSet
//        return flashcardSet
//    }
//
//
//    // VIEW IMPLEMENTED METHODS
//
//    private fun hideIPAKeyboard() {
//        dB.apply {
//            isIPAKeyboardVisible = false
//            ipaKeyboard.goINVISIBLE()
//            ipaKeyboard.db.keyboardParent.isClickable = false
//            layoutAddFlashcard.ipaKeyboardPlaceholder.goGONE()
//        }
//    }
//
//    private fun showIPAKeyboard() {
//        dB.apply {
//            ipaKeyboard.goVISIBLE()
//            ipaKeyboard.db.keyboardParent.isClickable = true
//            isIPAKeyboardVisible = true
//            layoutAddFlashcard.ipaKeyboardPlaceholder.goVISIBLE()
//            hideVirtualKeyboard()
//            Thread(kotlinx.coroutines.Runnable {
//                Thread.sleep(50)
//                runOnUiThread {
//                    layoutAddFlashcard.scrollview.scrollToBottom()
//                }
//            }).start()
//            clearAllScreenFocus()
//        }
//    }
//
//    private fun clearAllScreenFocus() {
//        dB.activityAddFlashcards.requestFocus()
//    }
//
//
//    override fun showTextInputError() {
//        dB.layoutAddFlashcard.edtText.requestFocus()
//        dB.layoutAddFlashcard.txtErrorText.goVISIBLE()
//    }
//
//    override fun showTranslationInputError() {
//        dB.layoutAddFlashcard.edtTranslation.goVISIBLE()
//        dB.layoutAddFlashcard.txtErrorTranslation.goVISIBLE()
//    }
//
//    override fun showInvalidFlashcardSetError(errorMessage: String) {
//        dB.txtInvalidSetName.text = errorMessage
//        dB.dialogInvalidFlashcardSet.show()
//    }
//
//    override fun onAddFlashcardSuccess() {
//        animatorSetSaving.start()
//        hideVirtualKeyboard()
//        currentFocus?.clearFocus()
//    }
//
//
//    private fun hideTextInputError() {
//        dB.layoutAddFlashcard.txtErrorText.goGONE()
//    }
//
//    private fun hideTranslationInputError() {
//        dB.layoutAddFlashcard.txtErrorTranslation.goGONE()
//    }
//
//    private fun onChooseLanguage(language: String) {
//        currentFocusedLanguageTextView?.text = language
//        if (currentFocusedLanguageTextView == dB.layoutAddFlashcard.txtFrontLang) {
//            viewModel.updateCurrentFrontLang(language)
//        } else if (currentFocusedLanguageTextView == dB.layoutAddFlashcard.txtBackLang) {
//            viewModel.updateCurrentBackLanguage(language)
//        }
//        animatorSetChooseLangDisappear.start()
//        addToUsedLanguageList(language)
//    }
//
//    private fun addToUsedLanguageList(language: String) {
//        rcvRecentChosenLanguageAdapter.addLanguage(language)
//        viewModel.addToUsedLanguageList(language)
//    }
//
//    // INJECTED METHOD
//
//
//    @Inject
//    fun initSaveAnimation(
//        @Named("ZoomToNothingAndRotate") groupDisappear: Animator,
//        @Named("ZoomFromNothingToOversizeThenNormalSize") savedImageAppear: Animator,
//        @Named("MoveRightAndFadeOutAnimtr") savedImageDisappear: Animator
//    ) {
//        dB.apply {
//
//            groupDisappear.apply {
//                setTarget(layoutAddFlashcard.root)
//                addListener(onEnd = { layoutAddFlashcard.root.goGONE() })
//            }
//
//            savedImageAppear.setTarget(imgSavedFlashcard)
//            savedImageDisappear.setTarget(imgSavedFlashcard)
//
//            val appearSet = AnimatorSet().apply { play(groupDisappear).before(savedImageAppear) }
//            val disappearSet = AnimatorSet().apply {
//                play(savedImageDisappear).after(TIME_THAT_SAVED_FLASHCARD_IMAGE_STAY_ON_SCREEN)
//            }
//            disappearSet.addListener(onEnd = {
//                finish()
//                quickStartActivity(AddFlashcardActivity::class.java)
//            })
//            animatorSetSaving.play(appearSet).before(disappearSet)
//        }
//    }
//
//    @Inject
//    fun initChooseLanguageAnimation(
//        @Named("FromNormalSizeToNothing") rcvChooseLanguageDisappear: Animator,
//        @Named("Disappear50Percents") blackBackgroundDisappear: Animator,
//        @Named("FromNothingToNormalSize") rcvChooseLanguageAppear: Animator,
//        @Named("Appear50Percents") blackBackgroundAppear: Animator
//    ) {
//        dB.apply {
//
//            rcvChooseLanguageDisappear.setTarget(viewgroupChooseLanguage)
//            blackBackgroundDisappear.setTarget(imgBlackBackgroundChooseLanguage)
//            animatorSetChooseLangDisappear.play(rcvChooseLanguageDisappear)
//                .before(blackBackgroundDisappear)
//            animatorSetChooseLangDisappear.addListener(onEnd = {
//                groupChooseLanguage.visibility = View.GONE
//                isInChooseLanguageMode = false
//            })
//
//            rcvChooseLanguageAppear.setTarget(viewgroupChooseLanguage)
//            blackBackgroundAppear.setTarget(imgBlackBackgroundChooseLanguage)
//            animatorSetChooseLangAppear.play(blackBackgroundAppear).before(rcvChooseLanguageAppear)
//            animatorSetChooseLangAppear.addListener(onStart = { groupChooseLanguage.goVISIBLE() },
//                onEnd = {
//                    isInChooseLanguageMode = true
//                })
//        }
//    }
//
//    @Inject
//    fun initSelectedTextAnimation(@Named("HighlightTextAnim") selectedTextHighlightAnim: Animation) {
//        dB.apply {
//            selectedTextHighlightAnim.addAnimationLister(
//                onStart = {
//                    val darkOrangeColor =
//                        ResourcesCompat.getColor(resources, R.color.dark_orange_text, null)
//                    currentFocusedLanguageTextView?.setTextColor(darkOrangeColor)
//                },
//                onEnd = {
//                    currentFocusedLanguageTextView?.setTextColor(Color.BLACK)
//                })
//            this@AddFlashcardActivity.selectedTextHighlightAnim = selectedTextHighlightAnim
//        }
//    }
}


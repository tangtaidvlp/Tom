package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.animation.addListener
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.databinding.ActivityEditFlashcardBinding
import com.teamttdvlp.memolang.view.activity.iview.EditFlashcardView
import com.teamttdvlp.memolang.view.adapter.RCV_FlashcardSetNameAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.EditFlashcardViewModel
import javax.inject.Inject
import javax.inject.Named

const val UPDATED_FLASHCARD = "updated_flashcard"

class EditFlashcardActivity : BaseActivity<ActivityEditFlashcardBinding, EditFlashcardViewModel>(), EditFlashcardView {

    // After this time, saved flashcard will move right and disappear
    private val SAVED_FLASHCARD_APPEAR_INTERVAL = 400L

    private val animatorSetSaving = AnimatorSet()

    private val animatorSetCancelSavingAppear = AnimatorSet()

    private val animatorSetCancelSavingDisappear = AnimatorSet()

    private val animatorSetChooseCardTypeAppear = AnimatorSet()

    private val animatorSetChooseCardTypeDisappear = AnimatorSet()

    private var isIPAKeyboardVisible: Boolean = false

    lateinit var rcvChooseSetNameAdapter : RCV_FlashcardSetNameAdapter
    @Inject set

    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    @Inject set


    override fun getLayoutId(): Int = R.layout.activity_edit_flashcard

    override fun takeViewModel(): EditFlashcardViewModel = getActivityViewModel(viewModelProviderFactory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        dB.vwModel = this@EditFlashcardActivity.viewModel
    }

    override fun addViewControls() { dB.apply {
        val editingCard : Flashcard = getNeedEdittingFlashcard()
        this@EditFlashcardActivity.viewModel.setOriginalCard(editingCard)

        val RED_COLOR = Color.parseColor("#F65046")
        layoutAddFlashcard.edtText.setTextColor(RED_COLOR)
        ipaKeyboard.setFocusedText(layoutAddFlashcard.edtPronunciation)

        adjustSomeUIFeatures()

        dB.layoutAddFlashcard.rcvFlashcardSetName.adapter = rcvChooseSetNameAdapter
        viewModel.getAll_SameLanguagesFCSet_WithNoCardList (editingCard.frontLanguage, editingCard.backLanguage) {
            rcvChooseSetNameAdapter.setData(it)
        }
    }}

    private fun adjustSomeUIFeatures() { dB.apply {
        layoutAddFlashcard.btnAdd.text = "Save"
    }}

    override fun addViewEvents() { dB.apply {

        ipaKeyboard.setOnBtnDoneClickListener {
            hideIPAKeyboard()
        }

        ipaKeyboard.setOnDeviceVirtualKeyboardShow {
            hideIPAKeyboard()
        }

        layoutAddFlashcard.edtPronunciation.setOnClickListener {
            showIPAKeyboard()
        }

        layoutAddFlashcard.btnAdd.setOnClickListener {
            log("nkhjk")
            hideIPAKeyboard()
            hideVirtualKeyboard()
            layoutAddFlashcard.apply {
                val text = edtText.text.toString()
                val translation = edtTranslation.text.toString()
                val setName = edtSetName.text.toString()
                val example = edtExample.text.toString()
                val exampleMean = edtMeanOfExample.text.toString()
                val sourceLang = txtFrontLang.text.toString()
                val targetLang = txtBackLang.text.toString()
                val type = edtType.text.toString()
                val pronunciation = edtPronunciation.text.toString()
                this@EditFlashcardActivity.viewModel.updateCard(
                    sourceLang, targetLang,
                    setName, type,
                    text, translation,
                    example, exampleMean, pronunciation
                )
            }
        }

        imgBlackBackgroundCancelSavingWidgets.setOnClickListener {
            animatorSetCancelSavingDisappear.start()
        }

        btnCancelIt.setOnClickListener {
            animatorSetCancelSavingDisappear.addListener(onEnd = {
                finish()
            })
            animatorSetCancelSavingDisappear.start()
        }

        layoutAddFlashcard.edtType.setOnClickListener {
            animatorSetChooseCardTypeAppear.start()
        }

        rcvChooseCardType.setOnItemClickListener { type ->
            layoutAddFlashcard.edtType.setText(type)
            animatorSetChooseCardTypeDisappear.start()
        }

        rcvChooseSetNameAdapter.setOnItemClickListener { flashcardSet ->
            layoutAddFlashcard.edtSetName.setText(flashcardSet.name)
            layoutAddFlashcard.txtFrontLang.text = flashcardSet.frontLanguage
            layoutAddFlashcard.txtBackLang.text = flashcardSet.backLanguage
            hideChooseFlashcardSetOptions()
        }

        layoutAddFlashcard.btnCreateNewSet.setOnClickListener {
            layoutAddFlashcard.apply {
                edtSetName.setText("")
                edtSetName.requestFocus()
                showVirtualKeyboard()
                hideChooseFlashcardSetOptions()
            }
        }

        layoutAddFlashcard.imgSetNameSpinner.setOnClickListener {
            if (layoutAddFlashcard.btnCreateNewSet.isVisible.not()) {
                showChooseFlashcardSetOptions()
            } else {
                hideChooseFlashcardSetOptions()
            }
        }

    }}

    private fun showChooseFlashcardSetOptions() {
        if (rcvChooseSetNameAdapter.itemCount != 0) {
            dB.layoutAddFlashcard.rcvFlashcardSetName.animate().alpha(1f).setDuration(100)
                .setLiteListener(onEnd = {
                    dB.layoutAddFlashcard.rcvFlashcardSetName.goVISIBLE()
                })
        }

        dB.layoutAddFlashcard.btnCreateNewSet.animate().alpha(1f).setDuration(100)
            .setLiteListener(onEnd = {
                dB.layoutAddFlashcard.btnCreateNewSet.goVISIBLE()
            })
    }

    private fun hideChooseFlashcardSetOptions() {
        if (rcvChooseSetNameAdapter.itemCount != 0) {
            dB.layoutAddFlashcard.rcvFlashcardSetName.animate().alpha(0f).setDuration(100)
                .setLiteListener(onEnd = {
                    dB.layoutAddFlashcard.rcvFlashcardSetName.goGONE()
                })
        }

        dB.layoutAddFlashcard.btnCreateNewSet.animate().alpha(0f).setDuration(100)
            .setLiteListener(onEnd = {
                dB.layoutAddFlashcard.btnCreateNewSet.goGONE()
            })
    }

    override fun onBackPressed() {
        dB.apply {
            if (isIPAKeyboardVisible)
                hideIPAKeyboard()
            else if (viewgroupCancelSaving.isGone) {
                val newFlashcard : Flashcard
                layoutAddFlashcard.apply {
                    val setName = edtSetName.text.toString()
                    val text = edtText.text.toString()
                    val translation = edtTranslation.text.toString()
                    val example = edtExample.text.toString()
                    val exampleMean  = edtMeanOfExample.text.toString()
                    val type = edtType.text.toString()
                    val pronunciation = edtPronunciation.text.toString()
                    val sourceLang = txtFrontLang.text.toString()
                    val targetLang = txtBackLang.text.toString()
                    newFlashcard = Flashcard(frontLanguage = sourceLang, backLanguage = targetLang,
                                                                    text = text, translation = translation,
                                                                    type = type, setOwner = setName,
                                                                    example = example, meanOfExample = exampleMean,
                                                                    pronunciation = pronunciation)
                }
                if (viewModel.doesUserChangeInfo(newFlashcard)) {
                    animatorSetCancelSavingAppear.start()
                    hideVirtualKeyboard()
                } else finish()
            }
        }
    }

    fun getNeedEdittingFlashcard () : Flashcard = intent.getSerializableExtra(FLASHCARD_KEY) as Flashcard

    // VIEW IMPLEMENTED METHODS

    private fun hideIPAKeyboard() { dB.apply {
        isIPAKeyboardVisible = false
        ipaKeyboard.goINVISIBLE()
        ipaKeyboard.db.keyboardParent.isClickable = false
        layoutAddFlashcard.ipaKeyboardPlaceholder.goGONE()
    }}

    private fun showIPAKeyboard() { dB.apply {
        ipaKeyboard.goVISIBLE()
        ipaKeyboard.db.keyboardParent.isClickable = true
        isIPAKeyboardVisible = true
        layoutAddFlashcard.ipaKeyboardPlaceholder.goVISIBLE()
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
        dB.layoutAddFlashcard.txtErrorText.goVISIBLE()
    }

    override fun showTranslationInputError() {
        dB.layoutAddFlashcard.txtErrorTranslation.goVISIBLE()
    }

    override fun endEditing() {
        finish()
    }

    override fun onUpdateFlashcardSuccess(newCard : Flashcard) {
        returnUpdatedFlashcardResult(newCard)
        animatorSetSaving.start()
        hideVirtualKeyboard()
        currentFocus?.clearFocus()
    }

    fun hideTextInputError() {
        dB.layoutAddFlashcard.txtErrorText.goGONE()
    }

    fun hideTranslationInputError() {
        dB.layoutAddFlashcard.txtErrorTranslation.goGONE()
    }


    /**
     * RECEIVER
     * @see ViewFlashCardListActivity
     */

    fun returnUpdatedFlashcardResult (updatedFlashcard : Flashcard) {
        val intent = Intent()
        intent.putExtra(UPDATED_FLASHCARD, updatedFlashcard)
        setResult(Activity.RESULT_OK, intent)
    }


    /**
     * Dependencies Injected Block0
     */

    @Inject
    fun initSaveAnimation(
        @Named("ZoomToNothingAndRotate") groupDisappear: Animator,
        @Named("ZoomFromNothingToOversizeThenNormalSize") savedImageAppear: Animator,
        @Named("MoveRightAndFadeOutAnimtr") savedImageDisappear: Animator
    ) {
        dB.apply {

            groupDisappear.apply {
                setTarget(viewgroupAddFlashcard)
                addListener(onEnd = { viewgroupAddFlashcard.goGONE() })
            }

            savedImageAppear.setTarget(imgSavedFlashcard)
            savedImageDisappear.setTarget(imgSavedFlashcard)

            val appearSet = AnimatorSet().apply { play(groupDisappear).before(savedImageAppear) }
            val disappearSet = AnimatorSet().apply { play(savedImageDisappear).after(SAVED_FLASHCARD_APPEAR_INTERVAL) }
            disappearSet.addListener(onEnd = { finish() })
            animatorSetSaving.play(appearSet).before(disappearSet)
        }
    }

    @Inject
    fun initCancelSavingAnimation(
        @Named("FromNormalSizeToNothing") viewgroupCancelSavingDisappear: Animator,
        @Named("Disappear50Percents") blackBackgroundDisappear: Animator,
        @Named("FromNothingToNormalSize") viewgroupCancelSavingAppear: Animator,
        @Named("Appear50Percents") blackBackgroundAppear: Animator
    ) {
        dB.apply {

            viewgroupCancelSavingDisappear.setTarget(viewgroupCancelSaving)
            blackBackgroundDisappear.setTarget(imgBlackBackgroundCancelSavingWidgets)
            animatorSetCancelSavingDisappear.play(viewgroupCancelSavingDisappear).before(blackBackgroundDisappear)
            animatorSetCancelSavingDisappear.addListener(onEnd = { groupCancelSavingWidgets.goGONE() })

            viewgroupCancelSavingAppear.setTarget(viewgroupCancelSaving)
            blackBackgroundAppear.setTarget(imgBlackBackgroundCancelSavingWidgets)
            animatorSetCancelSavingAppear.play(blackBackgroundAppear).before(viewgroupCancelSavingAppear)
            animatorSetCancelSavingAppear.addListener(onStart = { groupCancelSavingWidgets.goVISIBLE() })
        }
    }

}

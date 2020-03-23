package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.animation.addListener
import androidx.core.view.isGone
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityEditFlashcardBinding
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.entity.Language.Companion.SOURCE_LANGUAGE
import com.teamttdvlp.memolang.model.entity.Language.Companion.TARGET_LANGUAGE
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.model.entity.Language.Companion.LANG_DIVIDER
import com.teamttdvlp.memolang.view.activity.iview.EditFlashcardView
import com.teamttdvlp.memolang.viewmodel.EditFlashcardViewModel
//import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager
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


    // These variables are used for checking if the user change card's informations
    // OG: Original
    private lateinit var OG_SourceLang: String
    private lateinit var OG_TargetLang: String
    private lateinit var OG_Text: String
    private lateinit var OG_Translation: String
    private lateinit var OG_Using: String

    lateinit var flashcardRepository: FlashcardRepository
    @Inject set

//    lateinit var onlineFlashcardManager: OnlineFlashcardDBManager
//    @Inject set

    override fun getLayoutId(): Int = R.layout.activity_edit_flashcard

    override fun takeViewModel(): EditFlashcardViewModel = getActivityViewModel() {
        EditFlashcardViewModel(/*onlineFlashcardManager,*/
            flashcardRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
    }

    override fun initProperties() {
        dB.apply {
            val edittingCard : Flashcard = getData().apply {
                val languageDetail = languagePair.split(LANG_DIVIDER)
                val sourceLang = languageDetail[SOURCE_LANGUAGE]
                val targetLang = languageDetail[TARGET_LANGUAGE]

                OG_SourceLang = sourceLang
                OG_TargetLang = targetLang
                OG_Text = text
                OG_Translation = translation
                OG_Using = example
            }
            this@EditFlashcardActivity.viewModel.setOriginalCard(edittingCard)
            dB.viewModel = this@EditFlashcardActivity.viewModel
        }
    }

    override fun addViewControls() { dB.apply {
        val RED_COLOR = Color.parseColor("#F65046")
        layoutAddFlashcard.edtText.setTextColor(RED_COLOR)
        ipaKeyboard.setFocusedText(layoutAddFlashcard.edtPronunciation)

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
            quickLog("nkhjk")
            hideIPAKeyboard()
            hideVirtualKeyboard()
            layoutAddFlashcard.apply {
                val text = edtText.text.toString()
                val translation = edtTranslation.text.toString()
                val setName = edtSetName.text.toString()
                val example = edtExample.text.toString()
                val exampleMean = edtMeanOfExample.text.toString()
                val sourceLang = txtSourceLang.text.toString()
                val targetLang = txtTargetLang.text.toString()
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
            layoutAddFlashcard.edtType.text = type
            animatorSetChooseCardTypeDisappear.start()
        }

    }}

    override fun onBackPressed() {
        dB.apply {
            if (isIPAKeyboardVisible)
                hideIPAKeyboard()
            else if (viewgroupCancelSaving.isGone) {
                if (doesUserChangeInfo()) {
                    animatorSetCancelSavingAppear.start()
                    hideVirtualKeyboard()
                } else finish()
            }
        }
    }

    fun getData () : Flashcard = intent.getSerializableExtra(FLASHCARD_KEY) as Flashcard

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
        dB.layoutAddFlashcard.txtErrorText.disappear()
    }

    fun hideTranslationInputError() {
        dB.layoutAddFlashcard.txtErrorTranslation.disappear()
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


    private fun doesUserChangeInfo(): Boolean {
        dB.layoutAddFlashcard.apply {
            val newSourceLang = txtSourceLang.text.toString()
            val newTargetLang = txtTargetLang.text.toString()
            val newText = edtText.text.toString()
            val newTranslation = edtTranslation.text.toString()
            val newUsing = edtExample.text.toString()

            return (OG_SourceLang != newSourceLang) || (OG_TargetLang != newTargetLang) || (OG_Text != newText) || (OG_Translation != newTranslation) || (OG_Using != newUsing)
        }
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
                addListener(onEnd = { viewgroupAddFlashcard.disappear() })
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
            animatorSetCancelSavingDisappear.addListener(onEnd = { groupCancelSavingWidgets.disappear() })

            viewgroupCancelSavingAppear.setTarget(viewgroupCancelSaving)
            blackBackgroundAppear.setTarget(imgBlackBackgroundCancelSavingWidgets)
            animatorSetCancelSavingAppear.play(blackBackgroundAppear).before(viewgroupCancelSavingAppear)
            animatorSetCancelSavingAppear.addListener(onStart = { groupCancelSavingWidgets.appear() })
        }
    }

}

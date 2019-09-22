package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.graphics.Color
import com.teamttdvlp.memolang.view.activity.base.BaseActivity
import com.teamttdvlp.memolang.viewmodel.add_flashcard.AddFlashCardActivityViewModel
import com.teamttdvlp.memolang.databinding.ActivityAddFlashcardBinding
import android.view.animation.*
import com.teamttdvlp.memolang.R
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.view.activity.adapter.ChooseLanguageRCVAdapter
import com.teamttdvlp.memolang.view.activity.helper.*
import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.model.model.Language
import com.teamttdvlp.memolang.model.sqlite.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.model.sqlite.repository.FlashcardRepository
import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager
import javax.inject.Inject
import javax.inject.Named


class AddFlashcardActivity : BaseActivity<ActivityAddFlashcardBinding, AddFlashCardActivityViewModel>() {

    // After this time, saved flashcard will move right and disappear
    private val SAVED_FLASHCARD_APPEAR_INTERVAL = 400L

    private var selectedTextHighlightAnim : Animation? = null

    private val animatorSetSaving = AnimatorSet()

    private val animatorSetCancelSavingAppear = AnimatorSet()

    private val animatorSetCancelSavingDisappear = AnimatorSet()

    private val animatorSetChooseLangAppear = AnimatorSet()

    private val animatorSetChooseLangDisappear = AnimatorSet()

    private var selectedLanguageSessionTextView : TextView? = null

    var rcvChooseLanguageAdapter : ChooseLanguageRCVAdapter = ChooseLanguageRCVAdapter(this)

    var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

    // These variables are used for checking if the user change card's informations
    // OG: Original
    private lateinit var OG_SourceLang : String
    private lateinit var OG_TargetLang : String
    private lateinit var OG_Text: String
    private lateinit var OG_Translation: String
    private lateinit var OG_Using: String

    override fun getLayoutId(): Int  = R.layout.activity_add_flashcard

    lateinit var database : MemoLangSqliteDataBase
    @Inject set

    lateinit var onlineFlashcardManager: OnlineFlashcardDBManager
    @Inject set

    lateinit var flashcardRepository: FlashcardRepository
    @Inject set

    override fun takeViewModel(): AddFlashCardActivityViewModel = getActivityViewModel {
        AddFlashCardActivityViewModel(database, onlineFlashcardManager, flashcardRepository)
    }

    override fun initProperties() { dataBinding.apply {
        OG_SourceLang = txtSourceLang.text.toString()
        OG_TargetLang = txtTargetLang.text.toString()
        OG_Text  = edtText.text.toString()
        OG_Translation = edtTranslation.text.toString()
        OG_Using = edtUsing.text.toString()
    }}

    override fun addViewControls() { dataBinding.apply {
        /**
         *  The $viewgroupCancelSaving is clicked through
         *  which leads to triggering the listener of the $imgBlackBackgroundCancelSavingWidgets
         *  lying below it. Set clickable true to this view to avoid that
         */
        viewgroupCancelSaving.isClickable = true
        rcvChooseSourceLanguage.adapter = rcvChooseLanguageAdapter
        rcvChooseSourceLanguage.layoutManager = linearLayoutManager!!
        if (viewModel.getSingletonUser() != null) {
            txtSourceLang.text = viewModel.getSingletonUser()!!.motherLanguage
            txtSourceLang.text = viewModel.getSingletonUser()!!.targetLanguage
        } else {
            txtSourceLang.text = Language.ENGLISH
            txtTargetLang.text = Language.VIETNAMESE
        }

    }}

    override fun addViewEvents() { dataBinding.apply {

        btnSave.setOnClickListener {
            val text = edtText.text.toString()
            val translation = edtTranslation.text.toString()
            val using = edtUsing.text.toString()
            val type = txtSourceLang.text.toString() + "-" + txtTargetLang.text.toString()
            val newCard = Flashcard(0, text, translation, type, using)
            viewModel.addFlashcardToOfflineDB(newCard) { isSuccess, insertedCardId, exception ->
                if (isSuccess) {
                    animatorSetSaving.start()
                    hideVirtualKeyboard()
                    currentFocus?.clearFocus()

                    newCard.id = insertedCardId.toInt()
                    val flashcardSetId = "${txtSourceLang.text}-${txtTargetLang.text}"
                    viewModel.addFlashcardToOnlineDB(flashcardSetId, newCard) { isSuccess, exception ->
                        if (isSuccess) {
                            quickLog("Storing this flashcard to local online storage success")
                        } else {
                            quickLog("Storing this flashcard to local online storage failed")
                            exception!!.printStackTrace()
                        }
                    }
                } else {
                    quickLog("Storing this flashcard to local storage failed. Please check again")
                    exception!!.printStackTrace()
                }
            }
        }

        btnCancel.setOnClickListener {
            if (doesUserChangeInfo()) {
                animatorSetCancelSavingAppear.start()
                hideVirtualKeyboard()
            } else finish()
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

        viewgroupTxtSourceLang.setOnClickListener {
            animatorSetChooseLangAppear.start()
            selectedLanguageSessionTextView = txtSourceLang
        }

        viewgroupTxtTargetLang.setOnClickListener {
            animatorSetChooseLangAppear.start()
            selectedLanguageSessionTextView = txtTargetLang
        }

        imgBlackBackgroundChooseLanguage.setOnClickListener {
            animatorSetChooseLangDisappear.start()
        }

        rcvChooseLanguageAdapter.setOnItemClickListener {
            selectedLanguageSessionTextView?.text = it
            animatorSetChooseLangDisappear.start()
            quickLog("Click")
        }
    }}

    override fun overrideEnterAnim() {
        overridePendingTransition(R.anim.from_left_to_centre, R.anim.nothing)
    }

    override fun onBackPressed() { dataBinding.apply {
        if (viewgroupCancelSaving.isGone) {
            btnCancel.performClick()
        }
    }}

    private fun doesUserChangeInfo () : Boolean { dataBinding.apply {
        val newSourceLang = txtSourceLang.text.toString()
        val newTargetLang = txtTargetLang.text.toString()
        val newText = edtText.text.toString()
        val newTranslation = edtTranslation.text.toString()
        val newUsing = edtUsing.text.toString()

        return (OG_SourceLang != newSourceLang) || (OG_TargetLang != newTargetLang) || (OG_Text != newText) || (OG_Translation != newTranslation) || (OG_Using != newUsing)
    }}

    @Inject
    fun initSaveAnimation (
        @Named("ZoomToNothingAndRotate_") groupDisappear : Animator,
        @Named("ZoomFromNothingToOversizeThenNormalSize_") savedImageAppear : Animator,
        @Named("MoveRightAndFadeOut_") savedImageDisappear : Animator) { dataBinding.apply {

        groupDisappear.apply {
            setTarget(viewgroupAddFlashcard)
            addListener(onEnd = { viewgroupAddFlashcard.disappear() })
        }

        savedImageAppear.setTarget(imgSavedFlashcard)
        savedImageDisappear.setTarget(imgSavedFlashcard)

        val appearSet = AnimatorSet().apply { play(groupDisappear).before(savedImageAppear) }
        val disappearSet = AnimatorSet().apply { play(savedImageDisappear).after(SAVED_FLASHCARD_APPEAR_INTERVAL)}
        disappearSet.addListener(onEnd = { finish() })
        animatorSetSaving.play(appearSet).before(disappearSet)
    }}

    @Inject
    fun initCancelSavingAnimation (
        @Named("FromNormalSizeToNothing_") viewgroupCancelSavingDisappear: Animator,
        @Named("Disappear50percents_") blackBackgroundDisappear: Animator,
        @Named("FromNothingToNormalSize_") viewgroupCancelSavingAppear: Animator,
        @Named("Appear50percents_") blackBackgroundAppear: Animator) { dataBinding.apply {

        viewgroupCancelSavingDisappear.setTarget(viewgroupCancelSaving)
        blackBackgroundDisappear.setTarget(imgBlackBackgroundCancelSavingWidgets)
        animatorSetCancelSavingDisappear.play(viewgroupCancelSavingDisappear).before(blackBackgroundDisappear)
        animatorSetCancelSavingDisappear.addListener (onEnd = { groupCancelSavingWidgets.disappear()})

        viewgroupCancelSavingAppear.setTarget(viewgroupCancelSaving)
        blackBackgroundAppear.setTarget(imgBlackBackgroundCancelSavingWidgets)
        animatorSetCancelSavingAppear.play(blackBackgroundAppear).before(viewgroupCancelSavingAppear)
        animatorSetCancelSavingAppear.addListener(onStart = {groupCancelSavingWidgets.appear()})
    }}

    @Inject
    fun initChooseLanguageAnimation (
        @Named("FromNormalSizeToNothing_") rcvChooseLanguageDisappear : Animator,
        @Named("Disappear50percents_") blackBackgroundDisappear : Animator,
        @Named("FromNothingToNormalSize_") rcvChooseLanguageAppear : Animator,
        @Named("Appear50percents_") blackBackgroundAppear : Animator) { dataBinding.apply {

        rcvChooseLanguageDisappear.setTarget(rcvChooseSourceLanguage)
        blackBackgroundDisappear.setTarget(imgBlackBackgroundChooseLanguage)
        animatorSetChooseLangDisappear.play(rcvChooseLanguageDisappear).before(blackBackgroundDisappear)
        animatorSetChooseLangDisappear.addListener (onEnd = {
            groupChooseLanguage.disappear()
            selectedLanguageSessionTextView?.startAnimation(selectedTextHighlightAnim)
        })

        rcvChooseLanguageAppear.setTarget(rcvChooseSourceLanguage)
        blackBackgroundAppear.setTarget(imgBlackBackgroundChooseLanguage)
        animatorSetChooseLangAppear.play(blackBackgroundAppear).before(rcvChooseLanguageAppear)
        animatorSetChooseLangAppear.addListener(onStart = {groupChooseLanguage.appear()})
    }}

    @Inject
    fun initSelectedTextAnimation (@Named("HighlightTextAnim") selectedTextHighlightAnim : Animation) { dataBinding.apply {
        selectedTextHighlightAnim.addAnimationLister(
            onStart = {
            val darkOrangeColor = ResourcesCompat.getColor(resources, R.color.dark_orange_text, null)
            selectedLanguageSessionTextView?.setTextColor(darkOrangeColor)
        },
            onEnd = {
            selectedLanguageSessionTextView?.setTextColor(Color.BLACK)
        })
        this@AddFlashcardActivity.selectedTextHighlightAnim = selectedTextHighlightAnim
    }}

}


package com.teamttdvlp.memolang.view.Activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Color
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.viewmodel.add_flashcard.AddFlashCardActivityViewModel
import com.teamttdvlp.memolang.databinding.ActivityAddFlashcardBinding
import android.view.animation.*
import com.teamttdvlp.memolang.R
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatorInflaterCompat
import com.teamttdvlp.memolang.view.Activity.adapter.ChooseLanguageRCVAdapter
import com.teamttdvlp.memolang.view.Activity.di.MemoLang
import com.teamttdvlp.memolang.view.Activity.helper.*
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

    override fun getLayoutId(): Int  = R.layout.activity_add_flashcard

    override fun takeViewModel(): AddFlashCardActivityViewModel = getActivityViewModel()

    override fun addViewControls() { dataBinding.apply {
        /**
         *  The $viewgroupCancelSaving is clicked through
         *  which leads to triggering the listener of the $imgBlackBackgroundCancelSavingWidgets
         *  lying below it. Set clickable true to this view to avoid that
         */
        viewgroupCancelSaving.isClickable = true
        rcvChooseSourceLanguage.adapter = rcvChooseLanguageAdapter
        rcvChooseSourceLanguage.layoutManager = linearLayoutManager

    }}

    override fun addViewEvents() { dataBinding.apply {

        btnSave.setOnClickListener {
            animatorSetSaving.start()
            hideVirtualKeyboard()
            currentFocus?.clearFocus()
        }

        btnCancel.setOnClickListener {
            animatorSetCancelSavingAppear.start()
        }

        imgBlackBackgroundCancelSavingWidgets.setOnClickListener {
            animatorSetCancelSavingDisappear.start()
        }

        btnCancelIt.setOnClickListener {
            finish()
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

    @Inject
    fun initSaveAnimation (
        @Named("ZoomToNothingAndRotate") groupDisappear : Animator,
        @Named("ZoomFromNothingToOversizeThenNormalSize") savedImageAppear : Animator,
        @Named("MoveRightAndFadeOut") savedImageDisappear : Animator) { dataBinding.apply {

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
    fun initCancelAnimation (
        @Named("FromNormalSizeToNothing") viewgroupCancelSavingDisappear: Animator,
        @Named("Disappear50percents") blackBackgroundDisappear: Animator,
        @Named("FromNothingToNormalSize") viewgroupCancelSavingAppear: Animator,
        @Named("Appear50percents") blackBackgroundAppear: Animator) { dataBinding.apply {

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
        @Named("FromNormalSizeToNothing") rcvChooseLanguageDisappear : Animator,
        @Named("Disappear50percents") blackBackgroundDisappear : Animator,
        @Named("FromNothingToNormalSize") rcvChooseLanguageAppear : Animator,
        @Named("Appear50percents") blackBackgroundAppear : Animator) { dataBinding.apply {

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
    fun initSelectedTextAnimation (selectedTextHighlightAnim : Animation) { dataBinding.apply {
        selectedTextHighlightAnim!!.addAnimationLister(onStart = {
            val darkOrangeColor = ResourcesCompat.getColor(resources, R.color.dark_orange_text, null)
            selectedLanguageSessionTextView?.setTextColor(darkOrangeColor)
        }, onEnd = {
            selectedLanguageSessionTextView?.setTextColor(Color.BLACK)
        })
        this@AddFlashcardActivity.selectedTextHighlightAnim = selectedTextHighlightAnim
    }}

}


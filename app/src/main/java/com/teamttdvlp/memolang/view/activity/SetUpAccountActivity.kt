package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.animation.addListener
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.language.Language
import com.teamttdvlp.memolang.databinding.ActivitySetUpAccountBinding
import com.teamttdvlp.memolang.view.activity.iview.SetUpAccountView
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.ViewModelProviderFactory
import com.teamttdvlp.memolang.view.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.helper.goVISIBLE
import com.teamttdvlp.memolang.view.helper.quickStartActivity
import com.teamttdvlp.memolang.viewmodel.SetUpAccountViewModel
import javax.inject.Inject
import javax.inject.Named

class SetUpAccountActivity : BaseActivity<ActivitySetUpAccountBinding, SetUpAccountViewModel>()
                            ,SetUpAccountView {

    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    @Inject set

    lateinit var rcvChooseLanguageAdapter : RCVChooseLanguageAdapter
    @Inject set

    private var beingFocusedTextView : TextView? = null

    private val animatorSetChooseLangAppear = AnimatorSet()

    private val animatorSetChooseLangDisappear = AnimatorSet()

    private var isInChooseLanguageMode = false


    override fun getLayoutId(): Int  = R.layout.activity_set_up_account

    override fun takeViewModel(): SetUpAccountViewModel = getActivityViewModel(viewModelProviderFactory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        viewModel.setUpView(this)
        viewModel.checkUserInfoSetUpStatus()
        setStatusBarColor(Color.parseColor("#301F889B"))

    }

    override fun addViewSettings() {
        dB.apply {
            layoutChooseLang.rcvChooseLanguage.adapter = rcvChooseLanguageAdapter
            rcvChooseLanguageAdapter.setData(Language.languageList)
        }
    }

    override fun addViewEvents() { dB.apply {

        btnDone.setOnClickListener {
            val defaultBackLanguage = txtCurrentUsedLanguage.text.toString()
            val defaultFrontLanguage = txtCurrentLearnedLanguage.text.toString()
            viewModel.createAndSaveUserInfoStatus(defaultFrontLanguage, defaultBackLanguage)
            navigateToMenuScreen()
        }

        rcvChooseLanguageAdapter.setOnItemClickListener { lang ->
            beingFocusedTextView!!.text = lang
            animatorSetChooseLangDisappear.start()
        }

        txtCurrentUsedLanguage.setOnClickListener {
            beingFocusedTextView = txtCurrentUsedLanguage
            animatorSetChooseLangAppear.start()
        }

        txtCurrentLearnedLanguage.setOnClickListener {
            beingFocusedTextView = txtCurrentLearnedLanguage
            animatorSetChooseLangAppear.start()
        }

        layoutChooseLang.imgBlackBackgroundChooseLanguage.setOnClickListener {
            animatorSetChooseLangDisappear.start()
        }
    }}

    override fun navigateToMenuScreen() {
        quickStartActivity(MenuActivity::class.java)
        finish()
    }

    @Inject
    fun initChooseLanguageAnimation (
        @Named("_FromNormalSizeToNothing_") rcvChooseLanguageDisappear : Animator,
        @Named("_Disappear50percents_") blackBackgroundDisappear : Animator,
        @Named("_FromNothingToNormalSize_") rcvChooseLanguageAppear : Animator,
        @Named("_Appear50percents_") blackBackgroundAppear : Animator
    ) { dB.apply {

        rcvChooseLanguageDisappear.setTarget(layoutChooseLang.viewgroupChooseLanguage)
        blackBackgroundDisappear.setTarget(layoutChooseLang.imgBlackBackgroundChooseLanguage)
        animatorSetChooseLangDisappear.play(rcvChooseLanguageDisappear).before(blackBackgroundDisappear)
        animatorSetChooseLangDisappear.addListener (onEnd = {
            layoutChooseLang.groupChooseLanguage.visibility = View.GONE
            isInChooseLanguageMode = false
        })

        rcvChooseLanguageAppear.setTarget(layoutChooseLang.viewgroupChooseLanguage)
        blackBackgroundAppear.setTarget(layoutChooseLang.imgBlackBackgroundChooseLanguage)
        animatorSetChooseLangAppear.play(blackBackgroundAppear).before(rcvChooseLanguageAppear)
        animatorSetChooseLangAppear.addListener(onStart = {layoutChooseLang.groupChooseLanguage.goVISIBLE()},
            onEnd = {
                isInChooseLanguageMode = true
            })
    }}

    override fun onBackPressed() {
        if (isInChooseLanguageMode) animatorSetChooseLangDisappear.start()
        else super.onBackPressed()
    }

}

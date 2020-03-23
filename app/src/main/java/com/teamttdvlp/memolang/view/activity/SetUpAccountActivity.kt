package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivitySetUpAccountBinding
import com.teamttdvlp.memolang.database.sql.repository.UserRepository
import com.teamttdvlp.memolang.view.activity.iview.SetUpAccountView
import com.teamttdvlp.memolang.view.adapter.RCVChooseLanguageAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.appear
import com.teamttdvlp.memolang.view.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.helper.quickStartActivity
import com.teamttdvlp.memolang.viewmodel.SetUpAccountViewModel
import javax.inject.Inject
import javax.inject.Named

class SetUpAccountActivity : BaseActivity<ActivitySetUpAccountBinding, SetUpAccountViewModel>()
                            ,SetUpAccountView{

    lateinit var firebaseAuth : FirebaseAuth
    @Inject set

    lateinit var userRepository: UserRepository
    @Inject set

    lateinit var rcvChooseLanguageAdapter : RCVChooseLanguageAdapter
    @Inject set

    lateinit var linearLayoutManager: LinearLayoutManager
    @Inject set

    private var beingFocusedTextView : TextView? = null

    private val animatorSetChooseLangAppear = AnimatorSet()

    private val animatorSetChooseLangDisappear = AnimatorSet()

    private var isInChooseLanguageMode = false


    override fun getLayoutId(): Int  = R.layout.activity_set_up_account

    override fun takeViewModel(): SetUpAccountViewModel = getActivityViewModel {
        SetUpAccountViewModel(
            firebaseAuth,
            userRepository,
            application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        viewModel.checkUserInfoBeenInitilized()
    }

    override fun addViewControls() { dB.apply {
        layoutChooseLang.rcvChooseLanguage.adapter = rcvChooseLanguageAdapter
        layoutChooseLang.rcvChooseLanguage.layoutManager = linearLayoutManager
    }}

    override fun addViewEvents() { dB.apply {

        btnDone.setOnClickListener {
            val motherLang = txtMotherLanguage.text.toString()
            val targetLang = txtTargetLanguage.text.toString()
            viewModel.createUserInfo(motherLang, targetLang)
            viewModel.getUser().addLanguageToRecentUseList(targetLang)
            viewModel.getUser().addLanguageToRecentUseList(motherLang)
        }

        rcvChooseLanguageAdapter.setOnItemClickListener { lang ->
            beingFocusedTextView!!.text = lang
            animatorSetChooseLangDisappear.start()
        }

        txtMotherLanguage.setOnClickListener {
            beingFocusedTextView = txtMotherLanguage
            animatorSetChooseLangAppear.start()
        }

        txtTargetLanguage.setOnClickListener {
            beingFocusedTextView = txtTargetLanguage
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
        animatorSetChooseLangAppear.addListener(onStart = {layoutChooseLang.groupChooseLanguage.appear()},
            onEnd = {
                isInChooseLanguageMode = true
            })
    }}

    override fun onBackPressed() {
        if (isInChooseLanguageMode) animatorSetChooseLangDisappear.start()
        else super.onBackPressed()
    }

}

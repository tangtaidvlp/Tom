package com.teamttdvlp.memolang.view.Activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Color
import android.view.View
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.viewmodel.add_flashcard.AddFlashCardActivityViewModel
import com.teamttdvlp.memolang.databinding.ActivityAddFlashcardBinding
import android.view.animation.*
import com.teamttdvlp.memolang.R
import android.util.DisplayMetrics
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.content.res.ResourcesCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.view.Activity.adapter.ChooseLanguageRCVAdapter
import com.teamttdvlp.memolang.view.Activity.helper.appear
import com.teamttdvlp.memolang.view.Activity.helper.disappear
import com.teamttdvlp.memolang.view.Activity.helper.quickLog


class AddFlashcardActivity : BaseActivity<ActivityAddFlashcardBinding, AddFlashCardActivityViewModel>() {

    private val VIEWGROUP_ADD_FLASHCARD_SAVING_DURATION = 400L

    private val VIEWGROUP_CANCEL_SAVING_DURATION = 400L

    private val VIEWGROUP_CHOOSE_LANG_DURATION = 400L

    // After this time, saved flashcard will move right and disappear
    private val SAVED_FLASHCARD_APPEAR_INTERVAL = 400L

    lateinit var selectedTextHighlightAnim : Animation

    private val animatorSetSaving = AnimatorSet()
    private val animatorSetCancelSavingAppear = AnimatorSet()

    private val animatorSetCancelSavingDisappear = AnimatorSet()
    private val animatorSetChooseLangAppear = AnimatorSet()

    private val animatorSetChooseLangDisappear = AnimatorSet()

    private var selectedLanguageSessionTextView : TextView? = null

    var rcvChooseLanguageAdapter : ChooseLanguageRCVAdapter = ChooseLanguageRCVAdapter(this)
    var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

    override fun getLayoutId(): Int  = R.layout.activity_add_flashcard

    override fun takeViewModel(): AddFlashCardActivityViewModel {
        return getActivityViewModel()
    }

    override fun initProperties() {
        initSaveAnimation()
        initCancelAnimation()
        initChooseLanguageAnimation()
        initSelectedTextAnimation()
    }

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


    private fun initSaveAnimation () { dataBinding.apply {

       fun getScreenWidth () : Int {
           val displayMetrics = DisplayMetrics()
           windowManager.defaultDisplay.getMetrics(displayMetrics)
           return displayMetrics.widthPixels
       }

        val scaleXSmaller = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f)
        val scaleYSmaller = PropertyValuesHolder.ofFloat(View.SCALE_Y,1f, 0f)
        val rotateRight = PropertyValuesHolder.ofFloat(View.ROTATION,0f, 30f)
        val groupDisappear = ObjectAnimator.ofPropertyValuesHolder(viewgroupAddFlashcard
            , scaleXSmaller, scaleYSmaller, rotateRight).apply {
            duration = VIEWGROUP_ADD_FLASHCARD_SAVING_DURATION
            interpolator = AccelerateInterpolator()
            addListener(onEnd = { viewgroupAddFlashcard.disappear() })
        }

        val scaleXTooBigThenNormal = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1.1f, 1f)
        val scaleYTooBigThenNormal = PropertyValuesHolder.ofFloat(View.SCALE_Y,0f, 1.1f, 1f)
        val disappear = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)
        val moveRight = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, getScreenWidth()/1.2f)
        val savedImageAppear = ObjectAnimator.ofPropertyValuesHolder(imgSavedFlashcard
            , scaleXTooBigThenNormal, scaleYTooBigThenNormal).apply {
            duration = VIEWGROUP_ADD_FLASHCARD_SAVING_DURATION
            interpolator = FastOutSlowInInterpolator()
        }
        val savedImageDisappear = ObjectAnimator.ofPropertyValuesHolder(imgSavedFlashcard, moveRight, disappear).apply {
            duration = VIEWGROUP_ADD_FLASHCARD_SAVING_DURATION + 100
            interpolator = DecelerateInterpolator()
        }

        val appearSet = AnimatorSet().apply { play(groupDisappear).before(savedImageAppear) }
        val disappearSet = AnimatorSet().apply { play(savedImageDisappear).after(SAVED_FLASHCARD_APPEAR_INTERVAL)}
        disappearSet.addListener(onEnd = { finish() })
        animatorSetSaving.play(appearSet).before(disappearSet)
    }}

    private fun initCancelAnimation () { dataBinding.apply {
        val scaleXSmaller = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f)
        val scaleYSmaller = PropertyValuesHolder.ofFloat(View.SCALE_Y,1f, 0f)
        val disappearHaft = PropertyValuesHolder.ofFloat(View.ALPHA, 0.5f, 0f)
        val viewgroupCancelSavingScaleSmaller = ObjectAnimator.ofPropertyValuesHolder(viewgroupCancelSaving
            , scaleXSmaller, scaleYSmaller).apply {
            duration = VIEWGROUP_CANCEL_SAVING_DURATION
            interpolator = FastOutSlowInInterpolator()
        }
        val blackBackgroundDisappear = ObjectAnimator.ofPropertyValuesHolder(imgBlackBackgroundCancelSavingWidgets
            , disappearHaft).apply {
            duration = VIEWGROUP_CANCEL_SAVING_DURATION - 300
            interpolator = LinearInterpolator()
        }
        animatorSetCancelSavingDisappear.play(viewgroupCancelSavingScaleSmaller).before(blackBackgroundDisappear)
        animatorSetCancelSavingDisappear.addListener (onEnd = { groupCancelSavingWidgets.disappear()})

        val scaleXBigger = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f)
        val scaleYBigger = PropertyValuesHolder.ofFloat(View.SCALE_Y,0f, 1f)
        val appearHaft = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 0.5f)
        val viewgroupCancelSavingScaleBigger = ObjectAnimator.ofPropertyValuesHolder(viewgroupCancelSaving
            , scaleXBigger, scaleYBigger).apply {
            duration = VIEWGROUP_CANCEL_SAVING_DURATION
            interpolator = FastOutSlowInInterpolator()
        }
        val blackBackgroundAppear = ObjectAnimator.ofPropertyValuesHolder(imgBlackBackgroundCancelSavingWidgets
            , appearHaft).apply {
                duration = VIEWGROUP_CANCEL_SAVING_DURATION - 300
                interpolator = LinearInterpolator()
        }
        animatorSetCancelSavingAppear.play(blackBackgroundAppear).before(viewgroupCancelSavingScaleBigger)
        animatorSetCancelSavingAppear.addListener(onStart = {groupCancelSavingWidgets.appear()})
    }}

    private fun initChooseLanguageAnimation () { dataBinding.apply {
        val scaleXSmaller = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f)
        val scaleYSmaller = PropertyValuesHolder.ofFloat(View.SCALE_Y,1f, 0f)
        val disappearHaft = PropertyValuesHolder.ofFloat(View.ALPHA, 0.5f, 0f)
        val rcvChooseLanguageScaleSmaller = ObjectAnimator.ofPropertyValuesHolder(rcvChooseSourceLanguage
            , scaleXSmaller, scaleYSmaller).apply {
            duration = VIEWGROUP_CHOOSE_LANG_DURATION
            interpolator = FastOutSlowInInterpolator()
        }
        val blackBackgroundDisappear = ObjectAnimator.ofPropertyValuesHolder(imgBlackBackgroundChooseLanguage
            , disappearHaft).apply {
            duration = VIEWGROUP_CHOOSE_LANG_DURATION - 300
            interpolator = LinearInterpolator()
        }
        animatorSetChooseLangDisappear.play(rcvChooseLanguageScaleSmaller).before(blackBackgroundDisappear)
        animatorSetChooseLangDisappear.addListener (onEnd = {
            groupChooseLanguage.disappear()
            selectedLanguageSessionTextView?.startAnimation(selectedTextHighlightAnim)
        })

        val scaleXBigger = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f)
        val scaleYBigger = PropertyValuesHolder.ofFloat(View.SCALE_Y,0f, 1f)
        val appearHaft = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 0.5f)
        val rcvChooseLanguageScaleBigger = ObjectAnimator.ofPropertyValuesHolder(rcvChooseSourceLanguage
            , scaleXBigger, scaleYBigger).apply {
            duration = VIEWGROUP_CHOOSE_LANG_DURATION
            interpolator = FastOutSlowInInterpolator()
        }
        val blackBackgroundAppear = ObjectAnimator.ofPropertyValuesHolder(imgBlackBackgroundChooseLanguage
            , appearHaft).apply {
            duration = VIEWGROUP_CHOOSE_LANG_DURATION - 300
            interpolator = LinearInterpolator()
        }
        animatorSetChooseLangAppear.play(blackBackgroundAppear).before(rcvChooseLanguageScaleBigger)
        animatorSetChooseLangAppear.addListener(onStart = {groupChooseLanguage.appear()})
    }}

    private fun initSelectedTextAnimation () { dataBinding.apply {
        selectedTextHighlightAnim = AnimationUtils.loadAnimation(this@AddFlashcardActivity , R.anim.scale_in)
        selectedTextHighlightAnim.setAnimationListener (object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                selectedLanguageSessionTextView?.setTextColor(Color.BLACK)
            }

            override fun onAnimationStart(p0: Animation?) {
                val darkOrangeColor = ResourcesCompat.getColor(resources, R.color.dark_orange_text, null)
                selectedLanguageSessionTextView?.setTextColor(darkOrangeColor)
            }
        })
    }}
}


package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivitySeeVocabularyBinding
import com.teamttdvlp.memolang.model.entity.Language.Companion.ENGLISH_VALUE
import com.teamttdvlp.memolang.model.entity.Language.Companion.VIETNAMESE_VALUE
import com.teamttdvlp.memolang.model.entity.vocabulary.TransAndExamp
import com.teamttdvlp.memolang.model.entity.vocabulary.Using
import com.teamttdvlp.memolang.model.entity.vocabulary.Vocabulary
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.database.sql.repository.UserSearchHistoryRepository
import com.teamttdvlp.memolang.model.RecentAddedFlashcardManager
import com.teamttdvlp.memolang.view.activity.iview.SeeVocabularyView
import com.teamttdvlp.memolang.view.adapter.RCVSimpleListAdapter2
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.customview.ExampleVocabularyTextView
import com.teamttdvlp.memolang.view.customview.MeanExampleVocabularyTextView
import com.teamttdvlp.memolang.view.customview.MeanVocabularyTextView
import com.teamttdvlp.memolang.view.customview.TypeVocabularyTextView
import com.teamttdvlp.memolang.view.helper.appear
import com.teamttdvlp.memolang.view.helper.disappear
import com.teamttdvlp.memolang.view.helper.getActivityViewModel
import com.teamttdvlp.memolang.viewmodel.SeeVocabularyActivityViewModel
import javax.inject.Inject
import javax.inject.Named

class SeeVocabularyActivity : BaseActivity<ActivitySeeVocabularyBinding, SeeVocabularyActivityViewModel>(),
    SeeVocabularyView {

    lateinit var rcvChooseTypeAdapter : RCVSimpleListAdapter2
    @Inject set

    lateinit var rcvChooseTransAdapter : RCVSimpleListAdapter2
    @Inject set

    lateinit var rcvChooseExampleAdapter : RCVSimpleListAdapter2
    @Inject set

    lateinit var rcvChooseSetNameAdapter : RCVSimpleListAdapter2
        @Inject set

    lateinit var flashcardRepository: FlashcardRepository
    @Inject set

    lateinit var userSearchHistoryRepository: UserSearchHistoryRepository
    @Inject set

    lateinit var recentAddedFlashcardManager: RecentAddedFlashcardManager
    @Inject set

    private lateinit var usingList : Array<Using>

    private lateinit var transAndExampsList : Array<TransAndExamp>


    override fun getLayoutId(): Int = R.layout.activity_see_vocabulary

    override fun takeViewModel(): SeeVocabularyActivityViewModel = getActivityViewModel {
        SeeVocabularyActivityViewModel(
            this.application,
            recentAddedFlashcardManager,
            userSearchHistoryRepository,
            flashcardRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        viewModel.getVocabularyFromVocaInfo(getData())
        viewModel.getVocabulary().observe(this@SeeVocabularyActivity, Observer { vocabulary ->
            createViewByVocabulary(vocabulary)
            setUpAddFCPanel(vocabulary)
        })
    }

    override fun addViewControls() { dB.apply {
        rcvChooseTranslation.adapter = rcvChooseTransAdapter
        rcvChooseType.adapter = rcvChooseTypeAdapter
        rcvChooseExample.adapter = rcvChooseExampleAdapter
        rcvChooseSetName.adapter = rcvChooseSetNameAdapter
        rcvChooseSetNameAdapter.setData(viewModel.getFlashcardSetNameList())
    }}

    override fun addViewEvents() { dB.apply {

        btnAdd.setOnClickListener {
            addFCPanelAppear.start()
        }

        btnSpeaker.setOnClickListener {
            viewModel.speak(txtText.text.toString())
        }

        btnPanelAdd.setOnClickListener {
            val setName = edtPanelSetName.text.toString()
            val text = edtPanelText.text.toString()
            val translation = edtPanelTranslation.text.toString()
            var example = edtPanelExample.text.toString()
            var meanExample = edtPanelMeanExample.text.toString()
            val type = edtPanelType.text.toString()
            val pronunciation = txtPronunciation.text.toString()
            viewModel.addFlashcard(ENGLISH_VALUE, VIETNAMESE_VALUE, setName, type, text, translation, example, meanExample, pronunciation)
        }

        imgBlackBgAddFlashcardPanel.setOnClickListener {
            addFCPanelDisappear.start()
            rcvChooseType.disappear()
            rcvChooseExample.disappear()
            rcvChooseTranslation.disappear()
        }

        rcvChooseTypeAdapter.setOnItemClickListener { type ->
            val corresUsing : Using
            for (using in usingList) {
                if (using.type == type) {
                    corresUsing = using
                    updateRCVChooseTrans(corresUsing)
                    updateRCVChooseExample(corresUsing.transAndExamps.first())
                    edtPanelType.setText(corresUsing.type)
                    break
                }
            }
            rcvChooseType.disappear()
            rcvChooseExample.disappear()
            rcvChooseTranslation.disappear()
            rcvChooseSetName.disappear()
        }

        rcvChooseTransAdapter.setOnItemClickListener { translation ->
            for (transAndEx in transAndExampsList) {
                if (translation == transAndEx.trans) {
                    edtPanelTranslation.setText(translation)
                    updateRCVChooseExample(transAndEx)
                }
            }
            rcvChooseType.disappear()
            rcvChooseExample.disappear()
            rcvChooseTranslation.disappear()
            rcvChooseSetName.disappear()
        }

        rcvChooseExampleAdapter.setOnItemClickListener { example ->
            dB.edtPanelExample.setText(example)
            rcvChooseType.disappear()
            rcvChooseExample.disappear()
            rcvChooseTranslation.disappear()
            rcvChooseSetName.disappear()
        }

        rcvChooseSetNameAdapter.setOnItemClickListener { setName ->
            dB.edtPanelSetName.setText(setName)
            rcvChooseType.disappear()
            rcvChooseExample.disappear()
            rcvChooseTranslation.disappear()
            rcvChooseSetName.disappear()
        }

        imgChooseTypeSpinner.setOnClickListener {
            if (!rcvChooseType.isVisible) {
                rcvChooseType.appear()
            } else {
                rcvChooseType.disappear()
            }
            rcvChooseExample.disappear()
            rcvChooseTranslation.disappear()
            rcvChooseSetName.disappear()
        }

        imgChooseTranslationSpinner.setOnClickListener {
            if (!rcvChooseTranslation.isVisible) {
                rcvChooseTranslation.appear()
            } else {
                rcvChooseTranslation.disappear()
            }

            rcvChooseExample.disappear()
            rcvChooseType.disappear()
            rcvChooseSetName.disappear()
        }

        imgChooseExampleSpinner.setOnClickListener {
            if (!rcvChooseExample.isVisible) {
                rcvChooseExample.appear()
            } else {
                rcvChooseExample.disappear()
            }

            rcvChooseTranslation.disappear()
            rcvChooseType.disappear()
            rcvChooseSetName.disappear()
        }

        imgChooseSetNameSpinner.setOnClickListener {
            if (!rcvChooseSetName.isVisible) {
                rcvChooseSetName.appear()
            } else {
                rcvChooseSetName.disappear()
            }
            rcvChooseTranslation.disappear()
            rcvChooseType.disappear()
            rcvChooseExample.disappear()
        }
    }}

    override fun onBackPressed() {
        if (dB.panelAddFlashcard.isVisible) {
            addFCPanelDisappear.start()
        } else {
            super.onBackPressed()
        }
    }

    private fun getData () : String {
        return intent.extras!!.getString(VOCABULARY_INFO, "").apply {
            if (this == "") throw java.lang.NullPointerException("Info was null, please check again (SeeVocabularyActivity.kt)")
        }
    }


    fun createViewByVocabulary (vocabulary : Vocabulary) { dB.apply {
        txtPronunciation.setText(vocabulary.pronunciation)
        txtText.setText(vocabulary.text)
        if (vocabulary.usings != null) {
            for (using in vocabulary.usings!!) {
                var isFirstMeanTextView = true
                val newedtPanelType = TypeVocabularyTextView(this@SeeVocabularyActivity)
                    .apply {
                        setText(using.type)
                    }
                contentParent.addView(newedtPanelType)

                for (meanAndEx in using.transAndExamps) {
                    val newTxtMean = MeanVocabularyTextView(this@SeeVocabularyActivity)
                        .apply {
                            setText(meanAndEx.trans)
                        }
                    if (isFirstMeanTextView) {
                        newTxtMean.clearMarginTop()
                        isFirstMeanTextView = false
                    }
                    contentParent.addView(newTxtMean)
                    if (meanAndEx.examples != null) {
                        for (example in meanAndEx.examples!!) {
                            val newTxtExample = ExampleVocabularyTextView(this@SeeVocabularyActivity)
                                .apply {
                                    setText("> " + example.text)
                                }
                            contentParent.addView(newTxtExample)

                            val exHasMean = (example.mean != "")
                            if (exHasMean) {
                                val newTxtMeanEx = MeanExampleVocabularyTextView(this@SeeVocabularyActivity)
                                    .apply {
                                        setText(example.mean)
                                    }
                                contentParent.addView(newTxtMeanEx)
                            }
                        }
                    }
                }
            }
        }
    }}

    fun setUpAddFCPanel(vocabulary : Vocabulary) { dB.apply {
        usingList = vocabulary.usings!!
        edtPanelText.setText(vocabulary.text)
        edtPanelType.setText(usingList.first().type)

        if (usingList != null) {
            val typeList = ArrayList<String>()
            usingList.forEach { using ->
                typeList.add(using.type)
            }

            rcvChooseTypeAdapter.setData(typeList)
            val firstUsing = usingList.first()
            updateRCVChooseTrans(firstUsing)
            val firstTransAndExamp = firstUsing.transAndExamps.first()
            updateRCVChooseExample(firstTransAndExamp)
        }
    }}

    fun updateRCVChooseTrans (using : Using) {
        this.transAndExampsList = using.transAndExamps
        val transList = ArrayList<String>()
        transAndExampsList.forEach { transAndEx ->
            transList.add(transAndEx.trans)
        }
        updateTxtTrans(transList.first())
        rcvChooseTransAdapter.setData(transList)
    }

    fun updateRCVChooseExample (transAndEx : TransAndExamp) {
        if (transAndEx.examples != null) {
            val exampleList = ArrayList<String>()
            transAndEx.examples!!.forEach { ex ->
                exampleList.add(ex.text)
            }
            rcvChooseExampleAdapter.setData(exampleList)
            updateTxtExample(exampleList.first())
        } else {
            updateTxtExample("")
        }
    }

    fun updateTxtExample (example : String) {
        dB.edtPanelExample.setText(example)
    }

    fun updateTxtTrans (trans : String) {
        dB.edtPanelTranslation.setText(trans)
    }

    // VIEW IMPLEMENTED METHODS

    override fun onAddFlashcardSuccess() {
        addFCSuccessAnimator.start()
    }

    // INJECTED METHODS

    private val addFCPanelAppear : AnimatorSet = AnimatorSet()
    private val addFCPanelDisappear : AnimatorSet = AnimatorSet()
    private val addFCSuccessAnimator : AnimatorSet = AnimatorSet()

    @Inject
    fun initAddFlashcardPanelAnimations (
        @Named("Appear50Percents") blackBackgroundAppear : Animator,
        @Named("Disappear50Percents") blackBackgroundDisappear : Animator,
        @Named("FromNothingToNormalSize") panelAppear : Animator,
        @Named("FromNormalSizeToNothing") panelDisappear: Animator
    ) { dB.apply {

        blackBackgroundAppear.setTarget(imgBlackBgAddFlashcardPanel)
        panelAppear.setTarget(panelAddFlashcard)
        addFCPanelAppear.play(blackBackgroundAppear).before(panelAppear)
        addFCPanelAppear.addListener (onStart = {
            groupAddFlashcard.appear()
            btnAdd.disappear()
        })

        blackBackgroundDisappear.setTarget(imgBlackBgAddFlashcardPanel)
        panelDisappear.setTarget(panelAddFlashcard)
        addFCPanelDisappear.play(panelDisappear).before(blackBackgroundDisappear)
        addFCPanelDisappear.addListener (onEnd = {
            btnAdd.appear()
            groupAddFlashcard.disappear()
        })

    }}


    @Inject
    fun initAddFlashcardSuccessAnimation (
        @Named("Appear100Percents") addSuccessPanelAppear : Animator,
        @Named("Disappear100Percents") addSuccessPanelDisappear : Animator)
    { dB.apply {

        addSuccessPanelAppear.apply {
            setTarget(imgAddFcSuccess)
            duration = 250
            interpolator = LinearInterpolator()
        }

        addSuccessPanelDisappear.apply {
            setTarget(imgAddFcSuccess)
            startDelay = 1500
            duration = 250
            interpolator = LinearInterpolator()
        }

        val successPanelAppearForAWhileAS = AnimatorSet()
        successPanelAppearForAWhileAS.play(addSuccessPanelAppear).before(addSuccessPanelDisappear)

        addFCSuccessAnimator.play(addFCPanelDisappear).before(successPanelAppearForAWhileAS)
    }}
}
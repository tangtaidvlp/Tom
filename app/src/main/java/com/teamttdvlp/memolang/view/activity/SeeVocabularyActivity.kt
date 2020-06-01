package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.lifecycle.Observer
import com.example.dictionary.model.TransAndExamp
import com.example.dictionary.model.Vocabulary
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.language.Language.Companion.ENGLISH_VALUE
import com.teamttdvlp.memolang.data.model.entity.language.Language.Companion.VIETNAMESE_VALUE
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.Example
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.SingleMeanExample
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.Using
import com.teamttdvlp.memolang.data.model.other.vocabulary.MultiMeanExample
import com.teamttdvlp.memolang.databinding.ActivitySeeVocabularyBinding
import com.teamttdvlp.memolang.view.activity.iview.SeeVocabularyView
import com.teamttdvlp.memolang.view.adapter.*
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.customview.NormalOutExtraSlowIn
import com.teamttdvlp.memolang.view.customview.see_vocabulary.*
import com.teamttdvlp.memolang.view.customview.see_vocabulary.sub_example.SubExampleTranslationView
import com.teamttdvlp.memolang.view.customview.see_vocabulary.sub_example.SubExampleView
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.SeeVocabularyActivityViewModel
import javax.inject.Inject
import javax.inject.Named


class SeeVocabularyActivity : BaseActivity<ActivitySeeVocabularyBinding, SeeVocabularyActivityViewModel>(),
    SeeVocabularyView {

     lateinit var rcvSearchDictionaryAdapter : RCVSearchDictionaryAdapter
    @Inject set

    lateinit var rcvRecentSearchDicAdapter : RCVRecent_SearchDictionary_Adapter
    @Inject set

    lateinit var rcvChooseTypeAdapter : RCVSimpleListAdapter2
    @Inject set

    lateinit var rcvChooseTransAdapter : RCVSimpleListAdapter2
    @Inject set

    lateinit var rcvChooseExampleAdapter : RCVGenericSimpleListAdapter2<SingleMeanExample>
    @Inject set

    lateinit var rcvChooseSetNameAdapter : RCVSimpleListChooseSetNameAdapter
     @Inject set

    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    @Inject set

    private lateinit var usingList : ArrayList<Using>

    private lateinit var transAndExampsList : ArrayList<TransAndExamp>



    override fun getLayoutId(): Int = R.layout.activity_see_vocabulary

    override fun takeViewModel(): SeeVocabularyActivityViewModel = getActivityViewModel(viewModelProviderFactory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        viewModel.getVocabulary().observe(this@SeeVocabularyActivity, Observer { vocabulary ->
            dB.contentParent.removeAllViews()
            createViewByVocabulary(vocabulary)
            setUpAddFCPanel(vocabulary)
        })
        setStatusBarColor(resources.getColor(R.color.app_blue))
    }

    override fun onStart() {
        super.onStart()
        dB.btnNavigateToSearchBar.performClick()
    }

    private fun setUpShowedRecyclerView() {
        val thereIsNoVocaOnScreen = rcvRecentSearchDicAdapter.itemCount == 0
        if (thereIsNoVocaOnScreen) {
            rcvSearchDictionaryAdapter.search("a")
            dB.rcvDictionary.goVISIBLE()
            dB.rcvDictionary.alpha = 0f
            dB.rcvDictionary.animate().alpha(1f)
                .setDuration(100).setInterpolator(NormalOutExtraSlowIn())
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopAllTextSpeaker()
        viewModel.saveSearchingHistory()
    }

    override fun addViewControls() { dB.apply {
        // Choose TRANSLATION
        rcvChooseTranslation.adapter = rcvChooseTransAdapter

        // Choose TYPE
        rcvChooseCardType.adapter = rcvChooseTypeAdapter

        // Choose EXAMPLE
        rcvChooseExample.adapter = rcvChooseExampleAdapter

        // Choose SET NAME
        rcvChooseSetName.adapter = rcvChooseSetNameAdapter
        viewModel.getAllEnglishVNFlashcardSets { engVNFlashcardSetList ->
            rcvChooseSetNameAdapter.setData(engVNFlashcardSetList)
        }

        // SEARCH dictionary
        dB.rcvDictionary.adapter = rcvSearchDictionaryAdapter
        dB.rcvRecentSearchDic.adapter = rcvRecentSearchDicAdapter
        rcvSearchDictionaryAdapter.textColor = Color.BLACK
        rcvRecentSearchDicAdapter.textColor = Color.BLACK

        viewModel.getAll_RecentSearchedVocaList {
            rcvRecentSearchDicAdapter.setData(it)
            setUpShowedRecyclerView()
        }

        edtPanelSetName.setText(viewModel.getLastedUseFlashcardSetName())

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
            val example = edtPanelExample.text.toString()
            val meanExample = edtPanelMeanExample.text.toString()
            val type = edtPanelType.text.toString()
            val pronunciation = txtPronunciation.text.toString()

            viewModel.addFlashcard(frontLanguage = ENGLISH_VALUE, backLanguage = VIETNAMESE_VALUE,
                setName = setName, type = type, text = text, translation = translation, example =  example,
                meanExample = meanExample, pronunciation =  pronunciation)
        }

        imgBlackBgAddFlashcardPanel.setOnClickListener {
            addFCPanelDisappear.start()
            rcvChooseCardType.goGONE()
            rcvChooseExample.goGONE()
            rcvChooseTranslation.goGONE()
        }

        rcvChooseTypeAdapter.setOnItemClickListener { type ->
            val corresUsing : Using
            for (using in usingList) {
                if (using.type == type) {
                    corresUsing = using
                    updateRCVChooseTrans(corresUsing)
                    updateRCVChooseExample(corresUsing.transAndExamsList.first())
                    edtPanelType.setText(corresUsing.type)
                    break
                }
            }
            rcvChooseCardType.goGONE()
            rcvChooseExample.goGONE()
            rcvChooseTranslation.goGONE()
            rcvChooseSetName.goGONE()
        }

        rcvChooseSetNameAdapter.setOnItemClickListener { flashcardSet ->
            dB.edtPanelSetName.setText(flashcardSet.name)
            rcvChooseCardType.goGONE()
            rcvChooseExample.goGONE()
            rcvChooseTranslation.goGONE()
            rcvChooseSetName.goGONE()
        }

        rcvChooseTransAdapter.setOnItemClickListener { translation ->
            for (transAndEx in transAndExampsList) {
                if (translation == transAndEx.translation) {
                    edtPanelTranslation.setText(translation)
                    updateRCVChooseExample(transAndEx)
                }
            }
            rcvChooseCardType.goGONE()
            rcvChooseExample.goGONE()
            rcvChooseTranslation.goGONE()
            rcvChooseSetName.goGONE()
        }

        rcvChooseExampleAdapter.setOnItemClickListener { example ->
            dB.edtPanelExample.setText(example.text)
            dB.edtPanelMeanExample.setText(example.mean)

            rcvChooseCardType.goGONE()
            rcvChooseExample.goGONE()
            rcvChooseTranslation.goGONE()
            rcvChooseSetName.goGONE()
        }

        imgChooseTypeSpinner.setOnClickListener {
            if (!rcvChooseCardType.isVisible) {
                rcvChooseCardType.goVISIBLE()
            } else {
                rcvChooseCardType.goGONE()
            }
            rcvChooseExample.goGONE()
            rcvChooseTranslation.goGONE()
            rcvChooseSetName.goGONE()
        }

        imgChooseTranslationSpinner.setOnClickListener {
            if (!rcvChooseTranslation.isVisible) {
                rcvChooseTranslation.goVISIBLE()
            } else {
                rcvChooseTranslation.goGONE()
            }

            rcvChooseExample.goGONE()
            rcvChooseCardType.goGONE()
            rcvChooseSetName.goGONE()
        }

        imgChooseExampleSpinner.setOnClickListener {
            if (!rcvChooseExample.isVisible) {
                rcvChooseExample.goVISIBLE()
            } else {
                rcvChooseExample.goGONE()
            }

            rcvChooseTranslation.goGONE()
            rcvChooseCardType.goGONE()
            rcvChooseSetName.goGONE()
        }

        imgChooseSetNameSpinner.setOnClickListener {
            if (!rcvChooseSetName.isVisible) {
                rcvChooseSetName.goVISIBLE()
            } else {
                rcvChooseSetName.goGONE()
            }
            rcvChooseTranslation.goGONE()
            rcvChooseCardType.goGONE()
            rcvChooseExample.goGONE()
        }

        rcvSearchDictionaryAdapter.setOnItemClickListener { rawVocabulary ->
            onChooseVocabulary(rawVocabulary as TypicalRawVocabulary)
        }

        rcvRecentSearchDicAdapter.setOnItemClickListener { rawVocabulary ->
            onChooseVocabulary(rawVocabulary)
        }

        edtEngViDictionary.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val key = edtEngViDictionary.text.toString()
                val fullContent = rcvSearchDictionaryAdapter.getVocabularyByKey(key)
                quickLog("FULL: $fullContent")
                if (fullContent != null) {
                    onChooseVocabulary(fullContent)
                } else {
                    quickToast("Navigate to online search")
                }
                true
            }
            false
        }

        edtEngViDictionary.addTextChangeListener (onTextChanged = { text, _, _, _ ->
            if (text == "") {
                rcvDictionary.goGONE()
            } else {
                rcvDictionary.goVISIBLE()
                if (rcvDictionary.alpha == 0f) {
                    rcvDictionary.alpha = 1f
                }
                rcvSearchDictionaryAdapter.search(text)
            }
        })

        edtEngViDictionary.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                onNavigateToSearchBar()
            }
        }

        btnNavigateToSearchBar.setOnClickListener {
            onNavigateToSearchBar()
            showVirtualKeyboard()
            edtEngViDictionary.setText("")
            edtEngViDictionary.requestFocus()
        }

    }}

    private fun onNavigateToSearchBar() {
        val thereIsNoRecentCardOnScreen = rcvRecentSearchDicAdapter.itemCount == 0
        if (thereIsNoRecentCardOnScreen) {
            showDictionaryRecyclerView()
        }
        hideVocabularyInfo()
    }

    private fun onChooseVocabulary(item: TypicalRawVocabulary) {
        hideVirtualKeyboard()
        hideDictionaryRecyclerViews()
        showVocabularyInfo()
        rcvRecentSearchDicAdapter.addData(item)
        dB.edtEngViDictionary.clearFocus()

        viewModel.addVoca_ToRecentSearchedList(item)
        viewModel.convertToVocabularyAndSendToObserver(item)
    }

    private val COMMON_FADE_DURATION = 100L

    private fun showDictionaryRecyclerView () { dB.apply {
        quickLog("????")
        rcvDictionary.animate().alpha(1f).setDuration(COMMON_FADE_DURATION).setLiteListener (onStart = {
            rcvDictionary.goVISIBLE()
        })
    }}

    private fun hideDictionaryRecyclerViews () { dB.apply {
        rcvDictionary.animate().alpha(0f).setDuration(COMMON_FADE_DURATION).setLiteListener (onEnd = {
            rcvDictionary.goGONE()
        })
    }}

    private fun showVocabularyInfo () { dB.apply {
        vwgrpVocaInfo.animate().alpha(1f).setDuration(COMMON_FADE_DURATION).setLiteListener (onStart = {
            vwgrpVocaInfo.goVISIBLE()
        })
    }}

    private fun hideVocabularyInfo () { dB.apply {
        vwgrpVocaInfo.animate().alpha(0f).setDuration(COMMON_FADE_DURATION).setLiteListener (onEnd = {
            vwgrpVocaInfo.goGONE()
        })
    }}


    private fun hideAddFlashcardPanel() {
        addFCPanelDisappear.start()
    }

    private fun hideDictionaryAndShowVocaInfo () {
        hideDictionaryRecyclerViews()
        showVocabularyInfo()
        clearSearchBarFocus()
    }

    override fun onBackPressed() {
        val isAddingFlashcard = dB.panelAddFlashcard.isVisible
        val vocaInfoPanel_IsShowing_AVocaInfo = dB.txtText.text.isNotEmpty()
        val vocaPanel_IsCoveredByDictionary = dB.vwgrpVocaInfo.isVisible().not()
        if (isAddingFlashcard) {
            hideAddFlashcardPanel()
        } else if (vocaPanel_IsCoveredByDictionary and vocaInfoPanel_IsShowing_AVocaInfo) {
            hideDictionaryAndShowVocaInfo()
        } else {
            super.onBackPressed()
        }
    }

    private fun clearSearchBarFocus () {
        dB.edtEngViDictionary.clearFocus()
        dB.contentParent.requestFocus()
    }

    private fun createViewByVocabulary (vocabulary : Vocabulary) { dB.apply {
        txtPronunciation.text = vocabulary.pronunciation
        txtText.text = vocabulary.text
        for (using in vocabulary.usings) {
            var isFirstMeanTextView = true
            val newTxtPanelType = TypeVocabularyTextView(this@SeeVocabularyActivity)
            newTxtPanelType.text = using.type
            contentParent.addView(newTxtPanelType)

            for (usingTranslationAndExample in using.transAndExamsList) {
                val newVocaMean = VocabularyMeanView(
                        this@SeeVocabularyActivity, usingTranslationAndExample.translation)

                newVocaMean.button.setOnClickListener {
                    edtPanelType.setText(using.type)
                    updateTxtTrans(usingTranslationAndExample.translation)
                    updateRCVChooseExample(usingTranslationAndExample)
                    addFCPanelAppear.start()
                }

                if (isFirstMeanTextView) {
//                    newVocaMean.clearMarginTop()
                    isFirstMeanTextView = false
                }
                contentParent.addView(newVocaMean)

                if (usingTranslationAndExample.subExampleList.size != 0) {
                    for (example in usingTranslationAndExample.subExampleList) {
                        if (example is SingleMeanExample) {
                            val newTxtExample = SingleMeanTranslationView(
                                    this@SeeVocabularyActivity, example.text)
                            contentParent.addView(newTxtExample)
                            val exHasMean = (example.mean != "")
                            if (exHasMean) {
                                val newTxtMeanEx = ExampleTranslationTextView(this@SeeVocabularyActivity)
                                newTxtMeanEx.text = example.mean
                                contentParent.addView(newTxtMeanEx)
                            }

                        } else if (example is MultiMeanExample) {
                            val newTxtMultiMeanExample = MultiMeanExampleView(this@SeeVocabularyActivity, example.text)
                            contentParent.addView(newTxtMultiMeanExample)
                            example.transAndSubExamp_List.forEach { transAndSubExamp ->
                                val newSubExample = ExampleTranslationTextView(this@SeeVocabularyActivity)
                                newSubExample.text = transAndSubExamp.translation
                                contentParent.addView(newSubExample)

                                transAndSubExamp.subExampleList.forEach { example ->
                                    example as SingleMeanExample

                                    val newTxtExample = SubExampleView(this@SeeVocabularyActivity, example.text)
                                    contentParent.addView(newTxtExample)

                                    val exHasMean = (example.mean != "")
                                    if (exHasMean) {
                                        val newTxtMeanEx = SubExampleTranslationView(this@SeeVocabularyActivity)
                                        newTxtMeanEx.text = example.mean
                                        contentParent.addView(newTxtMeanEx)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }}

    private fun setUpAddFCPanel(vocabulary : Vocabulary) { dB.apply {
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
            val firstTransAndExamp = firstUsing.transAndExamsList.first()
            updateRCVChooseExample(firstTransAndExamp)
        }
    }}

    private fun updateRCVChooseTrans (using : Using) {
        transAndExampsList = using.transAndExamsList
        val transList = ArrayList<String>()

        transAndExampsList.forEach { transAndEx ->
            transList.add(transAndEx.translation)
        }

        if (transAndExampsList.size != 0) {
            updateTxtTrans(transList.first())
        }

        rcvChooseTransAdapter.setData(transList)
    }

    private fun updateRCVChooseExample (transAndEx : TransAndExamp) {
        if (transAndEx.subExampleList.size != 0) {
            val fullSingleExampleList = convertExampleListTo_FullSingleExampleList(transAndEx.subExampleList!!)
            rcvChooseExampleAdapter.setData(fullSingleExampleList)
            updateTxtExampleAndExampleMean(fullSingleExampleList.first() as SingleMeanExample)
        } else {
            updateTxtExampleAndExampleMean(null)
        }
    }

    private fun convertExampleListTo_FullSingleExampleList (exampleList : ArrayList<Example>) : ArrayList<SingleMeanExample> {
        val result = ArrayList<SingleMeanExample>()
        exampleList.forEach { example ->
            if (example is SingleMeanExample) {
                result.add(example)
            } else if (example is MultiMeanExample) {
                example.transAndSubExamp_List.forEach { transAndExam ->
                    result.add(SingleMeanExample(example.text, transAndExam.translation))

                        transAndExam.subExampleList.forEach { example ->
                            if (example is SingleMeanExample) {
                                result.add(example)
                            } else Log.e("Error", "It can not contain multi-mean")
                        }

                }


            }
        }
        return result
    }

    private fun updateTxtExampleAndExampleMean (example : SingleMeanExample?) {
        if (example != null) {
            dB.edtPanelExample.setText(example.text)
            dB.edtPanelMeanExample.setText(example.mean)
        } else {
            dB.edtPanelExample.setText("")
            dB.edtPanelMeanExample.setText("")
        }
    }

    fun updateTxtTrans (trans : String) {
        dB.edtPanelTranslation.setText(trans)
    }

    // VIEW IMPLEMENTED METHODS

    override fun onAddFlashcardSuccess() {
        addFCPanelDisappear.start()
        val AddFCPanelDisappear_RelativeDuration = 100L
        playImageAddSuccessAnim(AddFCPanelDisappear_RelativeDuration)
        hideVirtualKeyboard()
    }

    private fun playImageAddSuccessAnim(startDelay : Long = 0) { dB.apply {
        imgAddFcSuccess.animate().scaleX(2f).scaleY(2f).alpha(1f).
                setDuration(250).setInterpolator(FastOutLinearInInterpolator()).setStartDelay(startDelay)
            .setLiteListener (
                onEnd = {
                    imgAddFcSuccess.animate().alpha(0f)
                        .setStartDelay(0) // Reset startDelay
                        .setLiteListener (onEnd = {
                        // Do nothing, Reset the above setLiteListener()
                    })
                })
    }}

    // INJECTED METHODS

    private val addFCPanelAppear : AnimatorSet = AnimatorSet()
    private val addFCPanelDisappear : AnimatorSet = AnimatorSet()

    @Inject
    fun initAddFlashcardPanelAnimations (
        @Named("Appear50Percents") blackBackgroundAppear : Animator,
        @Named("FromNothingToNormalSize") panelAppear : Animator,
        @Named("Disappear50Percents") blackBackgroundDisappear : Animator,
        @Named("FromNormalSizeToNothing") panelDisappear: Animator
    ) { dB.apply {

        blackBackgroundAppear.setTarget(imgBlackBgAddFlashcardPanel)
        panelAppear.setTarget(panelAddFlashcard)
        addFCPanelAppear.play(blackBackgroundAppear).before(panelAppear)
        addFCPanelAppear.addListener (onStart = {
            groupAddFlashcard.goVISIBLE()
        })

        blackBackgroundDisappear.setTarget(imgBlackBgAddFlashcardPanel)
        panelDisappear.setTarget(panelAddFlashcard)
        addFCPanelDisappear.play(panelDisappear).before(blackBackgroundDisappear)
        addFCPanelDisappear.addListener (onEnd = {
            groupAddFlashcard.goGONE()
        })

    }}


    override fun overrideEnterAnim() {
        overridePendingTransition(R.anim.appear, R.anim.nothing)
    }

    override fun overrideExitAnim() {
        overridePendingTransition(R.anim.nothing, R.anim.disappear)
    }

}
package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.core.animation.addListener
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.lifecycle.Observer
import com.example.dictionary.model.TranslationAndExample
import com.example.dictionary.model.Vocabulary
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.CardProperty
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.language.Language.Companion.ENGLISH_VALUE
import com.teamttdvlp.memolang.data.model.entity.language.Language.Companion.VIETNAMESE_VALUE
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.*
import com.teamttdvlp.memolang.data.model.other.vocabulary.VocabularyOtherStructure
import com.teamttdvlp.memolang.databinding.ActivityEngVietDictionaryBinding
import com.teamttdvlp.memolang.view.activity.iview.SeeVocabularyView
import com.teamttdvlp.memolang.view.adapter.*
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.customview.NormalOutExtraSlowIn
import com.teamttdvlp.memolang.view.customview.see_vocabulary.Vocabulary_ExampleTranslation_View
import com.teamttdvlp.memolang.view.customview.vocabulary_info.Vocabulary_Example_View
import com.teamttdvlp.memolang.view.customview.vocabulary_info.Vocabulary_Mean_TextView
import com.teamttdvlp.memolang.view.customview.vocabulary_info.Vocabulary_Type_TextView
import com.teamttdvlp.memolang.view.customview.vocabulary_info.other_structure.OtherStructure_ExampleTranslation_View
import com.teamttdvlp.memolang.view.customview.vocabulary_info.other_structure.OtherStructure_Example_View
import com.teamttdvlp.memolang.view.customview.vocabulary_info.other_structure.OtherStructure_Mean_View
import com.teamttdvlp.memolang.view.customview.vocabulary_info.other_structure.OtherStructure_Text_View
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.EngVietDictionaryActivityViewModel
import javax.inject.Inject
import javax.inject.Named


class EngVietDictionaryActivity :
    BaseActivity<ActivityEngVietDictionaryBinding, EngVietDictionaryActivityViewModel>(),
    SeeVocabularyView {

    lateinit var rcvSearchDictionaryAdapter: RCVSearchDictionaryAdapter
        @Inject set

    lateinit var rcvRecentSearchDicAdapter: RCVRecent_SearchDictionary_Adapter
        @Inject set

    lateinit var rcvChooseTypeAdapter: RCVSimpleListAdapter2
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

    private lateinit var translationAndExampsList: ArrayList<TranslationAndExample>


    override fun getLayoutId(): Int = R.layout.activity_eng_viet_dictionary

    override fun takeViewModel(): EngVietDictionaryActivityViewModel =
        getActivityViewModel(viewModelProviderFactory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        viewModel.getVocabulary().observe(this@EngVietDictionaryActivity, Observer { vocabulary ->
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
                .setDuration(100).interpolator = NormalOutExtraSlowIn()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopAllTextSpeaker()
        viewModel.saveSearchingHistory()
    }

    override fun addViewSettings() {
        dB.apply {
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
            if (engVNFlashcardSetList.size <= 0) {
                imgChooseSetNameSpinner.goGONE()
            } else {
                imgChooseSetNameSpinner.goVISIBLE()
            }
        }

        // SEARCH dictionary
        dB.rcvDictionary.adapter = rcvSearchDictionaryAdapter
        dB.rcvRecentSearchDic.adapter = rcvRecentSearchDicAdapter
        rcvSearchDictionaryAdapter.textColor = resources.getColor(R.color.app_blue)
        rcvRecentSearchDicAdapter.textColor = resources.getColor(R.color.app_blue)

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

            val newCard = Flashcard(
                id = 0, text = text, translation = translation,
                frontLanguage = ENGLISH_VALUE, backLanguage = VIETNAMESE_VALUE,
                setOwner = setName, type = type, example = example, meanOfExample = meanExample,
                pronunciation = pronunciation, cardProperty = CardProperty()
            )
            // TODO ( Check card property )

            viewModel.proceedAddFlashcard(newCard)
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
                    updateRCVChooseExample(corresUsing.translationAndExamsList.first())
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
            for (transAndEx in translationAndExampsList) {
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
            if (!rcvChooseCardType.isVisible()) {
                rcvChooseCardType.goVISIBLE()
            } else {
                rcvChooseCardType.goGONE()
            }
            rcvChooseExample.goGONE()
            rcvChooseTranslation.goGONE()
            rcvChooseSetName.goGONE()
        }

        imgChooseTranslationSpinner.setOnClickListener {
            if (!rcvChooseTranslation.isVisible()) {
                rcvChooseTranslation.goVISIBLE()
            } else {
                rcvChooseTranslation.goGONE()
            }

            rcvChooseExample.goGONE()
            rcvChooseCardType.goGONE()
            rcvChooseSetName.goGONE()
        }

        imgChooseExampleSpinner.setOnClickListener {
            if (!rcvChooseExample.isVisible()) {
                rcvChooseExample.goVISIBLE()
            } else {
                rcvChooseExample.goGONE()
            }

            rcvChooseTranslation.goGONE()
            rcvChooseCardType.goGONE()
            rcvChooseSetName.goGONE()
        }

        imgChooseSetNameSpinner.setOnClickListener {
            if (!rcvChooseSetName.isVisible()) {
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

        rcvSearchDictionaryAdapter.setOnBtnBringTextUpClickListener { rawVocabulary ->
            onBringTextUp(rawVocabulary)
        }

        // Recent search
        rcvRecentSearchDicAdapter.setOnItemClickListener { rawVocabulary ->
            onChooseVocabulary(rawVocabulary)
        }

        rcvRecentSearchDicAdapter.setOnBtnBringTextUpClickListener { rawVocabulary ->
            onBringTextUp(rawVocabulary)
        }

        edtEngViDictionary.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val key = edtEngViDictionary.text.toString()
                val fullContent = rcvSearchDictionaryAdapter.getVocabularyByKey(key)
                if (fullContent != null) {
                    onChooseVocabulary(fullContent)
                } else {
                    SearchOnlineActivity.requestSearchOnline(this@EngVietDictionaryActivity, key)
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
            }
            rcvSearchDictionaryAdapter.search(text)
        })

        rcvSearchDictionaryAdapter.setOnGetTwoFirstVocabulary { twoFirstVoca ->

            if (twoFirstVoca.size == 2) {
                txtFirstVoca.text = twoFirstVoca.get(0).key
                txtSecondVoca.text = twoFirstVoca.get(1).key
                vwgrpSecondVoca.goVISIBLE()
                vwgrpFirstVoca.goVISIBLE()
                vwgrpBottomOptions.goVISIBLE()
                vwgrpClearAllSearchText.goGONE()

            } else if (twoFirstVoca.size == 1) {
                txtFirstVoca.text = twoFirstVoca.get(0).key
                vwgrpFirstVoca.goVISIBLE()
                vwgrpSecondVoca.goGONE()
                vwgrpBottomOptions.goVISIBLE()
                vwgrpClearAllSearchText.goGONE()

            } else {
                vwgrpSecondVoca.goGONE()
                vwgrpFirstVoca.goGONE()
                if (edtEngViDictionary.text.isNotEmpty())
                    vwgrpClearAllSearchText.goVISIBLE()
                else
                    vwgrpBottomOptions.goGONE()
            }
        }

        setOnBottomButtonsClick()

        edtEngViDictionary.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                onNavigateToSearchBar()
            }
        }

        btnNavigateToSearchBar.setOnClickListener {
            onNavigateToSearchBar()
            showVirtualKeyboard()
            edtEngViDictionary.requestFocus()
        }

    }
    }

    private fun setOnBottomButtonsClick() {
        dB.apply {
            vwgrpFirstVoca.setOnClickListener {
                rcvDictionary.getChildAt(0).performClick()
            }

            vwgrpSecondVoca.setOnClickListener {
                rcvDictionary.getChildAt(1).performClick()
            }

            vwgrpClearAllSearchText.setOnClickListener {
                edtEngViDictionary.setText("")
            }
        }
    }

    private fun onBringTextUp(rawVocabulary: RawVocabulary) {
        dB.edtEngViDictionary.setText(rawVocabulary.key)
        dB.edtEngViDictionary.setSelection(rawVocabulary.key.length)
    }

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
        vwgrpVocaInfo.animate().alpha(0f).setDuration(COMMON_FADE_DURATION)
            .setLiteListener(onEnd = {
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
        val isAddingFlashcard = dB.panelAddFlashcard.isVisible()
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
        var isFirstVocabularyTypeView = true
        for (using in vocabulary.usings) {
            var isFirstMeanTextView = true
            val newTxtPanelType = Vocabulary_Type_TextView(this@EngVietDictionaryActivity)
            newTxtPanelType.text = using.type
            if (isFirstVocabularyTypeView) {
                newTxtPanelType.clearMarginTop()
                isFirstVocabularyTypeView = false
            }
            contentParent.addView(newTxtPanelType)

            for (usingTranslationAndExample in using.translationAndExamsList) {
                val newVocaMean = Vocabulary_Mean_TextView(
                    this@EngVietDictionaryActivity, usingTranslationAndExample.translation
                )
                systemOutLogging("Vocabulary_Mean_TextView: ${usingTranslationAndExample.translation}")
                newVocaMean.addButton.setOnClickListener {
                    edtPanelType.setText(using.type)
                    edtPanelText.setText(vocabulary.text)
                    updateTxtTrans(usingTranslationAndExample.translation)
                    updateRCVChooseExample(usingTranslationAndExample)
                    addFCPanelAppear.start()
                }

                if (isFirstMeanTextView) {
                    newVocaMean.clearMarginTop()
                    isFirstMeanTextView = false
                }
                contentParent.addView(newVocaMean)

                if (usingTranslationAndExample.subExampleList.size != 0) {
                    for (example in usingTranslationAndExample.subExampleList) {
                        if (example is SingleMeanExample) {
                            val newTxtExample = Vocabulary_Example_View(
                                this@EngVietDictionaryActivity,
                                example.text
                            )
                            systemOutLogging("Vocabulary_Example_View: ${example.text}")
                            contentParent.addView(newTxtExample)
                            val exHasMean = (example.mean != "")
                            if (exHasMean) {
                                val newTxtMeanEx =
                                    Vocabulary_ExampleTranslation_View(this@EngVietDictionaryActivity)
                                newTxtMeanEx.text = example.mean
                                systemOutLogging("Vocabulary_ExampleTranslation_View: ${example.mean}")
                                contentParent.addView(newTxtMeanEx)
                            }

                        } else if (example is VocabularyOtherStructure) {
                            val newOtherStructureTextView = OtherStructure_Text_View(
                                this@EngVietDictionaryActivity,
                                example.text
                            )
                            systemOutLogging("OtherStructure_Text_View: ${example.text}")
                            contentParent.addView(newOtherStructureTextView)
                            example.translationAndExample_List.forEach { transAndSubExamp ->
                                val newVocabularyExamTransView =
                                    OtherStructure_Mean_View(
                                        this@EngVietDictionaryActivity,
                                        text = transAndSubExamp.translation
                                    )
                                systemOutLogging("OtherStructure_Mean_View: ${transAndSubExamp.translation}")
                                newVocabularyExamTransView.addButton.setOnClickListener {
                                    edtPanelType.setText("")
                                    edtPanelText.setText(example.text)
                                    updateTxtTrans(transAndSubExamp.translation)
                                    updateRCVChooseExample(transAndSubExamp)
                                    addFCPanelAppear.start()
                                }

                                contentParent.addView(newVocabularyExamTransView)

                                transAndSubExamp.subExampleList.forEach { example ->
                                    example as SingleMeanExample

                                    val newTxtExample =
                                        OtherStructure_Example_View(
                                            this@EngVietDictionaryActivity,
                                            example.text
                                        )
                                    systemOutLogging("OtherStructure_Example_View: ${example.text}")
                                    contentParent.addView(newTxtExample)

                                    val exHasMean = (example.mean != "")
                                    if (exHasMean) {
                                        val newTxtMeanEx =
                                            OtherStructure_ExampleTranslation_View(this@EngVietDictionaryActivity)
                                        newTxtMeanEx.text = example.mean
                                        systemOutLogging("SubExampleTranslationView: ${example.mean}")
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
        usingList = vocabulary.usings
        edtPanelText.setText(vocabulary.text)
        edtPanelType.setText(usingList.first().type)

        if (usingList != null) {
            val typeList = ArrayList<String>()
            usingList.forEach { using ->
                typeList.add(using.type)
            }

            if (typeList.size <= 1) {
                imgChooseTypeSpinner.goGONE()
            } else {
                imgChooseTypeSpinner.goVISIBLE()
            }
            rcvChooseTypeAdapter.setData(typeList)
            val firstUsing = usingList.first()
            updateRCVChooseTrans(firstUsing)
            val firstTransAndExamp = firstUsing.translationAndExamsList.first()
            updateRCVChooseExample(firstTransAndExamp)
        }
    }}

    private fun updateRCVChooseTrans (using : Using) {
        translationAndExampsList = using.translationAndExamsList
        val transList = ArrayList<String>()

        translationAndExampsList.forEach { transAndEx ->
            transList.add(transAndEx.translation)
        }

        if (translationAndExampsList.size <= 1) {
            dB.imgChooseTranslationSpinner.goGONE()
        } else {
            dB.imgChooseTranslationSpinner.goVISIBLE()
        }

        if (translationAndExampsList.size != 0) {
            updateTxtTrans(transList.first())
        }

        rcvChooseTransAdapter.setData(transList)
    }

    private fun updateRCVChooseExample(translationAndEx: TranslationAndExample) {
        if (translationAndEx.subExampleList.size != 0) {
            val fullSingleExampleList =
                convertExampleListTo_FullSingleExampleList(translationAndEx.subExampleList)
            if (fullSingleExampleList.size <= 1) {
                dB.imgChooseExampleSpinner.goGONE()
            } else {
                dB.imgChooseExampleSpinner.goVISIBLE()
            }
            rcvChooseExampleAdapter.setData(fullSingleExampleList)
            updateTxtExampleAndExampleMean(fullSingleExampleList.first())
        } else {
            updateTxtExampleAndExampleMean(null)
        }
    }

    private fun convertExampleListTo_FullSingleExampleList (exampleList : ArrayList<Example>) : ArrayList<SingleMeanExample> {
        val result = ArrayList<SingleMeanExample>()
        exampleList.forEach { example ->
            if (example is SingleMeanExample) {
                result.add(example)
            } else if (example is VocabularyOtherStructure) {
                example.translationAndExample_List.forEach { transAndExam ->
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
        resetAllInputFields()
        playImageAddSuccessAnim(startDelay = AddFCPanelDisappear_RelativeDuration)
        hideVirtualKeyboard()
    }

    private fun resetAllInputFields() {
        dB.apply {
            edtPanelText.setText("")
            edtPanelTranslation.setText("")
            edtPanelType.setText("")
            edtPanelExample.setText("")
            edtPanelMeanExample.setText("")
        }
    }

    private fun playImageAddSuccessAnim(startDelay: Long = 0) {
        dB.apply {
            imgAddFcSuccess.animate().scaleX(2f).scaleY(2f).alpha(1f).setDuration(250)
                .setInterpolator(FastOutLinearInInterpolator()).setStartDelay(startDelay)
                .setLiteListener(
                    onEnd = {
                        imgAddFcSuccess.animate().alpha(0f)
                            .setStartDelay(0) // Reset startDelay
                            .setLiteListener(onEnd = {
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
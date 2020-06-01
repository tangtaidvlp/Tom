package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.animation.addListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivitySearchEngVnDictionaryBinding
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import com.teamttdvlp.memolang.view.activity.iview.SearchEngVNDictionaryView
import com.teamttdvlp.memolang.view.adapter.RCVRecent_SearchDictionary_Adapter
import com.teamttdvlp.memolang.view.adapter.RCVSearchDictionaryAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.*
import com.teamttdvlp.memolang.viewmodel.PARTS_DEVIDER
import com.teamttdvlp.memolang.viewmodel.SearchEngVNDictionaryViewModel
import javax.inject.Inject
import javax.inject.Named

const val VOCABULARY_INFO = "voca_inf"

class SearchEngVNDictionaryActivity :
    BaseActivity<ActivitySearchEngVnDictionaryBinding, SearchEngVNDictionaryViewModel>(),
    SearchEngVNDictionaryView{

    lateinit var rcvSearchDictionaryAdapter : RCVSearchDictionaryAdapter
    @Inject set

    lateinit var rcvRecentSearchDicAdapter : RCVRecent_SearchDictionary_Adapter
    @Inject set

    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    @Inject set

    private lateinit var rcvDictionaryAppearAnimator : Animator

    private lateinit var rcvDictionaryDisappearAnimator : Animator

    override fun getLayoutId(): Int = R.layout.activity_search_eng_vn_dictionary

    override fun takeViewModel(): SearchEngVNDictionaryViewModel {
        return getActivityViewModel(viewModelProviderFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        rcvDictionaryAppearAnimator.start()
        dB.edtEngViDictionary.requestFocus()
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveSearchingHistory()
    }

    override fun addViewControls() {
        dB.rcvDictionary.adapter = rcvSearchDictionaryAdapter
        dB.rcvDictionary.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        dB.rcvRecentSearchDic.adapter = rcvRecentSearchDicAdapter
        dB.rcvRecentSearchDic.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        viewModel.getAll_RecentSearchedVocaList {
            rcvRecentSearchDicAdapter.setData(it)
        }
    }

    override fun addViewEvents() { dB.apply {

        btnSearch.setOnClickListener {
            edtEngViDictionary.requestFocus()
            showVirtualKeyboard()   
        }

        rcvSearchDictionaryAdapter.setOnItemClickListener { rawVocabulary ->
            onChooseVocabulary(rawVocabulary as TypicalRawVocabulary)
        }

        rcvRecentSearchDicAdapter.setOnItemClickListener { rawVocabulary ->
            onChooseVocabulary(rawVocabulary)
        }

        btnClearAllText.setOnClickListener {
            edtEngViDictionary.setText("")
        }

        edtEngViDictionary.addTextChangeListener (onTextChanged = { text, _, _, _ ->
            if (text == "") {
                rcvRecentSearchDic.goVISIBLE()
                rcvDictionary.goGONE()
            } else {
                rcvRecentSearchDic.goGONE()
                rcvDictionary.goVISIBLE()
            }

            rcvSearchDictionaryAdapter.search(text)

            })

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
    }}

    private fun onChooseVocabulary(item: TypicalRawVocabulary) {
        rcvRecentSearchDicAdapter.addData(item)
        viewModel.addVoca_ToRecentSearchedList(item)
        sendContentToActivitySeeFlashcard(item)
    }

    override fun onStart() {
        super.onStart()
        dB.edtEngViDictionary.requestFocus()
    }

    override fun overrideEnterAnim () {
        overridePendingTransition(R.anim.appear, R.anim.nothing)
    }

    override fun overrideExitAnim () {
        overridePendingTransition(R.anim.nothing,R.anim.disappear)
    }

    private fun sendContentToActivitySeeFlashcard (vocabularyContent : TypicalRawVocabulary) {
        val intent = Intent(this@SearchEngVNDictionaryActivity, SeeVocabularyActivity::class.java)
        intent.putExtra(VOCABULARY_INFO, vocabularyContent)
        startActivity(intent)
    }

    @Inject
    fun initDictionaryAnimation (
        @Named("Appear100Percents") dictionaryAppearAnimation : Animator,
        @Named("Disappear100Percents") dictionaryDisappearAnimation : Animator
    ) { dB.apply {
        rcvDictionaryAppearAnimator = dictionaryAppearAnimation
        rcvDictionaryAppearAnimator.setTarget(rcvRecentSearchDic)
        rcvDictionaryAppearAnimator.addListener (onStart = {
            rcvRecentSearchDic.alpha = 0f
            rcvRecentSearchDic.goVISIBLE()
        })

        rcvDictionaryDisappearAnimator = dictionaryDisappearAnimation
        rcvDictionaryDisappearAnimator.setTarget(rcvRecentSearchDic)
        rcvDictionaryDisappearAnimator.addListener (onStart = {
            rcvRecentSearchDic.goGONE()
        })

    }}

}

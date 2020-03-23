package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import androidx.core.animation.addListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivitySearchEngVnDictionaryBinding
import com.teamttdvlp.memolang.model.entity.vocabulary.SearchVocaHistoryHolder
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.database.sql.repository.UserSearchHistoryRepository
import com.teamttdvlp.memolang.view.activity.iview.SearchEngVNDictionaryView
import com.teamttdvlp.memolang.view.adapter.RCVRecent_SearchDictionary_Adapter
import com.teamttdvlp.memolang.view.adapter.RCVSearchDictionaryAdapter
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.addTextChangeListener
import com.teamttdvlp.memolang.view.helper.appear
import com.teamttdvlp.memolang.view.helper.disappear
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

    lateinit var userSearchHistoryRepository: UserSearchHistoryRepository
    @Inject set

    lateinit var flashcardRepository: FlashcardRepository
    @Inject set

    private lateinit var rcvDictionaryAppearAnimator : Animator

    private lateinit var rcvDictionaryDisappearAnimator : Animator

    override fun getLayoutId(): Int = R.layout.activity_search_eng_vn_dictionary

    override fun takeViewModel(): SearchEngVNDictionaryViewModel {
        return SearchEngVNDictionaryViewModel(this.application, userSearchHistoryRepository, flashcardRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUpView(this)
        rcvDictionaryAppearAnimator.start()
        dB.txtEngViDictionary.requestFocus()
    }

    override fun addViewControls() {
        dB.rcvDictionary.adapter = rcvSearchDictionaryAdapter
        dB.rcvDictionary.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        dB.rcvRecentSearchDic.adapter = rcvRecentSearchDicAdapter
        dB.rcvRecentSearchDic.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        viewModel.getAllSearchHistoryInfo {
            rcvRecentSearchDicAdapter.setData(it)
        }
    }

    override fun addViewEvents() { dB.apply {

        btnSearch.setOnClickListener {
            txtEngViDictionary.requestFocus()
            showVirtualKeyboard()   
        }

        rcvSearchDictionaryAdapter.setOnItemClickListener { key, content, navigatableKey ->
            val item = SearchVocaHistoryHolder(
                navigatableKey ?: key,
                content
            )
            rcvRecentSearchDicAdapter.addData(item)
            viewModel.updateSearchHistoryOffline(rcvRecentSearchDicAdapter.searchHistoryList)
            sendContentToActivitySeeFlashcard(content)
        }

        rcvRecentSearchDicAdapter.setOnItemClickListener { key, content ->
            val item = SearchVocaHistoryHolder(
                key,
                content
            )
            rcvRecentSearchDicAdapter.addData(item)
            viewModel.updateSearchHistoryOffline(rcvRecentSearchDicAdapter.searchHistoryList)
            sendContentToActivitySeeFlashcard(content)
        }

        btnClearAllText.setOnClickListener {
            txtEngViDictionary.setText("")
        }

        txtEngViDictionary.addTextChangeListener (onTextChanged = { text, _ , _, _ ->
            if (text == "") {
                rcvRecentSearchDic.appear()
                rcvDictionary.disappear()
            } else {
                rcvRecentSearchDic.disappear()
                rcvDictionary.appear()
            }

            rcvSearchDictionaryAdapter.search(text)
        })
    }}

    override fun onStart() {
        super.onStart()
        dB.txtEngViDictionary.requestFocus()
    }

    override fun overrideEnterAnim () {
        overridePendingTransition(R.anim.appear, R.anim.nothing)
    }

    override fun overrideExitAnim () {
        overridePendingTransition(R.anim.nothing,R.anim.disappear)
    }

    fun sendContentToActivitySeeFlashcard (vocabularyContent : String) {
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
            rcvRecentSearchDic.appear()
        })

        rcvDictionaryDisappearAnimator = dictionaryDisappearAnimation
        rcvDictionaryDisappearAnimator.setTarget(rcvRecentSearchDic)
        rcvDictionaryDisappearAnimator.addListener (onStart = {
            rcvRecentSearchDic.disappear()
        })

    }}

}

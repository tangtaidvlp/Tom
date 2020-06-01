package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import com.teamttdvlp.memolang.model.repository.UserUsingHistoryRepos
import com.teamttdvlp.memolang.view.activity.iview.SearchEngVNDictionaryView
import com.teamttdvlp.memolang.view.base.BaseViewModel


class SearchEngVNDictionaryViewModel (var app : Application,
                                      var userUsingHistoryRepos: UserUsingHistoryRepos): BaseViewModel<SearchEngVNDictionaryView>() {

    fun getAll_RecentSearchedVocaList (onGetAllSearchHistory : (ArrayList<TypicalRawVocabulary>) -> Unit) {
        userUsingHistoryRepos.getRecent_SearchedVocabularyList (onGetAllSearchHistory)
    }

    fun addVoca_ToRecentSearchedList (rawVocabulary: TypicalRawVocabulary) {
        userUsingHistoryRepos.addToRecent_SearchedVocabularyList(rawVocabulary)
    }

    fun saveSearchingHistory () {
        userUsingHistoryRepos.saveUsingHistoryInfo()
    }

}
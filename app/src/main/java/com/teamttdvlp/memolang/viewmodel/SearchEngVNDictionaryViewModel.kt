package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.teamttdvlp.memolang.model.entity.vocabulary.SearchVocaHistoryHolder
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.database.sql.repository.UserSearchHistoryRepository
import com.teamttdvlp.memolang.view.activity.iview.SearchEngVNDictionaryView
import com.teamttdvlp.memolang.view.base.BaseAndroidViewModel
import com.teamttdvlp.memolang.view.helper.quickLog


class SearchEngVNDictionaryViewModel (var app : Application, var userSearchHistoryRepos : UserSearchHistoryRepository, var flashcardRepository: FlashcardRepository): BaseAndroidViewModel<SearchEngVNDictionaryView>(app) {

    private val VOCABULARY_DIVIDER = "<:>"

    private val VOCA_INFO_DIVIDER = "[:]"

    private val SEARCH_DICTIONARY_HISTORY = "SEARCHED_DICTIONARIES_HISTORY"

    private val HISTORY = "History"

    private val KEY = 0

    private val CONTENT = 1

    fun getAllSearchHistoryInfo (onGetAllSearchHistory : (ArrayList<SearchVocaHistoryHolder>) -> Unit) {
        val preference = app.getSharedPreferences(SEARCH_DICTIONARY_HISTORY, MODE_PRIVATE)
        val data = preference.getString(HISTORY, "") + ""
        val historyArrList = ArrayList<SearchVocaHistoryHolder>()
        for (vocabulary in data.split(VOCABULARY_DIVIDER)) {
            if (vocabulary.isNotEmpty()) {
                val vocaInfo = vocabulary.split(VOCA_INFO_DIVIDER)
                try {
                    val item =
                        SearchVocaHistoryHolder(
                            vocaInfo.get(KEY),
                            vocaInfo.get(CONTENT)
                        )
                    historyArrList.add(item)
                } catch (ex : Exception) {
                    quickLog("ERROR")
                    ex.printStackTrace()
                }
            }
        }
        onGetAllSearchHistory(historyArrList)
    }

    fun updateSearchHistoryOffline (newHistory : ArrayList<SearchVocaHistoryHolder>) {
        val preference = app.getSharedPreferences("SEARCHED_DICTIONARIES_HISTORY", MODE_PRIVATE)
        var historyTextForm = ""
        newHistory.forEach { holder ->
            val holderTextForm = holder.key + VOCA_INFO_DIVIDER + holder.content
            historyTextForm += holderTextForm + VOCABULARY_DIVIDER
        }
        historyTextForm.removeSuffix(VOCABULARY_DIVIDER)
        val editor = preference.edit()
        editor.putString(HISTORY, historyTextForm)
        editor.apply()
    }

}
package com.teamttdvlp.memolang.model

import android.app.Application
import android.content.Context


class SearchOnlineSharedPreference (app : Application) {

    private val SEARCH_ONLINE_INFO = "search_online_info"

    private val CURRENT_SOURCE_LANGUAGE = "cur_source_lang"

    private val CURRENT_TARGET_LANGUAGE = "cur_target_lang"

    private val sharedPref = app.getSharedPreferences(SEARCH_ONLINE_INFO, Context.MODE_PRIVATE)

    var currentTargetLanguage: String
        get () =  sharedPref.getString(CURRENT_TARGET_LANGUAGE, "") + ""
        set (language) = sharedPref.edit().putString(CURRENT_TARGET_LANGUAGE, language).apply()

    var currentSourceLanguage: String
        get () =  sharedPref.getString(CURRENT_SOURCE_LANGUAGE, "") + ""
        set (language) = sharedPref.edit().putString(CURRENT_SOURCE_LANGUAGE, language).apply()

}


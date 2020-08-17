package com.teamttdvlp.memolang.model.sharepref

import android.app.Application

class SearchOnlineActivitySharePref(app: Application) : BaseAppInfoSharedPreference(app) {

    var currentSourceLang: String
        get() = currentFrontCardLanguage
        set(value) {
            currentFrontCardLanguage = value
        }

    var currentTargetLang: String
        get() = currentBackCardLanguage
        set(value) {
            currentBackCardLanguage = value
        }

    override fun getKey(): String {
        return "search_onl_key"
    }

}
package com.teamttdvlp.memolang.model.sharepref

import android.app.Application
import android.content.Context


abstract class BaseAppInfoSharedPreference(app: Application) {

    private val CURRENT_FRONT_CARD_LANGUAGE = "cur_front_card_lang"

    private val CURRENT_BACK_CARD_LANGUAGE = "cur_back_card_lang"

    private val LAST_USED_FLASHCARD_SET = "lasted_use_fc_set"

    private val sharedPref = app.getSharedPreferences(getKey(), Context.MODE_PRIVATE)


    var currentFrontCardLanguage: String
        get() = sharedPref.getString(CURRENT_FRONT_CARD_LANGUAGE, "") + ""
        set(language) {
            sharedPref.edit().putString(CURRENT_FRONT_CARD_LANGUAGE, language).apply()
        }

    var currentBackCardLanguage: String
        get() = sharedPref.getString(CURRENT_BACK_CARD_LANGUAGE, "") + ""
        set(language) {
            sharedPref.edit().putString(CURRENT_BACK_CARD_LANGUAGE, language).apply()
        }

    var lastUsedFlashcardSetName: String
        get() = sharedPref.getString(LAST_USED_FLASHCARD_SET, "") + ""
        set(language) {
            sharedPref.edit().putString(LAST_USED_FLASHCARD_SET, language).apply()
        }

    abstract fun getKey(): String

}


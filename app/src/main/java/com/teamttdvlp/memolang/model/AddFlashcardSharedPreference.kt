package com.teamttdvlp.memolang.model

import android.app.Application
import android.content.Context


class AddFlashcardSharedPreference (app : Application) {

    private val ADD_FLASHCARD_INFO = "add_fc_info"

    private val CURRENT_FRONT_CARD_LANGUAGE = "cur_front_card_lang"

    private val CURRENT_BACK_CARD_LANGUAGE = "cur_back_card_lang"

    private val sharedPref = app.getSharedPreferences(ADD_FLASHCARD_INFO, Context.MODE_PRIVATE)

    var currentFrontCardLanguage : String
    get () = sharedPref.getString(CURRENT_FRONT_CARD_LANGUAGE, "") + ""
    set (language) {
        sharedPref.edit().putString(CURRENT_FRONT_CARD_LANGUAGE, language).apply()
    }

    var currentBackCardLanguage : String
        get () = sharedPref.getString(CURRENT_BACK_CARD_LANGUAGE, "") + ""
        set (language) {
            sharedPref.edit().putString(CURRENT_BACK_CARD_LANGUAGE, language).apply()
        }

}


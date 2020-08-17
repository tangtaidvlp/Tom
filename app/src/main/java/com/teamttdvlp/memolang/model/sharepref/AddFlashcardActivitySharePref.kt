package com.teamttdvlp.memolang.model.sharepref

import android.app.Application

class AddFlashcardActivitySharePref(app: Application) : BaseAppInfoSharedPreference(app) {
    override fun getKey(): String {
        return "add_fc_key"
    }
}
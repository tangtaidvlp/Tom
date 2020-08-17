package com.teamttdvlp.memolang.model.sharepref

import android.app.Application

class EngVietDictionaryActivitySharePref(app: Application) : BaseAppInfoSharedPreference(app) {
    override fun getKey(): String {
        return "en_vi_dic_key"
    }
}
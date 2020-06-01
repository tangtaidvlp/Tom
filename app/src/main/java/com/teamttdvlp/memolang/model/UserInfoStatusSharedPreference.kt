package com.teamttdvlp.memolang.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences


class UserInfoStatusSharedPreference (app : Application) {

    private val SET_UP_INFO = "suif"

    private val FIRST_TIMES_SET_UP = "ftsu"

    private val sharedPref = app.getSharedPreferences(SET_UP_INFO, Context.MODE_PRIVATE)

    fun didUserSetUpBasicInfoBefore () : Boolean {
        return sharedPref.getBoolean(FIRST_TIMES_SET_UP, false)
    }

    fun markUserInfo_IsSetUp() {
        sharedPref.edit().putBoolean(FIRST_TIMES_SET_UP, true).apply()
    }

}

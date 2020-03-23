package com.teamttdvlp.memolang.viewmodel

import android.app.ActivityManager
import android.app.Application
import android.content.Context.ACTIVITY_SERVICE
import com.teamttdvlp.memolang.model.RecentAddedFlashcardManager
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.view.activity.iview.MenuView
import com.teamttdvlp.memolang.view.base.BaseAndroidViewModel

const val FIRST_TIMES_SIGNED_IN = "first_times_signed_in"

const val SIGN_IN_INFO = "signed_in_info"

class MenuActivityViewModel (private var app : Application, private var recentAddedFlashcardManager : RecentAddedFlashcardManager): BaseAndroidViewModel<MenuView>(app) {

    fun getRecentAddedFlashcard (onGetSuccess : (ArrayList<Flashcard>) -> Unit) {
        recentAddedFlashcardManager.getRecentAddedFlashcard (onGetSuccess)
    }

    fun clearUserInfomation() {
        (app.getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
    }

    fun setUpUserInfo (motherLang : String, targetLang : String) {
        getUser().recentTargetLanguage = motherLang
        getUser().recentSourceLanguage = targetLang
    }


}
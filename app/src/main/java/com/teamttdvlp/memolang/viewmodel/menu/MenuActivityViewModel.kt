package com.teamttdvlp.memolang.viewmodel.menu

import android.app.ActivityManager
import android.app.Application
import android.content.Context.ACTIVITY_SERVICE
import com.teamttdvlp.memolang.view.activity.iview.MenuView
import com.teamttdvlp.memolang.view.base.BaseAndroidViewModel

class MenuActivityViewModel (var app : Application): BaseAndroidViewModel<MenuView>(app) {

    fun clearUserInfomation() {
        (app.getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
    }

}
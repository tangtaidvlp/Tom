package com.teamttdvlp.memolang.viewmodel.menu

import android.app.ActivityManager
import android.app.Application
import android.content.Context.ACTIVITY_SERVICE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

class MenuActivityViewModel (var app : Application): AndroidViewModel(app) {

    fun clearUserInfomation() {
        (app.getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
    }

}
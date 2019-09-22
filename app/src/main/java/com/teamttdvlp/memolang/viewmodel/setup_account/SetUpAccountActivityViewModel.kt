package com.teamttdvlp.memolang.viewmodel.setup_account

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.google.firebase.auth.FirebaseAuth
import com.teamttdvlp.memolang.model.model.User
import com.teamttdvlp.memolang.model.sqlite.repository.UserRepository
import com.teamttdvlp.memolang.model.sqlite.converter.UserConverter
import com.teamttdvlp.memolang.view.activity.base.BaseAndroidViewModel
import com.teamttdvlp.memolang.viewmodel.auth.FIRST_TIMES_SIGNED_IN
import com.teamttdvlp.memolang.viewmodel.auth.SIGN_IN_INFO
import com.teamttdvlp.memolang.viewmodel.reusable.OnlineUserDBManager

/**
 * @see com.teamttdvlp.memolang.view.activity.SetUpAccountActivity
 */
class SetUpAccountActivityViewModel(var authManager : FirebaseAuth, var userRepository : UserRepository, var onlineUserDBManager: OnlineUserDBManager, var app : Application) : BaseAndroidViewModel(app) {

    fun writeUserToOfflineDatabase (user : User) {
        userRepository.insertUser(UserConverter.toUserEntity(user))
    }

    fun writeUserToOnlineDatabase (user : User) {
        onlineUserDBManager.writeUserInfo(user)
    }

    fun saveSignedInStatus () {
        val sharePref = app.getSharedPreferences(SIGN_IN_INFO, MODE_PRIVATE)
        sharePref.edit().apply {
            putBoolean(FIRST_TIMES_SIGNED_IN, true)
            apply()
        }
    }

}

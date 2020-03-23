package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.firebase.auth.FirebaseAuth
import com.teamttdvlp.memolang.model.entity.User
import com.teamttdvlp.memolang.database.sql.repository.UserRepository
import com.teamttdvlp.memolang.database.sql.entity.user.UserConverter
import com.teamttdvlp.memolang.view.activity.iview.SetUpAccountView
import com.teamttdvlp.memolang.view.base.BaseAndroidViewModel

//import com.teamttdvlp.memolang.viewmodel.auth.FIRST_TIMES_SIGNED_IN
//import com.teamttdvlp.memolang.viewmodel.auth.SIGN_IN_INFO
//import com.teamttdvlp.memolang.viewmodel.reusable.OnlineUserDBManager

/**
 * @see com.teamttdvlp.memolang.view.activity.SetUpAccountActivity
 */
class SetUpAccountViewModel(var authManager : FirebaseAuth
                            , var userRepository : UserRepository
//                                    , var onlineUserDBManager: OnlineUserDBManager
                            , var app : Application) : BaseAndroidViewModel<SetUpAccountView>(app) {

    fun writeUserToOfflineDatabase (user : User) {
        userRepository.insertUser(UserConverter.toUserEntity(user))
    }


    fun checkUserInfoBeenInitilized () {
        val sharePref = app.getSharedPreferences(SIGN_IN_INFO, Context.MODE_PRIVATE)
        val userHasSetUpInfoBefore = sharePref.getBoolean(FIRST_TIMES_SIGNED_IN, false)
        if (userHasSetUpInfoBefore) {
            getUserInfoAndNavigateToMenu()
        }
    }

    fun getUserInfoAndNavigateToMenu () {
        userRepository.triggerGetUser { userEntity ->
            createUser( "", userEntity!!.motherLanguage, userEntity.targetLanguage)
            getUser().apply {
                recentUseLanguages = userEntity.recentUseLanguages
                flashcardSetNames = userEntity.flashcardSetNames
                customTypes = userEntity.customTypes
                recentUseFlashcardSet  = userEntity.recentUseFlashcardSet
            }
        }
        view.navigateToMenuScreen()
    }

//    fun writeUserToOnlineDatabase (user : User) {
//        onlineUserDBManager.writeUserInfo(user)
//    }

    fun saveSignedInStatus () {
        val sharePref = app.getSharedPreferences(SIGN_IN_INFO, MODE_PRIVATE)
        sharePref.edit().apply {
            putBoolean(FIRST_TIMES_SIGNED_IN, true)
            apply()
        }
    }

    fun createUserInfo(motherLang: String, targetLang: String) {
        createUser("", motherLang, targetLang)
        writeUserToOfflineDatabase(getUser())
        saveSignedInStatus()
        view.navigateToMenuScreen()
    }

}

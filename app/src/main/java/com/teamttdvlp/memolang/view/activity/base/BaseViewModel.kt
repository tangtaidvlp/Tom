package com.teamttdvlp.memolang.view.activity.base

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.AndroidViewModel
import com.teamttdvlp.memolang.model.model.User

/**
 * Bot of BaseViewModel and BaseAndroidViewModel must do exactly the samething, excluding BaseAndroidViewModel has application variable
 */

abstract class BaseAndroidViewModel (app : Application) :  AndroidViewModel (app) {

    fun createSingletonUser (id : String, motherLang : String, targetLang : String) {
        User.createInstance(id, motherLang, targetLang)
    }

    fun getSingletonUser () : User?{
        return User.getInstance()
    }

}

abstract class BaseViewModel () : ViewModel () {

    fun createSingletonUser (id : String, motherLang : String, targetLang : String) {
        User.createInstance(id, motherLang, targetLang)
    }

    fun getSingletonUser () : User? {
        return User.getInstance()
    }

}
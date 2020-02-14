package com.teamttdvlp.memolang.view.base

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.AndroidViewModel
import com.teamttdvlp.memolang.model.entity.User
import com.teamttdvlp.memolang.view.activity.iview.View

/**
 * Bot of BaseViewModel and BaseAndroidViewModel must do exactly the samething, excluding BaseAndroidViewModel has application variable
 */

abstract class BaseAndroidViewModel <V : View> (app : Application) :  AndroidViewModel (app) {

    protected lateinit var view : V

    fun setSingletonUser (id : String = "", motherLang : String, targetLang : String) {
        User.setInstanceInfo(id, motherLang, targetLang)
    }

    fun getSingletonUser () : User?{
        return User.getInstance()
    }

    fun setUpView (view : V) {
        this.view = view
    }

}

abstract class BaseViewModel <V : View> () : ViewModel () {

    protected lateinit var view : V


    fun createSingletonUser (id : String, motherLang : String, targetLang : String) {
        User.setInstanceInfo(id, motherLang, targetLang)
    }

    fun getSingletonUser () : User? {
        return User.getInstance()
    }

    fun setUpView (view : V) {
        this.view = view
    }

}
package com.teamttdvlp.memolang.view.base

import androidx.lifecycle.ViewModel
import com.teamttdvlp.memolang.data.model.entity.user.User
import com.teamttdvlp.memolang.view.activity.iview.View

abstract class BaseViewModel <V : View> : ViewModel () {

    protected lateinit var view : V

    companion object {
        private var user : User? = null
    }

    protected fun getUser () : User {
        if (user == null) {
            user = User()
        }
        return user!!
    }

    protected fun setUser (user : User) {
        BaseViewModel.user = user
    }

    fun setUpView (view : V) {
        this.view = view
    }

    open fun create () : BaseViewModel <V> {
        return this
    }

}
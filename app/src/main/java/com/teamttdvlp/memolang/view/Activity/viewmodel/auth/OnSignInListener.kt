package com.teamttdvlp.memolang.view.Activity.viewmodel.auth

import com.google.firebase.auth.FirebaseUser
import java.lang.Exception

interface OnSignInListener {

    fun onSuccess(user : FirebaseUser)

    fun onFailed (ex : Exception?)

}
package com.teamttdvlp.memolang.view.Activity.viewmodel.signup

import androidx.lifecycle.ViewModel
import java.net.PasswordAuthentication

class SignUpViewModel : ViewModel () {

    private var signUpManager : SignUpManager = SignUpManager()

    fun signUp (email : String, password : String) {
        signUpManager.signUp(email, password)
    }

    fun setOnSignUpListener (onSignUpListener: OnSignUpListener) {
        signUpManager.setOnSignUpListener(onSignUpListener)
    }



}
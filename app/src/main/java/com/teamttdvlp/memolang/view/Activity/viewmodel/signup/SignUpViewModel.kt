package com.teamttdvlp.memolang.view.Activity.viewmodel.signup

import androidx.lifecycle.ViewModel
import java.net.PasswordAuthentication
import java.util.regex.Pattern

class SignUpViewModel : ViewModel () {

    private var signUpManager : SignUpManager = SignUpManager()

    fun signUp (email : String, password : String) {
        signUpManager.signUp(email, password)
    }

    fun setOnSignUpListener (onSignUpListener: OnSignUpListener) {
        signUpManager.setOnSignUpListener(onSignUpListener)
    }

    fun isPasswordValid (password : String) : Boolean {
        return (isPasswordLongEnough(password)
                                        and
                doesPasswordContainsSpecialChar(password))
    }

    private fun isPasswordLongEnough (password : String) : Boolean {
        return password.length >= 6
    }

    private fun doesPasswordContainsSpecialChar (password : String) : Boolean {
        val p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE)
        val m = p.matcher(password)
        val isContainSpecialCharacter = m.find()
        return !isContainSpecialCharacter
    }



}
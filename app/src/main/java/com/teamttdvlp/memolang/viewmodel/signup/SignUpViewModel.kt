package com.teamttdvlp.memolang.viewmodel.signup

import com.teamttdvlp.memolang.view.base.BaseViewModel
import java.util.regex.Pattern

class SignUpViewModel(var signUpManager : SignUpManager) : BaseViewModel () {

    fun signUp (email : String, password : String) {
        signUpManager.signUp(email, password)
    }

    fun setOnSignUpListener (onSignUpListener: OnSignUpListener) {
        signUpManager.setOnSignUpListener(onSignUpListener)
    }


    // In AuthActivity onStart, it would check FirebaseAuth.currentUser to check that if user had signed in before and
    // stored their account on device (without Signing out)
    fun clearFirebaseAuthCurrentUser () {
        signUpManager.clearFirebaseAuthCurrentUser()
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
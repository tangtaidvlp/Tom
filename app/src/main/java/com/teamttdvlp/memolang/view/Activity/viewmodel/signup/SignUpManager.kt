package com.teamttdvlp.memolang.view.Activity.viewmodel.signup

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpManager {

    private val SIGN_UP_TAG = "Sign up"

    private var auth : FirebaseAuth = FirebaseAuth.getInstance()

    private var onSignUpListener : OnSignUpListener? = null

    fun signUp(email : String, password : String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e(SIGN_UP_TAG, "signUpWithCredential:success: " + task.result!!.credential)
                onSignUpListener?.onSuccess(auth.currentUser!!)
            } else {
                Log.e(SIGN_UP_TAG, "signUpWithCredential:failure", task.exception)
                onSignUpListener?.onFailed(task.exception)
            }
        }
    }

    fun setOnSignUpListener(onSignUpListener: OnSignUpListener) {
        this.onSignUpListener = onSignUpListener
    }

}
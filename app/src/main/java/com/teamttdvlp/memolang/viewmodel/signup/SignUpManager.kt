package com.teamttdvlp.memolang.viewmodel.signup

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class SignUpManager @Inject constructor(var auth : FirebaseAuth) {

    private val SIGN_UP_TAG = "Sign up"

    private var onSignUpListener : OnSignUpListener? = null

    fun signUp(email : String, password : String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e(SIGN_UP_TAG, "signUpWithCredential:success: " + task.result!!.credential)
                onSignUpListener?.onSuccess(auth.currentUser!!)
            } else {
                Log.e(SIGN_UP_TAG, "signUpWithCredential:failure - " +  task.exception?.message)
                onSignUpListener?.onFailed(task.exception)
            }
        }
    }

    fun setOnSignUpListener(onSignUpListener: OnSignUpListener) {
        this.onSignUpListener = onSignUpListener
    }

    // After sign up successfully, FirebaseAuth will have an current user which has been the one we've just signed up
    fun clearFirebaseAuthCurrentUser() {
        auth.signOut()
    }
}
package com.teamttdvlp.memolang.view.Activity.viewmodel.auth

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * @see com.teamttdvlp.memolang.view.Activity.AuthActivity
 */
class AuthActivityViewModel(var app : Application) : AndroidViewModel(app) {

    private var accountLiveData : MutableLiveData<FirebaseUser> = MutableLiveData()

    private var authManager : AuthManager = AuthManager(app)

    fun getGoogleSignInIntent () : Intent {
       return authManager.getGoogleSignInIntent()
    }

    fun signInWithGoogle(acct: GoogleSignInAccount) {
        authManager.signInWithGoogle(acct)
    }


    fun requestFacebookSignIn(activity : Activity) {
        authManager.requestFacebookSignIn(activity)
    }

    fun signInWithFacebook(token: AccessToken) {
        authManager.signInWithFacebook(token)
    }


    fun signInWithEmailAndPassword (email : String, password : String) {
        authManager.signInWithEmailAndPassword(email, password)
    }

    fun setOnSignInListener (onSignInListener: OnSignInListener) {
        authManager.setOnSignInListener(onSignInListener)
    }

    fun userHasSignedIn () : Boolean {
        return authManager.userHasSignedIn()
    }
}
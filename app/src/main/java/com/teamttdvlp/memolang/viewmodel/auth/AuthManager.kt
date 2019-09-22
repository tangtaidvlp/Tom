package com.teamttdvlp.memolang.viewmodel.auth

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.auth.User
import com.teamttdvlp.memolang.R
import javax.inject.Inject

class AuthManager @Inject constructor(var auth : FirebaseAuth, private var app : Application) {

    private val FB_TAG = "FacebookSignIn"

    private val GG_TAG = "GoogleSignIn"

    private val NORMAL_TAG = "NormalSignIn"

    private var onSignInListener : OnSignInListener? = null

    init {
        FacebookSdk.sdkInitialize(app)
    }

    fun userHasSignedIn () : Boolean {
        return auth.currentUser != null
    }

    fun setOnSignInListener(onSignInListener: OnSignInListener) {
        this.onSignInListener = onSignInListener
    }

    fun signInWithEmailAndPassword (email : String, password : String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e(NORMAL_TAG, "signInWithCredential:success")
                    Log.e(NORMAL_TAG, "credential: " + task.result!!.credential)
                    onSignInListener?.onSuccess(auth.currentUser!!)
                } else {
                    Log.e(NORMAL_TAG, "signInWithCredential:failure", task.exception)
                    onSignInListener?.onFailed(task.exception)
                }
            }
    }

    fun getCurrentUser () : FirebaseUser {
        return auth.currentUser!!
    }


    /**
     * GOOGLE sign in
     */
    fun getGoogleSignInIntent () : Intent {
        val gso = GoogleSignInOptions.Builder()
            .requestIdToken(app.getString(R.string.default_web_client_id))
            .requestProfile()
            .requestEmail()
            .build()

        val signInIntent= GoogleSignIn.getClient(app, gso).signInIntent
        return signInIntent
    }

    fun signInWithGoogle(acct: GoogleSignInAccount) {
        Log.d(GG_TAG, "Id: " + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(GG_TAG, "Success: " + task.result!!.credential)
                    onSignInListener?.onSuccess(auth.currentUser!!)
                } else {
                    Log.w(GG_TAG, "Failed", task.exception)
                    onSignInListener?.onFailed(task.exception)
                }
            }
    }


    /**
     * FACEBOOK sign in
     */
    fun signInWithFacebook(token: AccessToken) {
        Log.d(FB_TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(FB_TAG, "signInWithCredential:success: " + task.result!!.credential)
                    onSignInListener?.onSuccess(auth.currentUser!!)
                } else {
                    Log.w(FB_TAG, "signInWithCredential:failure", task.exception)
                    onSignInListener?.onFailed(task.exception)
                }
            }
    }

    fun requestFacebookSignIn(activity : Activity) {
        val permission = ArrayList<String>()
        permission.add("public_profile")
        LoginManager.getInstance().logInWithReadPermissions(activity, permission)
    }

}
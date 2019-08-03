package com.teamttdvlp.memolang.view.Activity.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInApi
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.teamttdvlp.memolang.R

class AuthActivityViewModel(var app : Application) : AndroidViewModel(app) {

    fun signInWithGoogle () {
        var gso = GoogleSignInOptions.Builder()
                                        .requestIdToken(app.getString(R.string.default_web_client_id))
                                        .requestProfile()
                                        .build()

        var intent= GoogleSignIn.getClient(app, gso).signInIntent
    }

}
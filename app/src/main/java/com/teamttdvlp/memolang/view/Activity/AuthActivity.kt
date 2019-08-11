package com.teamttdvlp.memolang.view.Activity

import android.app.Activity
import android.content.Intent
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityAuthBinding
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.helper.quickStartActivity
import com.teamttdvlp.memolang.view.Activity.helper.quickToast
import com.teamttdvlp.memolang.view.Activity.viewmodel.auth.AuthActivityViewModel
import java.lang.Exception

class AuthActivity :  BaseActivity<ActivityAuthBinding, AuthActivityViewModel>()  {

    private val GOOGLE_SIGN_IN_CODE = 18

    private lateinit var callbackManager : CallbackManager

    override fun getLayoutId(): Int = R.layout.activity_auth

    override fun takeViewModel(): AuthActivityViewModel = getActivityViewModel()

    override fun addViewEvents() {
        dataBinding.apply {

            btnSignUp.setOnClickListener {
                quickStartActivity(SignUpActivity::class.java)
            }

            btnSignIn.setOnClickListener {
                quickStartActivity(MenuActivity::class.java)
            }

            btnGoogleSignIn.setOnClickListener {
                var signIntent = viewModel.getGoogleSignInIntent()
                startActivityForResult(signIntent, GOOGLE_SIGN_IN_CODE)
            }

            btnFacebookSignIn.setOnClickListener {
                viewModel.requestFacebookSignIn(this@AuthActivity)
            }

            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    if (result == null) {
                        quickToast("Error happened. Please check")
                        return
                    }
                    viewModel.signInWithFacebook(result.accessToken)
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException?) {

                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.userHasSignedIn()) {
            // TODO Something here
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        if (data != null) callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data!!)
            try {
                var account = task.getResult(ApiException::class.java)
                if (account == null) {
                    quickToast("Error happened. Please check again")
                    return
                }
                viewModel.signInWithGoogle(account)
            } catch (ex : Exception) {
                ex.printStackTrace()
            }
        }
    }
}


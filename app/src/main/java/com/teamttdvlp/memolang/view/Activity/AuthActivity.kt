package com.teamttdvlp.memolang.view.Activity

import com.google.firebase.auth.FirebaseAuth
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityAuthBinding
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.helper.quickStartActivity
import com.teamttdvlp.memolang.view.Activity.helper.quickToast
import com.teamttdvlp.memolang.view.Activity.viewmodel.AuthActivityViewModel
import javax.inject.Inject

class AuthActivity :  BaseActivity<ActivityAuthBinding, AuthActivityViewModel>()  {

    var auth : FirebaseAuth? = null
    @Inject set

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
                viewModel.signInWithGoogle()
            }

            btnFacebookSignIn.setOnClickListener {

            }
        }
    }


}


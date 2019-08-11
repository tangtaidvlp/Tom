package com.teamttdvlp.memolang.view.Activity

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseUser
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivitySignUpBinding
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.helper.quickToast
import com.teamttdvlp.memolang.view.Activity.viewmodel.signup.OnSignUpListener
import com.teamttdvlp.memolang.view.Activity.viewmodel.signup.SignUpViewModel
import java.lang.Exception

class SignUpActivity : BaseActivity<ActivitySignUpBinding, SignUpViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_sign_up

    override fun takeViewModel(): SignUpViewModel = getActivityViewModel()

    override fun addViewEvents () { dataBinding.apply {
         btnSignUp.setOnClickListener {
             signUp()
         }

         btnBackToSignIn.setOnClickListener {
             finish()
         }

         btnCheckAgain.setOnClickListener {
             backgroundSignUpFail.visibility = GONE
             signUpWidgetsGroup.visibility = VISIBLE
         }

        edtEmail.setOnFocusChangeListener { _, isFocus ->
            if (isFocus and txtErrorEmail.isVisible) txtErrorEmail.visibility = GONE
        }

        edtPassword.setOnFocusChangeListener { _, isFocus ->
            if (isFocus and txtErrorPassword.isVisible) txtErrorPassword.visibility = GONE
        }

        edtReenterPassword.setOnFocusChangeListener { _, isFocus ->
             if (isFocus and txtReenterPassword.isVisible) txtErrorReenterPassword.visibility = GONE
         }
    }}

    override fun addEventsListener() { dataBinding.apply {
        viewModel.setOnSignUpListener(object : OnSignUpListener {
            override fun onSuccess(user: FirebaseUser) {
                signUpWidgetsGroup.visibility = GONE
                backgroundSignUpSuccessfully.visibility = VISIBLE
            }

            override fun onFailed(ex: Exception?) {
                signUpWidgetsGroup.visibility = GONE
                backgroundSignUpFail.visibility = VISIBLE
            }
        })
    }}

    private fun signUp () {
        dataBinding.apply {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            val reEnterPassword = edtReenterPassword.text.toString()

            if ( ! isInformationValid (email, password, reEnterPassword)) return

            try {
                viewModel.signUp(email, password)
            } catch (ex : Exception) {
                quickToast("Error happened. Please check again")
            }
        }
    }

    private fun isInformationValid(email : String, password : String, reEnteredPassword : String) : Boolean{
        dataBinding.apply {
            if (email == "") {
                txtErrorEmail.visibility = VISIBLE
                return false
            }

            if (password == "") {
                txtErrorPassword.visibility = VISIBLE
                return false
            }

            if (password.trim() != reEnteredPassword.trim()) {
                txtErrorReenterPassword.visibility = VISIBLE
                return false
            }

            return  true
        }
    }
}




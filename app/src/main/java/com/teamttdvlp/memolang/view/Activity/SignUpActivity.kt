package com.teamttdvlp.memolang.view.Activity

import android.view.View
import android.view.View.GONE
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivitySignUpBinding
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.Activity.helper.quickToast
import com.teamttdvlp.memolang.view.Activity.viewmodel.SignUpViewModel
import javax.inject.Inject

class SignUpActivity : BaseActivity<ActivitySignUpBinding, SignUpViewModel>() {

    var auth: FirebaseAuth? = null
    @Inject set

    override fun getLayoutId(): Int = R.layout.activity_sign_up

    override fun takeViewModel(): SignUpViewModel = getActivityViewModel()

    override fun addViewEvents () {
     dataBinding.apply {
         btnSignUp.setOnClickListener {
             signUp()
         }

         btnBackToSignIn.setOnClickListener {
             finish()
         }

         btnCheckAgain.setOnClickListener {
             backgroundSignUpFail.visibility = GONE
         }

         edtId.setOnFocusChangeListener { _, isFocus ->
            if (isFocus and txtErrorId.isVisible) txtErrorId.visibility = GONE
         }

         edtPassword.setOnFocusChangeListener { _, isFocus ->
             if (isFocus and txtErrorPassword.isVisible) txtErrorPassword.visibility = GONE
         }

         edtName.setOnFocusChangeListener { _, isFocus ->
             if (isFocus and txtErrorName.isVisible) txtErrorName.visibility = GONE
         }
     }
    }


    private fun signUp () {
        dataBinding.apply {

            val id = edtId.text.toString()
            val password = edtId.text.toString()
            val name = edtName.text.toString()

            if ( ! isInformationValid (id, password, name)) return

            try {
                auth!!.createUserWithEmailAndPassword(id, password)
                    .addOnSuccessListener {
                        backgroundSignUpSuccessfully.visibility = View.VISIBLE
                    }
                    .addOnFailureListener {
                        backgroundSignUpFail.visibility = View.VISIBLE
                    }
            } catch (ex : NullPointerException) {
                quickToast("Error happened. Please check again")
            }

        }
    }

    private fun isInformationValid(id : String, password : String, name : String) : Boolean{
        dataBinding.apply {
            if (id == "") {
                txtErrorId.visibility = View.VISIBLE
                return false
            }

            if (password == "") {
                txtErrorPassword.visibility = View.VISIBLE
                return false
            }

            if (name == "") {
                txtErrorName.visibility = View.VISIBLE
                return false
            }

            return  true
        }
    }
}




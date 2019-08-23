package com.teamttdvlp.memolang.view.Activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.animation.Animation
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityAuthBinding
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.*
import com.teamttdvlp.memolang.view.Activity.viewmodel.auth.AuthActivityViewModel
import com.teamttdvlp.memolang.view.Activity.viewmodel.auth.OnSignInListener
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named

class AuthActivity :  BaseActivity<ActivityAuthBinding, AuthActivityViewModel>()  {

    private val GOOGLE_SIGN_IN_CODE = 18

    private lateinit var callbackManager : CallbackManager

    private var signInAnimatorSet : AnimatorSet = AnimatorSet()

    private var signInSuccessAnimatorSet : AnimatorSet = AnimatorSet()

    private var signInFailedAnimatorSet : AnimatorSet = AnimatorSet()

    private var checkInfoAgainAnimatorSet : AnimatorSet = AnimatorSet()

    private var vibrate : Animation? = null

    private val redBorderRoundBackground : Drawable? by lazy {
        AppCompatResources.getDrawable(this, R.drawable.round_white_background_with_red_border)
    }

    private val normalBorderRoundBackground : Drawable? by lazy {
        AppCompatResources.getDrawable(this, R.drawable.round_white_background_with_border)
    }

    override fun getLayoutId(): Int = R.layout.activity_auth

    override fun takeViewModel(): AuthActivityViewModel = getActivityViewModel()

    override fun addViewEvents() { dataBinding.apply {

        btnSignUp.setOnClickListener {
            quickStartActivity(SignUpActivity::class.java)
        }

        btnSignIn.setOnClickListener {
            var id = edtId.text.toString()
            var password = edtPassword.text.toString()
            if (!isInformationValid(id, password)) return@setOnClickListener
            viewModel.signInWithEmailAndPassword(edtId.text.toString(), edtPassword.text.toString())
            signInAnimatorSet.start()
        }

        btnGoogleSignIn.setOnClickListener {
            var signIntent = viewModel.getGoogleSignInIntent()
            startActivityForResult(signIntent, GOOGLE_SIGN_IN_CODE)
        }

        btnFacebookSignIn.setOnClickListener {
            viewModel.requestFacebookSignIn(this@AuthActivity)
        }

        btnCheckAgain.setOnClickListener {
            checkInfoAgainAnimatorSet.start()
        }

        edtId.setOnFocusChangeListener {_, isFocused ->
            if (isFocused and txtErrorId.isVisible) {
                txtErrorId.hide()
                edtId.background = normalBorderRoundBackground
            }
        }

        edtPassword.setOnFocusChangeListener {_, isFocused ->
            if (isFocused and txtErrorPassword.isVisible) {
                txtErrorPassword.hide()
                edtPassword.background = normalBorderRoundBackground
            }
        }
    }}

    override fun initProperties() {
        callbackManager = CallbackManager.Factory.create()
    }

    /**
     * This variable is created to make sure that "imgSignInProgressbar" rotate animation finishes
     * at least 1 times before running the "signInFailedAnimatorSet"
     * ("signInFailedAnimatorSet"  is called in initSignInAnimations() function below)
     */
    private var signInFailed = false
    override fun addEventsListener() {
        viewModel.setOnSignInListener(object : OnSignInListener {
            override fun onSuccess(user: FirebaseUser) {
                signInFailed = false
                signInSuccessAnimatorSet.start()
            }

            override fun onFailed(ex: Exception?) {
                signInFailed = true
            }
        })

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

    private fun isInformationValid(email : String, password : String) : Boolean{
        dataBinding.apply {
            var isValid = true

            if (email == "") {
                txtErrorId.appear()
                edtId.background = redBorderRoundBackground
                edtId.startAnimation(vibrate)
                isValid = false
            }

            if (password == "") {
                txtErrorPassword.appear()
                edtPassword.background = redBorderRoundBackground
                edtPassword.startAnimation(vibrate)
                isValid = false
            }

            return isValid
        }
    }



    // Check if the first times finishes rotate animation
    var theFirstTimesFinishRotateAnimation = false
    @Inject
    fun initSignInAnimations(
        @Named("RotateForever") progressBarRotate : Animation,
        @Named("FromNothingToNormalSize") progressBarAppear : Animator,
        @Named("FromNormalSizeToNothing") viewgroupSignInDisappear : Animator) { dataBinding.apply {
        progressBarRotate.addAnimationLister(
            onRepeat = {
                // #signInfailed is revalued by SignInListener.OnFailed(Exception) of ViewModel which is created above
                if (signInFailed) {
                    theFirstTimesFinishRotateAnimation = !theFirstTimesFinishRotateAnimation
                    if (theFirstTimesFinishRotateAnimation) {
                        signInFailedAnimatorSet.start()
                    }
                }
            }
        )

        imgSignInProgressBar.startAnimation(progressBarRotate)

        progressBarAppear.apply {
            setTarget(viewgroupImgSignInProgressBar)
            addListener (onStart = {
                viewgroupImgSignInProgressBar.appear()
            })
        }

        viewgroupSignInDisappear.apply {
            setTarget(viewgroupSignInWidgets)
            addListener(onEnd = {
                viewgroupSignInWidgets.disappear()
            })
        }

        signInAnimatorSet.play(viewgroupSignInDisappear).before(progressBarAppear)

    }}


    @Inject
    fun initSignInFailedAnimations(
        @Named("FromNothingToNormalSize") viewgroupSignInFailedAppear : Animator,
        @Named("FromNormalSizeToNothing") progressBarDisappear : Animator) { dataBinding.apply {

        viewgroupSignInFailedAppear.apply {
            setTarget(viewgroupSignInFailed)
            addListener (onStart = {
                viewgroupSignInFailed.alpha = 1f
                viewgroupSignInFailed.appear()
            })
        }

        progressBarDisappear.apply {
            setTarget(viewgroupImgSignInProgressBar)
            addListener(onEnd = {
                viewgroupImgSignInProgressBar.disappear()
            })
        }

        signInFailedAnimatorSet.play(progressBarDisappear).before(viewgroupSignInFailedAppear)

    }}

    @Inject
    fun initCheckInfoAgainAnimations(
        @Named("Disappear100percents") groupSignInFailedDisappear : Animator,
        @Named("Appear100percents") groupSignInWidgetsAppear : Animator) { dataBinding.apply {
        groupSignInFailedDisappear.apply {
            setTarget(viewgroupSignInFailed)
            addListener (onEnd = {
                viewgroupSignInFailed.disappear()
            })
        }

        groupSignInWidgetsAppear.apply {
            setTarget(viewgroupSignInWidgets)
            addListener (onStart = {
                viewgroupSignInWidgets.scaleX = 1f
                viewgroupSignInWidgets.scaleY = 1f
                viewgroupSignInWidgets.appear()
            })
        }
        checkInfoAgainAnimatorSet.play(groupSignInFailedDisappear).before(groupSignInWidgetsAppear)
    }}

    @Inject
    fun initVibrateAnimation (@Named("Vibrate") vibrate : Animation) {
        this.vibrate = vibrate
    }
}


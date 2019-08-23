package com.teamttdvlp.memolang.view.Activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.graphics.drawable.Drawable
import android.view.animation.Animation
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseUser
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivitySignUpBinding
import com.teamttdvlp.memolang.view.Activity.base.BaseActivity
import com.teamttdvlp.memolang.view.Activity.helper.*
import com.teamttdvlp.memolang.view.Activity.viewmodel.signup.OnSignUpListener
import com.teamttdvlp.memolang.view.Activity.viewmodel.signup.SignUpViewModel
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named

class SignUpActivity : BaseActivity<ActivitySignUpBinding, SignUpViewModel>() {

    private var signUpAnimatorSet : AnimatorSet = AnimatorSet()

    private var signUpSuccessAnimatorSet : AnimatorSet = AnimatorSet()

    private var signUpFailedAnimatorSet : AnimatorSet = AnimatorSet()

    private var checkInfoAgainAnimatorSet : AnimatorSet = AnimatorSet()

    private var vibrate : Animation? = null

    private val redBorderRoundBackground : Drawable? by lazy {
        AppCompatResources.getDrawable(this, R.drawable.round_white_background_with_red_border)
    }

    private val normalBorderRoundBackground : Drawable? by lazy {
        AppCompatResources.getDrawable(this, R.drawable.round_white_background_with_border)
    }

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
            checkInfoAgainAnimatorSet.start()
         }

        edtEmail.setOnFocusChangeListener { _, isFocus ->
            if (isFocus and txtErrorEmail.isVisible) {
                txtErrorEmail.hide()
                edtEmail.background = normalBorderRoundBackground
            }
        }

        edtPassword.setOnFocusChangeListener { _, isFocus ->
            if (isFocus and txtErrorPassword.isVisible) {
                txtErrorPassword.hide()
                edtPassword.background = normalBorderRoundBackground
            }
        }

        edtReenterPassword.setOnFocusChangeListener { _, isFocus ->
             if (isFocus and txtErrorReenterPassword.isVisible) {
                 txtErrorReenterPassword.hide()
                 edtReenterPassword.background = normalBorderRoundBackground
             }
         }
    }}

    /**
     * This variable has the same function with $signInFailed variable in AuthActivity
     * @see AuthActivity.signInFailed
     */
    private var signUpFailed = false
    override fun addEventsListener() { dataBinding.apply {
        viewModel.setOnSignUpListener(object : OnSignUpListener {
            override fun onSuccess(user: FirebaseUser) {
                signUpFailed = false
                signUpSuccessAnimatorSet.start()
            }

            override fun onFailed(ex: Exception?) {
                signUpFailed = true
            }
        })
    }}

    private fun signUp () {
        dataBinding.apply {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            val reEnterPassword = edtReenterPassword.text.toString()

            if ( ! isInformationValid (email, password, reEnterPassword)) return

            signUpAnimatorSet.start()
            try {
                viewModel.signUp(email, password)
            } catch (ex : Exception) {
                quickToast("Error happened. Please check again")
            }
        }
    }

    private fun isInformationValid(email : String, password : String, reEnteredPassword : String) : Boolean{
        dataBinding.apply {
            var isValid = true
            if (email == "") {
                txtErrorEmail.appear()
                edtEmail.startAnimation(vibrate)
                edtEmail.background = redBorderRoundBackground
                isValid = false
            }

            if (password == "") {
                txtErrorPassword.appear()
                edtPassword.startAnimation(vibrate)
                edtPassword.background = redBorderRoundBackground
                isValid = false
            }

            if (password.trim() != reEnteredPassword.trim()) {
                txtErrorReenterPassword.appear()
                edtReenterPassword.startAnimation(vibrate)
                edtReenterPassword.background = redBorderRoundBackground
                isValid = false
            }

            return isValid
        }
    }


    /**
     * Code block injected by Dagger
     */
    // Check if the first times finishes rotate animation
    private var theFirstTimesFinishRotateAnimation = false
    @Inject
    fun initSignUpAnimations(
        @Named("RotateForever") progressBarRotate : Animation,
        @Named("FromNothingToNormalSize") progressBarAppear : Animator,
        @Named("FromNormalSizeToNothing") viewgroupSignUpDisappear : Animator
    ) { dataBinding.apply {
        progressBarRotate.addAnimationLister(
            onRepeat = {
                // #signUpFailed is revalued by SignUpListener.OnFailed(Exception) of ViewModel which is created above
                if (signUpFailed) {
                    theFirstTimesFinishRotateAnimation = !theFirstTimesFinishRotateAnimation
                    if (theFirstTimesFinishRotateAnimation) {
                        signUpFailedAnimatorSet.start()
                    }
                }
            }
        )

        imgSignUpProgressBar.startAnimation(progressBarRotate)

        progressBarAppear.apply {
            setTarget(viewgroupImgSignUpProgressBar)
            addListener (onStart = {
                viewgroupImgSignUpProgressBar.appear()
            })
        }

        viewgroupSignUpDisappear.apply {
            setTarget(viewgroupSignUp)
            addListener(onEnd = {
                viewgroupSignUp.disappear()
            })
        }

        signUpAnimatorSet.play(viewgroupSignUpDisappear).before(progressBarAppear)

    }}


    @Inject
    fun initSignUpSuccessAnimations(
        @Named("FromNothingToNormalSize") viewgroupSignUpSuccessAppear : Animator,
        @Named("FromNormalSizeToNothing") progressBarDisappear : Animator
    ) { dataBinding.apply {

        viewgroupSignUpSuccessAppear.apply {
            setTarget(viewgroupSignUpSuccessfully)
            addListener (onStart = {
                viewgroupSignUpSuccessfully.alpha = 1f
                viewgroupSignUpSuccessfully.appear()
            })
        }

        progressBarDisappear.apply {
            setTarget(viewgroupImgSignUpProgressBar)
            addListener(onEnd = {
                viewgroupImgSignUpProgressBar.disappear()
            })
        }

        signUpSuccessAnimatorSet.play(progressBarDisappear).before(viewgroupSignUpSuccessAppear)

    }}

    @Inject
    fun initSignUpFailedAnimations(
        @Named("FromNothingToNormalSize") viewgroupSignUpFailAppear : Animator,
        @Named("FromNormalSizeToNothing") progressBarDisappear : Animator
    ) { dataBinding.apply {

        viewgroupSignUpFailAppear.apply {
            setTarget(viewgroupSignUpFail)
            addListener (onStart = {
                viewgroupSignUpFail.alpha = 1f
                viewgroupSignUpFail.appear()
            })
        }

        progressBarDisappear.apply {
            setTarget(viewgroupImgSignUpProgressBar)
            addListener(onEnd = {
                viewgroupImgSignUpProgressBar.disappear()
            })
        }

        signUpFailedAnimatorSet.play(progressBarDisappear).before(viewgroupSignUpFailAppear)

    }}

    @Inject
    fun initCheckInfoAgainAnimations(
        @Named("Disappear100percents") groupSignUpFailedDisappear : Animator,
        @Named("Appear100percents") groupSignUpAppear : Animator
    ) { dataBinding.apply {
        groupSignUpFailedDisappear.apply {
            setTarget(viewgroupSignUpFail)
            addListener (onEnd = {
                viewgroupSignUpFail.disappear()
            })
        }

        groupSignUpAppear.apply {
            setTarget(viewgroupSignUp)
            addListener (onStart = {
                viewgroupSignUp.scaleX = 1f
                viewgroupSignUp.scaleY = 1f
                viewgroupSignUp.appear()
            })
        }
        checkInfoAgainAnimatorSet.play(groupSignUpFailedDisappear).before(groupSignUpAppear)
    }}

    @Inject
    fun initVibrateAnimation (@Named("Vibrate") vibrate : Animation) {
        this.vibrate = vibrate
    }
}


package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.graphics.drawable.Drawable
import android.view.animation.Animation
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseUser
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivitySignUpBinding
import com.teamttdvlp.memolang.view.activity.base.BaseActivity
import com.teamttdvlp.memolang.view.activity.helper.*
import com.teamttdvlp.memolang.viewmodel.signup.OnSignUpListener
import com.teamttdvlp.memolang.viewmodel.signup.SignUpManager
import com.teamttdvlp.memolang.viewmodel.signup.SignUpViewModel
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

    lateinit var signUpManager : SignUpManager
    @Inject set

    override fun getLayoutId(): Int = R.layout.activity_sign_up

    override fun takeViewModel(): SignUpViewModel = getActivityViewModel() {
        SignUpViewModel(signUpManager)
    }

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
                edtPassword.background = normalBorderRoundBackground
                // Do this to make #txtErrorPassword have only 1 line, to make UI looks nice and have no flaw
                txtErrorPassword.text = "Make password only have 1 line"
                txtErrorPassword.hide()
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
                viewModel.clearFirebaseAuthCurrentUser()
            }

            override fun onFailed(ex: Exception?) {
                signUpFailed = true
            }
        })
    }}


    /**
     * Support function
     */
    private fun signUp () { dataBinding.apply {
        signUpFailed = false

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
    }}

    private fun isAnInfoValid (info : String, inputField : EditText, errorText : TextView,
                               vararg conditions : (info : String) -> Boolean) : Boolean{
        var isValid = true
        for (condition in conditions) {
            isValid = condition(info)
            if (!isValid) {
                errorText.appear()
                inputField.startAnimation(vibrate)
                inputField.background = redBorderRoundBackground
                break
            } else {
                // The errortext is visible means that the conditions has been met but the field is still alerted
                if (errorText.isVisible) {
                    inputField.background = normalBorderRoundBackground
                    errorText.text = ""
                    errorText.disappear()
                }
            }
        }

        return isValid
    }

    private fun isInformationValid(email : String, password : String, reEnteredPassword : String) : Boolean{
        dataBinding.apply {
            val isEmailValid = isAnInfoValid(email, edtEmail, txtErrorEmail,
                {
                    val isNotEmpty = it != ""
                    if (!isNotEmpty) txtErrorEmail.text = "Please input your email"
                    return@isAnInfoValid isNotEmpty
                })

            val isPasswordValid = isAnInfoValid(password, edtPassword, txtErrorPassword,
                {
                    val isNotEmpty = it != ""
                    if (!isNotEmpty) txtErrorPassword.text = "Please input your password"
                    return@isAnInfoValid isNotEmpty
                },
                {
                    val isValid = (viewModel.isPasswordValid(password))
                    if (!isValid) txtErrorPassword.text = "- Password must have at least 6 characters\n- Can not contain special character like !, @, #, ..."
                    return@isAnInfoValid isValid
                })

            val isReenterPasswordValid = isAnInfoValid(reEnteredPassword, edtReenterPassword, txtErrorReenterPassword,
                 {
                    val isNotEmpty = it != ""
                    if (!isNotEmpty) txtErrorReenterPassword.text = "Please input your password again"
                     return@isAnInfoValid isNotEmpty
                 },
                 {
                    val isMatch = (password.trim() == reEnteredPassword.trim())
                     if (!isMatch) txtErrorReenterPassword.text = "Your password does not match"
                     return@isAnInfoValid isMatch
                 })

            return (isEmailValid) and  (isPasswordValid) and (isReenterPasswordValid)
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


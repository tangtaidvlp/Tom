package com.teamttdvlp.memolang.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivityAuthBinding
import com.teamttdvlp.memolang.model.model.User
import com.teamttdvlp.memolang.model.sqlite.repository.FlashcardRepository
import com.teamttdvlp.memolang.model.sqlite.repository.UserRepository
import com.teamttdvlp.memolang.view.activity.base.BaseActivity
import com.teamttdvlp.memolang.view.activity.helper.*
import com.teamttdvlp.memolang.viewmodel.auth.AuthActivityViewModel
import com.teamttdvlp.memolang.viewmodel.auth.AuthManager
import com.teamttdvlp.memolang.viewmodel.auth.OnSignInListener
import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager
import com.teamttdvlp.memolang.viewmodel.reusable.OnlineUserDBManager
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named

const val DELAY_TIME_BEFORE_CHECK_INFO_AGAIN = 1500L

const val DELAY_TIME_BEFORE_NAVIGATE_TO_MENU = 500L


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

    lateinit var userRepository : UserRepository
    @Inject set

    lateinit var authManager : AuthManager
    @Inject set

    lateinit var firebaseAuth : FirebaseAuth
    @Inject set

    lateinit var onlineUserDBManager: OnlineUserDBManager
    @Inject set

    lateinit var onlineFlashcardDBManager: OnlineFlashcardDBManager
    @Inject set

    lateinit var flashcardRepository: FlashcardRepository
    @Inject set


    // ================== BASE ACTIVITY OVERRIDE FUNCTION ================


    override fun getLayoutId(): Int = R.layout.activity_auth

    override fun takeViewModel() : AuthActivityViewModel = getActivityViewModel  {
        AuthActivityViewModel(onlineUserDBManager,
            onlineFlashcardDBManager,
            firebaseAuth,
            authManager,
            userRepository,
            flashcardRepository,
            application)
    }

    override fun addViewEvents() { dataBinding.apply {

        btnSignUp.setOnClickListener {
            quickStartActivity(SignUpActivity::class.java)
        }

        btnSignIn.setOnClickListener {
            val id = edtId.text.toString()
            val password = edtPassword.text.toString()
            if (!isInformationValid(id, password)) return@setOnClickListener
            signInFailed = false
            hideVirtualKeyboard()
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
     * This var make sure that loading animation run at least 1 times before running the "signInFailedAnimatorSet"
     * ("signInFailedAnimatorSet"  is called in INITSIGNINANIMATIONS() FUNCTION below)
     */
    private var signInFailed = false

    override fun addEventsListener() {
        super.addEventsListener()
        viewModel.setOnSignInListener(object : OnSignInListener {
            override fun onSuccess(user: FirebaseUser) {
                 startSignInSuccessAnimation {
                     onSignInSuccessful()
                 }
            }

            override fun onFailed(ex: Exception?) {
                signInFailed = true
                quickLog("Error " + ex?.message.toString())
                //This is called in RotateForever animation below (in #INITSIGNINANIMATION() FUNCTION)
                startSignInFailedAnimation {
                    checkInfoAgainAnimatorSet.start()
                }
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

    override fun overrideExitAnim() {
        overridePendingTransition(R.anim.from_right_to_centre, R.anim.nothing)
    }



    // ================== ACTIVITY LIFECYCLE FUNCTION ============--



    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.userHadSignedInBefore()) {
            onSignInSuccessful()
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




    // ================== SERVICE FUNCTION ==================



    /**
     * @param user : User can be taken by checking if the user is the first times has signed in to their DEVICE IN FUNCTION #onStart above
     * So to not make this step do twice, we simply cache the user then pass TO THIS FUNCTION
     */
    private fun setUpInfoThenNavigateToMenu(user : User.MockUser? = null) {
        setUpSingletonUserInfo(user)
        navigateToMenu()
    }

    private fun navigateToMenu () {
        quickStartActivity(MenuActivity::class.java)
        finish()
    }

    /**
     *  Alfter Memolang is reset by some action like being overlayed by some other app (It will reload to AuthActivity)
     *  so those function setUpSingleUserInfo can be called again (while it had been created before, app is not
     *  completely deleted from backstack so Static Java Object still be there, check if it had been create to
     *  avoid throwing error from User
     *  @see User.createInstance
     */

    private fun setUpSingletonUserInfo (user: User.MockUser?) {
        viewModel.apply {
            if (getSingletonUser() == null)
                createSingletonUser(user!!.id, user.motherLanguage, user.targetLanguage)
            writeUserToOfflineDatabase(getSingletonUser()!!)
            saveSignedInStatus()
        }
    }

    private fun onSignInSuccessful () {
        // This make checking is that the first times user has signed in with the more quickly way
        // The first times we check, we will store signed in status in SharePreference for the next times
        // we don't need to check if the first times user has signed in "Onlinely"
        // but we only need to check in SharePreference
        if (!viewModel.checkIfTheFirstTimesUserSignedInToSystem_OFFLINE()) {
            viewModel.checkIfTheFirstTimesUserSignedInToSystem_ONLINE { onlineDBUser, isTheFirstTimes ->
                val isNotTheFirstTimes_And_UserCannotBeNull = !isTheFirstTimes
                if (isTheFirstTimes) {
                    if (viewModel.getSingletonUser() == null)
                        viewModel.createSingletonUser(viewModel.getCurrentUser().uid, "", "")
                    quickStartActivity(SetUpAccountActivity::class.java)
                    finish()
                } else if (isNotTheFirstTimes_And_UserCannotBeNull) {
                    setUpInfoThenNavigateToMenu(onlineDBUser)
                }
                viewModel.synchronizeDataOnlineAndOffline { isSuccess, ex ->
                    if (!isSuccess) {
                        quickToast("Synchronized data failed")
                        quickLog("Synchronized failed. Error happened")
                        ex?.printStackTrace()
                    } else {
                        quickToast("Synchronized data success")
                    }
                }
            }
        } else {
            viewModel.triggerGetTheOnlyUserInOfflineDB {
                setUpInfoThenNavigateToMenu(User.MockUser(it!!.id, it.motherLanguage, it.targetLanguage))
            }
        }
    }

    private fun startSignInSuccessAnimation (onEnd : () -> Unit) {
        signInSuccessAnimatorSet.addListener (onEnd = {
            onEnd()
        })
        signInSuccessAnimatorSet.start()
    }

    private fun startSignInFailedAnimation (onEnd : () -> Unit) {
        signInFailedAnimatorSet.addListener(onEnd = {
            onEnd()
        })
        signInFailedAnimatorSet.start()
    }


    private fun resetProgressBarState() { dataBinding.apply {
        viewgroupImgSignInProgressBar.appear()
        viewgroupImgSignInProgressBar.alpha = 1f
        viewgroupLoginFailed.alpha = 0f
        viewgroupLoginSuccess.alpha = 0f
        txtProcessing.alpha = 1f
        txtSuccessful.alpha = 0f
        txtFailed.alpha = 0f
        clfProgressbar.transToStaticMode()
    }}

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



    // =============================== INIT ANIMATIONS FUNCTION ============================



    @Inject
    fun initSignInAnimations(
        @Named("FromNothingToNormalSize") progressBarAppear : Animator,
        @Named("Disappear100percents") viewgroupSignInDisappear : Animator) { dataBinding.apply {

        progressBarAppear.apply {
            setTarget(viewgroupImgSignInProgressBar)
            addListener (onStart = {
                resetProgressBarState()
                clfProgressbar.transToActionMode()
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
    fun initCheckInfoAgainAnimations(
        @Named("Disappear100percents") viewgroupProgressBarDisappear : Animator,
        @Named("Appear100percents") groupSignInWidgetsAppear : Animator) { dataBinding.apply {

        viewgroupProgressBarDisappear.apply {
            setTarget(viewgroupImgSignInProgressBar)
            addListener (onEnd = {
                viewgroupImgSignInProgressBar.disappear()
            })
        }

        groupSignInWidgetsAppear.apply {
            setTarget(viewgroupSignInWidgets)
            addListener (onStart = {
                viewgroupSignInWidgets.appear()
            })
        }
        checkInfoAgainAnimatorSet.play(viewgroupProgressBarDisappear).before(groupSignInWidgetsAppear)
    }}

    @Inject
    fun initSignInFailedAnimations(
        @Named("Appear100percents") viewgroupFailedAppear : Animator,
        @Named("Appear100percents") textFailedAppear : Animator,
        @Named("Disappear100percents") textProcessingDisappear : Animator,
        @Named("Waiting") delayTimeAnimator : Animator) { dataBinding.apply {

        viewgroupFailedAppear.setTarget(viewgroupLoginFailed)
        textFailedAppear.setTarget(txtFailed)
        textProcessingDisappear.setTarget(txtProcessing)

        // This animator delays time when to start to MenuActivity
        // This make user can see the notification that they've signed in sucessfully
        // Just make UI better
        delayTimeAnimator.setTarget(viewgroupImgSignInProgressBar)
        delayTimeAnimator.duration = DELAY_TIME_BEFORE_CHECK_INFO_AGAIN

        val failedAnimatorSet = AnimatorSet().apply {
            play(viewgroupFailedAppear).with(textProcessingDisappear)
                .before(textFailedAppear)
        }

        val navigateAnimatorSet = AnimatorSet().apply {
            play(failedAnimatorSet).before(delayTimeAnimator)
        }

        signInFailedAnimatorSet
            .play(clfProgressbar.getEndAnim())
            .before(navigateAnimatorSet)

    }}

    @Inject
    fun initSignInSuccessAnimations(
        @Named("Appear100percents") viewgroupSuccessAppear : Animator,
        @Named("Appear100percents") textSuccessAppear : Animator,
        @Named("Disappear100percents") textProcessingDisappear : Animator,
        @Named("Waiting") delayTimeAnimator : Animator) { dataBinding.apply {

        viewgroupSuccessAppear.setTarget(viewgroupLoginSuccess)
        textSuccessAppear.setTarget(txtSuccessful)
        textProcessingDisappear.setTarget(txtProcessing)

        // This animator delays time when to start to MenuActivity
        // This make user can see the notification that they've signed in sucessfully
        // Just make UI better
        delayTimeAnimator.setTarget(viewgroupImgSignInProgressBar)
        delayTimeAnimator.duration = DELAY_TIME_BEFORE_NAVIGATE_TO_MENU

        val successAnimatorSet = AnimatorSet().apply {
            play(viewgroupSuccessAppear).with(textProcessingDisappear)
           .before(textSuccessAppear)
        }

        val navigateAnimatorSet = AnimatorSet().apply {
            play(successAnimatorSet).before(delayTimeAnimator)
        }

        signInSuccessAnimatorSet
            .play(clfProgressbar.getEndAnim())
            .before(navigateAnimatorSet)

    }}


    @Inject
    fun initVibrateAnimation (@Named("Vibrate") vibrate : Animation) {
        this.vibrate = vibrate
    }

}


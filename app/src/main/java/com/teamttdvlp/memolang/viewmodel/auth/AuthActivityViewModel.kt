package com.teamttdvlp.memolang.viewmodel.auth

import android.app.Activity
import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.teamttdvlp.memolang.model.model.User
import com.teamttdvlp.memolang.model.sqlite.converter.UserConverter
import com.teamttdvlp.memolang.model.sqlite.repository.UserRepository
import com.teamttdvlp.memolang.model.sqlite.entity.UserEntity
import com.teamttdvlp.memolang.model.sqlite.repository.FlashcardRepository
import com.teamttdvlp.memolang.view.activity.base.BaseAndroidViewModel
import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager
import com.teamttdvlp.memolang.viewmodel.reusable.OnlineUserDBManager

/**
 * @see com.teamttdvlp.memolang.view.activity.AuthActivity
 */

const val FIRST_TIMES_SIGNED_IN = "first_times_signed_in"
const val SIGN_IN_INFO = "signed_in_info"

class AuthActivityViewModel (var onlineUserDBManager: OnlineUserDBManager,
                             var onlineFlashcardDBManager: OnlineFlashcardDBManager,
                             var firebaseAuth: FirebaseAuth,
                             var authManager: AuthManager,
                             var userRepository: UserRepository,
                             var flashcardRepository: FlashcardRepository,
                             var app : Application) : BaseAndroidViewModel(app) {


    fun getGoogleSignInIntent () : Intent {
       return authManager.getGoogleSignInIntent()
    }

    fun signInWithGoogle(acct: GoogleSignInAccount) {
        authManager.signInWithGoogle(acct)
    }

    fun requestFacebookSignIn(activity : Activity) {
        authManager.requestFacebookSignIn(activity)
    }

    fun signInWithFacebook(token: AccessToken) {
        authManager.signInWithFacebook(token)
    }

    fun signInWithEmailAndPassword (email : String, password : String) {
        authManager.signInWithEmailAndPassword(email, password)
    }

    fun setOnSignInListener (onSignInListener: OnSignInListener) {
        authManager.setOnSignInListener(onSignInListener)
    }

    fun userHadSignedInBefore () : Boolean {
        return authManager.userHasSignedIn()
    }

    fun triggerGetTheOnlyUserInOfflineDB(onGetSuccess : (UserEntity?) -> Unit) {
        userRepository.triggerGetUser(authManager.getCurrentUser().uid, onGetSuccess)
    }

    fun getUserFromOnlineDatabase (onGetSuccess : (User.MockUser?) -> Unit) {
        onlineUserDBManager.getUserById(authManager.getCurrentUser().uid, onGetSuccess)
    }

    fun checkIfTheFirstTimesUserSignedInToSystem_ONLINE (onGetResult : (User.MockUser?, Boolean) -> Unit ) {
        getUserFromOnlineDatabase { mockUser ->
            val isTheFirstTimes = (mockUser == null)
            onGetResult.invoke(mockUser, isTheFirstTimes)
        }
    }

    fun checkIfTheFirstTimesUserSignedInToSystem_OFFLINE () : Boolean{
        val sharePref = app.getSharedPreferences(SIGN_IN_INFO, MODE_PRIVATE)
        val isTheFirstTimesSignedIn = sharePref.getBoolean(FIRST_TIMES_SIGNED_IN, false)
        return isTheFirstTimesSignedIn
    }

    fun saveSignedInStatus () {
        val sharePref = app.getSharedPreferences(SIGN_IN_INFO, MODE_PRIVATE)
        sharePref.edit().apply {
            putBoolean(FIRST_TIMES_SIGNED_IN, true)
            apply()
        }
    }

    fun writeUserToOfflineDatabase (user : User) {
        userRepository.insertUser(UserConverter.toUserEntity(user))
    }

    fun getCurrentUser () : FirebaseUser {
        return firebaseAuth.currentUser!!
    }

    fun synchronizeDataOnlineAndOffline (onSynchronizeListener : (Boolean, Exception?) -> Unit) {
        // ONline
        onlineFlashcardDBManager.readAllFlashcard(getSingletonUser()!!.id) { allOnlineFlashcards, exception ->
            if (allOnlineFlashcards != null) {
                // OFFline
                flashcardRepository.getAllFlashcards { allOfflineFlashcards ->
                    val userFirstSignInToNewDevice = (allOnlineFlashcards.size != 0) and (allOfflineFlashcards.size == 0)
                    if (userFirstSignInToNewDevice) {
                        flashcardRepository.insertFlashcards(allOnlineFlashcards, onSynchronizeListener)
                    }
                }
            }

        }
    }
}
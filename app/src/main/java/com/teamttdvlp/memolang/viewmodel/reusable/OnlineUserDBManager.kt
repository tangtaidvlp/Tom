package com.teamttdvlp.memolang.viewmodel.reusable

import com.google.firebase.firestore.FirebaseFirestore
import com.teamttdvlp.memolang.model.model.User
import com.teamttdvlp.memolang.view.activity.helper.quickLog
import javax.inject.Inject

class OnlineUserDBManager @Inject constructor(var firestoreRef : FirebaseFirestore) {

    private val USER_INFO = "user_info"

    /**
     * Although two function #writeUserInfo(User) and #createUser(User) do exactly the same thing but i
     * do separately them for clean code, for easy to understand what they actually do
     *
     * writeUserInfo : set up user info such as Mother Language and Target Language, this is completed in SetUpAccountActivity
     * createUser : createUser in firestore when they sign up successfully
     */

    fun writeUserInfo (user : User) {
        firestoreRef.collection(user.id).document(USER_INFO).set(user).addOnCompleteListener {
            if (it.isComplete) {
                quickLog("Set User Success")
            } else {
                quickLog("Set User Failed")
            }
        }
    }

    fun createUser (user : User) {
        writeUserInfo(user)
    }

    fun getUserById (uid : String, onGetUser : (User.MockUser?) -> Unit ) {
        firestoreRef.collection(uid).document(USER_INFO).get().addOnCompleteListener {
            val mockUser = it.result?.toObject(User.MockUser::class.java)
            onGetUser.invoke(mockUser)
        }
    }

}
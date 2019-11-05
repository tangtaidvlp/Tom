package com.teamttdvlp.memolang.view.activity

import com.google.firebase.auth.FirebaseAuth
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.databinding.ActivitySetUpAccountBinding
import com.teamttdvlp.memolang.model.sqlite.repository.UserRepository
import com.teamttdvlp.memolang.view.base.BaseActivity
import com.teamttdvlp.memolang.view.helper.getActivityViewModel
import com.teamttdvlp.memolang.view.helper.quickStartActivity
import com.teamttdvlp.memolang.viewmodel.reusable.OnlineUserDBManager
import com.teamttdvlp.memolang.viewmodel.setup_account.SetUpAccountActivityViewModel
import javax.inject.Inject

class SetUpAccountActivity : BaseActivity<ActivitySetUpAccountBinding, SetUpAccountActivityViewModel>() {

    override fun getLayoutId(): Int  = R.layout.activity_set_up_account

    lateinit var firebaseAuth : FirebaseAuth
    @Inject set

    lateinit var userRepository: UserRepository
    @Inject set

    lateinit var onlineUserDBManager: OnlineUserDBManager
    @Inject set

    override fun takeViewModel(): SetUpAccountActivityViewModel = getActivityViewModel {
        SetUpAccountActivityViewModel(firebaseAuth, userRepository, onlineUserDBManager, application)
    }

    override fun addViewEvents() { dataBinding.apply {
        btnDone.setOnClickListener {
            viewModel.apply {
                val user = getSingletonUser()
                user!!.motherLanguage = txtMotherLanguage.text.toString()
                user.targetLanguage = txtTargetLanguage.text.toString()
                writeUserToOfflineDatabase(user)
                writeUserToOnlineDatabase(user)
                saveSignedInStatus()
                quickStartActivity(MenuActivity::class.java)
                finish()
            }
        }
    }}
}

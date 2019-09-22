package com.teamttdvlp.memolang.model.sqlite.repository

import android.os.AsyncTask
import com.teamttdvlp.memolang.model.sqlite.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.model.sqlite.dao.UserDAO
import com.teamttdvlp.memolang.model.sqlite.entity.UserEntity
import javax.inject.Inject

class UserRepository(database: MemoLangSqliteDataBase) {

    private var userDAO : UserDAO = database.getUserDAO()

    fun insertUser (userEntity: UserEntity) {
        InsertUserTask().execute(userEntity)
    }

    fun triggerGetUser (userId : String, onGetUser : (UserEntity?) -> Unit) {
        GetUserTask().executeWithListener(userId, onGetUser)
    }

    private inner class InsertUserTask : AsyncTask<UserEntity, Unit, Unit>() {
        override fun doInBackground(vararg params: UserEntity?) {
            userDAO.insertUser(params[0]!!)
        }
    }

    private inner class GetUserTask : AsyncTask<String, Unit, Unit>() {

        lateinit var onTaskSuccess : (UserEntity?) -> Unit

        override fun doInBackground(vararg params: String) {
            val userId = params[0]
            onTaskSuccess.invoke(userDAO.getUserById(userId))
        }

        fun executeWithListener (id : String, onTaskSuccess : (UserEntity?) -> Unit) {
            this.onTaskSuccess = onTaskSuccess
            execute(id)
        }
    }


}
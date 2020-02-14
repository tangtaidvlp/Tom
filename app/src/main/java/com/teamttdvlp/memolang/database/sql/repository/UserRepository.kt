package com.teamttdvlp.memolang.database.sql.repository

import android.os.AsyncTask
import com.teamttdvlp.memolang.database.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.database.sql.dao.UserDAO
import com.teamttdvlp.memolang.database.sql.entity.user.UserEntity

class UserRepository(database: MemoLangSqliteDataBase) {

    private var userDAO : UserDAO = database.getUserDAO()

    fun insertUser (userEntity: UserEntity) {
        InsertUserTask().execute(userEntity)
    }

    // INSERT STRAGEGY = REPLACE so update is similar to insert
    fun updateUser (userEntity: UserEntity) {
        insertUser(userEntity)
    }

    fun triggerGetUser (onGetUser : (UserEntity?) -> Unit) {
        GetUserTask().executeWithListener(onGetUser)
    }

    private inner class InsertUserTask : AsyncTask<UserEntity, Unit, Unit>() {
        override fun doInBackground(vararg params: UserEntity?) {
            userDAO.insertUser(params[0]!!)
        }
    }

    private inner class GetUserTask : AsyncTask<String, Unit, UserEntity>() {

        lateinit var onTaskSuccess : (UserEntity?) -> Unit

        override fun doInBackground(vararg params: String) : UserEntity {
            return userDAO.getUser()!!
        }

        override fun onPostExecute(result: UserEntity?) {
            super.onPostExecute(result)
            onTaskSuccess.invoke (result)
        }

        fun executeWithListener (onTaskSuccess : (UserEntity?) -> Unit) {
            this.onTaskSuccess = onTaskSuccess
            execute()
        }
    }


}
package com.teamttdvlp.memolang.model.repository

import android.os.AsyncTask
import com.teamttdvlp.memolang.data.sql.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.data.sql.dao.UserDAO
import com.teamttdvlp.memolang.data.model.entity.user.User

class UserRepos(database: MemoLangSqliteDataBase) {

    private var userDAO : UserDAO = database.getUserDAO()

    fun insertUser (user: User) {
        InsertUserTask().execute(user)
    }

    // INSERT STRAGEGY = REPLACE so update is similar to insert
    fun updateUser (user: User) {
        insertUser(user)
    }

    fun removeFlashcardSet (setName : String) {

    }

    fun triggerGetUser (onGetUser : (User?) -> Unit) {
        GetUserTask().executeWithListener(onGetUser)
    }

    private inner class InsertUserTask : AsyncTask<User, Unit, Unit>() {
        override fun doInBackground(vararg params: User?) {
            userDAO.insertUser(params[0]!!)
        }
    }

    private inner class GetUserTask : AsyncTask<String, Unit, User>() {

        lateinit var onTaskSuccess : (User?) -> Unit

        override fun doInBackground(vararg params: String) : User {
            return userDAO.getUser()!!
        }

        override fun onPostExecute(result: User?) {
            super.onPostExecute (result)
            onTaskSuccess.invoke (result)
        }

        fun executeWithListener (onTaskSuccess : (User?) -> Unit) {
            this.onTaskSuccess = onTaskSuccess
            execute()
        }
    }


}
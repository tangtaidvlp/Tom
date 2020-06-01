package com.teamttdvlp.memolang.data.sql.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.teamttdvlp.memolang.data.model.entity.user.User

@Dao
abstract class UserDAO {
        @Query("SELECT * FROM User")
        abstract fun getUser() : User?

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        abstract fun insertUser (user: User)
}
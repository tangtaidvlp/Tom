package com.teamttdvlp.memolang.database.sql.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.teamttdvlp.memolang.database.sql.entity.user.UserEntity

@Dao
abstract class UserDAO {
        @Query("SELECT * FROM User")
        abstract fun getUser() : UserEntity?

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        abstract fun insertUser (user: UserEntity)
}
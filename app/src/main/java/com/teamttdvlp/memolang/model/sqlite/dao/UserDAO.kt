package com.teamttdvlp.memolang.model.sqlite.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.teamttdvlp.memolang.model.sqlite.entity.UserEntity

@Dao
abstract class UserDAO {
        @Query("SELECT * FROM User WHERE id == :id")
        abstract fun geUserById(id : String) : UserEntity

        @Insert
        abstract fun insertUser (user: UserEntity)
}
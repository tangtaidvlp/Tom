package com.teamttdvlp.memolang.model.sqlite.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.teamttdvlp.memolang.model.sqlite.entity.UserEntity

@Dao
abstract class UserDAO {
        @Query("SELECT * FROM User WHERE id == :id")
        abstract fun getUserById(id : String) : UserEntity?

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        abstract fun insertUser (user: UserEntity)
}
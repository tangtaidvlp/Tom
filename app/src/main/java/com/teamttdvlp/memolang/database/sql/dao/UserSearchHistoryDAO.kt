package com.teamttdvlp.memolang.database.sql.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.teamttdvlp.memolang.database.sql.entity.UserSearchHistoryHolderEntity

@Dao
abstract class UserSearchHistoryDAO  {
        @Query ("SELECT * FROM UserSearchHistory")
        abstract fun getAllRecentSearchFlashcards () : List<UserSearchHistoryHolderEntity>

        @Insert
        abstract fun insert (userSearchHistoryHolderEntity: UserSearchHistoryHolderEntity)
}
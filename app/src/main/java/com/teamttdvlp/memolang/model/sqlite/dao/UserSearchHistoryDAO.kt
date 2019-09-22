package com.teamttdvlp.memolang.model.sqlite.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.teamttdvlp.memolang.model.sqlite.entity.UserSearchHistoryHolderEntity

@Dao
abstract class UserSearchHistoryDAO  {
        @Query ("SELECT * FROM UserSearchHistory")
        abstract fun getAllRecentSearchFlashcards () : List<UserSearchHistoryHolderEntity>

        @Insert
        abstract fun insert (userSearchHistoryHolderEntity: UserSearchHistoryHolderEntity)
}
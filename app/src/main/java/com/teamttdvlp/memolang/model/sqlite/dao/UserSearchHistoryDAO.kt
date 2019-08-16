package com.teamttdvlp.memolang.model.sqlite.dao

import androidx.room.Dao
import androidx.room.Query
import com.teamttdvlp.memolang.model.sqlite.entity.UserSearchHistoryEntity

@Dao
abstract class UserSearchHistoryDAO  {

        @Query ("SELECT * FROM UserSearchHistory WHERE searchedAt >= :time LIMIT :count")
        abstract fun getHistoryBySearchedTimeFrom (time : Long, count : Int) : List<UserSearchHistoryEntity>
}
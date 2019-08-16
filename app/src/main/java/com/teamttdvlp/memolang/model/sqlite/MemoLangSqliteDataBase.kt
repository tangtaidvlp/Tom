package com.teamttdvlp.memolang.model.sqlite

import androidx.room.Database
import androidx.room.RoomDatabase
import com.teamttdvlp.memolang.model.sqlite.dao.MemoCardDAO
import com.teamttdvlp.memolang.model.sqlite.dao.UserDAO
import com.teamttdvlp.memolang.model.sqlite.dao.UserSearchHistoryDAO
import com.teamttdvlp.memolang.model.sqlite.entity.MemoCardEntity
import com.teamttdvlp.memolang.model.sqlite.entity.UserEntity
import com.teamttdvlp.memolang.model.sqlite.entity.UserSearchHistoryEntity

@Database(entities = [UserEntity::class, MemoCardEntity::class, UserSearchHistoryEntity::class], version = 1)
abstract class MemoLangSqliteDataBase : RoomDatabase() {

    companion object {
        // create migration here
    }
    abstract fun getUserDAO () : UserDAO
    abstract fun getUserSearchHistoryDAO () : UserSearchHistoryDAO
    abstract fun getMemoCardDAO () : MemoCardDAO
}
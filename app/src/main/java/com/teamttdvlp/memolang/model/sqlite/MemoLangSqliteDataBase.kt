package com.teamttdvlp.memolang.model.sqlite

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.teamttdvlp.memolang.model.sqlite.dao.FlashcardDAO
import com.teamttdvlp.memolang.model.sqlite.dao.UserDAO
import com.teamttdvlp.memolang.model.sqlite.dao.UserSearchHistoryDAO
import com.teamttdvlp.memolang.model.sqlite.entity.FlashCardEntity
import com.teamttdvlp.memolang.model.sqlite.entity.UserEntity
import com.teamttdvlp.memolang.model.sqlite.entity.UserSearchHistoryHolderEntity

@Database(entities = [UserEntity::class, FlashCardEntity::class, UserSearchHistoryHolderEntity::class], version = 3)
abstract class MemoLangSqliteDataBase : RoomDatabase() {

    companion object {
        // create migration here
        val DB_NAME = "memolang_db"
        val MIGRATION_2_3 = object : Migration (2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE temp_user (id TEXT not null, motherLanguage TEXT not null, targetLanguage TEXT not null, PRIMARY KEY (id))")
                database.execSQL("DROP TABLE User")
                database.execSQL("ALTER TABLE temp_user RENAME TO User")
            }
        }
    }

    abstract fun getUserDAO () : UserDAO
    abstract fun getUserSearchHistoryDAO () : UserSearchHistoryDAO
    abstract fun getMemoCardDAO () : FlashcardDAO
}
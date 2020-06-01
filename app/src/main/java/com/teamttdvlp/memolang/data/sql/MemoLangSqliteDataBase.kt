package com.teamttdvlp.memolang.data.sqlimport androidx.room.Databaseimport androidx.room.RoomDatabaseimport com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcardimport com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSetimport com.teamttdvlp.memolang.data.model.entity.user.Userimport com.teamttdvlp.memolang.data.model.entity.user.UserUsingHistoryimport com.teamttdvlp.memolang.data.sql.dao.FlashcardDAOimport com.teamttdvlp.memolang.data.sql.dao.FlashcardSetDAOimport com.teamttdvlp.memolang.data.sql.dao.UserDAOimport com.teamttdvlp.memolang.data.sql.dao.UserUsingHistoryDAO@Database(entities = [User::class, Flashcard::class, FlashcardSet::class, UserUsingHistory::class], version = 1)abstract class MemoLangSqliteDataBase : RoomDatabase() {    companion object {        // create migration here        val DB_NAME = "memolang_db"    }    abstract fun getUserDAO () : UserDAO    abstract fun getUserUsingHistoryDAO () : UserUsingHistoryDAO    abstract fun getFlashcardDAO () : FlashcardDAO    abstract fun getFlashcardSetDAO (): FlashcardSetDAO}
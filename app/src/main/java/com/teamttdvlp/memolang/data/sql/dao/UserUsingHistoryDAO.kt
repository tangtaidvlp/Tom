package com.teamttdvlp.memolang.data.sql.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.teamttdvlp.memolang.data.model.entity.user.UserUsingHistory

@Dao
abstract class UserUsingHistoryDAO  {

        @Query ("SELECT * FROM user_using_history where id = 0" )
        abstract fun getUsingHistory () : UserUsingHistory

        @Insert(entity = UserUsingHistory::class, onConflict = REPLACE)
        abstract fun updateUserHistory (userUsingHistory: UserUsingHistory)

//        @Query("SELECT recentSearchedVocaList FROM user_using_history WHERE id = 0")
//        abstract fun getRecentSearchedVocaList () : ArrayList<RawVocabulary>
//
//        @Query("SELECT recentOnlineSearchedFlashcardList FROM user_using_history WHERE id = 0")
//        abstract fun getRecentOnlineSearchedFlashCardList () : ArrayList<Flashcard>
//
//        @Query("SELECT recentAddedFlashcardList FROM user_using_history WHERE id = 0")
//        abstract fun getRecentAddedFlashcardList () : ArrayList<Flashcard>

}
package com.teamttdvlp.memolang.database.sql.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.teamttdvlp.memolang.database.sql.entity.flashcard.FlashCardEntity

@Dao
abstract  class FlashcardDAO {

    @Insert(onConflict = REPLACE)
    abstract fun insertNewCard (card : FlashCardEntity) : Long

    @Insert(onConflict = REPLACE)
    abstract fun insertSetOfCards (setOfCards : ArrayList<FlashCardEntity>)

    @Delete
    abstract fun deleteCards (vararg cards : FlashCardEntity)

    @Query("DELETE FROM Flashcard WHERE id = :cardId")
    abstract fun deleteCardById (cardId  : String)

    @Query("SELECT * FROM Flashcard WHERE id IN (:ids) ORDER BY createdAt DESC")
    abstract fun getCardsByIds (ids : ArrayList<Int>) : List<FlashCardEntity>

    @Query("SELECT * FROM Flashcard" +
            " WHERE languagePair == :languagePair")
    abstract fun getAllCardByType (languagePair : String) : List<FlashCardEntity>

    @Query("SELECT * FROM Flashcard")
    abstract fun getAllCard () :  List<FlashCardEntity>

}
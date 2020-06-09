package com.teamttdvlp.memolang.data.sql.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard

@Dao
abstract class FlashcardDAO {

    @Insert(onConflict = REPLACE)
    abstract fun insertNewCard(card: Flashcard): Long

    @Insert(onConflict = REPLACE)
    abstract fun insertSetOfCards(setOfCards: ArrayList<Flashcard>)

    @Update
    abstract fun updateFlashcard(flashcard: Flashcard)

    @Delete
    abstract fun deleteCards(vararg cards: Flashcard)

    @Query("DELETE FROM flashcard WHERE id = :cardId")
    abstract fun deleteCardById(cardId: String)

    @Query("SELECT * FROM flashcard WHERE id IN (:ids)")
    abstract fun getCardsByIds(ids: ArrayList<Int>): List<Flashcard>

    @Query("SELECT * FROM flashcard")
    abstract fun getAllCard () :  List<Flashcard>

}
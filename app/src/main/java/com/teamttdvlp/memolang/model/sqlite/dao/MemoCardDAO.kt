package com.teamttdvlp.memolang.model.sqlite.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.teamttdvlp.memolang.model.model.Flashcard
import com.teamttdvlp.memolang.model.sqlite.entity.FlashCardEntity

@Dao
abstract  class FlashcardDAO {

        @Insert(onConflict = REPLACE)
        abstract fun insertNewCard (card : FlashCardEntity) : Long

        @Insert(onConflict = REPLACE)
        abstract fun insertSetOfCards (setOfCards : ArrayList<FlashCardEntity>)

        @Delete
        abstract fun deleteCard (card : FlashCardEntity)

        @Query("DELETE FROM Flashcard WHERE id = :cardId")
        abstract fun deleteCardById (cardId  : String)

        @Query("SELECT * FROM Flashcard WHERE id IN (:ids) ORDER BY createdAt DESC")
        abstract fun getCardsByIds (ids : ArrayList<Int>) : List<FlashCardEntity>

        @Query("SELECT * FROM Flashcard" +
                " WHERE kind == :kind")
        abstract fun getAllCardByType (kind : String) : List<FlashCardEntity>

        @Query("SELECT * FROM Flashcard")
        abstract fun getAllCard () :  List<FlashCardEntity>

        @Query("SELECT * FROM Flashcard" +
                " WHERE kind = :kind AND text == :text")
        abstract fun getCardByToBeTranslatedWord (kind : String, text : String) : FlashCardEntity
}
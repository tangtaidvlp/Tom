package com.teamttdvlp.memolang.data.sql.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSetWithCardList

@Dao
interface FlashcardSetDAO {

    @Query("SELECT * FROM flashcard_set")
    fun getAllFlashcardSetWithCardList () : List<FlashcardSetWithCardList>

    @Query("SELECT * FROM flashcard_set")
    fun getAllFlashcardSetWithNOCardList(): List<Deck>

    @Query("SELECT * FROM flashcard_set WHERE setName == :setName")
    fun getFlashcardSetByName (setName : String) : FlashcardSetWithCardList?

    @Delete
    fun deleteFlashcardSet(deck: Deck)

    @Insert(onConflict = REPLACE)
    fun insertFlashcardSet(deck: Deck)

}
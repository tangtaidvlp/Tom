package com.teamttdvlp.memolang.model.sqlite.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.teamttdvlp.memolang.model.sqlite.entity.MemoCardEntity

@Dao
abstract  class MemoCardDAO {

        @Insert
        abstract fun insertNewCard (card : MemoCardEntity)
        @Delete
        abstract fun deleteCard (card : MemoCardEntity)

        @Query("DELETE FROM MemoCard WHERE id = :cardId")
        abstract fun deleteCardById (cardId  : String)

        @Query("SELECT * FROM MemoCard WHERE id == :cardId")
        abstract fun getCardById (cardId: String) : MemoCardEntity

        @Query("SELECT * FROM MemoCard" +
                " WHERE kind == :kind")
        abstract fun getAllCardByType (kind : String) : List<MemoCardEntity>

        @Query("SELECT * FROM MemoCard" +
                " WHERE kind = :kind AND createdAt >= :createdTime ORDER BY  createdAt DESC LIMIT :count")
        abstract fun getCardByTypeFromCreatedTime (kind : String, createdTime : Long, count : Int) : List<MemoCardEntity>

        @Query("SELECT * FROM Memocard" +
                " WHERE kind = :kind AND toBeTranslatedWord == :toBeTranslatedWord")
        abstract fun getCardByToBeTranslatedWord (kind : String, toBeTranslatedWord : String) : MemoCardEntity
}
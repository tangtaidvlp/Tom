package com.teamttdvlp.memolang.data.sql.dao

import androidx.room.*
import com.teamttdvlp.memolang.data.model.entity.flashcard.CardQuizInfor

@Dao
interface CardQuizInforDAO {

    @Query ("SELECT * FROM card_quiz where answerSet == :setId")
    fun getAllCardQuizInformation_BySetId (setId : String) : List<CardQuizInfor>

    @Query ("DELETE FROM card_quiz where answerSet == :setId")
    fun deleteAll_BySetId (setId : String)

    @Delete
    fun delete (cardInfor : CardQuizInfor)

    @Update
    fun update (cardInforQuiz : CardQuizInfor)

    @Insert
    fun insert(newQuizInfor: CardQuizInfor)

}
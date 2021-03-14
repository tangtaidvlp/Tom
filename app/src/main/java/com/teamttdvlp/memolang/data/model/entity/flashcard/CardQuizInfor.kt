package com.teamttdvlp.memolang.data.model.entity.flashcard

import androidx.room.*
import com.teamttdvlp.memolang.data.sql.converter.StringListConverter

@Entity(tableName = "card_quiz", foreignKeys = arrayOf(
    ForeignKey(entity = Deck::class, parentColumns = arrayOf("setName"), childColumns = arrayOf("deckId")),
    ForeignKey(entity = Flashcard::class, parentColumns = arrayOf("id"), childColumns = arrayOf("cardId")))
)
class CardQuizInfor (

) {
    @PrimaryKey (autoGenerate = true)
    var id : Int = 0

    var deckId : String = ""

    var cardId : Int = 0

    @TypeConverters(StringListConverter::class)
    var answerSet : ArrayList<String> = ArrayList()


    fun getAnswer1(): String {
        return answerSet.get(0)
    }

    fun getAnswer2(): String {
        return answerSet.get(1)
    }

    fun getAnswer3(): String {
        return answerSet.get(2)
    }

}
package com.teamttdvlp.memolang.data.model.entity.user

import androidx.room.*
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import com.teamttdvlp.memolang.data.sql.converter.FlashcardListConverter
import com.teamttdvlp.memolang.data.sql.converter.RawVocaListConverter
import com.teamttdvlp.memolang.data.sql.converter.StringListConverter
import com.teamttdvlp.memolang.view.helper.notContains

/**
 * The reason why that i use ArrayList<Flashcard> to save
 * flashcards instead of using ArrayList<Int> to
 * save only the card's id then query those card by their id with FlashcardRepository
 * is: "When the card is deleted by user, it will be gone forever and we can't
 * query those cards anymore. So, if user delete a card, that card will be disappeared
 * in recentAddedFlashcardList or something similar"
 */
@Entity(tableName = "user_using_history")
class UserUsingHistory (

    @PrimaryKey
    var id : Int = 0) {

    @TypeConverters(RawVocaListConverter::class)
    var recentSearchedVocaList : ArrayList<TypicalRawVocabulary> = ArrayList()

    @TypeConverters(FlashcardListConverter::class)
    var recentOnlineSearchedFlashcardList : ArrayList<Flashcard> = ArrayList()

    @TypeConverters(FlashcardListConverter::class)
    var recentAddedFlashcardList : ArrayList<Flashcard> = ArrayList()

    @TypeConverters(StringListConverter::class)
    var usedLanguageList : ArrayList<String> = ArrayList()

    fun addToUsedLanguageList (language : String) {
        usedLanguageList.apply {
            if (notContains(language)) {
                if (size == 5) {
                    removeAt(0)
                }
                add(language)
            }
        }
    }
}
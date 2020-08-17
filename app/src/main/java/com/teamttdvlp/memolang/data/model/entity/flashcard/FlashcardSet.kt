package com.teamttdvlp.memolang.data.model.entity.flashcard

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.teamttdvlp.memolang.data.model.entity.flashcard.SetNameUtils.Companion.getSetNameFromLangPair
import java.io.Serializable

const val DEFAULT_SET_NAME = ""

@Entity(tableName = "flashcard_set")
class FlashcardSet constructor(
    /**
     * Check out init {} code block, if field `name` is empty, it will
     * be set to default
     */
    @PrimaryKey
    @ColumnInfo(name = "setName")
    var name: String,

    var frontLanguage : String,

    var backLanguage : String) : Serializable {

    @Ignore
    var flashcards: ArrayList<Flashcard> = ArrayList()

    init {
        if (name == DEFAULT_SET_NAME) {
            name = getSetNameFromLangPair(frontLanguage, backLanguage)
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is FlashcardSet) {
            ((other.name == name) && (other.frontLanguage == frontLanguage) && (other.backLanguage == backLanguage)
                    && (other.flashcards.containsAll(flashcards)))
        } else {
            super.equals(other)
        }
    }
}
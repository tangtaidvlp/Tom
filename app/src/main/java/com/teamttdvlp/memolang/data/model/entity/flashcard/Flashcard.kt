package com.teamttdvlp.memolang.data.model.entity.flashcard

import androidx.annotation.NonNull
import androidx.room.*
import com.teamttdvlp.memolang.view.helper.systemOutLogging
import java.io.Serializable

@Entity(tableName = "flashcard", foreignKeys = arrayOf(ForeignKey(entity = Deck::class, parentColumns = arrayOf("setName"), childColumns = arrayOf("setOwner"))))
data class Flashcard (

    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,

    @NonNull
    var setOwner : String,

    @NonNull
    var text : String,

    @NonNull
    var translation : String,

    @NonNull
    var frontLanguage : String,

    @NonNull
    var backLanguage: String,

    var example: String = "",

    var meanOfExample: String = "",

    var type: String = "",

    var pronunciation: String = "//",

    @Embedded
    var cardProperty: CardProperty = CardProperty()
) : Serializable {

    var frontIllustrationPictureName: String? = null

    var backIllustrationPictureName: String? = null

    override fun equals(other: Any?): Boolean {
        if ((other is Flashcard).not() || other == null) {
            return false
        }

        return (other as Flashcard).id.equals(id)
    }

    init {
        text = text.trim()
        setOwner = setOwner.trim()
        if (setOwner == "") systemOutLogging("Set Owner can not be null. Please check and fix code properly")
        text = text.trim()
        translation = translation.trim()
        example = example.trim()
        meanOfExample = meanOfExample.trim()
        type = type.trim()
    }

}
package com.teamttdvlp.memolang.data.sql.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import java.util.*

class FlashcardListConverter {

    @TypeConverter
    fun convertStringToFlashcardList (rawVocaList_JSON : String) : ArrayList<Flashcard> {
        val typeFlashcardList = object : TypeToken<ArrayList<Flashcard>>(){}.type
        return Gson().fromJson(rawVocaList_JSON, typeFlashcardList)
    }

    @TypeConverter
    fun convertFlashcardListToString(rawVocaList : ArrayList<Flashcard>) : String {
        return Gson().toJson(rawVocaList)
    }
}
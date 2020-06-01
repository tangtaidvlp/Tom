package com.teamttdvlp.memolang.data.sql.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import java.util.*

class RawVocaListConverter {

    @TypeConverter
    fun convertStringToRawVocaList (rawVocaList_JSON : String) : ArrayList<TypicalRawVocabulary> {
        val typeRawVocabularyList = object : TypeToken<ArrayList<TypicalRawVocabulary>>(){}.type
        return Gson().fromJson(rawVocaList_JSON, typeRawVocabularyList)
    }

    @TypeConverter
    fun convertRawVocaListToString(rawVocaList : ArrayList<TypicalRawVocabulary>) : String {
        return Gson().toJson(rawVocaList)
    }
}
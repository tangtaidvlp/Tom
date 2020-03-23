package com.teamttdvlp.memolang.database.sql.converter.sql_converter

import androidx.room.TypeConverter
import java.util.*

class StringListConverter {

    private val DIVIDER = "/"

    @TypeConverter
    fun convertStringToLanguageList(string : String) : ArrayList<String> {
        if (string.length == 0) return ArrayList<String>()
        val languageArray = string.split(DIVIDER)
        val languageList = ArrayList<String>()
        for (language in languageArray) {
            languageList.add(language)
        }
        return languageList
    }

    @TypeConverter
    fun convertLanguageListToString(languageList : ArrayList<String>) : String {
        if (languageList.size == 0) return ""
        var result = ""
        for (language in languageList) {
            result += DIVIDER + language
        }
        return result.removePrefix(DIVIDER)
    }
}
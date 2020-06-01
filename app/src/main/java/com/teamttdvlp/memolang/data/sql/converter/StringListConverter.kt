package com.teamttdvlp.memolang.data.sql.converter

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.util.*

class StringListConverter {

    @TypeConverter
    fun convertStringToStringList(stringList_JSON : String) : ArrayList<String> {
        val typeStringList = object : TypeToken<ArrayList<String>>(){}.type
        return Gson().fromJson(stringList_JSON, typeStringList)
    }

    @TypeConverter
    fun convertStringListToString(stringList : ArrayList<String>) : String {
        return Gson().toJson(stringList)
    }

}
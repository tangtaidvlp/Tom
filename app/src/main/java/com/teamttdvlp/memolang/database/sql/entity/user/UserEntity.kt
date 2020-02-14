package com.teamttdvlp.memolang.database.sql.entity.user

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.teamttdvlp.memolang.database.sql.converter.sql_converter.LanguageStringListConverter

@Entity(tableName = "User")
class UserEntity {

    @PrimaryKey
    lateinit var id : String

    @NonNull
    lateinit var motherLanguage : String

    @NonNull
    lateinit var targetLanguage : String

    @TypeConverters(LanguageStringListConverter::class)
    lateinit var recentUseLanguages : ArrayList<String>


}
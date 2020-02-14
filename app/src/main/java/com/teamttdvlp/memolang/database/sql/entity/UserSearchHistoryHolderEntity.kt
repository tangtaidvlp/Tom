package com.teamttdvlp.memolang.database.sql.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.teamttdvlp.memolang.database.sql.converter.sql_converter.DateTypeConverter
import java.util.*

@Entity(tableName = "UserSearchHistory")
class UserSearchHistoryHolderEntity {

    @PrimaryKey (autoGenerate = true)
    var id : Int = 0

    var cardId : Int = 0

    @ColumnInfo
    @TypeConverters(DateTypeConverter::class)
    lateinit var searchedDate : Date

}
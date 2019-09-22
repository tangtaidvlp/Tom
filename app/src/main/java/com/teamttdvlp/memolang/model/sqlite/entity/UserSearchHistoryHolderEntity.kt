package com.teamttdvlp.memolang.model.sqlite.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.teamttdvlp.memolang.model.sqlite.converter.DateTypeConverter
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
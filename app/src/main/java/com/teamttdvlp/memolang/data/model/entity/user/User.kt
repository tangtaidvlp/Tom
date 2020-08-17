package com.teamttdvlp.memolang.data.model.entity.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.teamttdvlp.memolang.data.sql.converter.StringListConverter

@Entity(tableName = "user")
class User {

    @PrimaryKey
    var id : Int = 0

    @TypeConverters(StringListConverter::class)
    var ownCardTypeList : ArrayList<String> = ArrayList()

    fun addToCardTypeList (cardType : String) {
        if (ownCardTypeList.contains(cardType)) {
            ownCardTypeList.remove(cardType)
        }
        ownCardTypeList.add(0, cardType)
    }
}

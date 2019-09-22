package com.teamttdvlp.memolang.model.sqlite.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
class UserEntity {
    @NonNull
    @PrimaryKey
    lateinit var id : String

    @NonNull
    lateinit var motherLanguage : String

    @NonNull
    lateinit var targetLanguage : String

}
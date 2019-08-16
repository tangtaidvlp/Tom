package com.teamttdvlp.memolang.model.sqlite.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.teamttdvlp.memolang.model.sqlite.converter.DateTypeConverter
import java.util.*

@Entity(tableName = "MemoCard")
class MemoCardEntity {
    @PrimaryKey
    @NonNull
    lateinit  var id : String
    @NonNull
    lateinit var translatedWord : String
    @NonNull
    lateinit var toBeTranslatedWord : String
    // Dịch từ ngôn ngữ nào sang ngôn ngữ nào
    // en-vi, vi-en, fi-vi
    @NonNull
    lateinit var  type : String
    lateinit var  using : String
    lateinit var synonym : String
    // Loại từ gì
    // danh từ, tính từ,... viết bằng ngôn ngữ giống {toBeTranslatedWord}
    @NonNull
    lateinit var kind : String
    // cách đánh vần. viết theo ngôn ngữ giống {toBeTranslatedWord}
    @NonNull
    lateinit var spelling : String

    // ngày tạo, không phải ngày search
    @ColumnInfo
    @TypeConverters(DateTypeConverter::class)
    lateinit var createdAt : Date
}
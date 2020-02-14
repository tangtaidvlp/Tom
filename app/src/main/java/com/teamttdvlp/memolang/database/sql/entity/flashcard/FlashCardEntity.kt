package com.teamttdvlp.memolang.database.sql.entity.flashcard

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.teamttdvlp.memolang.database.sql.converter.sql_converter.DateTypeConverter
import java.util.*

@Entity(tableName = "Flashcard")
class FlashCardEntity {

  @PrimaryKey(autoGenerate = true)
  @NonNull
  var id : Int = 0

  @NonNull
  lateinit var translation : String

  @NonNull
  lateinit var text : String

  @NonNull
  lateinit var  languagePair : String

  lateinit var  using : String

  lateinit var  meanUsing : String

  lateinit var synonym : String

  @NonNull
  lateinit var kind : String

  @NonNull
  lateinit var pronunciation : String

  @NonNull
  lateinit var setName : String

  // ngày tạo, không phải ngày search
  @ColumnInfo
  @TypeConverters(DateTypeConverter::class)
  lateinit var createdAt : Date

}
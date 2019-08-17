package com.teamttdvlp.memolang.view.Activity.mockmodel

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.teamttdvlp.memolang.model.sqlite.converter.DateTypeConverter
import java.util.*

data class MemoCard (var id : String = "",
                     var translatedWord : String = "",
                     var toBeTranslatedWord : String = "",
                     var  type : String = "",
                     var  using : String = "",
                     var synonym : String = "",
                     var kind : String = "",
                     var spelling : String = "",
                     var createdAt : Date = Date(Calendar.getInstance().timeInMillis))
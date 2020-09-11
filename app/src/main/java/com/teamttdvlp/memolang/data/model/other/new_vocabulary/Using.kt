package com.teamttdvlp.memolang.data.model.other.new_vocabulary

import com.example.dictionary.model.TranslationAndExample

data class Using(
    var type: String = "",
    var translationAndExamsList: ArrayList<TranslationAndExample> = ArrayList()
)

package com.example.dictionary.model

import com.teamttdvlp.memolang.data.model.other.new_vocabulary.Example

data class TranslationAndExample(
    var translation: String = "",
    var subExampleList: ArrayList<Example> = ArrayList()
)


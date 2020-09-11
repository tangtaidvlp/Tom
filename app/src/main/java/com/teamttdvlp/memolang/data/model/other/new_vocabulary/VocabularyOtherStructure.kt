package com.teamttdvlp.memolang.data.model.other.vocabulary

import com.example.dictionary.model.TranslationAndExample
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.Example

data class VocabularyOtherStructure(
    var text: String = "",
    var translationAndExample_List: ArrayList<TranslationAndExample> = ArrayList()
) : Example

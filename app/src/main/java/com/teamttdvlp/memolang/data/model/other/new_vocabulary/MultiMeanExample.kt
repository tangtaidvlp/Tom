package com.teamttdvlp.memolang.data.model.other.vocabulary

import com.example.dictionary.model.TransAndExamp
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.Example

data class MultiMeanExample (
    var text : String = "",
    var transAndSubExamp_List : ArrayList<TransAndExamp> = ArrayList()) : Example

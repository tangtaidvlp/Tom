package com.teamttdvlp.memolang.model

class CardType {
    companion object {
        val TYPE_LIST = ArrayList<String>().apply {
            add("Verb")
            add("Noun")
            add("Adjective")
            add("Adverb")
            add("Preposition")
            add("Acronyms")
        }
    }
}
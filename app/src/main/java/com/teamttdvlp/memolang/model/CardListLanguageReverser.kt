package com.teamttdvlp.memolang.model

import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard

class CardListLanguageReverser {
    companion object {

        fun reverse_LIST_Card_TextAndTranslation(flashcardList: ArrayList<Flashcard>) {
            for (card in flashcardList) {
                reverse_Card_TextAndTranslation(card)
            }
        }

        fun reverse_Card_TextAndTranslation (card : Flashcard) {
            val textHolder = card.text
            card.text = card.translation
            card.translation = textHolder
        }


        fun reverse_ListCard_ExampleAndMeanExample  (flashcardList : ArrayList<Flashcard>) {
            for (card in flashcardList)
                reverse_Card_ExampleAndMeanExample(card)
        }

        fun reverse_Card_ExampleAndMeanExample (card : Flashcard) {
            val exampleHolder = card.example
            card.example = card.meanOfExample
            card.meanOfExample = exampleHolder
        }
    }
}
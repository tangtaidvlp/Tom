package com.teamttdvlp.memolang.model.sqlite.converter

import com.teamttdvlp.memolang.model.sqlite.entity.FlashCardEntity
import com.teamttdvlp.memolang.model.model.Flashcard
import kotlin.collections.ArrayList

object FlashcardConverter {
    fun toCardEntity (card : Flashcard) : FlashCardEntity {
        return FlashCardEntity().apply {
            id = card.id
            createdAt = card.createdAt
            translation = card.translation
            text = card.text
            type = card.type
            using = card.using
            synonym = card.synonym
            kind = card.kind
            spelling = card.spelling
        }
    }

    fun toNormalCard(card : FlashCardEntity) : Flashcard {
        return Flashcard().apply {
            id = card.id
            createdAt = card.createdAt
            translation = card.translation
            text = card.text
            type = card.type
            using = card.using
            synonym = card.synonym
            kind = card.kind
            spelling = card.spelling
        }
    }

    fun toNormalCardCollection(cards : Collection<FlashCardEntity>) : ArrayList<Flashcard>{
        val collection = ArrayList<Flashcard>()
        for (card in cards) {
            collection.add(Flashcard().apply {
                id = card.id
                createdAt = card.createdAt
                translation = card.translation
                text = card.text
                type = card.type
                using = card.using
                synonym = card.synonym
                kind = card.kind
                spelling = card.spelling
            })
        }
        return collection
    }

    fun toCardEntityCollection(cards : Collection<Flashcard>) : ArrayList<FlashCardEntity>{
        val collection = ArrayList<FlashCardEntity>()
        for (card in cards) {
            collection.add(FlashCardEntity().apply {
                id = card.id
                createdAt = card.createdAt
                translation = card.translation
                text = card.text
                type = card.type
                using = card.using
                synonym = card.synonym
                kind = card.kind
                spelling = card.spelling
            })
        }
        return collection
    }

}
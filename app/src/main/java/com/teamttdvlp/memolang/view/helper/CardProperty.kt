package com.teamttdvlp.memolang.view.helper

const val FLASHCARD_WIDE_HEIGHT_RATE: Float = 32f / 23f

class CardProperty {

    val heightOption: FlashcardHeightOption = FlashcardHeightOption.NORMAL

    val frontSide = Side()

    val bacKSide = Side()

    data class Side(var hasText: Boolean = true)

    enum class FlashcardHeightOption {
        NORMAL,
        WIDE
    }
}
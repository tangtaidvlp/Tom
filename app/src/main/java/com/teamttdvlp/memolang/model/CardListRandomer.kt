package com.teamttdvlp.memolang.model

import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import kotlin.random.Random

private const val MIN_RANDOM_TIMES = 3

private const val MAX_RANDOM_TIMES = 10

class CardListRandomer {

    fun random (data : ArrayList<Flashcard>) : ArrayList<Flashcard>{
        val newData = ArrayList<Flashcard>()
        for (i in 1..data.size) {
            val randomPos = Random.nextInt(data.size)
            newData.add(data.get(randomPos))
            data.remove(data.get(randomPos))
        }
        return newData
    }

}

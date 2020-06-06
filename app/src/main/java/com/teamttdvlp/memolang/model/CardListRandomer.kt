package com.teamttdvlp.memolang.model

import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import kotlin.random.Random

private const val MIN_RANDOM_TIMES = 3

private const val MAX_RANDOM_TIMES = 10

class CardListRandomer {

    fun random(data: ArrayList<Flashcard>): ArrayList<Flashcard> {
        val cloneData = ArrayList<Flashcard>()
        cloneData.addAll(data)
        val newData = ArrayList<Flashcard>()
        for (i in 1..cloneData.size) {
            val randomPos = Random.nextInt(cloneData.size)
            newData.add(cloneData.get(randomPos))
            cloneData.remove(cloneData.get(randomPos))
        }
        return newData
    }

}

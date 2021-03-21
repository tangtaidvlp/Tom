package com.teamttdvlp.memolang.model

import com.teamttdvlp.memolang.view.helper.systemOutLogging
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


object StringCompare {
    const val _100_PERCENTS : Float = 1f

    fun compareTwoStrings (str1: String, str2: String) : Float{
        val charactersPosition = compareCharacterPosition (str1, str2)
        val countOfEachCharacter = compareCharacterAmountAndCount (str1, str2)
        systemOutLogging("")
        systemOutLogging("--- Compare \"$str1\" and \"$str2\" ---")
        systemOutLogging("  * Position: $charactersPosition")
        systemOutLogging("  * Count: $countOfEachCharacter")
        systemOutLogging("  * Final result: ${countOfEachCharacter * 0.7f + charactersPosition * 0.3f}")

        return countOfEachCharacter * 0.7f + charactersPosition * 0.3f
    }

    private fun compareCharacterAmountAndCount(str1: String, str2: String): Float {

        if (str1 == str2) {
            return _100_PERCENTS
        }

        if (str1.isEmpty() || str2.isEmpty()) {
            return 0f
        }


        val charAmountMap1 = HashMap<Char, Int?>()
        val charAmountMap2 = HashMap<Char, Int?>()

        for (char in str1.toCharArray()) {
            if (charAmountMap1.get(char) != null) {
                charAmountMap1[char] = charAmountMap1.get(char)!! + 1
            } else {
                charAmountMap1[char] = 1
            }
        }

        for (char in str2.toCharArray()) {
            if (charAmountMap2.get(char) != null) {
                charAmountMap2[char] = charAmountMap2.get(char)!! + 1
            } else {
                charAmountMap2[char] = 1
            }
        }

        var similarityCharCount = 0

        val shortMap : HashMap <Char, Int?>
        val bigMap : HashMap <Char, Int?>

        if (charAmountMap1.size <= charAmountMap2.size) {
            shortMap = charAmountMap1
            bigMap = charAmountMap2
        } else {
            shortMap = charAmountMap2
            bigMap = charAmountMap1
        }

        for (key in shortMap.keys) {
            if (bigMap.get(key) != null) {
                val differenceAmount = shortMap.get(key)!! - bigMap.get(key)!!

                if (differenceAmount < abs(1)) {
                    similarityCharCount++
                }
            }
        }

        return similarityCharCount.toFloat() / bigMap.size.toFloat()
    }

    private fun compareCharacterPosition (str1 : String, str2 : String) : Float {
        var similiarityCharCount = 0

        if (str1 == str2) {
            return _100_PERCENTS
        }

        if (str1.isEmpty() || str2.isEmpty()) {
            return 0f
        }

        val maxLength = max (str1.length, str2.length)
        val minLength = min (str1.length, str2.length)

        for (i in 0..minLength - 1) {
            if (str1[i] == str2[i]) {
                similiarityCharCount++
            }
        }

        return similiarityCharCount.toFloat() / maxLength.toFloat()
    }

}
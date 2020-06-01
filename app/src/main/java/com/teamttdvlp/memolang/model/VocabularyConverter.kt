package com.teamttdvlp.memolang.model

import com.example.dictionary.model.TransAndExamp
import com.example.dictionary.model.Vocabulary
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.SingleMeanExample
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.Using
import com.teamttdvlp.memolang.data.model.other.vocabulary.MultiMeanExample
import com.teamttdvlp.memolang.view.adapter.RCVSearchDictionaryAdapter
import com.teamttdvlp.memolang.view.helper.clearAll
import com.teamttdvlp.memolang.view.helper.foreachFromSecondElement
import com.teamttdvlp.memolang.view.helper.quickLog
import java.lang.Exception
import java.util.ArrayList

class VocabularyConverter {

    private val VOCABULARY_PREFIX = "@"
    private val USING_PREFIX = "*"
    private val TRANSLATION_PREFIX = "-"
    private val EXAMPLE_PREFIX = "="

    private val EXAMPLE = 0
    private val EXAMPLE_TRANSLATION = 1

    fun convertToVocabulary (holder: TypicalRawVocabulary) : Vocabulary {
        val vocabulary = Vocabulary()
        val linesList = holder.fullContent
        val cond_1= (linesList.first().contains(VOCABULARY_PREFIX))
        val cond_2 = linesList.first().trim().startsWith(VOCABULARY_PREFIX)
        val cond_3 = (linesList.first().length > 1) and (linesList.first()[1] == VOCABULARY_PREFIX[0])

        if (cond_1.not() and (cond_2 or cond_3).not()) {
            quickLog("ERR: ${linesList.first()}")
            throw Exception("STOP")
        }

        val firstLine = linesList.first().clearAll(VOCABULARY_PREFIX)
        val firstLineHasPronunciation = firstLine.contains(" /")

        // FIRST LINE
        if (firstLineHasPronunciation) {
            val hasMoreThan1Word = firstLine.contains(") /")
            val textAndPronunciation = if (hasMoreThan1Word)
                filtTextAndPronunciation(firstLine)
            else
                getTextAndPronunciation(firstLine)

            vocabulary.text = textAndPronunciation.text.trim()
            vocabulary.pronunciation = textAndPronunciation.pronun.trim()

        } else {
            vocabulary.text = firstLine.clearAll(VOCABULARY_PREFIX)
        }

        // MIDDLE
        var `isBuilding_Multi-Mean_Example` = false
        val usingList = ArrayList<Using>()
        linesList.foreachFromSecondElement { line ->
            // This is USING (TYPE only)
            if (line.trim().startsWith(USING_PREFIX)) {
                // Refresh
                val using = Using(type = line.clearAll(USING_PREFIX).trim())
                usingList.add(using)

                `isBuilding_Multi-Mean_Example` = false
            }

            // TRANSLATION
            else if (line.trim().startsWith(TRANSLATION_PREFIX)) {

                // Mean of multi-mean example has the same "-" prefix
                val isATranslation = `isBuilding_Multi-Mean_Example`.not()
                if (isATranslation) {
                    val thereIsNoType = (usingList.size == 0)
                    if (thereIsNoType) {
                        usingList.add(Using(type = ""))
                    }
                    val translation = line.clearAll(TRANSLATION_PREFIX).trim()
                    val transAndExample = TransAndExamp(translation = translation)
                    usingList.last().transAndExamsList.add(transAndExample)

                } else { // This is a TRANSLATION of an Multi-mean Example
                    val translation = line.clearAll(TRANSLATION_PREFIX).trim()
                    val currentUsing = usingList.last()
                    val currentTransAndExample = currentUsing.transAndExamsList.last()
                    val currentExample = currentTransAndExample.subExampleList.last()
                    if (currentExample is MultiMeanExample) {
                        currentExample.transAndSubExamp_List.add(TransAndExamp(translation))
                    }
                }

                // SINGLE MEAN EXAMPLE
            } else if (line.trim().startsWith(EXAMPLE_PREFIX)) {
                val example = SingleMeanExample()
                val hasMeaning = line.contains("+")
                if (hasMeaning) {
                    val exampleAndMean = line.trim().clearAll(EXAMPLE_PREFIX).split("+")
                    example.text = exampleAndMean.get(EXAMPLE).trim()
                    example.mean = exampleAndMean.get(EXAMPLE_TRANSLATION).trim()
                } else {
                    example.text = line.clearAll(EXAMPLE_PREFIX).trim()
                }

                if (`isBuilding_Multi-Mean_Example`.not()) {
                    val currentUsing = usingList.last()

                    if (currentUsing.transAndExamsList.size == 0) {
                        currentUsing.transAndExamsList.add(TransAndExamp(""))
                    }

                    val currentTransAndExample = currentUsing.transAndExamsList.last()
                    currentTransAndExample.subExampleList.add(example)

                } else {
                    val currentUsing = usingList.last()
                    if (currentUsing.transAndExamsList.size == 0) {
                        currentUsing.transAndExamsList.add(TransAndExamp(""))
                    }

                    val currentTransAndExample = currentUsing.transAndExamsList.last()
                    val currentExample = currentTransAndExample.subExampleList.last()
                    if (currentExample is MultiMeanExample) {
                        currentExample.transAndSubExamp_List.last().subExampleList.add(example)
                    }
                }

                // MULTI-MEAN EXAMPLE
            } else if (line.trim().startsWith("!")) {
                `isBuilding_Multi-Mean_Example` = true
                val englishExample = line.trim().clearAll("!")
                val example = MultiMeanExample(text = englishExample)

                val currentUsing = usingList.last()
                val currentTransAndExample = currentUsing.transAndExamsList.last()
                currentTransAndExample.subExampleList.add(example)
            } else {
                quickLog("SOMETHING WRONG WITH THIS LINE: $line")
            }
        }

        vocabulary.usings = usingList
        return vocabulary
    }


    private val TEXT = 0
    private val PRONUNCIATION = 1

    private fun getTextAndPronunciation (source: String) : RCVSearchDictionaryAdapter.TextAndPronun {
        val textAndPronun = source.split(" /")
        return RCVSearchDictionaryAdapter.TextAndPronun(textAndPronun[TEXT], "/" + textAndPronun[PRONUNCIATION])
    }

    private fun filtTextAndPronunciation(source : String) : RCVSearchDictionaryAdapter.TextAndPronun {

        fun getPairFromText (text : String) : RCVSearchDictionaryAdapter.TextAndPronun {
            val textAndPronunPair = text.split(" /")
            return try {
                RCVSearchDictionaryAdapter.TextAndPronun(textAndPronunPair[0], "/" + textAndPronunPair[1])
            } catch (ex : Exception) {
                quickLog("ERROR PAIR: $text")
                RCVSearchDictionaryAdapter.TextAndPronun("Err", "Err")
            }
        }

        val pairsArray = source.split(" (")
        val targetPair = getPairFromText(pairsArray[0])
        for (i in 1 until pairsArray.size - 1) {
            val textAndPronunPair = getPairFromText(pairsArray[i])
            if (targetPair.text == textAndPronunPair.text.clearAll(")")) {
                targetPair.pronun = textAndPronunPair.pronun
            }
        }

        return targetPair
    }


}
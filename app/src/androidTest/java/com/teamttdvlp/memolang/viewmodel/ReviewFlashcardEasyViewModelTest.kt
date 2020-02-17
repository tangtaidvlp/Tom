package com.teamttdvlp.memolang.viewmodel

import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.view.activity.iview.ReviewFlashcardEasyView
import com.teamttdvlp.memolang.view.helper.TestEverything
import org.junit.Test

import org.junit.Assert.*
import kotlin.math.exp

class ReviewFlashcardEasyViewModelTest {

    val viewModel = ReviewFlashcardEasyViewModel()

    val view = object : ReviewFlashcardEasyView {
        override fun showTestSubjectOnScreen(
            testSubject: Flashcard,
            ansElements: Array<String>,
            useUsingForTestSubject: Boolean
        ) {

        }

        override fun performCorrectAnsElemtsOrderAnims() {

        }

        override fun performIncorrectAnsElemtsOrderAnims() {

        }

        override fun performPassBehaviours() {

        }

        override fun performNotPassBehaviours() {

        }

        override fun endReviewing() {

        }
    }

    @Test
    fun convertAnswerToElements() {
        val answer =  "  Test  "
        val expected1 = Array<String> (4) {
            return@Array when (it) {
                0 -> "T"
                1 -> "e"
                2 -> "s"
                3 -> "t"
                else -> "xxx"
            }
        }

        val answerElements = viewModel.convertAnswerToElements(answer)
        assertArrayEquals(answerElements, expected1)

        val answer2 = "   Test   number two   "
        val answerElements2 = viewModel.convertAnswerToElements(answer2)
        val expected2 = Array<String> (3) {
            return@Array when (it) {
                0 -> "Test"
                1 -> "number"
                2 -> "two"
                else -> "xxx"
            }
        }
        assertArrayEquals(answerElements2, expected2)

    }
}
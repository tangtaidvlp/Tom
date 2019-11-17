package com.teamttdvlp.memolang.view.helper

import com.teamttdvlp.memolang.model.model.FlashcardSet
import com.teamttdvlp.memolang.model.model.Flashcard

class TestEverything {
    companion object {
        fun mockFlashCardList () : ArrayList<Flashcard> {
            val flashcard = Flashcard(
                0,
                "Negotiate",
                "Đàm phán",
                "We negotiate for intergrate two force"
            )
            val flashcard1 = Flashcard(
                0,
                "Something",
                "Cái gì đó","",
                "We always do something to figure out the solution"
            )
            val flashcard2 = Flashcard(
                0,
                "Figure out",
                "Tìm ra","",
                "We always do something to figure out the solution"
            )
            val flashcard3 =
                Flashcard(0, "Until", "Cho đến khi", "Fight until the end")
            val flashcard4 = Flashcard(
                0,
                "Glorious",
                "Vẻ Vang","",
                "We're worthy to have that glorious victory"
            )
            val flashcard5 = Flashcard(
                0,
                "Curious",
                "Tò mò",
                "Successful people is always curious"
            )
            val flashcard6 = Flashcard(
                0,
                "Give in",
                "Buông xuôi",
                "Give in laziness make you down"
            )
            val flashcard7 = Flashcard(
                0,
                "Procastinate",
                "Trì hoãn",
                "Procastinating something when and only if you can do it better then"
            )
            val mockDataList = ArrayList<Flashcard>()
            mockDataList.add(flashcard)
            mockDataList.add(flashcard1)
            mockDataList.add(flashcard2)
            mockDataList.add(flashcard3)
//            mockDataList.add(flashcard4)
//            mockDataList.add(flashcard5)
//            mockDataList.add(flashcard6)
//            mockDataList.add(flashcard7)
//            mockDataList.add(flashcard)
//            mockDataList.add(flashcard1)
//            mockDataList.add(flashcard2)
//            mockDataList.add(flashcard3)
//            mockDataList.add(flashcard4)
//            mockDataList.add(flashcard5)
//            mockDataList.add(flashcard6)
//            mockDataList.add(flashcard7)
            return mockDataList
        }

        fun mockFlashcardSet (id : String) : FlashcardSet {
            return FlashcardSet(id ).apply {
                flashcards = mockFlashCardList()
            }
        }
    }
}
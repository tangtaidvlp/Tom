package com.teamttdvlp.memolang.view.helper

import com.teamttdvlp.memolang.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard

class TestEverything {
    companion object {
        fun mockFlashCardList () : ArrayList<Flashcard> {
            val flashcard = Flashcard(
                0,
                "Negotiate",
                "Đàm phán",
                "en-vi",
                "English-Vietnamese",
                "We negotiate for intergrating two force",
                "Chúng tôi đàm phán để liên kết hai lực lượng"
            )
            val flashcard1 = Flashcard(
                0,
                "Something",
                "Cái gì đó",
                "en-vi",
                "English-Vietnamese",
                "We always do something to figure out the solution",
                "Chúng ta thường làm gi đó để tìm ra cách giải quyết"
            )
            val flashcard2 = Flashcard(
                0,
                "Figure out",
                "Tìm ra",
                "en-vi",
                "English-Vietnamese",
                "",
                ""
            )
            val flashcard3 =
                Flashcard(
                    0,
                    "Until",
                    "Cho đến khi",
                    "en-vi",
                    "English-Vietnamese",
                    "Fight until the end",
                    "Chiến đấu đến cùng"
                )
            val flashcard4 = Flashcard(
                0,
                "Glorious",
                "Vẻ Vang",
                "",
                "en-vi",
                "English-Vietnamese",
                "We're worthy to have that glorious victory",
                "Chúng ta xứng đáng với chiến thắng vẻ vang đó"
            )
            val flashcard5 = Flashcard(
                0,
                "Curious",
                "Tò mò",
                "en-vi",
                "English-Vietnamese",
                "Successful people is always curious",
                "Những người thành công thì luôn tò mò"
            )
            val flashcard6 = Flashcard(
                0,
                "Give in",
                "Buông xuôi",
                "en-vi",
                "English-Vietnamese",
                "Give in laziness make you down",
                "Gục ngã trước sự lười biếng khiến bạn thất bại"
            )
            val flashcard7 = Flashcard(
                0,
                "Procastinate",
                "Trì hoãn",
                "en-vi",
                "English-Vietnamese",
                "Procastinating something when and only if you can do it better then",
                "Trì hoãn cái gì đó khi và chỉ khi sau đó bạn có thể làm nó tốt hơn"
            )
            val mockDataList = ArrayList<Flashcard>()
//            mockDataList.add(flashcard6)
//            mockDataList.add(flashcard7)
//            mockDataList.add(flashcard)
            mockDataList.add(flashcard1)
            mockDataList.add(flashcard2)
//            mockDataList.add(flashcard4)
            mockDataList.add(flashcard3)
//            mockDataList.add(flashcard5)
            return mockDataList
        }

        fun mockFlashcardSet (id : String) : FlashcardSet {
            return FlashcardSet(id).apply {
                flashcards = mockFlashCardList()
            }
        }

        fun mockFlashcardSetList (count : Int) : ArrayList<FlashcardSet> {
            var result = ArrayList<FlashcardSet>()

            IntArray(count) { number ->
                result.add(mockFlashcardSet("Set $number"))
                number
            }

            return result
        }
    }
}
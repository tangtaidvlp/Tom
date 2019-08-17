package com.teamttdvlp.memolang.view.Activity.helper

import com.teamttdvlp.memolang.view.Activity.mockmodel.MemoCard

class TestEverything {
    fun mockList () : ArrayList<MemoCard> {
        val flashcard = MemoCard("Negotiate", "Đàm phán", "We negotiate for intergrate two force")
        val flashcard1 = MemoCard("Something", "Cái gì đó", "We always do something to figure out the solution")
        val flashcard2 = MemoCard("Figure out", "Tìm ra", "We always do something to figure out the solution")
        val flashcard3 = MemoCard("Until", "Cho đến khi", "Fight until the end")
        val flashcard4 = MemoCard("Glorious", "Vẻ Vang", "We're worthy to have that glorious victory")
        val flashcard5 = MemoCard("Curious", "Tò mò", "Successful people is always curious")
        val flashcard6 = MemoCard("Give in", "Buông xuôi", "Give in laziness make you down")
        val flashcard7 = MemoCard("Procastinate", "Trì hoãn", "Procastinating something when and only if you can do it better then")
        val mockDataList = ArrayList<MemoCard>()
        mockDataList.add(flashcard)
        mockDataList.add(flashcard1)
        mockDataList.add(flashcard2)
        mockDataList.add(flashcard3)
        mockDataList.add(flashcard4)
        mockDataList.add(flashcard5)
        mockDataList.add(flashcard6)
        mockDataList.add(flashcard7)
        mockDataList.add(flashcard)
        mockDataList.add(flashcard1)
        mockDataList.add(flashcard2)
        mockDataList.add(flashcard3)
        mockDataList.add(flashcard4)
        mockDataList.add(flashcard5)
        mockDataList.add(flashcard6)
        mockDataList.add(flashcard7)
        return mockDataList
    }


}
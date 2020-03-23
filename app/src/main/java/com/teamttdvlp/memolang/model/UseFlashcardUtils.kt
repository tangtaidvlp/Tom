package com.teamttdvlp.memolang.model

import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.view.helper.quickLog

fun findTextFormInAnother (text : String, motherText : String) : String {
    val pos = motherText.toLowerCase().indexOf(text.toLowerCase())
    if (pos == -1) {
        quickLog("Error happened at findTextFormInAnother(String, String), ReviewFlashcardActivity.kt")
        Exception ("Using does not contain text").printStackTrace()
    }
    val textFormInSource = motherText.substring(pos, pos + text.length)
    return textFormInSource
}

fun canUseUsingForTestSubject (flashcard: Flashcard) : Boolean {
    return flashcard.example.toLowerCase().contains(flashcard.text.toLowerCase().trim()) and flashcard.exampleMean.isNotEmpty()
}

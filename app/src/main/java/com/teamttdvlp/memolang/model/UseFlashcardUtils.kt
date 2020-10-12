package com.teamttdvlp.memolang.model

import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.view.helper.systemOutLogging

fun findTextFormInAnother (text : String, motherText : String) : String {
    val pos = motherText.toLowerCase().indexOf(text.toLowerCase())
    if (pos == -1) {
        systemOutLogging("Error happened at findTextFormInAnother(String, String), ReviewFlashcardActivity.kt")
        Exception ("\"$motherText\" does not contain \"$text\"").printStackTrace()
    }
    val textFormInSource = motherText.substring(pos, pos + text.length)
    return textFormInSource
}

fun checkCanUseExampleForTestSubject (flashcard: Flashcard) : Boolean {
    return flashcard.example.toLowerCase().contains(flashcard.text.toLowerCase().trim()) and flashcard.meanOfExample.isNotEmpty()
}

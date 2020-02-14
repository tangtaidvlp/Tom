package com.teamttdvlp.memolang.model

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.*
import com.teamttdvlp.memolang.view.helper.quickLog
import java.util.*

class TextSpeaker (context : Context, language : String) {

    companion object {
        val LANG_NOT_SUPPORTED_ERR = "is not supported"

        val LANG_MISSING_DATA_ERR = "Missing data of"

        val LANG_NOT_AVAILABLE = "is not available"

        val INIT_SPEAKER_FAILED = "Text speaker is not available"

        val INIT_FAILED = 3

        val INIT_SUCCESS = 4
    }

    var error : String? = null

    private var tts : TextToSpeech

    init {
        tts = TextToSpeech(context) { status ->
            if ((status != SUCCESS)){
                error = INIT_SPEAKER_FAILED
            } else {
                setLanguage(clearLangSpecialFeature(language))
            }
        }
        tts.setPitch(1f)
        tts.setSpeechRate(0.75f)
    }

    private fun setLanguage (language : String) {
        var localeIsAvailable = false
        for (locale in Locale.getAvailableLocales()) {
            if (language == locale.displayLanguage)
            quickLog("${locale.displayLanguage} and <${locale.displayCountry}>")
            if ((locale.displayLanguage == language) and (locale.displayCountry.isNotEmpty())) {
                val result = tts.setLanguage(locale)
                quickLog("Result ${locale.displayLanguage} and $result")
                if (result == LANG_MISSING_DATA) {
                    error = "$LANG_MISSING_DATA_ERR $language"
                } else if (result == LANG_NOT_SUPPORTED) {
                    error = "$language $LANG_NOT_SUPPORTED_ERR"
                }
                localeIsAvailable = true
                break
            }
        }
        if (localeIsAvailable.not()) {
            error = "$language $LANG_NOT_AVAILABLE"
        }
    }

    fun speak (text : String) {
        tts.speak(text, QUEUE_FLUSH, null)
    }

    // Convert from "Chinese (Simplified)" --> "Chinese"
    fun clearLangSpecialFeature (lang : String) : String {
        var result = lang
        if ((lang.contains("(")) and (lang.contains(")"))) {
            val openParenthesis = lang.indexOf("(")
            val closeParenthesis = lang.indexOf(")")
            result = result.removeRange(openParenthesis, closeParenthesis + 1).trim()
        }
        return result
    }
}
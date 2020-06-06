package com.teamttdvlp.memolang.model

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.*
import android.speech.tts.UtteranceProgressListener
import java.util.*
import kotlin.collections.HashMap

class TextSpeaker (context : Context, language : String,  textCalled_AfterFinish_Initiating : String ="" ) {

    companion object {
        val LANG_NOT_SUPPORTED_ERR = "is not supported"

        val LANG_MISSING_DATA_ERR = "Missing data of"

        val LANG_NOT_AVAILABLE = "is not available"

        val INIT_SPEAKER_FAILED = "Text speaker is not available"

        val INIT_FAILED = 3

        val INIT_SUCCESS = 4
    }

    var error : String? = null

    private lateinit var tts : TextToSpeech

    private var onSpeakDone : (() -> Unit)? = null

    init {
        tts = TextToSpeech(context) { status ->
            if ((status != SUCCESS)){
                error = INIT_SPEAKER_FAILED
            } else {
                setLanguage(clearLangSpecialFeature(language))
                tts.setPitch(1f)
                tts.setSpeechRate(0.75f)
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener () {
                    override fun onDone(utteranceId: String?) {
                        onSpeakDone?.invoke()
                    }

                    override fun onError(utteranceId: String?) {

                    }

                    override fun onStart(utteranceId: String?) {

                    }

                })
                tts.speak(textCalled_AfterFinish_Initiating, QUEUE_FLUSH, params)
            }
        }
    }

    fun setOnSpeakTextDoneListener (onDone : () -> Unit) {
        if (onSpeakDone != onDone) {
            onSpeakDone = onDone
        }
    }

    private fun setLanguage (language : String) {
        var localeIsAvailable = false
        for (locale in Locale.getAvailableLocales()) {
            if (language == locale.displayLanguage)
            if ((locale.displayLanguage == language) and (locale.displayCountry.isNotEmpty())) {
                val result = tts.setLanguage(locale)
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

    val params = HashMap<String, String>().apply {
        put(Engine.KEY_PARAM_UTTERANCE_ID, "MeanlessID")
    }
    fun speak (text : String) {
        tts.speak(text, QUEUE_FLUSH, params)
    }

    // Convert from "Chinese (Simplified)" --> "Chinese"
    private fun clearLangSpecialFeature (lang : String) : String {
        var result = lang
        if ((lang.contains("(")) and (lang.contains(")"))) {
            val openParenthesis = lang.indexOf("(")
            val closeParenthesis = lang.indexOf(")")
            result = result.removeRange(openParenthesis, closeParenthesis + 1).trim()
        }
        return result
    }

    fun shutDown() {
        tts.shutdown()
    }
}
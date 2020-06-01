package com.teamttdvlp.memolang.model

import android.content.Context

class UseFCActivity_StatusManager (context: Context, var setName : String) {

    val speakerStatusManager : SpeakerStatus by lazy  {
        SpeakerStatus(context, getFormattedSetName())
    }

    // This is just for making sure that there'll be no conflict in SharePreference loading
    // if user name the set with some text similar with Application property's name
    private fun getFormattedSetName () : String {
        return "UseFC ${setName}Mng>"
    }

    fun selfRemove () {
        speakerStatusManager.selfRemove()
    }

    class SpeakerStatus (context : Context, var setName: String) {

        companion object {
            val SPEAK_TEXT_ONLY = 0

            val SPEAK_TRANSLATION_ONLY = 1

            val SPEAK_TEXT_AND_TRANSLATION = 2

            val ON = true

            val OFF = false
        }

        private val SPEAKER_FUNCTION = "spkr_func"

        private val SPEAKER_STATUS = "spkr_status"

        val sharePreference = context.getSharedPreferences(getFormattedSetName(), Context.MODE_PRIVATE)

        fun getFunction () : Int {
            return sharePreference.getInt(SPEAKER_FUNCTION, SPEAK_TEXT_AND_TRANSLATION)
        }

        fun getStatus () : Boolean {
            return sharePreference.getBoolean(SPEAKER_STATUS, ON)
        }

        fun saveFunction (function : Int) {
            sharePreference.edit().putInt(SPEAKER_FUNCTION, function).apply()
        }

        fun saveStatus (status : Boolean) {
            sharePreference.edit().putBoolean(SPEAKER_STATUS, status).apply()
        }

        fun getFormattedSetName () : String {
            return "<set_status: $setName>"
        }

        fun selfRemove () {
            sharePreference.edit().clear().apply()
        }

    }
}


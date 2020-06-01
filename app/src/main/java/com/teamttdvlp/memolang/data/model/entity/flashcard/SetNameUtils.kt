package com.teamttdvlp.memolang.data.model.entity.flashcard

class SetNameUtils {

    companion object {
        @JvmStatic
        fun getSetNameFromLangPair (frontLanguage : String, backLanguage : String) : String {
            return "$frontLanguage - $backLanguage"
        }

        @JvmStatic
        fun getLanguagePairForm (frontLanguage: String, backLanguage: String) : String {
            return "$frontLanguage - $backLanguage"
        }
    }

}
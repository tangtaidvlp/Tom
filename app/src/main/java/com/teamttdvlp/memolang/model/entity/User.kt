package com.teamttdvlp.memolang.model.entity

import com.teamttdvlp.memolang.view.helper.notContains

class User private constructor(id : String, motherLanguage: String, targetLanguage: String) {

    var id : String = id

    var recentUseFlashcardSet : String = ""

    var recentTargetLanguage : String = motherLanguage

    // It should be the language at which user want to be master first
    // Such as the most popular language in the world, English
    var recentSourceLanguage : String = targetLanguage

    var recentUseLanguages : ArrayList<String> = ArrayList()

    var flashcardSetNames : ArrayList<String> = ArrayList()

    var customTypes : ArrayList<String> = ArrayList()

    companion object {

        private var instance : User? = null

        fun getInstance() : User {
            if (instance == null) {
                instance = User("", "", "")
            }
            return instance!!
        }
    }

    fun addLanguageToRecentUseList(language : String) {
        recentUseLanguages.apply {
            if (notContains(language)) {
                if (size == 5) {
                    removeAt(0)
                }
                add(language)
            }
        }

    }
}

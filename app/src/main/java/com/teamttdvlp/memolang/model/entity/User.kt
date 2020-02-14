package com.teamttdvlp.memolang.model.entity

class User private constructor(id : String, motherLanguage: String, targetLanguage: String) {

    val id : String = id

    var recentTargetLanguage : String = motherLanguage

    // It should be the language at which user want to be master first
    // Such as the most popular language in the world, English
    var recentSourceLanguage : String = targetLanguage

    var recentUseLanguages : ArrayList<String> = ArrayList()

    companion object {

        private var instance : User? = null

        fun getInstance() : User? {
            return instance
        }

        fun setInstanceInfo (id : String, motherLanguage: String, targetLanguage: String) {
            if (instance == null) {
                instance = User(id, motherLanguage, targetLanguage)
            } else {
                instance!!.recentTargetLanguage = motherLanguage
                instance!!.recentSourceLanguage = targetLanguage
            }
        }
    }
}

package com.teamttdvlp.memolang.model.model

class User private constructor(id : String, motherLanguage: String, targetLanguage: String) {

    val id : String = id

    var motherLanguage : String = motherLanguage
        set(value) {
            if (value != "") recentUseLanguage.add(value)
        }

    // It should be the language at which user want to be master first
    // Such as the most popular language in the world, English
    var targetLanguage : String = targetLanguage
        set(value) {
            if (value != "") recentUseLanguage.add(value)
        }

    var recentUseLanguage : ArrayList<String> = ArrayList()

    companion object {

        private var instance : User? = null

        fun getInstance() : User? {
            return instance
        }

        fun createInstance (id : String, motherLanguage: String, targetLanguage: String) {
            if (instance == null) {
                instance = User(id, motherLanguage, targetLanguage)
            } else
                throw Exception("User object has been created")
        }
    }

    /**
     * This class is created with purpose that cast from Firebase Object to User Object
     * FirebaseObject -> MockUser -> User
     * Because class User can't have public empty constructor, only can be created
     * by using Singleton pattern
     */
    data class MockUser (val id : String = "",
                         var motherLanguage : String = "",
                         var targetLanguage : String = "")
}

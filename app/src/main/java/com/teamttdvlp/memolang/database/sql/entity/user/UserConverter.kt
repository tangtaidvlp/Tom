package com.teamttdvlp.memolang.database.sql.entity.user

import com.teamttdvlp.memolang.model.entity.User

class UserConverter {

    companion object {

        fun toUserEntity (user : User) : UserEntity {
            return UserEntity().apply {
                id = user.id
                motherLanguage = user.recentTargetLanguage
                targetLanguage = user.recentSourceLanguage
                recentUseLanguages = user.recentUseLanguages
                flashcardSetNames = user.flashcardSetNames
                recentUseFlashcardSet = user.recentUseFlashcardSet
                customTypes = user.customTypes
            }
        }

    }

}
package com.teamttdvlp.memolang.model.sqlite.converter

import com.teamttdvlp.memolang.model.model.User
import com.teamttdvlp.memolang.model.sqlite.entity.UserEntity

class UserConverter {

    companion object {

        fun toUserEntity (user : User) : UserEntity {
            return UserEntity().apply {
                id = user.id
                motherLanguage = user.motherLanguage
                targetLanguage = user.targetLanguage
            }
        }

    }

}
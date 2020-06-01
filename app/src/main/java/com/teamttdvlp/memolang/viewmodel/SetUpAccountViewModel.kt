package com.teamttdvlp.memolang.viewmodel

import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.data.model.entity.flashcard.SetNameUtils
import com.teamttdvlp.memolang.data.model.entity.user.User
import com.teamttdvlp.memolang.model.repository.UserRepos
import com.teamttdvlp.memolang.model.repository.UserUsingHistoryRepos
import com.teamttdvlp.memolang.model.AddFlashcardSharedPreference
import com.teamttdvlp.memolang.model.SearchOnlineSharedPreference
import com.teamttdvlp.memolang.model.UserInfoStatusSharedPreference
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.view.activity.iview.SetUpAccountView
import com.teamttdvlp.memolang.view.base.BaseViewModel


/**
 * @see com.teamttdvlp.memolang.view.activity.SetUpAccountActivity
 */
class SetUpAccountViewModel(
    var userRepos : UserRepos,
    var userUsingHistoryRepos: UserUsingHistoryRepos,
    var flashcardSetRepos: FlashcardSetRepos,
    var userInfoStatusSharedPreference: UserInfoStatusSharedPreference,
    var addFlashcardSharedPreference: AddFlashcardSharedPreference,
    var searchOnlineSharedPreference: SearchOnlineSharedPreference) : BaseViewModel<SetUpAccountView>() {


    fun checkUserInfoSetUpStatus () {
        val userDidSetUpInfoBefore = userInfoStatusSharedPreference.didUserSetUpBasicInfoBefore()
        if (userDidSetUpInfoBefore) {
            getUserInfoAndNavigateToMenu()
        }
    }

    fun getUserInfoAndNavigateToMenu () {
        userRepos.triggerGetUser { user ->
            initUser(user!!)
        }
        view.navigateToMenuScreen()
    }

    private fun setUpUserDefaultInfo (defaultFrontCardLang : String, defaultBackCardLang : String) {
        addFlashcardSharedPreference.currentFrontCardLanguage = defaultFrontCardLang
        addFlashcardSharedPreference.currentBackCardLanguage = defaultBackCardLang

        searchOnlineSharedPreference.currentSourceLanguage = defaultFrontCardLang
        searchOnlineSharedPreference.currentTargetLanguage = defaultBackCardLang
    }

    fun initUser (userInfo : User) {
        setUser(userInfo)
    }

    fun createAndSaveUserInfoStatus(defaultFrontCardLanguage: String, defaultBackCardLanguage: String) {
        setUpUserDefaultInfo(defaultFrontCardLanguage, defaultBackCardLanguage)
        addToUser_UsedLanguageList(defaultFrontCardLanguage)
        addToUser_UsedLanguageList(defaultBackCardLanguage)
        createUserDefaultSetName(defaultFrontCardLanguage, defaultBackCardLanguage)
        saveAll_Information()
    }

    private fun createUserDefaultSetName (frontLang : String, backLang : String) {
        val defaultFlashcardSet = FlashcardSet("", frontLang, backLang)
        getUser().lastest_Used_FlashcardSetName = defaultFlashcardSet.name
        flashcardSetRepos.insert(defaultFlashcardSet)
    }

    private fun addToUser_UsedLanguageList(language : String) {
        userUsingHistoryRepos.addToUsedLanguageList(language)
    }

    private fun saveAll_Information () {
        userRepos.insertUser(getUser())
        userUsingHistoryRepos.saveUsingHistoryInfo()
        userInfoStatusSharedPreference.markUserInfo_IsSetUp()
    }

}

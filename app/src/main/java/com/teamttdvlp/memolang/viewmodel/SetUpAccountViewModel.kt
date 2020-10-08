package com.teamttdvlp.memolang.viewmodel

import com.teamttdvlp.memolang.data.model.entity.flashcard.DEFAULT_SET_NAME
import com.teamttdvlp.memolang.data.model.entity.flashcard.Deck
import com.teamttdvlp.memolang.data.model.entity.user.User
import com.teamttdvlp.memolang.model.UserInfoStatusSharedPreference
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.model.repository.UserRepos
import com.teamttdvlp.memolang.model.repository.UserUsingHistoryRepos
import com.teamttdvlp.memolang.model.sharepref.AddFlashcardActivitySharePref
import com.teamttdvlp.memolang.model.sharepref.EngVietDictionaryActivitySharePref
import com.teamttdvlp.memolang.model.sharepref.SearchOnlineActivitySharePref
import com.teamttdvlp.memolang.view.activity.iview.SetUpAccountView
import com.teamttdvlp.memolang.view.base.BaseViewModel


/**
 * @see com.teamttdvlp.memolang.view.activity.SetUpAccountActivity
 */
class SetUpAccountViewModel(
    var userRepos: UserRepos,
    var userUsingHistoryRepos: UserUsingHistoryRepos,
    var flashcardSetRepos: FlashcardSetRepos,
    var userInfoStatusSharedPreference: UserInfoStatusSharedPreference,
    var addFlashcardSharedPreference: AddFlashcardActivitySharePref,
    var engVietDictionaryActivitySharePref: EngVietDictionaryActivitySharePref,
    var searchOnlineSharedPreference: SearchOnlineActivitySharePref
) : BaseViewModel<SetUpAccountView>() {


    fun checkUserInfoSetUpStatus() {
        val userDidSetUpInfoBefore = userInfoStatusSharedPreference.didUserSetUpBasicInfoBefore()
        if (userDidSetUpInfoBefore) {
            getUserInfoAndNavigateToMenu()
        }
    }

    fun getUserInfoAndNavigateToMenu() {
        userRepos.triggerGetUser { user ->
            initUser(user!!)
        }
        view.navigateToMenuScreen()
    }

    private fun setUpUserDefaultInfo (defaultFrontCardLang : String, defaultBackCardLang : String) {
        addFlashcardSharedPreference.currentFrontCardLanguage = defaultFrontCardLang
        addFlashcardSharedPreference.currentBackCardLanguage = defaultBackCardLang

        searchOnlineSharedPreference.currentSourceLang = defaultFrontCardLang
        searchOnlineSharedPreference.currentTargetLang = defaultBackCardLang
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

    private fun createUserDefaultSetName(frontLang: String, backLang: String) {
        val defaultFlashcardSet = Deck(DEFAULT_SET_NAME, frontLang, backLang)
        setDefaultUsedSetName(defaultFlashcardSet.name)
        flashcardSetRepos.insert(defaultFlashcardSet)
    }

    private fun setDefaultUsedSetName(setName: String) {
        addFlashcardSharedPreference.lastUsedFlashcardSetName = setName
        searchOnlineSharedPreference.lastUsedFlashcardSetName = setName
        engVietDictionaryActivitySharePref.lastUsedFlashcardSetName = setName
    }

    private fun addToUser_UsedLanguageList(language: String) {
        userUsingHistoryRepos.addToUsedLanguageList(language)
    }

    private fun saveAll_Information() {
        userRepos.insertUser(getUser())
        userUsingHistoryRepos.saveUsingHistoryInfo()
        userInfoStatusSharedPreference.markUserInfo_IsSetUp()
    }

}

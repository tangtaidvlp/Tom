package com.teamttdvlp.memolang.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.os.StrictMode
import androidx.databinding.ObservableField
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.teamttdvlp.memolang.R
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.flashcard.FlashcardSet
import com.teamttdvlp.memolang.data.model.entity.language.Language
import com.teamttdvlp.memolang.model.AddFlashcardExecutor
import com.teamttdvlp.memolang.model.repository.FlashcardSetRepos
import com.teamttdvlp.memolang.model.repository.UserRepos
import com.teamttdvlp.memolang.model.repository.UserUsingHistoryRepos
import com.teamttdvlp.memolang.model.sharepref.SearchOnlineActivitySharePref
import com.teamttdvlp.memolang.view.activity.iview.SearchVocabularyView
import com.teamttdvlp.memolang.view.base.BaseViewModel
import com.teamttdvlp.memolang.view.helper.log
import java.io.IOException
import java.util.*

class SearchOnlineViewModel(
    var app: Application,
    private var userRepos: UserRepos,
    private var flashcardSetRepos: FlashcardSetRepos,
    private var addFlashcardExecutor: AddFlashcardExecutor,
    private var userUsingHistoryRepos: UserUsingHistoryRepos,
    private var searchOnline_SharedPref: SearchOnlineActivitySharePref
) : BaseViewModel<SearchVocabularyView>() {

    private var translation = ObservableField<String>()
    private var translatedText: String? = null
    private var connected: Boolean = false
    private lateinit var translate: Translate
    private lateinit var onTranslateListener: OnTranslateListener
    private var requestList = ArrayList<String>()
    private val connectivityManager: ConnectivityManager


    init {
        getTranslateService()
        connectivityManager = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun getAllRecentOnlineSearchedFlashcard (onGet : (ArrayList<Flashcard>) -> Unit) {
        userUsingHistoryRepos.getRecent_OnlineSearchedFlashcardList(onGet)
    }

    fun addFlashcard_And_UpdateUserInfo (newCard: Flashcard, onAddSuccess : (newCard : Flashcard) -> Unit = {}) : Flashcard{

        addFlashcardExecutor.addFlashcardAndUpdateFlashcardSet (newCard) { isSuccess, insertedCardId, exception ->
            if (isSuccess) {
                newCard.id = insertedCardId.toInt()
                updateUserInfo(newCard)

                onAddSuccess.invoke(newCard)
                view.onAddFlashcardSuccess()
            } else {
                log("Add Failed To Offline DB")
                exception?.printStackTrace()
            }
        }

        return newCard
    }

    private fun updateUserInfo (newCard : Flashcard) {
        addSearchedCardToHistory(newCard)
        userUsingHistoryRepos.addToRecent_OnlineSearchedFlashcardList(newCard)
        userUsingHistoryRepos.addToRecent_AddedFlashcardList(newCard)
        updateLastUsedFlashcardSetName(newCard.setOwner)
        addToUserOwnCardTypes(newCard.type)
    }

    private fun addToUserOwnCardTypes(cardType: String) {
        getUser().addToCardTypeList(cardType)
    }

    fun getUserOwnCardTypes(): ArrayList<String> {
        return getUser().ownCardTypeList
    }


    private fun updateLastUsedFlashcardSetName(setName: String) {
        searchOnline_SharedPref.lastUsedFlashcardSetName = setName
    }

    private fun addSearchedCardToHistory(newCard: Flashcard) {
        userUsingHistoryRepos.addToRecent_OnlineSearchedFlashcardList(newCard)
    }

    fun addToUsedLanguageList(language: String) {
        userUsingHistoryRepos.addToUsedLanguageList(language)
    }

    fun updateUserRecentTargetLang (targetLang : String) {
        searchOnline_SharedPref.currentBackCardLanguage = targetLang
        userRepos.updateUser(getUser())
    }

    fun updateUserRecentSourceLang (sourceLang : String) {
        searchOnline_SharedPref.currentFrontCardLanguage = sourceLang
        userRepos.updateUser(getUser())
    }

    fun getTranslation () : ObservableField<String> = translation

    // Translate PART

    private var timeCounterThread : Thread? = null
    private var uiPostingHandler = Handler()
    private var text : String = ""
    private var sourceLang : String = ""
    private var targetLang : String = ""
    private val DURATION_TO_CHECK_USER_DONE_TYPING = 400
    private var timeCounter = DURATION_TO_CHECK_USER_DONE_TYPING

    fun translateText(text : String, sourceLang: String, targetLang: String, onTranslateListener : OnTranslateListener) {

        val hasConnection = checkInternetConnection()
        view.onCheckConnectionWhenSearch(hasConnection)
        if (hasConnection.not()) return

        this.text = text
        if (this.text == "") {
            translation.set("")
            return
        }
        this.sourceLang = Language.getLanguageValue(sourceLang) + ""
        this.targetLang = Language.getLanguageValue(targetLang) + ""
        this.onTranslateListener = onTranslateListener
        resetTimeCounter()
        if (userIsTyping()) {
            return
        } else if (userStartTyping()){
            startCheckUserDoneTypingAndThenTranslate()
        }
    }

    private fun startTranslate (whenTranslateDone : () -> Unit) {
        val requestText = this.text
        if (this.text == "") {
            return
        }
        try {
            requestList.add(requestText)
            val translation = translate.translate(
                this.text,
                Translate.TranslateOption.sourceLanguage(this.sourceLang),
                Translate.TranslateOption.targetLanguage(this.targetLang),
                Translate.TranslateOption.model("base")
            )

            translatedText = translation.translatedText
            translatedText = htmlDecode(translatedText + "")
            // This statement must wait the translation code block above complete
            // so while translating is not done cause of slow connection
            // User can clear all text so those translation is no longer necessary
            // and won't be up date to UI
            // The Request List has been cleared by cancelAllCurrentTranslating() function
            val request_IsStillUseful = requestList.contains(requestText)
            if (request_IsStillUseful) {
                requestList.remove(requestText)
                whenTranslateDone.invoke()
            }
        } catch (ex : Exception) {
            uiPostingHandler.post {
                onTranslateListener.onFailed(ex)
            }
            requestList.remove(requestText)
        }
    }

    private fun updateTranslationText () {
            uiPostingHandler.post {
                this@SearchOnlineViewModel.translation.set(translatedText)
            }
    }

    private fun resetTimeCounter () {
        timeCounter = DURATION_TO_CHECK_USER_DONE_TYPING
    }

    // If user is not typing means that he has started and been typing, time counter has been initialized and running
    private fun userIsTyping () : Boolean {
        return timeCounterThread != null
    }

    // If user start typing it means timeCounter is not initialized
    private fun userStartTyping () : Boolean {
        return timeCounterThread == null
    }

    private fun userDoneTyping (then : (() -> Unit)?) {
        timeCounterThread = null
        then?.invoke()
    }

    private fun startCheckUserDoneTypingAndThenTranslate () {
        timeCounterThread = Thread (Runnable {
            while (timeCounter > 0) {
                timeCounter -= 100
                Thread.sleep(100)
            }
            userDoneTyping (then = {
                startTranslate (whenTranslateDone = {
                    handleUI_WithTranslation()
                })
            })
        })
        timeCounterThread!!.start()
    }

    fun cancelAllCurrentTranslating () {
        clearAllRequest()
    }

    private fun handleUI_WithTranslation () {
        uiPostingHandler.post {
            onTranslateListener.onSuccess()
        }
        val thereIsNoTransRequestLeft = requestList.size == 0
        if (thereIsNoTransRequestLeft) {
            uiPostingHandler.post {
                view.hideTranslatingProgressBar()
            }
        }
        updateTranslationText()
    }

    private fun clearAllRequest () {
        requestList.clear()
    }

    private fun getTranslateService() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            app.resources.openRawResource(R.raw.translate_configuration).use { inputStream ->
                //Get credentials:
                val myCredentials = GoogleCredentials.fromStream(inputStream)
                //Set credentials and get translateText service:
                val translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build()
                translate = translateOptions.service
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }

    /**
     * Because some rules of HTML encode and decode, the translation we get has some weird phrase
     * like &#39; (stand for the sign ') => I'm = I&#39;m
     * and some other sign such as: ", <, >, &, ...
     * There're many pair of encode-decode signs like that but count of the most popular signs is 5
     */
    private fun htmlDecode (translation : String) : String {
        var result = translation
        if (translation.contains("&quot;")) {
            result = translation.replace("&quot;", "\"")
        }

        if (translation.contains("&#39;")) {
            result = translation.replace("&#39;", "\'")
        }

        if (translation.contains("&amp;")) {
            result = translation.replace("&amp;", "&")
        }

        if (translation.contains("&lt;")) {
            result = translation.replace("&lt;", "<")
        }

        if (translation.contains("&gt;")) {
            result = translation.replace("&gt;", ">")
        }

        return result
    }

    private fun checkInternetConnection() : Boolean {
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(
        ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED
        return connected
    }

    fun getUserUsedLanguageList(onGet : (ArrayList<String>) -> Unit) {
        userUsingHistoryRepos.getUsedLanguage(onGet)
    }

    fun getCurrentSourceLanguage(): String {
        return searchOnline_SharedPref.currentSourceLang
    }

    fun getCurrentTargetLanguage(): String {
        return searchOnline_SharedPref.currentTargetLang
    }

    fun getLastedUsedFlashcardSet() : String{
        return searchOnline_SharedPref.lastUsedFlashcardSetName
    }

    fun getAllFlashcardSetWithNOCardList (onGet : (ArrayList<FlashcardSet>) -> Unit) {
        flashcardSetRepos.getAll_CardSet_WithNOCardList {
            if (it != null) {
                onGet(it)
            }
        }
    }

    fun saveSearchHistory() {
        userUsingHistoryRepos.saveUsingHistoryInfo()
        userRepos.updateUser(getUser())
    }

    interface OnTranslateListener {

        fun onSuccess()

        fun onFailed (ex : Exception)

    }

}
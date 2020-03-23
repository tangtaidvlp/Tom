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
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.model.entity.Language
import com.teamttdvlp.memolang.model.entity.User
import com.teamttdvlp.memolang.database.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.database.sql.entity.flashcard.FlashcardConverter
import com.teamttdvlp.memolang.database.sql.entity.user.UserConverter
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.database.sql.repository.UserRepository
import com.teamttdvlp.memolang.database.sql.repository.UserSearchHistoryRepository
import com.teamttdvlp.memolang.model.RecentAddedFlashcardManager
import com.teamttdvlp.memolang.view.activity.iview.SearchVocabularyView
import com.teamttdvlp.memolang.view.base.BaseAndroidViewModel
import com.teamttdvlp.memolang.view.helper.notContains
import com.teamttdvlp.memolang.view.helper.quickLog
//import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager
import java.io.IOException
import java.util.*

class SearchOnlineViewModel(var app: Application,
                            var database : MemoLangSqliteDataBase,
                            var recentAddedFlashcardManager: RecentAddedFlashcardManager,
                            var userRepository : UserRepository,
                            var flashcardRepository: FlashcardRepository,
                            var userSearchHistoryRepository: UserSearchHistoryRepository) : BaseAndroidViewModel<SearchVocabularyView>(app) {

    private var translation = ObservableField<String>()
    private var translatedText: String? = null
    private var connected: Boolean = false
    private lateinit var translate: Translate
    private lateinit var onTranslateListener : OnTranslateListener
    private var requestList = ArrayList<String>()
    private val connectivityManager : ConnectivityManager

    private var haveJustBeenAddedFlashcard : Flashcard? = null

    init {
        getTranslateService()
        connectivityManager = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun setJustBeenAddedCard (addedCard : Flashcard) {
        this.haveJustBeenAddedFlashcard = addedCard
    }

    fun getjustBeenAddedCard () : Flashcard {
        return haveJustBeenAddedFlashcard!!
    }

    fun addFlashcardToOfflineDB (newCard : Flashcard, onInsertListener : (isSuccess : Boolean, cardId : Long, ex : Exception?) -> Unit) {
        flashcardRepository.insertFlashcard(newCard, onInsertListener)
    }

    fun addFlashcard (sourceLang : String, targetLang : String,
                      setName : String, type : String,
                      text : String, translation : String,
                      example : String, meanOfExample : String,  pronunciation : String, onAddSuccess : (newCard : Flashcard) -> Unit = {}) : Flashcard{

        val langInfo = "$sourceLang - $targetLang"
        val validSetName = if (setName.isNotEmpty()) setName else langInfo
        val newCard = Flashcard(0,
            text, translation, langInfo, validSetName,
            example,meanOfExample, "", type, pronunciation)

        addFlashcardToOfflineDB (newCard) { isSuccess, insertedCardId, exception ->
            if (isSuccess) {
                quickLog("Add Success To Offline DB")
                // If add to offline database successfully, we could get its unique id and pass to Online Database
                // and Search History
                newCard.id = insertedCardId.toInt()
                setJustBeenAddedCard(newCard)
                addSearchedCardToHistory(insertedCardId.toInt(), newCard.createdAt)
                view.onAddFlashcardSuccess()
                onAddSuccess.invoke(newCard)
                recentAddedFlashcardManager.add(newCard)
                updateUserFlashcardSets(newCard.setName)
            } else {
                quickLog("Add Failed To Offline DB")
            }
        }

        return newCard
    }

    fun updateUserFlashcardSets (setName : String) {
        User.getInstance().recentUseFlashcardSet = setName
        if (User.getInstance().flashcardSetNames.notContains(setName)) {
            User.getInstance().flashcardSetNames.add(setName)
        }
        userRepository.updateUser(UserConverter.toUserEntity(User.getInstance()))
    }

    fun addSearchedCardToHistory (cardId : Int, searchedTime : Date) {
        userSearchHistoryRepository.addNewCard(cardId, searchedTime)
    }

    fun getAllSearchHistoryInfo (onGetAllSearchHistory : (ArrayList<Flashcard>) -> Unit) {
        userSearchHistoryRepository.getAllHistory {
            val idSet = ArrayList<Int>()
            for (holder in it) {
                idSet.add(holder.cardId)
            }
            getAllFlashcardByIds(idSet, onGetAllSearchHistory)
        }
    }

    fun getAllFlashcardByIds (ids : ArrayList<Int>, onGetSuccess : (ArrayList<Flashcard>) -> Unit) {
        flashcardRepository.getFlashcardsByIds(ids) {
            onGetSuccess.invoke(FlashcardConverter.toNormalCardCollection(it))
        }
    }

    fun updateUserRecentTargetLang (targetLang : String) {
        User.getInstance().recentTargetLanguage = targetLang
        userRepository.updateUser(UserConverter.toUserEntity(User.getInstance()))
    }

    fun updateUserRecentSourceLang (sourceLang : String) {
        User.getInstance().recentSourceLanguage = sourceLang
        userRepository.updateUser(UserConverter.toUserEntity(User.getInstance()))
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
        if (!hasConnection) return

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
            quickLog("REQUEST COUNT: " + requestList.size)
            val translation = translate.translate(
                this.text,
                Translate.TranslateOption.sourceLanguage(this.sourceLang),
                Translate.TranslateOption.targetLanguage(this.targetLang),
                Translate.TranslateOption.model("base")
            )

            translatedText = translation.getTranslatedText()
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
        timeCounterThread = Thread (object : Runnable {
            override fun run() {
                while (timeCounter > 0) {
                    timeCounter -= 100
                    Thread.sleep(100)
                }
                userDoneTyping (then = {
                    startTranslate (whenTranslateDone = {
                        handleUI_WithTranslation()
                    })
                })
            }
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
        } else {
            for ( i in requestList) {
                quickLog("request: " + i)
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


    fun checkInternetConnection() : Boolean {
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(
        ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED
        return connected
    }

    interface OnTranslateListener {

        fun onSuccess()

        fun onFailed (ex : Exception)

    }

}
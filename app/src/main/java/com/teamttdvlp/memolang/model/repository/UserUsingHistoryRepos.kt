package com.teamttdvlp.memolang.model.repository

import android.os.AsyncTask
import com.teamttdvlp.memolang.data.model.entity.flashcard.Flashcard
import com.teamttdvlp.memolang.data.model.entity.user.UserUsingHistory
import com.teamttdvlp.memolang.data.model.other.new_vocabulary.TypicalRawVocabulary
import com.teamttdvlp.memolang.data.sql.MemoLangSqliteDataBase
import com.teamttdvlp.memolang.data.sql.dao.UserUsingHistoryDAO
import com.teamttdvlp.memolang.view.helper.log
import com.teamttdvlp.memolang.view.helper.notContains

class UserUsingHistoryRepos (database : MemoLangSqliteDataBase) {

    private val userUsingHistoryDAO = database.getUserUsingHistoryDAO()

    private lateinit var userUsingHistory : UserUsingHistory

    private lateinit var recentAddedFlashcards_Manager: RecentAddedFlashcardsManager

    private lateinit var recentOnlineSearchedFlashcards_Manager: RecentOnlineSearchedFlashcardsManager

    private lateinit var recentSearchedVoca_Manager: RecentSearchedVocabularyManager

    private lateinit var usedLanguages_Manager: UsedLanguagesManager


    init {
        initUserUsingHistory ()
    }

    // RECENT ADDED FLASHCARDS

    fun getRecent_AddedFlashcardList (onGet : (ArrayList<Flashcard>) -> Unit) {
        doOn_UsingHistoryInitialized {
            onGet.invoke(recentAddedFlashcards_Manager.get())
        }
    }

    fun addToRecent_AddedFlashcardList (newCard: Flashcard) {
        doOn_UsingHistoryInitialized {
            recentAddedFlashcards_Manager.add(newCard)
        }
    }



    // RECENT ONLINE SEARCHED FLASHCARDS

    fun getRecent_OnlineSearchedFlashcardList (onGet: (ArrayList<Flashcard>) -> Unit) {
        doOn_UsingHistoryInitialized {
            onGet.invoke(recentOnlineSearchedFlashcards_Manager.get())
        }
    }

    fun addToRecent_OnlineSearchedFlashcardList (newCard: Flashcard) {
         doOn_UsingHistoryInitialized {
             recentOnlineSearchedFlashcards_Manager.add(newCard)
         }
    }



    // RECENT SEARCHED VOCABULARIES

    fun getRecent_SearchedVocabularyList (onGet: (ArrayList<TypicalRawVocabulary>) -> Unit) {
        doOn_UsingHistoryInitialized {
            onGet(recentSearchedVoca_Manager.get())
        }
    }

    fun addToRecent_SearchedVocabularyList (vocabulary : TypicalRawVocabulary) {
        doOn_UsingHistoryInitialized {
            recentSearchedVoca_Manager.add(vocabulary)
        }
    }


    // USED LANGUAGES

    fun getUsedLanguage(onGet : (ArrayList<String>) -> Unit) {
        doOn_UsingHistoryInitialized {
            onGet(usedLanguages_Manager.get())
        }
    }

    fun addToUsedLanguageList(newLang: String) {
        doOn_UsingHistoryInitialized {
            usedLanguages_Manager.add(newLang)
        }
    }


    fun saveUsingHistoryInfo () {
        doOn_UsingHistoryInitialized {
            UpdateHistoryTask(userUsingHistoryDAO).update(userUsingHistory)
        }
    }



    private fun doOn_UsingHistoryInitialized  (statement: () -> Unit) {
        if (::userUsingHistory.isInitialized.not()) {
            initUserUsingHistory (onFinish = {
                statement()
            })
        } else {
            statement()
        }
    }

    private fun initUserUsingHistory (onFinish : (() -> Unit)? = null) {
        GetHistoryTask (userUsingHistoryDAO, onGet = { isSuccess, data, ex ->
            if (isSuccess) {
                if (data != null) {
                    userUsingHistory = data
                } else {
                    userUsingHistory = UserUsingHistory()
                    userUsingHistory.usedLanguageList = ArrayList<String>()
                }


                recentAddedFlashcards_Manager = RecentAddedFlashcardsManager()
                recentOnlineSearchedFlashcards_Manager =  RecentOnlineSearchedFlashcardsManager()
                recentSearchedVoca_Manager =  RecentSearchedVocabularyManager()
                usedLanguages_Manager =  UsedLanguagesManager()

                onFinish?.invoke()
            } else {
                log("Get UserUsingHistory has errors")
            }
        }).execute()
    }

    private class UpdateHistoryTask (private val userUsingHistoryDAO: UserUsingHistoryDAO): AsyncTask<UserUsingHistory, Unit, Unit>() {

        override fun doInBackground(vararg params: UserUsingHistory?) {
            try {
                val userUsingHistory = params[0]
                userUsingHistoryDAO.updateUserHistory(userUsingHistory!!)
                log("Update success")
            } catch (ex : Exception) {
                log("UPDATE FAILED")
                ex.printStackTrace()
            }
        }

        fun update (userUsingHistory: UserUsingHistory) {
            execute(userUsingHistory)
        }

    }

    private class GetHistoryTask (private val userUsingHistoryDAO: UserUsingHistoryDAO, private val onGet : (isSuccess : Boolean,
                                                             data : UserUsingHistory?, ex : Exception?) -> Unit) : AsyncTask<Unit, Unit, Unit>() {

        override fun doInBackground(vararg params: Unit?) {
            try {
                val userHistory = userUsingHistoryDAO.getUsingHistory()
                onGet.invoke(true, userHistory, null)
            } catch (ex : Exception) {
                onGet.invoke(false, null, ex)
                ex.printStackTrace()
            }
        }

    }

    private inner class RecentAddedFlashcardsManager {

        private val recentAddedCardList = userUsingHistory.recentAddedFlashcardList

        private val MAX_COUNT = 20

        fun add (flashcard : Flashcard) {
            if (recentAddedCardList.notContains(flashcard)) {
                recentAddedCardList.add(0, flashcard)
                if (recentAddedCardList.size >= MAX_COUNT) {
                    recentAddedCardList.removeAt(recentAddedCardList.size - 1)
                }
            } else if (recentAddedCardList.contains(flashcard)) {
                recentAddedCardList.remove(flashcard)
                recentAddedCardList.add(0, flashcard)
            }
        }

        fun get () : ArrayList<Flashcard> = recentAddedCardList

    }

    private inner class RecentOnlineSearchedFlashcardsManager {

        private val recentOnlineSearchedCardList = userUsingHistory.recentOnlineSearchedFlashcardList

        private val MAX_COUNT = 20

        fun add (flashcard : Flashcard) {
            if (recentOnlineSearchedCardList.notContains(flashcard)) {
                recentOnlineSearchedCardList.add(0, flashcard)
                if (recentOnlineSearchedCardList.size >= MAX_COUNT) {
                    recentOnlineSearchedCardList.removeAt(recentOnlineSearchedCardList.size - 1)
                }
            } else if (recentOnlineSearchedCardList.contains(flashcard)) {
                recentOnlineSearchedCardList.remove(flashcard)
                recentOnlineSearchedCardList.add(0, flashcard)
            }
        }

        fun get () : ArrayList<Flashcard> = recentOnlineSearchedCardList

    }

    private inner class RecentSearchedVocabularyManager {

        private val recentSearchedVocaList = userUsingHistory.recentSearchedVocaList

        private val MAX_COUNT = 20

        fun add(vocabulary: TypicalRawVocabulary) {
            if (recentSearchedVocaList.notContains(vocabulary)) {
                recentSearchedVocaList.add(0, vocabulary)
                if (recentSearchedVocaList.size >= MAX_COUNT) {
                    recentSearchedVocaList.removeAt(recentSearchedVocaList.size - 1)
                }
            } else if (recentSearchedVocaList.contains(vocabulary)) {
                recentSearchedVocaList.remove(vocabulary)
                recentSearchedVocaList.add(0, vocabulary)
            }
        }

        fun get () : ArrayList<TypicalRawVocabulary> = recentSearchedVocaList

    }

    private inner class UsedLanguagesManager {

        private val usedLanguageList = userUsingHistory.usedLanguageList

        private val MAX_COUNT = 5

        fun add (language : String) {
            if (usedLanguageList.notContains(language)) {
                usedLanguageList.add(0, language)
                if (usedLanguageList.size > MAX_COUNT) {
                    usedLanguageList.removeAt(usedLanguageList.size - 1)
                }
            } else if (usedLanguageList.contains(language)) {
                usedLanguageList.remove(language)
                usedLanguageList.add(0, language)
            }
        }

        fun get () : ArrayList<String> = usedLanguageList

    }



}
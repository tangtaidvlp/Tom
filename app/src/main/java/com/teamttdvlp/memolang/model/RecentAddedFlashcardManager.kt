package com.teamttdvlp.memolang.model

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.text.isDigitsOnly
import com.teamttdvlp.memolang.database.sql.entity.flashcard.FlashcardConverter
import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
import com.teamttdvlp.memolang.model.entity.flashcard.Flashcard

class RecentAddedFlashcardManager (
    private var context : Context,
    private var flashcardRepository: FlashcardRepository) {

    private val RECENT_ADDED_FLASHCARD = "rcntaddedfc"

    private val CARD_ID_LIST = "rcntaddfcid"

    private val ID_DEVIDER = "-"

    private var cardIdList = ArrayList<Int>()

    init {
        loadCardIdList()
    }

    fun add (flashcard : Flashcard) {
        cardIdList.add(flashcard.id)
        val sharePref = context.getSharedPreferences(RECENT_ADDED_FLASHCARD, MODE_PRIVATE)
        val editor = sharePref.edit()
        var idListTextForm = getCardIdlistTextForm()
        idListTextForm += ID_DEVIDER + flashcard.id
        editor.putString(CARD_ID_LIST, idListTextForm)
        editor.apply()
    }

    private fun getCardIdlistTextForm () : String {
        var result = ""
        for (id in cardIdList) {
            result += id.toString() + ID_DEVIDER
        }
        result = result.removeSuffix(ID_DEVIDER)
        return result
    }

    private fun loadCardIdList () {
        val sharePref = context.getSharedPreferences(RECENT_ADDED_FLASHCARD, MODE_PRIVATE)
        val idListInTextForm = sharePref.getString(CARD_ID_LIST, "").toString()
        if (idListInTextForm.isNotEmpty()) {
            for (id in idListInTextForm.split(ID_DEVIDER)) {
                if (id.isDigitsOnly() and id.isNotEmpty()) {
                    cardIdList.add(id.toInt())
                }
            }
        }
    }

    fun getRecentAddedFlashcard (onGetSuccess : (ArrayList<Flashcard>) -> Unit) {
        return flashcardRepository.getFlashcardsByIds(getRecentAddedFlashcardId()) { result ->
            onGetSuccess(FlashcardConverter.toNormalCardCollection(result))
        }
    }

    private fun getRecentAddedFlashcardId () : ArrayList<Int> {
        return cardIdList
    }

}
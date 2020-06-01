//package com.teamttdvlp.memolang.viewmodel
//
//import androidx.lifecycle.MutableLiveData
//import com.teamttdvlp.memolang.data.entity.model.flashcard.Flashcard
//import com.teamttdvlp.memolang.database.sql.repository.FlashcardRepository
//import com.teamttdvlp.memolang.view.activity.iview.SearchEditFlashcardView
//import com.teamttdvlp.memolang.view.base.BaseViewModel
//import com.teamttdvlp.memolang.view.helper.quickLog
////import com.teamttdvlp.memolang.viewmodel.reusable.OnlineFlashcardDBManager
//import java.lang.Exception
//
//class SearchEditFlashcardViewModel (/*var onlineFlashcardDBManager: OnlineFlashcardDBManager,*/ var flashcardRepository : FlashcardRepository) : BaseViewModel<SearchEditFlashcardView>() {
//
//    private lateinit var originalCard : Flashcard
//
//    val bindOGCard = MutableLiveData<Flashcard>()
//
//    fun setOriginalCard (flashcard : Flashcard) {
//        this.originalCard = flashcard
//        bindOGCard.value = flashcard
//    }
//
//    fun updateCard (newSourceLang : String, newTargetLang : String, setName : String, newType : String, newText : String, newTrans : String, newUsing : String, newPronunciation : String) {
//        if (doesUserChangeInfo("$newSourceLang-$newTargetLang", newType, newText, newTrans, newUsing, newPronunciation)) {
//            if ((newText == "") or (newText == null)) {
//                view.showTextInputError()
//                return
//            }
//
//            if ((newTrans == "") or (newTrans == null)) {
//                view.showTranslationInputError()
//                return
//            }
//
//            val languagePair = "$newSourceLang-$newTargetLang"
//            val newCard = Flashcard(
//                originalCard.id,
//                newText,
//                newTrans,
//                setName,
//                languagePair,
//                newUsing,
//                "", "",
//                newType,
//                newPronunciation
//            )
//            updateOfflineFlashcard(newCard) { isSuccess, insertedCardId, ex ->
//                if (isSuccess) {
//                    newCard.id = insertedCardId.toInt()
//                    view.onUpdateFlashcardSuccess()
//                } else {
//                    quickLog("Update flashcard in offline database failed. Please try again")
//                }
//            }
//        }
//    }
//
//    fun doesUserChangeInfo (newLanguagePair: String, newType : String, newText : String, newTranslation : String, newUsing : String, newPronunciation: String) : Boolean {
//        val isNewLanguagePair = originalCard.languagePair != newLanguagePair
//        val isNewType = originalCard.type != newType
//        val isNewText = originalCard.text != newText
//        val isNewTranslation = originalCard.translation != newTranslation
//        val isNewUsing = originalCard.example != newUsing
//        val isNewPronunciation = originalCard.pronunciation != newPronunciation
//        return isNewLanguagePair || isNewType || isNewText || isNewTranslation || isNewUsing || isNewPronunciation
//    }
//
//    fun updateOfflineFlashcard (flashcard : Flashcard, onInsertListener : (Boolean, Long, Exception?) -> Unit) {
//        flashcardRepository.updateFlashcard(flashcard, onInsertListener)
//    }
//
////    fun updateOnlineFlashcard (flashcardSetId : String, oldCard : Flashcard, newCard : Flashcard, onInsertListener : (Boolean, Exception?) -> Unit) {
////        onlineFlashcardDBManager.updateFlashcard(getSingletonUser()!!.id, flashcardSetId, oldCard, newCard, onInsertListener)
////    }
//
//}
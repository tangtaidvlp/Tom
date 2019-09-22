package com.teamttdvlp.memolang.viewmodel.reusable

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.teamttdvlp.memolang.view.activity.helper.quickLog
import com.teamttdvlp.memolang.model.model.FlashcardSet
import com.teamttdvlp.memolang.model.model.Flashcard
import javax.inject.Inject

class OnlineFlashcardDBManager @Inject constructor(var firestoreRef : FirebaseFirestore){

    private val FLASHCARD_ARRAY = "flashcards"

    private val FLASHCARD_SET_COL = "flashcard_sets"

    private val USER_FLASHCARD_DOC = "user_flashcards"


    fun writeFlashcard (uid : String, flashcardSetId: String, flashCard : Flashcard, onInsertListener : (isSuccess : Boolean, ex : Exception?) -> Unit)  {
        val flashcardSetsCollection = firestoreRef.collection(uid).document(USER_FLASHCARD_DOC).collection(FLASHCARD_SET_COL)
        flashcardSetsCollection.whereEqualTo("id", flashcardSetId).get()
            .addOnSuccessListener { docs ->
                val flashcardSetDoc = flashcardSetsCollection.document(flashcardSetId)
                val setExits = docs.size() != 0
                if (setExits) {
                    // Add a Flashcard
                    flashcardSetDoc.update(FLASHCARD_ARRAY, FieldValue.arrayUnion(flashCard))
                    onInsertListener.invoke(true, null)
                } else {
                    // Create new Set of Flashcard
                    val flashcardSet  = FlashcardSet(flashcardSetId)
                    flashcardSet.flashcards.add(flashCard)
                    flashcardSetDoc.set(flashcardSet).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            quickLog("Add FlashcardSet Success")
                            onInsertListener.invoke(true, null)
                        } else {
                            quickLog("Add FlashcardSet Failed")
                            onInsertListener.invoke(false, task.exception)
                        }
                    }
                }
            }
            .addOnFailureListener {
                quickLog("Write Flashcard Failed")
                onInsertListener.invoke(false, it)
            }
    }

    fun updateFlashcard (uid : String, flashcardSetId : String, old : Flashcard, new : Flashcard, onInsertListener : (Boolean, Exception?) -> Unit) {
        firestoreRef
            .collection(uid)
                .document(USER_FLASHCARD_DOC)
            .collection(FLASHCARD_SET_COL).document(flashcardSetId).apply {
                update(FLASHCARD_ARRAY, FieldValue.arrayRemove(old)).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    quickLog("Remove Success")
                    update("flashcards", FieldValue.arrayUnion(new)).addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            quickLog("Update Success")
                            onInsertListener.invoke(true, null)
                        } else {
                            quickLog("Update Failed")
                            task2.exception?.printStackTrace()
                            onInsertListener.invoke(false, task2.exception)
                        }
                    }
                } else {
                    quickLog("Remove Failed")
                    task.exception?.printStackTrace()
                    onInsertListener.invoke(false, task.exception)
                }
            }
        }
    }

    fun readAllFlashcard (uid : String, onGetAllFlashcards: (ArrayList<Flashcard>?, java.lang.Exception?) -> Unit) {
        firestoreRef.collection(uid).document(USER_FLASHCARD_DOC).collection(FLASHCARD_SET_COL).get().addOnSuccessListener{docs : QuerySnapshot ->
            val documents = docs.documents
            val allFlashcards= ArrayList<Flashcard>()
            for (doc in documents) {
                allFlashcards.addAll((doc.toObject(FlashcardSet::class.java)!!).flashcards)
            }
            onGetAllFlashcards(allFlashcards, null)
        }
        firestoreRef.collection(uid).document(USER_FLASHCARD_DOC).collection(FLASHCARD_SET_COL).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val documents = it.result!!.documents
                val allFlashcards= ArrayList<Flashcard>()
                for (doc in documents) {
                    allFlashcards.addAll((doc.toObject(FlashcardSet::class.java)!!).flashcards)
                }
                onGetAllFlashcards(allFlashcards, null)
            } else {
                onGetAllFlashcards(null, it.exception)
            }
        }
    }

    fun deleteFlashcard (uid : String, flashcard : Flashcard) {
        firestoreRef
            .collection(uid).document(USER_FLASHCARD_DOC)
            .collection(FLASHCARD_SET_COL).document(flashcard.type)
            .update(FLASHCARD_ARRAY, FieldValue.arrayRemove(flashcard)).addOnCompleteListener{
                if (it.isSuccessful) {
                    quickLog("Delete card successful")
                } else {
                    quickLog("Delete card failed")
                    it.exception?.printStackTrace()
                }
            }
    }

}
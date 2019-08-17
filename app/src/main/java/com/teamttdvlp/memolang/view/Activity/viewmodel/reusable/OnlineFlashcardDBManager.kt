package com.teamttdvlp.memolang.view.Activity.viewmodel.reusable

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.teamttdvlp.memolang.view.Activity.helper.quickLog
import com.teamttdvlp.memolang.view.Activity.mockmodel.FlashcardSet
import com.teamttdvlp.memolang.view.Activity.mockmodel.MemoCard

class OnlineFlashcardDBManager {

    private val firestoreRef  = FirebaseFirestore.getInstance()

    private val FLASHCARD_ARRAY = "flashcards"

    fun writeFlashcard (uid : String, flashcardSet: FlashcardSet, flashCard : MemoCard)  {
        val flashcardSetId = "${flashcardSet.sourceLang}-${flashcardSet.targetLang}"
        val userCollection = firestoreRef.collection("123")
        userCollection.whereEqualTo("id", flashcardSetId).get()
            .addOnSuccessListener { docs ->
                val flashcardSetDoc = userCollection.document(flashcardSetId)
                val setExits = docs.size() != 0
                if (setExits) {
                    // Add a Flashcard
                    flashcardSetDoc.update(FLASHCARD_ARRAY, FieldValue.arrayUnion(flashCard))
                } else {
                    flashcardSet.flashcards.add(flashCard)
                    flashcardSetDoc.set(flashcardSet).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            quickLog("Add FlashcardSet Success")
                        } else {
                            quickLog("Add FlashcardSet Failed")
                            task.exception?.printStackTrace()
                        }
                    }
                }
            }
            .addOnFailureListener {
                quickLog("Write Flashcard Failed")
            }
    }

    fun updateFlashcard (uid : String, flashcardSet : FlashcardSet, old : MemoCard, new : MemoCard) {
        val flashcardSetId = "${flashcardSet.sourceLang}-${flashcardSet.targetLang}"
        firestoreRef.collection("123").document(flashcardSetId).apply {
            update("flashcards", FieldValue.arrayRemove(old)).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    quickLog("Remove Success")
                    update("flashcards", FieldValue.arrayUnion(new)).addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            quickLog("Update Success")
                        } else {
                            quickLog("Update Failed")
                            task2.exception?.printStackTrace()
                        }
                    }
                } else {
                    quickLog("Remove Failed")
                    task.exception?.printStackTrace()
                }
            }
        }
    }

    fun readAllFlashcard (uid : String, onGetOneFlashcardSet : (ArrayList<FlashcardSet>) -> Unit) {
        firestoreRef.collection(uid).get().addOnSuccessListener{docs : QuerySnapshot ->
            val documents = docs.documents
            val allFlashcardSets = ArrayList<FlashcardSet>()
            for (doc in documents) {
                allFlashcardSets.add(doc.toObject(FlashcardSet::class.java)!!)
            }
            onGetOneFlashcardSet(allFlashcardSets)
        }
    }

    fun deleteFlashcard (uid : String, flashcard : MemoCard) {
        firestoreRef.collection(uid).document(flashcard.type).update("flashcards", FieldValue.arrayRemove(flashcard))
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    quickLog("Delete card successful")
                } else {
                    quickLog("Delete card failed")
                    it.exception?.printStackTrace()
                }
            }
    }

}
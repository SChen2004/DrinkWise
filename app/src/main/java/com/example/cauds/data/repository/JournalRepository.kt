package com.example.cauds.data.repository

import com.example.cauds.data.model.JournalData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

const val JOURNAL = "journal"

class JournalRepository (
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
){

    fun saveJournalEntry(
        userId: String,
        journalData: JournalData,
        onResult: (Boolean, String?, String?) -> Unit) {
        val journalWithMetadata = journalData.copy(
            userId = userId,
            createdAt = journalData.createdAt ?: Timestamp.now(),
        )

        db.collection(JOURNAL)
            .add(journalWithMetadata)
            .addOnSuccessListener { docRef ->
                onResult(true, null, docRef.id)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message, null)
            }
    }

    fun updateJournalEntry(
        documentId: String,
        journalData: JournalData,
        onResult: (Boolean, String?) -> Unit
    ) {
        val journalWithMetadata = journalData.copy(
            updatedAt = Timestamp.now()
            // createdAt should already be present in journalData; don't overwrite it
        )

        db.collection(JOURNAL)
            .document(documentId)
            .set(journalWithMetadata, SetOptions.merge())
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun deleteJournalEntry(
        documentId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        db.collection(JOURNAL)
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun getJournalEntries(
        userId: String,
        onResult: (Boolean, List<Pair<String, JournalData>>?, String?) -> Unit
    ) {
        db.collection(JOURNAL)
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(200)
            .get()
            .addOnSuccessListener { snapshot ->

                val entries = snapshot.documents.mapNotNull { doc ->
                    val data = doc.toObject(JournalData::class.java)
                    if (data != null) Pair(doc.id, data) else null
                }
                onResult(true, entries, null)
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }


}
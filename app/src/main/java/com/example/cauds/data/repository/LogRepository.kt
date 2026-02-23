package com.example.cauds.data.repository

import com.example.cauds.data.model.LogData
import com.example.cauds.data.model.LogItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

const val LOGS = "logs"
const val USER_ID = "userId"
const val TIMESTAMP = "timestamp"

class LogRepository (
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun saveLog(userId: String, logData: LogData, onResult: (Boolean, String?, String?) -> Unit) { // success, errorMsg, docId {
        val logWithMetadata = logData.copy(
            userId = userId,
            timestamp = logData.timestamp ?: Timestamp.now()
        )

        db.collection(LOGS)
            .add(logWithMetadata)
            .addOnSuccessListener { docRef ->
                onResult(true, null, docRef.id)   // <-- document ID
            }
            .addOnFailureListener { e ->
                onResult(false, e.message, null)
            }
    }

    fun fetchLogs(userId: String, onResult: (Boolean, List<LogItem>?, String?) -> Unit) {

        db.collection(LOGS)
            .whereEqualTo(USER_ID, userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val logs = snapshot.documents.mapNotNull { doc ->
                    val logData = doc.toObject(LogData::class.java)
                    logData?.let {
                        LogItem(
                            id = doc.id,
                            data = it
                        )
                    }
                }

                onResult(true, logs, null)
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }

    fun fetchLogsInRange(
        userId: String,
        start: Timestamp,
        end: Timestamp,
        onResult: (Boolean, List<LogItem>?, String?) -> Unit
    ) {
        db.collection(LOGS)
            .whereEqualTo(USER_ID, userId)
            .whereGreaterThanOrEqualTo(TIMESTAMP, start)
            .whereLessThan(TIMESTAMP, end) // end-exclusive is nice
            .get()
            .addOnSuccessListener { snapshot ->
                val logs = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(LogData::class.java)?.let { data ->
                        LogItem(id = doc.id, data = data)
                    }
                }
                onResult(true, logs, null)
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }

    fun deleteLog(logId: String, onResult: (Boolean, String?) -> Unit) {

        db.collection(LOGS)
            .document(logId)
            .delete()
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

}
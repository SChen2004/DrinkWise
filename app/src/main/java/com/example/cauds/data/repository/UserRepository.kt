package com.example.cauds.data.repository

import com.example.cauds.data.model.AudRisk
import com.example.cauds.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

const val USERS = "users"
const val AUDSCORE = "audScore"

class UserRepository {
    private val db = FirebaseFirestore.getInstance()

    // Save onboarding data
    fun saveOnboarding(userId: String, data: User, onResult: (Boolean, String?) -> Unit) {
        db.collection(USERS).document(userId).set(data)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun getAudRiskLevel(userId: String, onResult: (Boolean, AudRisk, String?) -> Unit) {
        db.collection(USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val riskLevel = doc.toObject(User::class.java)?.audScore ?: AudRisk.DEFAULT_RISK
                onResult(true, riskLevel, null)
            }
            .addOnFailureListener { e ->
                onResult(false, AudRisk.DEFAULT_RISK, e.message)
            }
    }

    fun saveAudRiskLevel(userId: String, riskLevel: AudRisk, onResult: (Boolean, String?) -> Unit) {
        db.collection(USERS)
            .document(userId)
            .update(AUDSCORE, riskLevel)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

}
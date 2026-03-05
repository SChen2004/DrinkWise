package com.example.cauds.data.repository

import com.example.cauds.data.model.AudRisk
import com.example.cauds.data.model.NotificationPreferences
import com.example.cauds.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

const val USERS = "users"
const val AUDSCORE = "audScore"
const val ONBOARDING_COMPLETED = "onboardingCompleted"
const val NOTIFICATION_PREFERENCES = "notificationPreferences"
const val FAVOURITE_DRINKS = "favouriteDrinks"


class UserRepository {
    private val db = FirebaseFirestore.getInstance()

    // Save onboarding data
    fun saveOnboarding(userId: String, data: User, onResult: (Boolean, String?) -> Unit) {
        db.collection(USERS).document(userId).set(data)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun getCompletedOnboarding(userId: String, onResult: (Boolean) -> Unit) {
        db.collection(USERS).document(userId).get()
            .addOnSuccessListener { doc ->
                val completed = doc.getBoolean(ONBOARDING_COMPLETED) ?: false
                onResult(completed)
            }
            .addOnFailureListener {
                onResult(false)  // Default to false if something goes wrong
            }
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

    fun saveNotificationPreferences(userId: String, prefs: NotificationPreferences, onResult: (Boolean, String?) -> Unit) {
        db.collection(USERS).document(userId)
            .update(NOTIFICATION_PREFERENCES, prefs)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }
    fun saveFavouriteDrinks(userId: String, drinks: List<String>, onResult: (Boolean, String?) -> Unit) {
        db.collection(USERS).document(userId)
            .update(FAVOURITE_DRINKS, drinks)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun completeOnboarding(userId: String, onResult: (Boolean, String?) -> Unit) {
        db.collection(USERS).document(userId)
            .update(ONBOARDING_COMPLETED, true)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun getUser(userId: String, onResult: (User) -> Unit) {
        db.collection(USERS).document(userId).get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java) ?: User()
                onResult(user)
            }
    }


}
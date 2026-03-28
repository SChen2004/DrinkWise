package com.example.cauds.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cauds.data.model.JournalData
import com.example.cauds.data.repository.AuthRepository
import com.example.cauds.data.repository.JournalRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.model.Values.timestamp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class JournalViewModel(
    private val journalRepo: JournalRepository = JournalRepository(),
    private val authRepo: AuthRepository = AuthRepository()
) : ViewModel() {

    // --- Load state ---
    var entries by mutableStateOf<List<Pair<String, JournalData>>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // --- Delete state ---
    var deleteError by mutableStateOf<String?>(null)
        private set

    // --- Save state ---
    var isSaving by mutableStateOf(false)
        private set

    var saveError by mutableStateOf<String?>(null)
        private set

    var selectedDate by mutableStateOf<LocalDate?>(null)
        private set

    // The document ID of the entry being edited, or null for a new entry
    var editingEntryId by mutableStateOf<String?>(null)
        private set

    // The pre-loaded text for editing
    var editingEntryText by mutableStateOf<String?>(null)
        private set

    fun setEntryDate(date: LocalDate) {
        selectedDate = date
    }

    // saveSuccess acts as a one-shot signal to the screen that the save worked.
    // The screen watches this, navigates away when it flips to true, then calls
    // onSaveHandled() to reset it back to false.
    var saveSuccess by mutableStateOf(false)
        private set

    fun loadEntries() {
        val userId = authRepo.getUserId() ?: return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            journalRepo.getJournalEntries(userId) { success, result, error ->
                isLoading = false
                if (success && result != null) {
                    entries = result
                } else {
                    errorMessage = error ?: "Failed to load entries"
                }
            }
        }
    }

    fun saveEntry(text: String) {
        val userId = authRepo.getUserId() ?: return
        if (text.isBlank()) return

        android.util.Log.d("JournalVM", "saveEntry called, userId=$userId, text=$text, selectedDate=$selectedDate")
        viewModelScope.launch {
            isSaving = true
            saveError = null

            // Use the selected date if set, otherwise default to now
            val timestamp = selectedDate?.let {
                val instant = it.atStartOfDay(ZoneId.systemDefault()).toInstant()
                Timestamp(Date.from(instant))
            } ?: Timestamp.now()

            android.util.Log.d("JournalVM", "timestamp=$timestamp, class=${timestamp.javaClass}")

            val journalData = JournalData(entry = text, createdAt = timestamp)

            journalRepo.saveJournalEntry(userId, journalData) { success, error, _ ->
                isSaving = false
                if (success) {
                    saveSuccess = true
                } else {
                    saveError = error ?: "Failed to save entry"
                    android.util.Log.d("JournalVM", "errormsg=failed to save message")

                }
            }
        }
    }

    // Called by the screen after it has reacted to saveSuccess, so the flag
    // doesn't keep triggering on recomposition.
    fun onSaveHandled() {
        saveSuccess = false
        selectedDate = null
    }

    fun deleteEntry(documentId: String) {
        viewModelScope.launch {
            journalRepo.deleteJournalEntry(documentId) { success, error ->
                if (success) {
                    entries = entries.filter { it.first != documentId }
                } else {
                    deleteError = error ?: "Failed to delete entry"
                }
            }
        }
    }

    fun clearDeleteError() {
        deleteError = null
    }

    fun setEditingEntry(docId: String) {
        editingEntryId = docId
        // Find the entry in the already-loaded list and grab its text
        val entry = entries.firstOrNull { it.first == docId }
        editingEntryText = entry?.second?.entry

        // Also set the date so the header shows correctly
        val entryDate = entry?.second?.createdAt?.toDate()?.toInstant()
            ?.atZone(java.time.ZoneId.systemDefault())
            ?.toLocalDate()
        if (entryDate != null) {
            setEntryDate(entryDate)
        }
    }

    fun clearEditingEntry() {
        editingEntryId = null
        editingEntryText = null
    }

}
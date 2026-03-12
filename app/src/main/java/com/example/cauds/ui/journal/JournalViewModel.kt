package com.example.cauds.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cauds.data.model.JournalData
import com.example.cauds.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class JournalUiState {
    object Loading : JournalUiState()
    data class Success(val entries: List<Pair<String, JournalData>>) : JournalUiState()
    data class Error(val message: String) : JournalUiState()
}

class JournalViewModel(
    private val repository: JournalRepository = JournalRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<JournalUiState>(JournalUiState.Loading)
    val uiState: StateFlow<JournalUiState> = _uiState

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError

    fun loadEntries(userId: String) {
        viewModelScope.launch {
            _uiState.value = JournalUiState.Loading

            repository.getJournalEntries(userId) { success, entries, error ->
                if (success && entries != null) {
                    _uiState.value = JournalUiState.Success(entries)
                } else {
                    _uiState.value = JournalUiState.Error(error ?: "Failed to load entries")
                }
            }
        }
    }

    fun deleteEntry(userId: String, documentId: String) {
        viewModelScope.launch {
            repository.deleteJournalEntry(documentId) { success, error ->
                if (success) {
                    val current = _uiState.value
                    if (current is JournalUiState.Success) {
                        _uiState.value = JournalUiState.Success(
                            current.entries.filter { it.first != documentId }
                        )
                    }
                } else {
                    _deleteError.value = error ?: "Failed to delete entry"
                }
            }
        }
    }

    fun clearDeleteError() {
        _deleteError.value = null
    }
}
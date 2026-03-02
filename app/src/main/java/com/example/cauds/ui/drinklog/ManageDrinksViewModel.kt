package com.example.cauds.ui.drinklog

import androidx.lifecycle.ViewModel
import com.example.cauds.data.model.DrinkData
import com.example.cauds.data.model.DrinkItem
import com.example.cauds.data.repository.AuthRepository
import com.example.cauds.data.repository.DrinkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * State for the Manage Drinks screen.
 */
data class ManageDrinksUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val allDrinks: List<DrinkItem> = emptyList(),
    val expandedCategories: Set<String> = emptySet(),
    val showMaxSelectedToast: Boolean = false // (Optional) can be used to limit selected drinks
)

class ManageDrinksViewModel(
    private val drinkRepository: DrinkRepository = DrinkRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageDrinksUiState())
    val uiState: StateFlow<ManageDrinksUiState> = _uiState.asStateFlow()

    init {
        loadDrinks()
    }

    fun loadDrinks() {
        val userId = authRepository.getUserId() ?: return
        
        _uiState.update { it.copy(isLoading = true) }
        
        drinkRepository.fetchDrinks(userId) { success, drinks, _ ->
            if (success && drinks != null) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        allDrinks = drinks
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleCategoryExpansion(category: String) {
        _uiState.update { state ->
            val expanded = state.expandedCategories.toMutableSet()
            if (expanded.contains(category)) {
                expanded.remove(category)
            } else {
                expanded.add(category)
            }
            state.copy(expandedCategories = expanded)
        }
    }

    fun toggleDrinkSelection(drink: DrinkItem) {
        val nextSelectionState = !drink.data.isSelected
        
        // Optimistic UI update
        _uiState.update { state ->
            val updatedDrinks = state.allDrinks.map { 
                if (it.id == drink.id) it.copy(data = it.data.copy(isSelected = nextSelectionState)) 
                else it 
            }
            state.copy(allDrinks = updatedDrinks)
        }

        drinkRepository.toggleDrinkSelection(drink.id, nextSelectionState) { success, _ ->
            if (!success) {
                // Revert if failed
                _uiState.update { state ->
                    val revertedDrinks = state.allDrinks.map { 
                        if (it.id == drink.id) it.copy(data = it.data.copy(isSelected = !nextSelectionState)) 
                        else it 
                    }
                    state.copy(allDrinks = revertedDrinks)
                }
            }
        }
    }

    fun deleteDrink(drink: DrinkItem) {
        // Optimistic UI update
        _uiState.update { state ->
            state.copy(allDrinks = state.allDrinks.filter { it.id != drink.id })
        }

        drinkRepository.deleteDrink(drink.id) { success, _ ->
            if (!success) {
                // Fetch to revert in case of failure
                loadDrinks()
            }
        }
    }

    // Methods for "Add New Drink"
    fun addCustomDrink(name: String, category: String, size: String, onComplete: () -> Unit) {
        val userId = authRepository.getUserId() ?: return
        
        val newDrink = DrinkData(
            userId = userId,
            name = name,
            category = category,
            defaultSize = size,
            isSelected = true,
            isCustom = true
        )
        
        drinkRepository.addDrink(userId, newDrink) { success, _, docId ->
            if (success && docId != null) {
                val newItem = DrinkItem(docId, newDrink)
                _uiState.update { state ->
                    // Add new drink and keep sorting
                    val newDrinksList = (state.allDrinks + newItem).sortedWith(compareBy({ it.data.category }, { it.data.name }))
                    state.copy(allDrinks = newDrinksList)
                }
                onComplete()
            }
        }
    }
}

package com.example.cauds.ui.drinklog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Represents a physical container size (e.g., FLIGHT, PINT, PITCHER).
 * Previously held volume data, but the app no longer tracks total volume intake.
 */
data class ContainerType(val name: String)

// Hardcoded list of available containers to be displayed in the HorizontalPager
val availableContainers = listOf(
    ContainerType("FLIGHT"),
    ContainerType("PINT"),
    ContainerType("PITCHER")
)

/**
 * Represents a specific type of alcoholic beverage.
 * Used in the VerticalPager (Wheel Picker) below the containers.
 */
data class DrinkType(val name: String)

// Hardcoded list of default drinks a user can select (TO DO: Show more drinks from database)
val availableDrinks = listOf(
    DrinkType("Ale"),
    DrinkType("Cider"),
    DrinkType("Rum"),
    DrinkType("Sauvignon"),
    DrinkType("Seltzer"),
    DrinkType("Whiskey")
)

/**
 * Holds the current state of the Drink Log UI.
 * Every time a user interacts with the UI (changes date, selects a drink, enters a cost),
 * this state is updated, which triggers the UI to automatically recompose (redraw).
 */
data class DrinkLogUiState(
    val selectedDate: String = "Feb 16", // TO DO: Change to current date

    val selectedDrinkType: String = availableDrinks[0].name, // The drink currently centered in the vertical wheel
    val selectedContainer: ContainerType = availableContainers[1], // The container currently centered in the horizontal pager (Default: PINT)
    val quantity: Int = 1, // The number shown in the '-' and '+' stepper
    val costInput: String = "", // The raw String typed into the cost text field
    val cost: Double = 0.0, // The parsed numerical value of the costInput
    val showDeletedToast: Boolean = false, // Controls the visibility of the "Drink deleted." top overlay
    val addedDrinks: List<LogDataWrapper> = emptyList() // The list of successfully logged drinks appearing above the inputs
)

/**
 * A data model representing a single successfully logged drink entry.
 * When a user adds multiple quantities at once, multiple wrappers are created to allow individual deletion later.
 */
data class LogDataWrapper(
    val id: String = java.util.UUID.randomUUID().toString(), // Unique ID for safe deletion capability
    val type: String,
    val containerName: String,
    val cost: Double
)

class DrinkLogViewModel : ViewModel() {

    // Internal mutable state flow backing the UI state.
    private val _uiState = MutableStateFlow(DrinkLogUiState())
    // Public read-only state flow exposed to the Compose UI components.
    val uiState: StateFlow<DrinkLogUiState> = _uiState.asStateFlow()

    // Updates the currently selected date.
    fun selectDate(date: String) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    // Called automatically when the VerticalPager (drink type wheel) snaps to a new item.
    fun selectDrink(type: String) {
        _uiState.value = _uiState.value.copy(selectedDrinkType = type)
    }

    // Called automatically when the HorizontalPager (container carousel) snaps to a new item.
    fun selectContainer(containerIndex: Int) {
        if (containerIndex in availableContainers.indices) {
            _uiState.value = _uiState.value.copy(
                selectedContainer = availableContainers[containerIndex]
            )
        }
    }

    // Handles the '+' and '-' buttons on the quantity stepper. Ensure it doesn't drop below 1.
    fun updateQuantity(delta: Int) {
        val newQty = (_uiState.value.quantity + delta).coerceAtLeast(1)
        _uiState.value = _uiState.value.copy(quantity = newQty)
    }

    // Handles typing in the cost input field. Binds to `costInput` (String) to allow fluid deletion of '0.00'.
    fun updateCostInput(newInput: String) {
        // Parse the input softly, falling back to 0.0 if empty or invalid.
        val parsedCost = newInput.toDoubleOrNull() ?: 0.0
        _uiState.value = _uiState.value.copy(
            costInput = newInput,
            cost = parsedCost
        )
    }

    /**
     * Triggered by the fixed black "ADD" button at the bottom.
     * Generates a new `LogDataWrapper` for each quantity selected and prefixes them
     * to the `addedDrinks` list, effectively putting newest entries at the top.
     * Finally, resets the input fields back to their defaults.
     */
    fun addDrink() {
        val current = _uiState.value
        
        // If quantity is > 1, create identical individual entries so they can be deleted individually if needed.
        val newLogs = List(current.quantity) {
            LogDataWrapper(
                type = current.selectedDrinkType,
                containerName = current.selectedContainer.name,
                cost = current.cost // Represents cost per single drink item
            )
        }

        _uiState.value = current.copy(
            addedDrinks = newLogs + current.addedDrinks, // Insert at top of list
            quantity = 1, // Reset stepper
            costInput = "", // Reset input field text
            cost = 0.0 // Reset internal cost metric
        )
    }

    // Triggered by the trailing "X" icon on an individual drink row.
    fun removeDrink(logId: String) {
        val current = _uiState.value
        // Filters out the matched log ID, leaving the rest.
        val newLogs = current.addedDrinks.filter { it.id != logId }
        
        _uiState.value = current.copy(
            addedDrinks = newLogs
        )
        triggerToast()
    }

    // Triggered by the trailing "X" icon on the grouped Batch Header row to delete all identical items at once.
    fun removeBatch(logIds: List<String>) {
        val current = _uiState.value
        // Filters out any logs whose IDs match the batch IDs provided.
        val newLogs = current.addedDrinks.filter { it.id !in logIds }
        
        _uiState.value = current.copy(
            addedDrinks = newLogs
        )
        triggerToast()
    }

    /**
     * Activates the top overlay toast "Drink deleted."
     * Uses a coroutine to automatically hide the toast after exactly 3000ms (3 seconds).
     */
    private fun triggerToast() {
        _uiState.value = _uiState.value.copy(showDeletedToast = true)
        viewModelScope.launch {
            delay(3000)
            _uiState.value = _uiState.value.copy(showDeletedToast = false)
        }
    }

    // Allows manual dismissal of the toast by clicking the little 'X' on it.
    fun hideToast() {
        _uiState.value = _uiState.value.copy(showDeletedToast = false)
    }
}

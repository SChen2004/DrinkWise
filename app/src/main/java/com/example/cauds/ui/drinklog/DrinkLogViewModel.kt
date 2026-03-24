package com.example.cauds.ui.drinklog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cauds.data.model.LogData
import com.example.cauds.data.repository.AuthRepository
import com.example.cauds.data.repository.LogRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Represents a physical container size (e.g., FLIGHT, PINT, PITCHER).
 * Previously held volume data, but the app no longer tracks total volume intake.
 */
data class ContainerType(val name: String)

data class DrinkType(val name: String, val category: String = "Beer")

// Global helper for sizing rules
fun getSizesForDrink(drinkName: String, category: String): List<ContainerType> {
    return when (category) {
        "Beer" -> listOf(ContainerType("FLIGHT"), ContainerType("PINT"), ContainerType("PITCHER"))
        "Wine" -> listOf(ContainerType("GLASS"), ContainerType("BOTTLE"))
        "Fermented" -> listOf(ContainerType("REGULAR"))
        "Spirit" -> listOf(ContainerType("REGULAR SHOT"), ContainerType("DOUBLE SHOT"))
        "Cocktail/Mixed" -> {
            if (drinkName.equals("Daiquiri", ignoreCase = true) || drinkName.equals("Margarita", ignoreCase = true)) {
                listOf(ContainerType("SINGLE"), ContainerType("DOUBLE"))
            } else {
                listOf(ContainerType("SINGLE"))
            }
        }
        else -> listOf(ContainerType("REGULAR"))
    }
}

fun getDefaultSizeForDrink(drinkName: String, category: String): String {
    return when (category) {
        "Beer" -> "PINT"
        "Wine" -> "GLASS"
        "Fermented" -> "REGULAR"
        "Spirit" -> "REGULAR SHOT"
        "Cocktail/Mixed" -> "SINGLE"
        else -> "REGULAR"
    }
}

// Hardcoded list of default drinks a user can select
val availableDrinks = listOf(
    DrinkType("Ale", "Beer"),
    DrinkType("Cider", "Fermented"),
    DrinkType("Whiskey", "Spirit")
)

/**
 * Holds the current state of the Drink Log UI.
 * Every time a user interacts with the UI (changes date, selects a drink, enters a cost),
 * this state is updated, which triggers the UI to automatically recompose (redraw).
 */
data class DrinkLogUiState(
    val selectedDateObj: LocalDate = LocalDate.now(), // Internal tracking of the date
    val selectedDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d")),

    val availableDrinkTypes: List<DrinkType> = availableDrinks, // Dynamic list of available drinks

    val selectedDrinkType: String = availableDrinks[0].name, // The drink currently centered in the vertical wheel
    val availableContainers: List<ContainerType> = getSizesForDrink(availableDrinks[0].name, availableDrinks[0].category),
    val selectedContainer: ContainerType = getSizesForDrink(availableDrinks[0].name, availableDrinks[0].category).let { containers ->
        val def = getDefaultSizeForDrink(availableDrinks[0].name, availableDrinks[0].category)
        containers.find { it.name == def } ?: containers.first()
    },

    val quantity: Int = 1, // The number shown in the '-' and '+' stepper
    val costInput: String = "", // The raw String typed into the cost text field
    val cost: Double = 0.0, // The parsed numerical value of the costInput
    val editModeId: String? = null, // If non-null, the screen is in Edit mode for this log ID
    val showLoggedToast: Boolean = false, // Controls the visibility of the "Drink logged." top overlay
    val showDeletedToast: Boolean = false, // Controls the visibility of the "Drink deleted." top overlay
    val showSavedToast: Boolean = false, // Controls the visibility of the "Drink saved." top overlay
    val lastToastQuantity: Int = 1, // The quantity associated with the last toast (for pluralization)
    val addedDrinks: List<LogDataWrapper> = emptyList() // The list of successfully logged drinks appearing above the inputs
)

/**
 * A data model representing a single successfully logged drink entry.
 * When a user adds multiple quantities at once, multiple wrappers are created to allow individual deletion later.
 */
data class LogDataWrapper(
    val id: String = java.util.UUID.randomUUID().toString(), // Unique ID for safe deletion capability
    val type: String,
    val drinkSize: String,
    val cost: Double
)

class DrinkLogViewModel(
    private val logRepository: LogRepository = LogRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val drinkRepository: com.example.cauds.data.repository.DrinkRepository = com.example.cauds.data.repository.DrinkRepository()
) : ViewModel() {

    // Internal mutable state flow backing the UI state.
    private val _uiState = MutableStateFlow(DrinkLogUiState())
    // Public read-only state flow exposed to the Compose UI components.
    val uiState: StateFlow<DrinkLogUiState> = _uiState.asStateFlow()

    init {
        val initialIsoDate = _uiState.value.selectedDateObj.format(DateTimeFormatter.ISO_LOCAL_DATE)
        loadLogsForDate(initialIsoDate)
        loadAvailableDrinks()
    }

    private fun loadAvailableDrinks() {
        val userId = authRepository.getUserId() ?: return
        drinkRepository.fetchDrinks(userId) { success, drinks, _ ->
            if (success && drinks != null) {
                val selectedDrinks = drinks.filter { it.data.isSelected }
                val types = if (selectedDrinks.isNotEmpty()) {
                    selectedDrinks.map { DrinkType(it.data.name, it.data.category) }
                } else {
                    listOf(DrinkType("No Drinks Selected", "Beer"))
                }
                
                _uiState.update { state ->
                    val currentType = state.selectedDrinkType
                    val newDrink = types.find { it.name == currentType } ?: types.first()
                    val newContainers = getSizesForDrink(newDrink.name, newDrink.category)
                    val defSize = getDefaultSizeForDrink(newDrink.name, newDrink.category)
                    
                    state.copy(
                        availableDrinkTypes = types,
                        selectedDrinkType = newDrink.name,
                        availableContainers = newContainers,
                        selectedContainer = newContainers.find { it.name == defSize } ?: newContainers.first()
                    )
                }
            }
        }
    }

    // Public method to manually trigger a refresh (e.g., from a LaunchedEffect in the UI)
    fun refreshLogs() {
        val isoDateStr = _uiState.value.selectedDateObj.format(DateTimeFormatter.ISO_LOCAL_DATE)
        loadLogsForDate(isoDateStr)
        loadAvailableDrinks()
    }

    private fun loadLogsForDate(isoDateStr: String) {
        val userId = authRepository.getUserId() ?: return
        logRepository.fetchLogs(userId) { success, logs, _ ->
            if (success && logs != null) {
                // Filter by the matching selected date and exclude ACTION_LOG markers
                val dayLogs = logs.filter { it.data.date == isoDateStr && it.data.drinkType != "ACTION_LOG" }.map {
                    LogDataWrapper(
                        id = it.id,
                        type = it.data.drinkType,
                        drinkSize = it.data.drinkSize,
                        cost = it.data.drinkCost
                    )
                }
                
                // Keep things ordered (newest first or just rely on fetch order)
                _uiState.update { state ->
                    state.copy(addedDrinks = dayLogs)
                }
            }
        }
    }

    // Updates the currently selected date.
    fun selectDate(dateObj: LocalDate) {
        val formatted = dateObj.format(DateTimeFormatter.ofPattern("MMM d"))
        val isoDateStr = dateObj.format(DateTimeFormatter.ISO_LOCAL_DATE)
        _uiState.update { it.copy(selectedDateObj = dateObj, selectedDate = formatted) }
        loadLogsForDate(isoDateStr)
    }

    fun previousDay() {
        val newDate = _uiState.value.selectedDateObj.minusDays(1)
        val formatted = newDate.format(DateTimeFormatter.ofPattern("MMM d"))
        val isoDateStr = newDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        _uiState.update { it.copy(selectedDateObj = newDate, selectedDate = formatted) }
        loadLogsForDate(isoDateStr)
    }

    fun nextDay() {
        val newDate = _uiState.value.selectedDateObj.plusDays(1)
        val formatted = newDate.format(DateTimeFormatter.ofPattern("MMM d"))
        val isoDateStr = newDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        _uiState.update { it.copy(selectedDateObj = newDate, selectedDate = formatted) }
        loadLogsForDate(isoDateStr)
    }

    // Called automatically when the VerticalPager (drink type wheel) snaps to a new item.
    fun selectDrink(type: String) {
        if (_uiState.value.selectedDrinkType == type) return

        val drinkType = _uiState.value.availableDrinkTypes.find { it.name == type }
        val category = drinkType?.category ?: "Beer"
        val newContainers = getSizesForDrink(type, category)
        val currentState = _uiState.value
        
        // Only reset containers if they actually change (due to category switch)
        if (currentState.availableContainers == newContainers) {
            _uiState.value = currentState.copy(selectedDrinkType = type)
        } else {
            val defSize = getDefaultSizeForDrink(type, category)
            val selectedContainer = newContainers.find { it.name == defSize } ?: newContainers.first()
            _uiState.value = currentState.copy(
                selectedDrinkType = type,
                availableContainers = newContainers,
                selectedContainer = selectedContainer
            )
        }
    }

    // Called automatically when the HorizontalPager (container carousel) snaps to a new item.
    fun selectContainer(containerIndex: Int) {
        val currentContainers = _uiState.value.availableContainers
        if (containerIndex in currentContainers.indices) {
            _uiState.value = _uiState.value.copy(
                selectedContainer = currentContainers[containerIndex]
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
        // Prevent negative numbers by checking for '-' or other invalid characters if necessary
        if (newInput.startsWith("-")) return

        // Parse the input softly, falling back to 0.0 if empty or invalid.
        val parsedCost = newInput.toDoubleOrNull() ?: 0.0
        
        // Final safety check for numeric value, no negative price allow
        if (parsedCost < 0) return

        _uiState.value = _uiState.value.copy(
            costInput = newInput,
            cost = parsedCost
        )
    }

    /**
     * Triggered by the fixed "ADD" button at the bottom，save each drink log of the day to database.
     */
    fun addDrink() {
        val current = _uiState.value
        val userId = authRepository.getUserId() ?: return
        
        // If quantity is > 1, create identical individual entries so they can be deleted individually if needed.
        val newLogs = List(current.quantity) {
            LogDataWrapper(
                type = current.selectedDrinkType,
                drinkSize = current.selectedContainer.name,
                cost = current.cost // Represents cost per single drink item
            )
        }

        _uiState.value = current.copy(
            addedDrinks = newLogs + current.addedDrinks, // Insert at top of list
            quantity = 1, // Reset stepper
            costInput = "", // Reset input field text
            cost = 0.0, // Reset internal cost metric
            showLoggedToast = true,
            lastToastQuantity = current.quantity
        )

        viewModelScope.launch {
            delay(3000)
            _uiState.update { it.copy(showLoggedToast = false) }
        }

        // Save to Firebase
        val isoDate = current.selectedDateObj.format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        // Ensure of a persistent ACTION_LOG exists for today so the streak persists even if individual drinks are deleted
        logRepository.fetchLogs(userId) { success, logs, _ ->
            if (success && logs != null) {
                val hasActionLog = logs.any { it.data.date == isoDate && it.data.drinkType == "ACTION_LOG" }
                if (!hasActionLog) {
                    val actionLog = LogData(
                        date = isoDate,
                        drinkType = "ACTION_LOG",
                        drinkSize = "LOGGED",
                        drinkCost = 0.0
                    )
                    logRepository.saveLog(userId, actionLog) { _, _, _ -> }
                }
            }
        }

        newLogs.forEach { wrapper ->
            val logData = LogData(
                date = isoDate,
                drinkType = wrapper.type,
                drinkSize = wrapper.drinkSize,
                drinkCost = wrapper.cost
            )
            logRepository.saveLog(userId, logData) { success, _, docId ->
                if (success && docId != null) {
                    _uiState.update { state -> 
                        state.copy(
                            addedDrinks = state.addedDrinks.map { 
                                if (it.id == wrapper.id) it.copy(id = docId) else it 
                            }
                        )
                    }
                }
            }
        }
        
        viewModelScope.launch {
            delay(3000)
            _uiState.value = _uiState.value.copy(showLoggedToast = false)
        }
    }

    /**
     * Finds the targeted log and copies its data directly into the active UI state parameters.
     * Transitions the UI into Edit Mode where the "ADD" button becomes "SAVE".
     */
    fun enterEditMode(logId: String) {
        val log = _uiState.value.addedDrinks.find { it.id == logId } ?: return
        
        // Find if this drink matches an existing available drink to get its correct sizes, or fallback to typical sizes
        val matchDrink = _uiState.value.availableDrinkTypes.find { it.name == log.type }
        val containersForEdit = if (matchDrink != null) getSizesForDrink(matchDrink.name, matchDrink.category) else getSizesForDrink(log.type, "Beer")
        val container = containersForEdit.find { it.name == log.drinkSize } ?: (containersForEdit.getOrNull(1) ?: containersForEdit.first())
        
        // Ensure the drink type exists in available types, otherwise add it temporarily or use it. It's fine since `selectedDrinkType` is just a string.
        _uiState.value = _uiState.value.copy(
            editModeId = logId,
            selectedDrinkType = log.type,
            availableContainers = containersForEdit,
            selectedContainer = container,
            quantity = 1, // Edit targets instances 1 at a time initially
            costInput = if (log.cost > 0) {
                if (log.cost % 1.0 == 0.0) log.cost.toInt().toString() else log.cost.toString()
            } else "",
            cost = log.cost
        )
    }

    /**
     * Swaps out the original logged drink matching `editModeId` with the newly modified parameters.
     */
    fun saveDrink() {
        val current = _uiState.value
        val editId = current.editModeId ?: return
        val userId = authRepository.getUserId() ?: return
        
        // Supports multiplying edited single items into sets (if quantity was updated > 1 during edit mode)
        val newLogsList = List(current.quantity) { index ->
            LogDataWrapper(
                id = java.util.UUID.randomUUID().toString(),
                type = current.selectedDrinkType,
                drinkSize = current.selectedContainer.name,
                cost = current.cost
            )
        }
        
        val baseIndex = current.addedDrinks.indexOfFirst { it.id == editId }
        val updatedAddedDrinks = current.addedDrinks.toMutableList()
        
        if (baseIndex != -1) {
            updatedAddedDrinks.removeAt(baseIndex) // Remove original entry
            updatedAddedDrinks.addAll(baseIndex, newLogsList) // Patch directly into same spot
        } else {
            updatedAddedDrinks.addAll(0, newLogsList) // Fallback pushing to top
        }

        _uiState.value = current.copy(
            addedDrinks = updatedAddedDrinks,
            editModeId = null, // Escape Edit Mode
            quantity = 1,
            costInput = "",
            cost = 0.0,
            showSavedToast = true,
            lastToastQuantity = current.quantity
        )
        
        viewModelScope.launch {
            delay(3000)
            _uiState.update { it.copy(showSavedToast = false) }
        }
        
        // Delete old from Firebase
        logRepository.deleteLog(editId) { _, _ -> }
        
        // Save new to Firebase
        newLogsList.forEach { wrapper ->
            val logData = LogData(
                date = current.selectedDateObj.format(DateTimeFormatter.ISO_LOCAL_DATE),
                drinkType = wrapper.type,
                drinkSize = wrapper.drinkSize,
                drinkCost = wrapper.cost
            )
            logRepository.saveLog(userId, logData) { success, _, docId ->
                if (success && docId != null) {
                    _uiState.update { state -> 
                        state.copy(
                            addedDrinks = state.addedDrinks.map { 
                                if (it.id == wrapper.id) it.copy(id = docId) else it 
                            }
                        )
                    }
                }
            }
        }
        
        // Result of saving: toast logic handled above

    }

    /**
     * Explicitly escapes edit mode without saving changes, reverting input variables back to defaults.
     */
    fun cancelEdit() {
        _uiState.value = _uiState.value.copy(
            editModeId = null,
            quantity = 1,
            costInput = "",
            cost = 0.0
        )
    }

    /**
     * Finds the log by its ID and creates a new identical log pointing to this exact moment.
     */
    fun duplicateDrink(logId: String) {
        val current = _uiState.value
        val logToDuplicate = current.addedDrinks.find { it.id == logId } ?: return
        
        val userId = authRepository.getUserId() ?: return
        
        val newLogData = LogData(
            date = current.selectedDateObj.format(DateTimeFormatter.ISO_LOCAL_DATE),
            drinkType = logToDuplicate.type,
            drinkSize = logToDuplicate.drinkSize,
            drinkCost = logToDuplicate.cost
        )

        logRepository.saveLog(userId, newLogData) { success, _, docId ->
             if (success && docId != null) {
                 val wrapper = LogDataWrapper(
                     id = docId,
                     type = newLogData.drinkType,
                     drinkSize = newLogData.drinkSize,
                     cost = newLogData.drinkCost
                 )
                 
                 _uiState.value = current.copy(
                     addedDrinks = listOf(wrapper) + current.addedDrinks,
                     showLoggedToast = true,
                     lastToastQuantity = 1
                 )
                 viewModelScope.launch {
                     delay(3000)
                     _uiState.update { it.copy(showLoggedToast = false) }
                 }
             }
        }
    }

    // Triggered by the trailing "X" icon on an individual drink row.
    fun removeDrink(logId: String) {
        val current = _uiState.value
        // Filters out the matched log ID, leaving the rest.
        val newLogs = current.addedDrinks.filter { it.id != logId }
        
        _uiState.value = current.copy(
            addedDrinks = newLogs
        )
        triggerToast(1)
        
        logRepository.deleteLog(logId) { _, _ -> }
    }

    // Triggered by the trailing "X" icon on the grouped Batch Header row to delete all identical items at once.
    fun removeBatch(type: String, drinkSize: String) {
        val current = _uiState.value
        val batchIds = current.addedDrinks.filter { it.type == type && it.drinkSize == drinkSize }.map { it.id }
        // Filters out any logs whose IDs match the batch IDs provided.
        val newLogs = current.addedDrinks.filter { it.id !in batchIds }
        
        _uiState.value = current.copy(
            addedDrinks = newLogs
        )
        triggerToast(batchIds.size)
        
        batchIds.forEach { logId ->
            logRepository.deleteLog(logId) { _, _ -> }
        }
    }

    /**
     * Activates the top overlay toast "Drink deleted."
     * Uses a coroutine to automatically hide the toast after exactly 3000ms (3 seconds).
     */
    private fun triggerToast(quantity: Int) {
        _uiState.value = _uiState.value.copy(showDeletedToast = true, lastToastQuantity = quantity)
        viewModelScope.launch {
            delay(3000)
            _uiState.update { it.copy(showDeletedToast = false) }
        }
    }

    // Allows manual dismissal of the toast by clicking the little 'X' on it.
    fun hideToast() {
        _uiState.value = _uiState.value.copy(
            showLoggedToast = false,
            showDeletedToast = false,
            showSavedToast = false
        )
    }
}

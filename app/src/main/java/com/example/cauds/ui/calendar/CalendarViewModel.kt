package com.example.cauds.ui.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.cauds.data.model.LogItem
import com.example.cauds.data.repository.AuthRepository
import com.example.cauds.data.repository.LogRepository
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date

class CalendarViewModel : ViewModel() {

    data class MonthSummary(
        val totalDrinks: Int,
        val totalMoneySpent: Int,
        val avgDrinksPerDay: Double
    )
    private val logRepo = LogRepository()
    private val authRepo = AuthRepository()

    var currentMonth by mutableStateOf(YearMonth.now())
        private set

    var monthLogs by mutableStateOf<List<LogItem>>(emptyList())
        private set

    var loggedDays by mutableStateOf<Set<LocalDate>>(emptySet())
        private set

    var monthSummary by mutableStateOf(
        MonthSummary(totalDrinks = 0, totalMoneySpent = 0, avgDrinksPerDay = 0.0)
    )
        private set

    var isLoading by mutableStateOf(false)
        private set

    var viewedDate by mutableStateOf<LocalDate?>(null)

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        // load current month on first open
        loadMonth(currentMonth)
    }

    fun goPrevMonth() = loadMonth(currentMonth.minusMonths(1))
    fun goNextMonth() = loadMonth(currentMonth.plusMonths(1))

    fun loadMonth(yearMonth: YearMonth) {
        currentMonth = yearMonth

        val userId = authRepo.getUserId()
        if (userId == null) {
            errorMessage = "Not logged in"
            return
        }

        val startOfMonth = yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault())
        val endOfMonth = yearMonth.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault())

        val startTimestamp = Timestamp(Date.from(startOfMonth.toInstant()))
        val endTimestamp = Timestamp(Date.from(endOfMonth.toInstant()))

        isLoading = true
        errorMessage = null

        logRepo.fetchLogsInRange(
            userId,
            startTimestamp,
            endTimestamp
        ) { success, logs, error ->
            isLoading = false

            if (success) {
                val list = logs ?: emptyList()
                monthLogs = list

                // Convert logs -> Set<LocalDate> to highlight days
                loggedDays = list.mapNotNull { item ->
                    item.data.timestamp?.toDate()?.toInstant()
                        ?.atZone(ZoneId.systemDefault())
                        ?.toLocalDate()
                }.toSet()

                monthSummary = computeMonthSummary(yearMonth, list)
            } else {
                monthLogs = emptyList()
                loggedDays = emptySet()
                monthSummary = MonthSummary(0, 0, 0.0)
                errorMessage = error ?: "Unknown error"
            }
        }
    }

    private fun computeMonthSummary(yearMonth: YearMonth, logs: List<LogItem>): MonthSummary {
        val totalDrinks = logs.sumOf { it.data.drinkCount.toInt() }
        val totalMoney = logs.sumOf { it.data.drinkCost.toInt() }

        val daysInMonth = yearMonth.lengthOfMonth()
        val avgPerDay = if (daysInMonth == 0) 0.0 else totalDrinks.toDouble() / daysInMonth

        return MonthSummary(
            totalDrinks = totalDrinks,
            totalMoneySpent = totalMoney,
            avgDrinksPerDay = avgPerDay
        )
    }

    fun deleteLog(logId: String) {
        logRepo.deleteLog(logId) { success, error ->
            if (success) {
                monthLogs = monthLogs.filter { it.id != logId }
                loggedDays = monthLogs.mapNotNull { item ->
                    item.data.timestamp?.toDate()?.toInstant()
                        ?.atZone(ZoneId.systemDefault())
                        ?.toLocalDate()
                }.toSet()
                monthSummary = computeMonthSummary(currentMonth, monthLogs)
            }
        }
    }
}

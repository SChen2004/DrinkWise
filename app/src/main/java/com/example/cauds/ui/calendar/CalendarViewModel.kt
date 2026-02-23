package com.example.cauds.ui.calendar

import androidx.lifecycle.ViewModel
import com.example.cauds.data.model.LogItem
import com.example.cauds.data.repository.AuthRepository
import com.example.cauds.data.repository.LogRepository
import com.google.firebase.Timestamp
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date


class CalendarViewModel : ViewModel() {

    private val logRepo = LogRepository()
    private val authRepo = AuthRepository()

    fun getMonthLogs(year: Int, month: Int) {

        val yearMonth = YearMonth.of(year, month)

        val startOfMonth = yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault())
        val endOfMonth = yearMonth.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault())

        val startTimestamp = Timestamp(Date.from(startOfMonth.toInstant()))
        val endTimestamp = Timestamp(Date.from(endOfMonth.toInstant()))

        logRepo.fetchLogsInRange(
            authRepo.getUserId()!!,
            startTimestamp,
            endTimestamp
        ) { success, logs, error ->

            if (success) {
                // update state with logs
            } else {
                // handle error
            }
        }

    }

    fun computeMonthSummary(yearMonth: YearMonth, logs: List<LogItem>): MonthSummary {
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
}
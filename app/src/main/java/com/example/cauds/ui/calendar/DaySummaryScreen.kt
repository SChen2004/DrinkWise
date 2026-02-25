package com.example.cauds.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun DaySummaryScreen(
    date: String,
    calendarViewModel: CalendarViewModel,
    onBack: () -> Unit
) {
    val localDate = remember(date) { LocalDate.parse(date) }

    val logsForDay = remember(calendarViewModel.monthLogs, date) {
        calendarViewModel.monthLogs
            .filter { it.data.date == date }
    }

    val totalDrinks = remember(logsForDay) {
        logsForDay.sumOf { it.data.drinkCount.toInt() }
    }

    val totalSpent = remember(logsForDay) {
        logsForDay.sumOf { it.data.drinkCost.toDouble() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Header
        WeekHeader(
            selectedDate = localDate,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Total section
        Text(
            text = "Drinks: $totalDrinks",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Total spent: $${"%.2f".format(totalSpent)}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        // Drink list
        logsForDay.forEach { logItem ->

            val drink = logItem.data

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = drink.drinkType,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (drink.drinkCount > 1) {
                        Text(
                            text = "${drink.drinkCount} drinks",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Text(
                    text = "$${"%.2f".format(drink.drinkCost.toDouble())}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Divider()
        }
    }
}

@Composable
private fun WeekHeader(
    selectedDate: LocalDate,
    onBack: () -> Unit,
) {
    val startOfWeek = selectedDate.minusDays(
        (selectedDate.dayOfWeek.value % 7).toLong()
    ) // Sunday start

    val weekDates = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Column(modifier = Modifier.fillMaxWidth()) {

        // Back + Month
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) {
                Text("< Back")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = selectedDate.month.name,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Weekday letters
        Row(modifier = Modifier.fillMaxWidth()) {
            weekDates.forEach { date ->
                Text(
                    text = date.dayOfWeek.name.first().toString(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Dates
        Row(modifier = Modifier.fillMaxWidth()) {
            weekDates.forEach { d ->
                val isSelected = d == selectedDate

                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.surface,
                        tonalElevation = if (isSelected) 6.dp else 0.dp
                    ) {
                        Text(
                            text = d.dayOfMonth.toString(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
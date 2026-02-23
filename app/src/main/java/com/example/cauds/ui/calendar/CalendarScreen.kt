package com.example.cauds.ui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

data class MonthSummary(
    val totalDrinks: Int,
    val totalMoneySpent: Int,
    val avgDrinksPerDay: Double
)

/**
 * Front-end calendar for any month.
 *
 * Later, when you fetch logs for a month, you can pass something like:
 * - a set of days that have logs (loggedDays)
 * - or a map of LocalDate -> count/sum/etc.
 */
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    initialMonth: YearMonth = YearMonth.now(),
    loggedDays: Set<LocalDate> = emptySet(), // hook for your log data later
    onDayClick: (LocalDate) -> Unit = {}
) {


    var currentMonth by remember { mutableStateOf(initialMonth) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {

        // ---- Header (Month + nav) ----
        MonthHeader(
            yearMonth = currentMonth,
            onPrev = { currentMonth = currentMonth.minusMonths(1) },
            onNext = { currentMonth = currentMonth.plusMonths(1) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ---- Weekday labels ----
        WeekdayRow()

        Spacer(modifier = Modifier.height(8.dp))

        // ---- Day grid ----
        val cells = remember(currentMonth) { buildMonthCells(currentMonth) }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false, // feels nicer for a calendar month view
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(cells) { cell ->
                DayCell(
                    cell = cell,
                    hasLog = cell.date?.let { it in loggedDays } == true,
                    onClick = { date ->
                        if (date != null) onDayClick(date)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TEMP: fake data for now
        MonthSummaryCard(
            summary = MonthSummary(
                totalDrinks = 0,
                totalMoneySpent = 0,
                avgDrinksPerDay = 0.0
            )
        )
    }
}

@Composable
private fun MonthHeader(
    yearMonth: YearMonth,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onPrev) {
            Text("Prev")
        }

        Spacer(modifier = Modifier.weight(1f))

        val monthName = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        Text(
            text = "$monthName ${yearMonth.year}",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = onNext) {
            Text("Next")
        }
    }
}

@Composable
private fun WeekdayRow() {
    // Common calendar layout: Mon..Sun
    val days = listOf(
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )

    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach { dow ->
            Text(
                text = dow.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

/**
 * A calendar grid cell.
 * - If date == null, it's a blank filler cell (before the 1st or after the last day).
 */
private data class CalendarCell(
    val date: LocalDate? // null = blank cell
)

private fun buildMonthCells(yearMonth: YearMonth): List<CalendarCell> {
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()

    // sunday == 0, sat == 6
    val startOffset = firstDay.dayOfWeek.value % 7


    val cells = mutableListOf<CalendarCell>()

    // leading blanks
    repeat(startOffset) { cells.add(CalendarCell(date = null)) }

    // actual days
    for (day in 1..daysInMonth) {
        cells.add(CalendarCell(date = yearMonth.atDay(day)))
    }

    // trailing blanks to complete final row (optional, but looks nicer)
    while (cells.size % 7 != 0) {
        cells.add(CalendarCell(date = null))
    }

    return cells
}

@Composable
private fun DayCell(
    cell: CalendarCell,
    hasLog: Boolean,
    onClick: (LocalDate?) -> Unit
) {
    val date = cell.date
    val isBlank = date == null

    // Visual states
    val containerColor =
        if (isBlank) MaterialTheme.colorScheme.surface
        else if (hasLog) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceVariant

    val textColor =
        if (isBlank) MaterialTheme.colorScheme.onSurface.copy(alpha = 0f)
        else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = if (hasLog) 2.dp else 0.dp,
        modifier = Modifier
            .aspectRatio(1f) // makes it square
            .fillMaxWidth()
            .then(
                if (!isBlank) Modifier.clickable { onClick(date) } else Modifier
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = date?.dayOfMonth?.toString() ?: "",
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun MonthSummaryCard(summary: MonthSummary, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Month summary", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow(label = "Total drinks", value = summary.totalDrinks.toString())
            SummaryRow(label = "Total spent", value = "$${summary.totalMoneySpent}")
            SummaryRow(
                label = "Avg drinks / day",
                value = String.format("%.2f", summary.avgDrinksPerDay)
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
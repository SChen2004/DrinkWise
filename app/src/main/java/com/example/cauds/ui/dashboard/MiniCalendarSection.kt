package com.example.cauds.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cauds.ui.theme.Poppins
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import com.example.cauds.ui.theme.rdp
import com.example.cauds.ui.theme.rsp
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.BowlbyOne
import com.example.cauds.ui.theme.CobaltDarker
import com.example.cauds.ui.theme.SkyNormal

/**
 * MiniCalendarSection — a compact month view for the dashboard's bottom-left slot.
 *
 * HOW IT WORKS:
 * The calendar builds a grid of 7 columns (Sun–Sat). Each day cell is either:
 *   - Blank (leading/trailing filler to align day 1 to the correct weekday)
 *   - A number, optionally highlighted based on drink count
 *
 * DRINK INTENSITY:
 * The `drinkCountByDay` map tells us how many drinks were logged on each day.

 * This gives a "heat map" effect — heavier drinking days are darker.
 *
 * TODAY'S DATE gets a special circle highlight so the user can spot it quickly.
 *
 * The whole section is one big clickable area that navigates to CalendarScreen.
 *
 * @param drinkCountByDay  Map of LocalDate → number of drinks logged that day
 * @param onClick          Navigates to the full CalendarScreen
 */
@Composable
fun MiniCalendarSection(
    drinkCountByDay: Map<LocalDate, Int>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val currentMonth = YearMonth.now()
    val monthTitle = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}"

    Column(
        modifier = modifier
            .background(SkyNormal)
            .clickable { onClick() }
            .padding(12.rdp())
    ) {
        // Month + year header
        Text(
            text = monthTitle,
            fontSize = 12.rsp(),
            fontFamily = BowlbyOne,
            fontWeight = FontWeight.Normal,
            color = CobaltDarker,
        )

        // Weekday header row: S M T W T F S
        val weekdays = listOf(
            DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            weekdays.forEach { dow ->
                val isWeekend = dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY
                Text(
                    // .NARROW gives single letter: "S", "M", "T", etc.
                    text = dow.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontFamily = Poppins,
                    fontSize = 10.rsp(),
                    fontWeight = FontWeight.SemiBold,
                    color = if (isWeekend) Color(0xFF000000).copy(alpha = 0.4f) else Color(0xFF1A3720)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.rdp()))

        // Build the grid cells for this month.
        // "cells" is a list of nullable LocalDates — null means a blank filler cell.
        val cells = remember(currentMonth) { buildMiniCalendarCells(currentMonth) }
        Column(
            modifier = Modifier.weight(1f), // let the date grid fill the space
            verticalArrangement = Arrangement.spacedBy(4.rdp()) // space between each row of week
        ) {
            // Chunk into rows of 7 (one per week) and render each row
            cells.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        Box(
                            modifier = Modifier
                                .weight(1f)        // Equal width for all 7 columns
                                .aspectRatio(1f),  // Square cells
                            contentAlignment = Alignment.Center
                        ) {
                            if (date != null) {
                                val drinkCount = drinkCountByDay[date] ?: 0
                                val isToday = date == today
                                val isFuture = date.isAfter(today)
                                val isWeekendDay =
                                    date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
                                // Background highlight — color intensity scales with drink count
                                val bgColor = when {
                                    drinkCount >= 5 -> Color(0x525900FF) // Marker_Red (Purple 32%)
                                    drinkCount >= 3 -> Color(0xB36074FF) // Marker_Blue (Blue 70%)
                                    drinkCount >= 2 -> Color(0x666EA8FE) // Marker_Yellow (SkyBlue 40%)
                                    drinkCount >= 1 -> Color(0x33FFFFFF) // Marker_Grey (White 20%)
                                    else -> Color.Transparent
                                }

                                // Text color — white on dark backgrounds, black otherwise
                                val textColor = when {
                                    isToday -> Color.White
                                    isFuture -> Color(0xFF121E30).copy(alpha = 0.2f)
                                    isWeekendDay -> Color(0xFF121E30).copy(alpha = 0.4f)
                                    else -> Color(0xFF121E30)
                                }

                                if (isToday) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.rdp())
                                            .background(Color.Black, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            fontSize = 12.rsp(),
                                            fontFamily = Poppins,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(0.85f)
                                            .background(bgColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            fontSize = 12.rsp(),
                                            fontFamily = Poppins,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textColor
                                        )
                                    }
                                }
                            }
                            // If date is null → blank cell, Box stays empty
                        }
                    }
                }
            }
        }
    }
}


/**
 * Builds a flat list of nullable LocalDates representing the calendar grid.
 *
 * The list starts with null entries for the blank cells before day 1
 * (to align it to the correct weekday column), then has the actual
 * dates 1..N, then trailing nulls to fill out the last row to 7 cells.
 *
 * Example for a month starting on Wednesday:
 * [null, null, null, Jan1, Jan2, Jan3, Jan4, Jan5, Jan6, ...]
 *  Sun   Mon   Tue   Wed   Thu   Fri   Sat
 */
private fun buildMiniCalendarCells(yearMonth: YearMonth): List<LocalDate?> {
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()

    // dayOfWeek.value: Monday=1 ... Sunday=7
    // We want Sunday=0 for a Sun-start calendar, so: value % 7
    val startOffset = firstDay.dayOfWeek.value % 7

    val cells = mutableListOf<LocalDate?>()

    // Leading blanks
    repeat(startOffset) { cells.add(null) }

    // Actual days
    for (day in 1..daysInMonth) {
        cells.add(yearMonth.atDay(day))
    }

    // Trailing blanks to complete the final row
    while (cells.size % 7 != 0) {
        cells.add(null)
    }

    return cells
}
package com.example.cauds.ui.dashboard

import androidx.compose.foundation.background
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

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
 * We pick a base highlight color and vary its alpha (opacity) based on count:
 *   - 0 drinks → no highlight
 *   - 1 drink  → light (alpha 0.3)
 *   - 2 drinks → medium (alpha 0.5)
 *   - 3+ drinks → full (alpha 0.8)
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
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE8E0F0))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        // Month + year header (e.g., "March 2026")
        Text(
            text = monthTitle,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Weekday header row: S M T W T F S
        val weekdays = listOf(
            DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            weekdays.forEach { dow ->
                Text(
                    // .NARROW gives single letter: "S", "M", "T", etc.
                    text = dow.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Build the grid cells for this month.
        // "cells" is a list of nullable LocalDates — null means a blank filler cell.
        val cells = remember(currentMonth) { buildMiniCalendarCells(currentMonth) }

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

                            // Background highlight — color intensity scales with drink count
                            val bgColor = when {
                                isToday -> Color.Black  // Today gets a solid dark circle
                                drinkCount >= 3 -> Color(0xFF7E57C2).copy(alpha = 0.8f)   // Heavy
                                drinkCount == 2 -> Color(0xFF7E57C2).copy(alpha = 0.5f)   // Medium
                                drinkCount == 1 -> Color(0xFF7E57C2).copy(alpha = 0.3f)   // Light
                                else -> Color.Transparent                                  // No drinks
                            }

                            // Text color — white on dark backgrounds, black otherwise
                            val textColor = when {
                                isToday -> Color.White
                                drinkCount >= 2 -> Color.White
                                else -> Color.Black
                            }

                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(bgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    fontSize = 9.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = textColor
                                )
                            }
                        }
                        // If date is null → blank cell, Box stays empty
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
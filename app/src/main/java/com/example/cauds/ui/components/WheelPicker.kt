package com.example.cauds.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cauds.ui.theme.BigShouldersDisplay
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


// ── General-purpose WheelPicker ───────────────────────────────

@Composable
fun WheelPicker(
    items: List<String>,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    initialIndex: Int = 0,
    showHighlight: Boolean = true,
    fontSize: TextUnit = 24.sp,
    textColor: Color = Color(0xFF1A3720),
    itemHeight: Dp = 48.dp,
    visibleItems: Int = 5
) {
    val padCount = visibleItems / 2
    val paddedItems = List(padCount) { "" } + items + List(padCount) { "" }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val centerIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex + padCount }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                val i = index + padCount
                if (i in padCount..items.size + padCount - 1) onItemSelected(i - padCount)
            }
    }

    Box(modifier = modifier.height(itemHeight * visibleItems)) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(paddedItems) { index, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        fontFamily = BigShouldersDisplay,
                        fontSize = fontSize,
                        textAlign = TextAlign.Center,
                        color = textColor.copy(alpha = if (index == centerIndex) 1f else 0.3f)
                    )
                }
            }
        }

        // Stationary highlight bar
        if (showHighlight) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .align(Alignment.Center)
                    .padding(horizontal = 48.dp)
                    .background(Color(0x1A3583A4))
            )
        }

        // Top fade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * (padCount.toFloat() + 0.5f))
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFEF5DC), Color.Transparent)
                    )
                )
        )

        // Bottom fade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * (padCount.toFloat() + 0.5f))
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xFFFEF5DC))
                    )
                )
        )
    }
}


// ── Date-specific WheelPicker (month + day side by side) ──────

@Composable
fun DateWheelPicker(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val months = Month.entries.map {
        it.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }
    val days = (1..31).map { it.toString() }

    var currentMonthIndex by remember { mutableIntStateOf(selectedDate.monthValue - 1) }
    var currentDayIndex by remember { mutableIntStateOf(selectedDate.dayOfMonth - 1) }

    val itemHeight = 40.dp

    Box(modifier = modifier.fillMaxWidth()) {
        // Stationary highlight bar spanning both pickers
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .align(Alignment.Center)
                .padding(horizontal = 32.dp)
                .background(Color(0x1A3583A4))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, end = 80.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WheelPicker(
                items = months,
                initialIndex = currentMonthIndex,
                onItemSelected = { index ->
                    currentMonthIndex = index
                    emitDate(currentMonthIndex, currentDayIndex, selectedDate.year, onDateSelected)
                },
                modifier = Modifier.weight(1f),
                showHighlight = false,
                fontSize = 20.sp,
                itemHeight = itemHeight,
                visibleItems = 3
            )

            Spacer(modifier = Modifier.width(4.dp))

            WheelPicker(
                items = days,
                initialIndex = currentDayIndex,
                onItemSelected = { index ->
                    currentDayIndex = index
                    emitDate(currentMonthIndex, currentDayIndex, selectedDate.year, onDateSelected)
                },
                modifier = Modifier.weight(0.65f),
                showHighlight = false,
                fontSize = 20.sp,
                itemHeight = itemHeight,
                visibleItems = 3
            )
        }
    }
}

/**
 * Safely builds a LocalDate from the picker indices.
 *
 * Not every month has 31 days — if the user selects February + day 31,
 * we clamp the day to the actual last day of that month (28 or 29)
 * using YearMonth.lengthOfMonth() instead of crashing.
 */
private fun emitDate(
    monthIndex: Int,
    dayIndex: Int,
    year: Int,
    onDateSelected: (LocalDate) -> Unit
) {
    val month = Month.entries[monthIndex]
    val maxDay = YearMonth.of(year, month).lengthOfMonth()
    val clampedDay = (dayIndex + 1).coerceAtMost(maxDay)
    onDateSelected(LocalDate.of(year, month, clampedDay))
}
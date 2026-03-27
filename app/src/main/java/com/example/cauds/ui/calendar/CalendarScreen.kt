package com.example.cauds.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cauds.ui.components.DrinkDots
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.Poppins
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner


private val CalendarBlue = Color(0xFFAFC9DC)
private val CreamBackground = Color(0xFFFEF5DC)
private val DarkNavy = Color(0xFF121E30)
private val WeekendColor = Color(0x9933578A)
private val RowDivider = Color(0x4D000000)
private val JournalCircle = Color(0xFF9EB5C6)


@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = viewModel(),
    onDayClick: (LocalDate) -> Unit = {},
    onBack: () -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val topPadding = if (screenHeight > 700) 64.dp else 48.dp
    val calendarFontSize = if (screenHeight > 700) 16.sp else 14.sp
    val daycellPadding = if (screenHeight > 700) 58.dp else 54.dp

    val currentMonth = viewModel.currentMonth
    val isCurrentMonth = currentMonth == YearMonth.now()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadMonth(viewModel.currentMonth)
                viewModel.loadJournalDays()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalendarBlue)
    ) {
        // ── Top cream section (back arrow + month title) ──────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CreamBackground)
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(topPadding))

            IconButton(
                onClick = onBack,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DarkNavy,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val monthName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                Text(
                    text = "$monthName ${currentMonth.year}",
                    fontFamily = BigShouldersDisplay,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Normal,
                    color = DarkNavy
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { viewModel.goPrevMonth() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous month",
                        tint = DarkNavy
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { viewModel.goNextMonth() },
                    enabled = !isCurrentMonth,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next month",
                        tint = if (isCurrentMonth) DarkNavy.copy(alpha = 0.2f) else DarkNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // ── Calendar grid section (blue background) ───────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // ── Weekday labels ────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth()) {
                val days = listOf(
                    DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
                )
                days.forEach { dow ->
                    val isWeekend = dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY
                    Text(
                        text = dow.getDisplayName(TextStyle.SHORT, Locale.getDefault()).first().uppercase(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontFamily = Poppins,
                        fontSize = calendarFontSize,
                        fontWeight = FontWeight.Normal,
                        color = if (isWeekend) WeekendColor else DarkNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // ── Day rows with dividers ────────────────────────
            val cells = remember(currentMonth) { buildMonthCells(currentMonth) }
            val rows = cells.chunked(7)

            rows.forEachIndexed { index, rowCells ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    rowCells.forEach { cell ->
                        Box(modifier = Modifier.weight(1f)) {
                            DayCell(
                                cell = cell,
                                hasJournal = cell.date?.let { it in viewModel.journalDays } == true,
                                drinkCount = cell.date?.let { viewModel.logCountByDay[it] } ?: 0,
                                isToday = cell.date == LocalDate.now(),
                                fontSize = calendarFontSize,
                                daycellPadding = daycellPadding,
                                onClick = { date ->
                                    if (date != null) onDayClick(date)
                                }
                            )
                        }
                    }
                }

                if (index < rows.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = RowDivider
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}


// ── Day cell ──────────────────────────────────────────────────

private data class CalendarCell(
    val date: LocalDate?
)

private fun buildMonthCells(yearMonth: YearMonth): List<CalendarCell> {
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val startOffset = firstDay.dayOfWeek.value % 7

    val cells = mutableListOf<CalendarCell>()
    repeat(startOffset) { cells.add(CalendarCell(date = null)) }
    for (day in 1..daysInMonth) {
        cells.add(CalendarCell(date = yearMonth.atDay(day)))
    }
    while (cells.size % 7 != 0) {
        cells.add(CalendarCell(date = null))
    }
    return cells
}

@Composable
private fun DayCell(
    cell: CalendarCell,
    hasJournal: Boolean,
    drinkCount: Int,
    isToday: Boolean,
    fontSize: TextUnit = 14.sp,
    daycellPadding: Dp = 54.dp,
    onClick: (LocalDate?) -> Unit
) {
    val date = cell.date
    val isBlank = date == null
    val isWeekend = date?.dayOfWeek?.value == 6 || date?.dayOfWeek?.value == 7

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(daycellPadding)
            .then(
                if (!isBlank) Modifier.clickable { onClick(date) } else Modifier
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isBlank && isToday) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(DarkNavy, CircleShape)
                    )
                } else if (!isBlank && hasJournal) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(JournalCircle, CircleShape)
                    )
                }

                Text(
                    text = date?.dayOfMonth?.toString() ?: "",
                    fontFamily = Poppins,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Normal,
                    color = when {
                        isBlank -> Color.Transparent
                        isToday -> Color.White
                        isWeekend -> WeekendColor
                        else -> DarkNavy
                    },
                    textAlign = TextAlign.Center
                )
            }

            if (drinkCount > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                DrinkDots(count = drinkCount)
            }
        }
    }
}
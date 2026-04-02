package com.example.cauds.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cauds.components.JournalEntry
import com.example.cauds.data.model.LogItem
import com.example.cauds.ui.navigation.Screen
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.Poppins
import com.example.cauds.viewmodel.JournalViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import com.example.cauds.R
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.TextUnit
import com.example.cauds.components.EmptyTodayCard
import com.example.cauds.components.JournalEntryPager
import com.example.cauds.ui.components.DrinkDots
import com.example.cauds.ui.drinklog.DrinkLogViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.cauds.ui.theme.CloverDarker


private val CreamBackground = Color(0xFFFEF5DC)
private val CalendarBlue     = Color(0xCCAFC9DC)
private val DarkNavy         = Color(0xFF121E30)
private val WeekendColor     = Color(0x9933578A)
private val JournalCircle    = Color(0xFF9EB5C6)


@Composable
fun DaySummaryScreen(
    date: String,
    calendarViewModel: CalendarViewModel,
    journalViewModel: JournalViewModel,
    drinkLogViewModel: DrinkLogViewModel,
    navController: NavController,
    onBack: () -> Unit
) {

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val calendarTopPadding = if (screenHeight > 700) 64.dp else 48.dp
    val calendarFontSize = if (screenHeight > 700) 16.sp else 14.sp

    // DO NOT REMOVE: This makes it so when you log something, it returns to the same day!
    var currentDate by remember {
        mutableStateOf(calendarViewModel.viewedDate ?: LocalDate.parse(date))
    }
    LaunchedEffect(currentDate) {
        calendarViewModel.viewedDate = currentDate
    }

    // DO NOT REMOVE:
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                calendarViewModel.loadMonth(calendarViewModel.currentMonth)
                calendarViewModel.loadJournalDays()
                journalViewModel.loadEntries()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val dateString = currentDate.toString()
    LaunchedEffect(dateString) {
        journalViewModel.loadEntries()
    }

    var didntDrink by remember(dateString) { mutableStateOf(false) }

    val logsForDay = remember(calendarViewModel.monthLogs, dateString) {
        calendarViewModel.monthLogs.filter { it.data.date == dateString }
    }

    val totalDrinks = remember(logsForDay) {
        logsForDay.size
    }

    val journalEntries = remember(journalViewModel.entries, dateString) {
        journalViewModel.entries.filter { (_, data) ->
            val entryDate = data.createdAt?.toDate()?.toInstant()
                ?.atZone(ZoneId.systemDefault())
                ?.toLocalDate()
            entryDate?.toString() == dateString
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        // ── Calendar header (blue background) ─────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CalendarBlue)
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = calendarTopPadding, bottom = 16.dp)        ) {
            WeekHeader(
                selectedDate = currentDate,
                onBack = onBack,
                onDayClick = { newDate -> currentDate = newDate },
                logCountByDay = calendarViewModel.logCountByDay,
                journalDays = calendarViewModel.journalDays,
                fontSize = calendarFontSize
            )
        }

        // ── Content (cream background, scrollable) ────────────
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 24.dp)
            ) {
                // ── Drinks header ─────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Drinks",
                        fontFamily = BigShouldersDisplay,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.W400,
                        color = DarkNavy
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "$totalDrinks",
                        fontFamily = BigShouldersDisplay,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = CloverDarker.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        painter = painterResource(id = R.drawable.plus_button),
                        contentDescription = "Add drink",
                        tint = DarkNavy,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable {
                                drinkLogViewModel.selectDate(currentDate)
                                navController.navigate(Screen.Tracking.route)
                            }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

// ── Drink content: list / "I didn't drink" / "No drinks" card
                if (totalDrinks > 0) {
                    // Normal drink list — user has logged drinks
                    DaySummaryDrinkList(
                        logs = logsForDay,
                        onRemoveItem = { logId -> calendarViewModel.deleteLog(logId) },
                        onRemoveBatch = { logIds -> logIds.forEach { calendarViewModel.deleteLog(it) } },
                        onItemClick = { logId ->
                            drinkLogViewModel.selectDate(currentDate)
                            drinkLogViewModel.enterEditMode(logId)
                            navController.navigate(Screen.Tracking.route)
                        }
                    )
                } else if (didntDrink) {
                    // User explicitly marked "I didn't drink" — show the confirmation card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(Color(0x80EDF5EF))
                            .border(0.5.dp, Color(0x80000000))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "No drinks",
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            color = Color(0xFF2E4A2E)
                        )
                    }
                } else {
                    // No drinks logged and no "didn't drink" flag — show the pill button
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0x80EDF5EF), shape = CircleShape)
                                .border(0.5.dp, Color(0x80000000), shape = CircleShape)
                                .clickable { didntDrink = true }
                                .padding(horizontal = 24.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "I didn't drink",
                                fontFamily = Poppins,
                                fontSize = 14.sp,
                                color = DarkNavy
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Journal header ────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Journal",
                        fontFamily = BigShouldersDisplay,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.W400,
                        color = DarkNavy
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${journalEntries.size}",
                        fontFamily = BigShouldersDisplay,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = CloverDarker.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        painter = painterResource(id = R.drawable.plus_button),
                        contentDescription = "Add journal entry",
                        tint = DarkNavy,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable {
                                journalViewModel.setEntryDate(currentDate)
                                navController.navigate(Screen.CreateEntry.route)
                            }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (journalEntries.isEmpty()) {
                    EmptyTodayCard(
                        date = currentDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM d")),
                        dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                        onClick = {
                            journalViewModel.setEntryDate(currentDate)
                            navController.navigate(Screen.CreateEntry.route)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                } else {
                    JournalEntryPager(
                        entries = journalEntries.map { (docId, data) ->
                            JournalEntry(
                                documentId = docId,
                                timestampMillis = data.createdAt?.toDate()?.time ?: 0L,
                                body = data.entry
                            )
                        },
                        onEntryClick = { docId ->
                            journalViewModel.setEditingEntry(docId)
                            navController.navigate(Screen.CreateEntry.route)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Top fade
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(CreamBackground, Color.Transparent)
                        )
                    )
            )

            // Bottom fade
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, CreamBackground)
                        )
                    )
            )
        }
    }
}


// ── Week header (lives inside the blue section) ───────────────

@Composable
private fun WeekHeader(
    selectedDate: LocalDate,
    onBack: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
    logCountByDay: Map<LocalDate, Int> = emptyMap(),
    journalDays: Set<LocalDate> = emptySet(),
    fontSize: TextUnit = 14.sp
) {
    val startOfWeek = selectedDate.minusDays(
        (selectedDate.dayOfWeek.value % 7).toLong()
    )
    val weekDates = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Column(modifier = Modifier.fillMaxWidth()) {

        // ── Back + month name ─────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = "Back",
                    tint = DarkNavy,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = selectedDate.month.getDisplayName(
                    TextStyle.FULL, Locale.getDefault()
                ),
                fontFamily = BigShouldersDisplay,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                color = DarkNavy
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Weekday letters ───────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth()) {
            weekDates.forEach { d ->
                val isWeekend = d.dayOfWeek.value == 6 || d.dayOfWeek.value == 7
                Text(
                    text = d.dayOfWeek.getDisplayName(
                        TextStyle.SHORT, Locale.getDefault()
                    ).first().uppercase(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontFamily = Poppins,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Normal,
                    color = if (isWeekend) WeekendColor else DarkNavy
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Date numbers + dots ───────────────────────────────
        Row(modifier = Modifier.fillMaxWidth()) {
            weekDates.forEach { d ->
                val isSelected = d == selectedDate
                val count = logCountByDay[d] ?: 0
                val hasJournal = d in journalDays
                val isFuture = d.isAfter(LocalDate.now())

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .then(if (!isFuture) Modifier.clickable { onDayClick(d) } else Modifier),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.height(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isFuture) {
                            Text(
                                text = d.dayOfMonth.toString(),
                                fontFamily = Poppins,
                                fontSize = fontSize,
                                fontWeight = FontWeight.Normal,
                                color = DarkNavy.copy(alpha = 0.2f),
                                textAlign = TextAlign.Center
                            )
                        } else if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(DarkNavy, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = d.dayOfMonth.toString(),
                                    fontFamily = Poppins,
                                    fontSize = fontSize,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            val isWeekend = d.dayOfWeek.value == 6 || d.dayOfWeek.value == 7

                            if (hasJournal) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(JournalCircle, CircleShape)
                                )
                            }

                            Text(
                                text = d.dayOfMonth.toString(),
                                fontFamily = Poppins,
                                fontSize = fontSize,
                                fontWeight = FontWeight.Normal,
                                color = if (isWeekend) WeekendColor else DarkNavy,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (count > 0 && !isFuture) {
                        Spacer(modifier = Modifier.height(12.dp))
                        DrinkDots(count = count)
                    }
                }
            }
        }
    }
}


// ── Grouped drink list ────────────────────────────────────────

@Composable
fun DaySummaryDrinkList(
    logs: List<LogItem>,
    onRemoveItem: (String) -> Unit,
    onRemoveBatch: (List<String>) -> Unit,
    onItemClick: (String) -> Unit
) {
    if (logs.isEmpty()) return

    val groupedLogs = logs.groupBy { "${it.data.drinkType}_${it.data.drinkSize}" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0x80EDF5EF))
            .border(0.35.dp, Color(0x80000000))
    ) {
        groupedLogs.values.forEachIndexed { groupIndex, group ->
            val first = group.first().data
            val groupTotal = group.sumOf { it.data.drinkCost }
            val groupIds = group.map { it.id }

            SwipeToDeleteRow(
                height = 44.dp,
                onDelete = { onRemoveBatch(groupIds) },
                onClick = { onItemClick(group.first().id) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = drinkIconRes(first.drinkType)),
                        contentDescription = first.drinkType,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = first.drinkType,
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Color.Black
                    )

                    if (first.drinkSize.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = first.drinkSize.uppercase(),
                            fontFamily = Poppins,
                            fontSize = 10.sp,
                            color = Color.Black.copy(alpha = 0.4f)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "$${String.format("%.2f", groupTotal)}",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }

            HorizontalDivider(color = Color(0xFF000000).copy(alpha = 0.5f), thickness = 0.5.dp)

            if (group.size > 1) {
                group.forEach { logItem ->
                    SwipeToDeleteRow(
                        height = 34.dp,
                        onDelete = { onRemoveItem(logItem.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(40.dp))

                            Text(
                                text = logItem.data.drinkType,
                                fontFamily = Poppins,
                                fontSize = 12.sp,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = "$${String.format("%.2f", logItem.data.drinkCost)}",
                                fontFamily = Poppins,
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFF000000).copy(alpha = 0.5f), thickness = 0.5.dp)
                }
            }
        }
    }
}


// ── Swipe-to-delete wrapper ───────────────────────────────────

@Composable
private fun SwipeToDeleteRow(
    height: Dp,
    onDelete: () -> Unit,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var showDelete by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().height(height)) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -15) showDelete = true
                        else if (dragAmount > 15) showDelete = false
                    }
                }
                .clickable {
                    if (showDelete) showDelete = false
                    else onClick()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                content()
            }

            if (showDelete) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(44.dp)
                        .background(Color(0xFFFF5252))
                        .clickable {
                            showDelete = false
                            onDelete()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


// ── Drink icon mapper ─────────────────────────────────────────

private fun drinkIconRes(drinkType: String): Int {
    val lower = drinkType.lowercase()
    return when {
        lower.contains("ale") || lower.contains("beer")
                || lower.contains("lager") || lower.contains("stout")
                || lower.contains("ipa")                                -> R.drawable.ic_beer_o
        lower.contains("cider") || lower.contains("seltzer")           -> R.drawable.ic_fermented_o
        lower.contains("wine") || lower.contains("sauvignon")
                || lower.contains("merlot") || lower.contains("pinot")
                || lower.contains("cabernet") || lower.contains("rosé")
                || lower.contains("rose") || lower.contains("champagne")
                || lower.contains("prosecco") || lower.contains("riesling")
                || lower.contains("chardonnay") || lower.contains("shiraz")
                || lower.contains("malbec")                             -> R.drawable.ic_wine_o
        lower.contains("cocktail") || lower.contains("margarita")
                || lower.contains("mojito") || lower.contains("martini")
                || lower.contains("bloody mary") || lower.contains("daiquiri")
                || lower.contains("cosmopolitan") || lower.contains("negroni")
                || lower.contains("old fashioned") || lower.contains("spritz") -> R.drawable.ic_cocktail_mixed_o
        lower.contains("vodka") || lower.contains("whiskey")
                || lower.contains("rum") || lower.contains("gin")
                || lower.contains("tequila") || lower.contains("brandy")
                || lower.contains("bourbon") || lower.contains("scotch") -> R.drawable.ic_spirit_o
        else                                                             -> R.drawable.ic_beer_o
    }
}
package com.example.cauds.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cauds.components.EmptyTodayCard
import com.example.cauds.components.JournalEntry
import com.example.cauds.components.JournalEntryCard
import com.example.cauds.components.JournalEntryPager
import com.example.cauds.ui.navigation.Screen
import com.example.cauds.viewmodel.JournalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.res.painterResource
import com.example.cauds.R
import com.example.cauds.ui.theme.BigShouldersDisplay

private val HeadingColor = Color(0xFF1A3720)

private val JournalCardColors = listOf(
    Color(0xFFC0CBDB),
    Color(0x99499F5D),
    Color(0x99FAAAA5),
    Color(0x99FFCB46)
)

@Composable
fun JournalScreen(navController: NavController, viewModel: JournalViewModel) {

    var expandedEntryId by remember { mutableStateOf<String?>(null) }

    val todayDate = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())
    val todayDayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())

    // Kick off the data load once when the screen first appears
    LaunchedEffect(Unit) {
        viewModel.loadEntries()
    }

    // Show a snackbar if a delete fails, then clear the error so it doesn't re-trigger
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.deleteError) {
        if (viewModel.deleteError != null) {
            snackbarHostState.showSnackbar("Failed to delete entry: ${viewModel.deleteError}")
            viewModel.clearDeleteError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFEF5DC))
                .padding(paddingValues)
        ) {
            // ── Header: pencil icon + "Write an entry" ────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.CreateEntry.route) }
                    .statusBarsPadding()
                    .padding(top = 64.dp, bottom = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pencil),
                    contentDescription = "Write entry",
                    tint = HeadingColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Write an entry",
                    fontFamily = BigShouldersDisplay,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Normal,
                    color = HeadingColor
                )
            }

            // Loading spinner
            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }

            // Error state
            if (viewModel.errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = viewModel.errorMessage!!, color = Color.Red)
                }
                return@Scaffold
            }

            // Convert raw Firestore pairs into JournalEntry objects the card can display.
            // mapNotNull safely skips any entry where createdAt is somehow null.
            val entries = viewModel.entries.mapNotNull { (docId, data) ->
                val millis = data.createdAt?.toDate()?.time ?: return@mapNotNull null
                JournalEntry(
                    documentId = docId,
                    timestampMillis = millis,
                    body = data.entry
                )
            }

            val hasTodayEntry = entries.any { it.date == todayDate }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {

                // Show empty today card if there's no entry for today yet
                if (!hasTodayEntry) {
                    item {
                        EmptyTodayCard(
                            date = todayDate,
                            dayOfWeek = todayDayOfWeek,
                            onClick = { navController.navigate(Screen.CreateEntry.route) }
                        )
                    }
                }

                // Group entries by date so same-day entries get condensed into one pager card.
                // groupBy preserves insertion order, so entries stay sorted by date.
                val groupedByDate = entries.groupBy { it.date }
                val groupedList = groupedByDate.entries.toList()

                itemsIndexed(groupedList, key = { _, entry -> entry.key }) { index, (date, dayEntries) ->
                    val cardColor = JournalCardColors[index % JournalCardColors.size]

                    if (dayEntries.size == 1) {
                        val entry = dayEntries.first()
                        JournalEntryCard(
                            entry = entry,
                            isExpanded = expandedEntryId == entry.documentId,
                            onClick = {
                                expandedEntryId = if (expandedEntryId == entry.documentId) null else entry.documentId
                            },
                            onDelete = {
                                viewModel.deleteEntry(entry.documentId)
                                expandedEntryId = null
                            },
                            backgroundColor = cardColor
                        )
                    } else {
                        JournalEntryPager(
                            entries = dayEntries,
                            onDelete = { docId -> viewModel.deleteEntry(docId) },
                            backgroundColor = cardColor
                        )
                    }
                }
            }
        }
    }
}
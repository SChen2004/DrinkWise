package com.example.cauds.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
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
import com.example.cauds.ui.navigation.Screen
import com.example.cauds.viewmodel.JournalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text("Journal", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
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
                // "Write an entry" button
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Screen.CreateEntry.route) }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Write entry",
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Write an entry", color = Color.Gray, fontSize = 15.sp)
                    }
                }

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

                items(entries, key = { it.documentId }) { entry ->
                    JournalEntryCard(
                        entry = entry,
                        isExpanded = expandedEntryId == entry.documentId,
                        onClick = {
                            expandedEntryId = if (expandedEntryId == entry.documentId) null else entry.documentId
                        },
                        onDelete = {
                            viewModel.deleteEntry(entry.documentId)
                            expandedEntryId = null
                        }
                    )
                }
            }
        }
    }
}
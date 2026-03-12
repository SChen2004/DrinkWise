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
import com.example.cauds.viewmodel.JournalUiState
import com.example.cauds.viewmodel.JournalViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalScreen(navController: NavController, viewModel: JournalViewModel) {

    // Get the current user's ID from Firebase Auth.
    // The ?: "" is Kotlin's "elvis operator" — if currentUser is null, fall back to "".
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // `collectAsState()` is how a Composable listens to a StateFlow.
    // Every time the ViewModel pushes a new state, this value updates
    // and the screen recomposes automatically.
    val uiState by viewModel.uiState.collectAsState()
    val deleteError by viewModel.deleteError.collectAsState()

    var expandedEntryId by remember { mutableStateOf<String?>(null) }

    // Today's date formatted the same way JournalEntry formats its dates,
    // so we can compare them to detect whether today already has an entry.
    val todayDate = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())
    val todayDayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())

    // LaunchedEffect(Unit) runs once when the screen first appears.
    // This is where we kick off the initial data load. We use Unit as the key
    // because we only want this to run once — not on every recomposition.
    LaunchedEffect(Unit) {
        viewModel.loadEntries(userId)
    }

    // If there's a delete error, show a snackbar. The `deleteError` flow
    // will be non-null only when a delete has just failed.
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(deleteError) {
        if (deleteError != null) {
            snackbarHostState.showSnackbar("Failed to delete entry: $deleteError")
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

            // Switch on the current UI state
            when (val state = uiState) {

                is JournalUiState.Loading -> {
                    // Center a spinner while entries are being fetched
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is JournalUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.Red)
                    }
                }

                is JournalUiState.Success -> {
                    // Convert the raw Firestore data (Pair<String, JournalData>) into
                    // JournalEntry objects that the card composable knows how to display.
                    // `mapNotNull` skips any entry where createdAt is null (shouldn't happen,
                    // but Firestore fields are nullable so we handle it safely).
                    val entries = state.entries.mapNotNull { (docId, data) ->
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
                                    .clickable { navController.navigate("dashboard") }
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

                        // Empty today card if no entry exists for today yet
                        if (!hasTodayEntry) {
                            item {
                                EmptyTodayCard(
                                    date = todayDate,
                                    dayOfWeek = todayDayOfWeek,
                                    onClick = { navController.navigate("dashboard") }
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
                                    viewModel.deleteEntry(userId, entry.documentId)
                                    expandedEntryId = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
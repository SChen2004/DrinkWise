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
fun JournalScreen(
    navController: NavController,
    viewModel: JournalViewModel
) {
    val todayDate = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())
    val todayDayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())

    LaunchedEffect(Unit) {
        viewModel.loadEntries()
    }

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

            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }

            if (viewModel.errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = viewModel.errorMessage!!, color = Color.Red)
                }
                return@Scaffold
            }

            val entries = viewModel.entries.mapNotNull { (docId, data) ->
                val millis = data.createdAt?.toDate()?.time ?: return@mapNotNull null
                JournalEntry(
                    documentId = docId,
                    timestampMillis = millis,
                    body = data.entry
                )
            }

            val hasTodayEntry = entries.any { it.date == todayDate }
            val groupedByDate = entries.groupBy { it.date }
            val groupedList = groupedByDate.entries.toList()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                if (!hasTodayEntry) {
                    item {
                        EmptyTodayCard(
                            date = todayDate,
                            dayOfWeek = todayDayOfWeek,
                            onClick = { navController.navigate(Screen.CreateEntry.route) }
                        )
                    }
                }

                itemsIndexed(groupedList, key = { _, entry -> entry.key }) { index, (_, dayEntries) ->
                    val cardColor = JournalCardColors[index % JournalCardColors.size]

                    if (dayEntries.size == 1) {
                        val entry = dayEntries.first()
                        JournalEntryCard(
                            entry = entry,
                            isExpanded = false,
                            onClick = {
                                viewModel.setEditingEntry(entry.documentId)
                                navController.navigate(Screen.CreateEntry.route)
                            },
                            backgroundColor = cardColor
                        )
                    } else {
                        JournalEntryPager(
                            entries = dayEntries,
                            onEntryClick = { docId ->
                                viewModel.setEditingEntry(docId)
                                navController.navigate(Screen.CreateEntry.route)
                            },
                            backgroundColor = cardColor
                        )
                    }
                }
            }
        }
    }
}
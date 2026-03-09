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

@Composable
fun JournalScreen(navController: NavController) {

    // `remember { mutableStateOf(...) }` creates state that survives recompositions.
    // When state changes (e.g. you delete an entry), Compose re-draws only the parts
    // of the UI that depend on it. This is the core of how Compose works.

    // We're using `mutableStateListOf` so that Compose notices when items are added/removed.
    // A regular List wouldn't trigger a redraw when you call .remove() on it.
    val entries = remember {
        mutableStateListOf(
            JournalEntry(1, "Feb 19", "Thursday", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eu turpis molestie, dictum est a, mattis tellus. Sed dignissim, metus nec fringilla accumsan."),
            JournalEntry(2, "Feb 17", "Tuesday", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eu turpis molestie, dictum est a, mattis tellus. Sed dignissim, metus nec fringilla accumsan."),
            JournalEntry(3, "Feb 16", "Monday", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eu turpis molestie, dictum est a, mattis tellus. Sed dignissim, metus nec fringilla accumsan.")
        )
    }

    // This tracks which card is currently expanded. We store the entry's id (Int?),
    // and null means nothing is expanded. Only one can be expanded at a time.
    var expandedEntryId by remember { mutableStateOf<Int?>(null) }

    // "Feb 19" is today's date as per the design. We check if there's already an entry
    // for today. If not, we show the EmptyTodayCard instead.
    val todayDate = "Feb 19"
    val todayDayOfWeek = "Thursday"
    val hasTodayEntry = entries.any { it.date == todayDate }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {

        // Top header bar (dark background like in the design)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text("Journal", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        }

        // LazyColumn is Compose's equivalent of RecyclerView — it only renders items
        // currently visible on screen, which is efficient for long lists.
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            // "Write an entry" button at the top
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("dashboard") } // placeholder for CreateEntry
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

            // If there's no entry for today, show the empty card first
            if (!hasTodayEntry) {
                item {
                    EmptyTodayCard(
                        date = todayDate,
                        dayOfWeek = todayDayOfWeek,
                        onClick = { navController.navigate("dashboard") } // placeholder for CreateEntry
                    )
                }
            }

            // Render each journal entry as a card.
            // `items(entries)` is a LazyColumn helper that loops over the list efficiently.
            items(entries, key = { it.id }) { entry ->
                JournalEntryCard(
                    entry = entry,
                    isExpanded = expandedEntryId == entry.id,
                    onClick = {
                        // Toggle: if this card is already expanded, collapse it.
                        // Otherwise, expand it (and collapse any other open card).
                        expandedEntryId = if (expandedEntryId == entry.id) null else entry.id
                    },
                    onDelete = {
                        entries.remove(entry)
                        expandedEntryId = null  // collapse after deleting
                    }
                )
            }
        }
    }
}
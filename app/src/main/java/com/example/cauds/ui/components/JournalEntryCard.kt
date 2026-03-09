package com.example.cauds.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// This data class represents a single journal entry.
// A data class in Kotlin is just a simple container for storing related fields.
data class JournalEntry(
    val id: Int,
    val date: String,       // e.g. "Feb 19"
    val dayOfWeek: String,  // e.g. "Thursday"
    val body: String
)

// This is the reusable card composable. By accepting an `isExpanded` flag and
// an `onDelete` callback, it can behave differently depending on context —
// collapsed on the list, expanded when tapped. The dashboard can reuse this
// same composable but pass isExpanded = false and no onDelete if it doesn't need those.
@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null,  // nullable — not every use case needs a delete button
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Header row: date on the left, day of week on the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = entry.date, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(text = entry.dayOfWeek, fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body text. When collapsed, maxLines = 2 so it truncates with "..."
            // When expanded, maxLines is effectively unlimited (Int.MAX_VALUE).
            Text(
                text = entry.body,
                fontSize = 14.sp,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            // Only show the delete button when the card is expanded AND a delete handler was provided
            if (isExpanded && onDelete != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

// This is the "empty state" card shown when there's no entry for today yet.
// It's a separate composable because its purpose and look are distinct —
// it's a prompt to write, not a display of content.
@Composable
fun EmptyTodayCard(
    date: String,
    dayOfWeek: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = date, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(text = dayOfWeek, fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // The placeholder text input look
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp))
                    .padding(10.dp)
            ) {
                Text("Type something...", color = Color.LightGray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Reflecting on your week can help you stay aware of your habits.",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
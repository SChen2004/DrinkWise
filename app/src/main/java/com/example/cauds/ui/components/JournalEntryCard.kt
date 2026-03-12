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
import java.text.SimpleDateFormat
import java.util.Locale

// Updated to use a String documentId (Firestore's ID) instead of an Int,
// and a Long timestamp (milliseconds) instead of pre-formatted date strings.
// The card itself will handle formatting the timestamp for display.
data class JournalEntry(
    val documentId: String,
    val timestampMillis: Long,
    val body: String
) {
    // These are computed properties — they derive their value from timestampMillis
    // automatically whenever you access them, so we don't have to store them separately.
    val date: String
        get() = SimpleDateFormat("MMM d", Locale.getDefault()).format(timestampMillis)

    val dayOfWeek: String
        get() = SimpleDateFormat("EEEE", Locale.getDefault()).format(timestampMillis)
}

@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null,
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
                Text(text = entry.date, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(text = entry.dayOfWeek, fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = entry.body,
                fontSize = 14.sp,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

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
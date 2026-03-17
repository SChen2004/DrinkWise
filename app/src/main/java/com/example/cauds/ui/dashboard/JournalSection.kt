package com.example.cauds.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * JournalSection — the mini journal card on the dashboard.
 *
 * This is NOT an actual text editor. It's a tappable card that mimics
 * the look of the CreateEntryScreen (date label + "Type something..." placeholder).
 * Tapping anywhere on it navigates to the real CreateEntryScreen.
 *
 * If today already has a journal entry, we show a preview of the first
 * ~100 chars instead of the placeholder. This gives the user a visual
 * cue that they've already written something today.
 *
 * @param todayEntryPreview  First ~100 chars of today's entry, or null if none exists
 * @param onClick            Navigates to CreateEntryScreen (or JournalScreen, your call)
 */
@Composable
fun JournalSection(
    todayEntryPreview: String?,
    onNewEntryClick: () -> Unit,
    onViewJournalClick: () -> Unit
) {
    val todayDate = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                // If there's already an entry, go to the journal list to view it.
                // If not, go straight to the create screen.
                if (todayEntryPreview != null) onViewJournalClick() else onNewEntryClick()
            }
            .padding(4.dp)
    ) {
        // Date badge — bold, sitting slightly above the card body
        Text(
            text = todayDate,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // The "text field" area — just a box with placeholder text, not an actual input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .background(Color(0xFFF5F5F0), RoundedCornerShape(4.dp))
                .padding(12.dp)
        ) {
            Text(
                text = todayEntryPreview ?: "Type something...",
                fontSize = 14.sp,
                // Gray if placeholder, darker if showing a real entry preview
                color = if (todayEntryPreview != null) Color.DarkGray else Color.LightGray
            )
        }
    }
}
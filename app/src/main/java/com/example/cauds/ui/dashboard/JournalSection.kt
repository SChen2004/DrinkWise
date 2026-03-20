package com.example.cauds.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.Poppins
import com.example.cauds.ui.theme.BackgroundSand
import com.example.cauds.ui.theme.rdp
import com.example.cauds.ui.theme.rsp

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
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val todayDate = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())
    val todayDayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundSand)
            .clickable {
                // If there's already an entry, go to the journal list to view it.
                // If not, go straight to the create screen.
                if (todayEntryPreview != null) onViewJournalClick() else onNewEntryClick()
            }
    ) {
        // The "text field" area — just a box with placeholder text, not an actual input
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (screenHeight < 700.dp) {
                    80.rdp()
                } else if (screenHeight < 850.dp) {
                    110.rdp()
                } else {
                    140.rdp()
                })
                .background(Color(0xFFEDF5EF).copy(alpha = 0.5f))
                .border(0.5.rdp(), Color.Black.copy(alpha = 0.5f))
                .padding(16.rdp())
        ) {
            // Date headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = todayDate,
                    fontSize = if (screenHeight < 700.dp) {
                        20.rsp()
                    } else if (screenHeight < 850.dp) {
                        24.rsp()
                    } else {
                        28.rsp()
                    },
                    fontFamily = BigShouldersDisplay,
                    color = Color(0xFF1A3720)
                )
                Text(
                    text = todayDayOfWeek,
                    fontSize = if (screenHeight < 700.dp) {
                        12.rsp()
                    } else if (screenHeight < 850.dp) {
                        16.rsp()
                    } else {
                        20.rsp()
                    },
                    fontFamily = BigShouldersDisplay,
                    color = Color(0xFF1A3720)
                )
            }

            Spacer(modifier = Modifier.height(16.rdp()))

            // Journal preview or empty place holder
            Text(
                text = todayEntryPreview ?: "Type something...",
                fontSize = 14.rsp(),
                fontFamily = Poppins,
                lineHeight = 22.rsp(),
                color = if (todayEntryPreview != null) Color(0xFF1A3720) else Color(0xFFA0A5A0),
                maxLines = if (screenHeight < 700.dp) {
                    1
                } else if (screenHeight < 850.dp) {
                    2
                } else {
                    3
                },
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
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
import com.example.cauds.ui.theme.CloverDarker
import com.example.cauds.ui.theme.Tertiary

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
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            modifier = Modifier
                .fillMaxWidth()
                .background(Tertiary)
                .padding(16.rdp())
        ) {
            // Date headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = todayDate,
                    fontSize = 24.rsp(),
                    fontFamily = BigShouldersDisplay,
                    color = CloverDarker,
                    style = androidx.compose.ui.text.TextStyle(
                        platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                            includeFontPadding = false
                        ),
                        lineHeightStyle = androidx.compose.ui.text.style.LineHeightStyle(
                            alignment = androidx.compose.ui.text.style.LineHeightStyle.Alignment.Center,
                            trim = androidx.compose.ui.text.style.LineHeightStyle.Trim.None
                        )
                    )
                )
                Text(
                    text = todayDayOfWeek,
                    fontSize = 20.rsp(),
                    fontFamily = BigShouldersDisplay,
                    color = CloverDarker,
                    style = androidx.compose.ui.text.TextStyle(
                        platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
            }

            // Journal preview or empty place holder
            Text(
                text = todayEntryPreview ?: "Type something...",
                fontSize = if (todayEntryPreview != null) 12.rsp() else 14.rsp(),
                fontFamily = Poppins,
                color = if (todayEntryPreview != null) CloverDarker else CloverDarker.copy(alpha = 0.4f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis, 
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.rdp(), end = 8.rdp())
                    .heightIn(min = 60.rdp()),
                style = androidx.compose.ui.text.TextStyle(
                    platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
        }
    }
}

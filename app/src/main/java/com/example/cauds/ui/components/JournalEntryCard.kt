package com.example.cauds.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextOverflow
import com.example.cauds.ui.theme.Poppins
import com.example.cauds.ui.theme.BigShouldersDisplay
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults


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
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0x80EDF5EF)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 148.dp)
            .background(backgroundColor)
            .border(0.5.dp, Color(0x801A3720))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = entry.date,
                    fontFamily = BigShouldersDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    color = Color(0xFF121E30)
                )
                Text(
                    text = entry.dayOfWeek,
                    fontFamily = BigShouldersDisplay,
                    fontSize = 20.sp,
                    color = Color(0xFF121E30)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = entry.body,
                fontFamily = Poppins,
                fontSize = 14.sp,
                color = Color(0xFF121E30),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (isExpanded && onDelete != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = BorderStroke(1.dp, Color.Red)
                    ) {
                        Text("Delete", fontFamily = Poppins)
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 148.dp)
            .background(Color(0x80EDF5EF))
            .border(0.5.dp, Color(0x801A3720))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = date,
                    fontFamily = BigShouldersDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    color = Color(0xFF121E30)
                )
                Text(
                    text = dayOfWeek,
                    fontFamily = BigShouldersDisplay,
                    fontSize = 20.sp,
                    color = Color(0xFF121E30)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Type something...",
                fontFamily = Poppins,
                fontSize = 14.sp,
                color = Color(0xFF121E30).copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun JournalEntryPager(
    entries: List<JournalEntry>,
    onEntryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0x80EDF5EF)
) {
    if (entries.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { entries.size })

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 148.dp)  // same height as single cards
            .background(backgroundColor)
            .border(0.5.dp, Color(0x801A3720))
    ) {
        Column {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val entry = entries[page]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEntryClick(entry.documentId) }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = entry.date,
                            fontFamily = BigShouldersDisplay,
                            fontWeight = FontWeight.Normal,
                            fontSize = 24.sp,
                            color = Color(0xFF121E30)
                        )
                        Text(
                            text = entry.dayOfWeek,
                            fontFamily = BigShouldersDisplay,
                            fontSize = 20.sp,
                            color = Color(0xFF121E30)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = entry.body,
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Color(0xFF121E30),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (entries.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        entries.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = if (index == pagerState.currentPage)
                                            Color(0xFF1A3720).copy(alpha = 0.3f)
                                        else
                                            Color(0xFF1A3720).copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // bottom right numbers
                    Text(
                        text = "${pagerState.currentPage + 1}/${entries.size}",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = Color(0xFF1A3720)
                    )
                }
            }
        }
    }
}
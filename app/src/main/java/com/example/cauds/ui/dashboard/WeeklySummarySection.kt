package com.example.cauds.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * WeekSummarySection — the green card in the dashboard's bottom-right slot.
 *
 * @param weekDrinkCount  Total drinks logged in the last 7 days
 * @param weekTotalSpent  Total spend in the last 7 days
 * @param onClick         Navigates to the DaySummary screen for today
 * @param modifier        Passed from parent for sizing (e.g., Modifier.weight())
 */
@Composable
fun WeekSummarySection(
    weekDrinkCount: Int,
    weekTotalSpent: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF2E7D32))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // "This week" header
        Text(
            text = "This week",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Big drink count — the main visual element.
        // Using a large font size so it dominates the card, matching the mockup.
        Text(
            text = weekDrinkCount.toString(),
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20),  // Darker green for contrast against the bg
            textAlign = TextAlign.Center
        )

        // "DRINKS" label beneath the number
        Text(
            text = "DRINKS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // "$X Spent" chip at the bottom — pill shape with a subtle border
        Surface(
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
            color = Color.Transparent
        ) {
            Text(
                text = String.format("$%.0f Spent", weekTotalSpent),
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}
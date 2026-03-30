package com.example.cauds.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.cauds.ui.theme.BowlbyOne
import com.example.cauds.ui.theme.CloverNormal
import com.example.cauds.ui.theme.Poppins
import com.example.cauds.ui.theme.rdp
import com.example.cauds.ui.theme.rsp

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
            .background(CloverNormal)
            .clickable { onClick() }
            .padding(12.rdp()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // "This week" header
        Text(
            text = "This week",
            fontSize = 12.rsp(),
            fontFamily = BowlbyOne,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF121E30),
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth(),
            style = androidx.compose.ui.text.TextStyle(
                platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // Big drink count
            Text(
                text = weekDrinkCount.toString(),
                fontSize = 96.rsp(),
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                color = Color(0xFF121E30),
                textAlign = TextAlign.Center,
                lineHeight = 75.rsp(),
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

            // "DRINKS" label
            // Reduced leading/internal padding and added negative offset to hug the number
            Text(
                text = "DRINKS",
                fontSize = 12.rsp(),
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                color = Color(0xFF121E30),
                letterSpacing = 0.rsp(),
                modifier = Modifier.offset(y = (-24).rdp()),
                style = androidx.compose.ui.text.TextStyle(
                    platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
        }

        // "$X Spent" chip at the bottom
        Surface(
            shape = RoundedCornerShape(100.dp),
            border = BorderStroke(0.5.rdp(), Color(0xFF1A3720).copy(alpha = 0.6f)),
            color = Color.Transparent
        ) {
            Text(
                text = String.format("$%.0f Spent", weekTotalSpent),
                fontSize = 12.rsp(),
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF121E30),
                modifier = Modifier.padding(horizontal = 12.rdp(), vertical = 4.rdp()),
                style = androidx.compose.ui.text.TextStyle(
                    platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
        }
    }
}

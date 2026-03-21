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
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val drinkCountFontSize = if (screenHeight < 700.dp) {
        64.rsp()
    } else if (screenHeight < 800.dp) {
        72.rsp()
    } else {
        96.rsp()
    }
    Column(
        modifier = modifier
            .background(Color(0xFF4A9D5B))
            .border(0.5.rdp(), Color.Black.copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(12.rdp()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // "This week" header
        Text(
            text = "This week",
            fontSize = 12.rsp(),
            fontFamily = BowlbyOne,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF121E30),
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(0.rdp()))

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Big drink count — the main visual element.
                // Using a large font size so it dominates the card, matching the mockup.
                Text(
                    text = weekDrinkCount.toString(),
                    fontSize = drinkCountFontSize,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins,
                    color = Color(0xFF121E30),
                    textAlign = TextAlign.Center,
                    lineHeight = 75.rsp()
                )

                // "DRINKS" label beneath the number
                Text(
                    text = "DRINKS",
                    fontSize = 12.rsp(),
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins,
                    color = Color(0xFF121E30),
                    letterSpacing = 0.rsp(),
                    modifier = Modifier.offset(y =
                        if (screenHeight < 700.dp) {
                            -8.rdp()
                        } else if (screenHeight < 800.dp) {
                            (-16).rdp()
                        } else {
                            (-28).rdp()
                        }
                    )
                )
                
            }
        }
        Spacer(modifier = Modifier.height(0.rdp()))

        // "$X Spent" chip at the bottom — pill shape with a subtle border
        Surface(
            shape = RoundedCornerShape(100),
            border = BorderStroke(0.5.rdp(), Color(0xFF1B3720).copy(alpha = 0.5f)),
            color = Color.Transparent
        ) {
            Text(
                text = String.format("$%.0f Spent", weekTotalSpent),
                fontSize = 12.rsp(),
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF121E30),
                modifier = Modifier.padding(horizontal = 12.rdp(), vertical = 4.rdp())
            )
        }
    }
}
package com.example.cauds.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.Poppins
import com.example.cauds.ui.theme.rdp
import com.example.cauds.R
import com.example.cauds.ui.theme.rsp
import androidx.compose.ui.res.painterResource
import com.example.cauds.ui.theme.CloverDarker
import com.example.cauds.ui.theme.CloverNormal
import com.example.cauds.ui.theme.Tertiary

/**
 * DrinkLogSection — the "Log your intake" card shown on the dashboard.
 *
 * LAYOUT (matches the design mockup):
 *   - A "+" symbol at the top
 *   - "Log your intake" text (tappable — navigates to DrinkLogScreen)
 *   - Below that, one of three states:
 *       • "I didn't drink" chip WITHOUT green dot (nothing logged, not yet tapped)
 *       • "I didn't drink" chip WITH green dot (user tapped it, sober day logged)
 *       • Two chips: "X Drink(s)" and "$Y Spent" (actual drinks logged)
 *
 * @param todayDrinkCount   Number of actual drinks logged today (excludes ACTION_LOG markers)
 * @param todayTotalSpent   Total dollar amount spent today
 * @param didntDrinkLogged  Whether the user has tapped "I didn't drink" today
 * @param onLogClick        Lambda to navigate to DrinkLogScreen ("+"/title tap)
 * @param onDidntDrinkClick Lambda to save the ACTION_LOG marker ("I didn't drink" tap)
 */
@Composable
fun DrinkLogSection(
    streakCount: Int,
    todayDrinkCount: Int,
    todayTotalSpent: Double,
    didntDrinkToggled: Boolean,
    onLogClick: () -> Unit,
    onDidntDrinkToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.rdp(), bottom = 24.rdp()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // "+" icon and "Log your intake" — these navigate to the drink log screen.
        // We wrap just these two in a clickable column instead of the whole section,
        // because the "I didn't drink" chip below has its OWN click action.
        Column(
            modifier = Modifier.clickable { onLogClick() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_plus),
                contentDescription = "Log Intake",
                tint = Color(0xFF1A3720),
                modifier = Modifier.size(32.rdp())
            )

            Spacer(modifier = Modifier.height(16.rdp()))

            Text(
                text = "Log your intake",
                fontSize = 32.rsp(),
                fontFamily = BigShouldersDisplay,
                color = Color(0xFF1A3720),
                textAlign = TextAlign.Center,
                style = androidx.compose.ui.text.TextStyle(
                    platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
        }

        Spacer(modifier = Modifier.height(16.rdp()))

        if (todayDrinkCount > 0) {
            // Drinks logged → show count + spend chips
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val drinkLabel = if (todayDrinkCount == 1) "1 Drink" else "$todayDrinkCount Drinks"
                DashboardChip(text = drinkLabel, showDot = true, dotColor = Color(0xFF6EA8FE))
                Spacer(modifier = Modifier.width(4.rdp()))
                DashboardChip(text = String.format("$%.0f Spent", todayTotalSpent), showDot = true, dotColor = Color(0xFF6EA8FE))
            }
        } else {
            // No drinks logged — show "I didn't drink" chip.
            // Always clickable: tapping toggles the green dot on/off.
            DashboardChip(
                text = "I didn't drink",
                showDot = didntDrinkToggled,
                dotColor = CloverNormal,
                onClick = onDidntDrinkToggle
            )
        }

        if (streakCount > 1) {
            Spacer(modifier = Modifier.height(18.rdp()))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$streakCount days streak",
                    fontSize = 12.rsp(),
                    color = CloverDarker,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.width(12.rdp()))
                Icon(
                    painter = painterResource(id = R.drawable.ic_streak),
                    contentDescription = "Streak",
                    tint = CloverDarker,
                    modifier = Modifier.size(16.rdp())
                )
            }
        }
    }
}


/**
 * DashboardChip — a small pill-shaped chip with optional green dot and optional click.
 *
 * @param text     The label to display (e.g., "I didn't drink", "1 Drink", "$5 Spent")
 * @param showDot  Whether to show the green dot indicator on the left.
 *                 When false, the dot is hidden — the chip looks "unconfirmed."
 * @param onClick  Optional click handler. When null, the chip isn't clickable.
 *                 This lets us make the "I didn't drink" chip tappable only
 *                 before the user has logged it, and inert afterward.
 */
@Composable
fun DashboardChip(
    text: String, 
    showDot: Boolean = true, 
    dotColor: Color = Color(0xFF4A9D5B),
    onClick: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        border = BorderStroke(0.5.rdp(), CloverDarker.copy(alpha = 0.6f)),
        color = Tertiary,
        modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.rdp(), vertical = 4.rdp()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showDot) {
                // Green dot — only visible when this chip represents a "confirmed" state
                Box(
                    modifier = Modifier
                        .size(6.rdp())
                        .background(dotColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.rdp()))
            }
            Text(
                text = text,
                fontSize = 14.rsp(),
                fontFamily = Poppins,
                color = Color(0xFF1A3720),
                fontWeight = FontWeight.Normal,
                style = androidx.compose.ui.text.TextStyle(
                    platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
        }
    }
}
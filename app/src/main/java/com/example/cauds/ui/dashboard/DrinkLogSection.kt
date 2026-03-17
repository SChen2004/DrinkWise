package com.example.cauds.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
 * @param todayDrinkCount   Number of actual drinks logged today (excludes SOBER markers)
 * @param todayTotalSpent   Total dollar amount spent today
 * @param didntDrinkLogged  Whether the user has tapped "I didn't drink" today
 * @param onLogClick        Lambda to navigate to DrinkLogScreen ("+"/title tap)
 * @param onDidntDrinkClick Lambda to save the SOBER marker ("I didn't drink" tap)
 */
@Composable
fun DrinkLogSection(
    todayDrinkCount: Int,
    todayTotalSpent: Double,
    didntDrinkToggled: Boolean,
    onLogClick: () -> Unit,
    onDidntDrinkToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // "+" icon and "Log your intake" — these navigate to the drink log screen.
        // We wrap just these two in a clickable column instead of the whole section,
        // because the "I didn't drink" chip below has its OWN click action.
        Column(
            modifier = Modifier.clickable { onLogClick() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "+",
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Log your intake",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (todayDrinkCount > 0) {
            // Drinks logged → show count + spend chips (always have green dots)
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val drinkLabel = if (todayDrinkCount == 1) "1 Drink" else "$todayDrinkCount Drinks"
                DashboardChip(text = drinkLabel, showDot = true)
                Spacer(modifier = Modifier.width(8.dp))
                DashboardChip(text = String.format("$%.0f Spent", todayTotalSpent), showDot = true)
            }
        } else {
            // No drinks logged — show "I didn't drink" chip.
            // Always clickable: tapping toggles the green dot on/off.
            DashboardChip(
                text = "I didn't drink",
                showDot = didntDrinkToggled,
                onClick = onDidntDrinkToggle
            )
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
fun DashboardChip(text: String, showDot: Boolean = true, onClick: (() -> Unit)? = null) {
    Surface(
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        color = Color.White,
        // Only attach a click modifier if onClick is provided.
        // Modifier.then() lets us conditionally chain modifiers —
        // if onClick is null, we add nothing extra.
        modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showDot) {
                // Green dot — only visible when this chip represents a "confirmed" state
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFF2E7D32), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                color = Color.Black
            )
        }
    }
}
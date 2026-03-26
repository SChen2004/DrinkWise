package com.example.cauds.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Drink count dots ──────────────────────────────────────────
// Color rules:
//   1 drink  → #5498FF
//   2 drinks → #324CFF
//   3-4      → #324CFF (placeholder, may change)
//   5+       → #4100BA (placeholder for future gradient)
// Max 5 dots shown. If more than 5, show 5 dots + a small "+"

fun dotColor(count: Int): Color {
    return when {
        count >= 5 -> Color(0xFF4100BA)
        count >= 3 -> Color(0xFF324CFF)
        count == 2 -> Color(0xFF324CFF)
        else       -> Color(0xFF5498FF)
    }
}

@Composable
fun DrinkDots(count: Int) {
    val displayCount = count.coerceAtMost(5)
    val showPlus = count > 5
    val color = dotColor(count)

    Row(
        horizontalArrangement = Arrangement.spacedBy(1.5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(displayCount) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(color, RectangleShape)
            )
        }

        if (showPlus) {
            Text(
                text = "+",
                fontSize = 8.sp,
                color = color,
                lineHeight = 6.sp
            )
        }
    }
}
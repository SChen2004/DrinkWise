package com.example.cauds.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

// ── Drink count dots ──────────────────────────────────────────
// Color rules:
//   1 drink  → #5498FF
//   2 drinks → #324CFF
//   3-4      → #324CFF (placeholder, may change)
//   5+       → #4100BA (placeholder for future gradient)
// Max 5 dots shown. If more than 5, show 5 dots + a small "+"

private val Purple = Color(0xFF4100BA)

fun dotColor(count: Int, index: Int, displayCount: Int): Color {
    return when {
        count >= 4 -> {
            val fromEnd = displayCount - 1 - index
            when (fromEnd) {
                0 -> Purple.copy(alpha = 0.2f)
                1 -> Purple.copy(alpha = 0.5f)
                else -> Purple
            }
        }
        count == 3 -> Color(0xFF324CFF)
        count == 2 -> Color(0xFF324CFF)
        else -> Color(0xFF5498FF)
    }
}

@Composable
fun DrinkDots(count: Int) {
    val displayCount = count.coerceAtMost(5)
    val showPlus = count > 5

    Row(
        horizontalArrangement = Arrangement.spacedBy(1.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = if (showPlus) Modifier.offset(x = (0).dp) else Modifier
    )  {
        repeat(displayCount) { index ->
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(dotColor(count, index, displayCount), RectangleShape)
            )
        }

        if (showPlus) {
            androidx.compose.foundation.Canvas(
                modifier = Modifier.size(4.25.dp)
            ) {
                val stroke = 0.9.dp.toPx()
                val center = size.width / 2
                drawLine(
                    color = Color(0xFF121E30),
                    start = androidx.compose.ui.geometry.Offset(center, 0f),
                    end = androidx.compose.ui.geometry.Offset(center, size.height),
                    strokeWidth = stroke
                )
                drawLine(
                    color = Color(0xFF121E30),
                    start = androidx.compose.ui.geometry.Offset(0f, center),
                    end = androidx.compose.ui.geometry.Offset(size.width, center),
                    strokeWidth = stroke
                )
            }
        }
    }
}
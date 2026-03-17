package com.example.cauds.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * DailyMessageSection — placeholder for a daily motivational quote.
 * Hardcoded for now. Will be replaced with dynamic content later.
 */
@Composable
fun DailyMessageSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "\u201CToday is a good day for a good day.\u201D",
            fontSize = 18.sp,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}
package com.example.cauds.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cauds.ui.theme.rdp
import com.example.cauds.ui.theme.rsp
import com.example.cauds.ui.theme.BigShouldersDisplay

/**
 * DailyMessageSection — placeholder for a daily motivational quote.
 * Hardcoded for now. Will be replaced with dynamic content later.
 */
@Composable
fun DailyMessageSection() {
    Text(
        text = "“Today is a good day for a good day.”",
        fontSize = 24.rsp(),
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

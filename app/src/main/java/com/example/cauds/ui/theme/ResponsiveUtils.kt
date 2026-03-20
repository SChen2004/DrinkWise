package com.example.cauds.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Base on 412dp figma design
 * Scale from 0.85 to 1.15
 */
@Composable
fun getScaleRatio(): Float {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    return (screenWidth / 412f).coerceIn(0.85f, 1.15f)
}

@Composable
fun Int.rdp(): Dp = (this * getScaleRatio()).dp

@Composable
fun Double.rdp(): Dp = (this * getScaleRatio()).dp

@Composable
fun Int.rsp(): TextUnit = (this * getScaleRatio()).sp

@Composable
fun Double.rsp(): TextUnit = (this * getScaleRatio()).sp

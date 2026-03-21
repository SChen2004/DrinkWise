package com.example.cauds.ui.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.cauds.ui.theme.Poppins

fun buildStyledDescription(text: String): AnnotatedString {
    return buildAnnotatedString {
        val parts = text.split("**")
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                withStyle(SpanStyle(fontFamily = Poppins, fontWeight = FontWeight.Black)) {
                    append(part)
                }
            } else {
                append(part)
            }
        }
    }
}
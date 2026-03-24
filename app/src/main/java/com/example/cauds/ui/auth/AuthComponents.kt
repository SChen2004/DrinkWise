package com.example.cauds.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cauds.R
import com.example.cauds.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.rdp())) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = Poppins,
                    fontSize = 16.rsp(),
                    color = Color.Black.copy(alpha = 0.3f),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            singleLine = true,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPassword) {
                    val iconRes = if (passwordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = "Toggle password visibility",
                            modifier = Modifier.size(20.rdp())
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = if (error.isNotEmpty()) ErrorRed else CloverDarker,
                unfocusedIndicatorColor = if (error.isNotEmpty()) ErrorRed else CloverDarker.copy(alpha = 0.3f),
                cursorColor = CloverDarker,
                errorIndicatorColor = ErrorRed
            ),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = Poppins,
                fontSize = 16.rsp(),
                color = CloverDarker,
                textAlign = TextAlign.Start
            )
        )

        if (error.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.rdp())
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_alert),
                    contentDescription = "Error",
                    tint = ErrorRed,
                    modifier = Modifier.size(12.rdp())
                )
                Text(
                    text = error,
                    fontFamily = Poppins,
                    fontSize = 12.rsp(),
                    color = ErrorRed
                )
            }
        }
    }
}

@Composable
fun OAuthButton(
    iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(44.rdp()),
        color = Color(0xFF3583A4).copy(alpha = 0.1f),
        shape = RoundedCornerShape(0.rdp())
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null
            )
        }
    }
}

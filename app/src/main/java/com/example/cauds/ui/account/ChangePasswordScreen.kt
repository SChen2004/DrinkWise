package com.example.cauds.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavController
import com.example.cauds.R
import com.example.cauds.ui.theme.BackgroundSand
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.CloverDarker
import com.example.cauds.ui.theme.Poppins
import com.example.cauds.ui.theme.rdp
import com.example.cauds.ui.theme.rsp
import com.example.cauds.ui.theme.SkyDark

/**
 * ChangePasswordScreen - High-fidelity implementation based on Figma Node 2355:21274.
 * Features absolute vertical centering and precise field spacing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = viewModel()
) {
    Scaffold(
        containerColor = BackgroundSand,
        // Bottom bar containing the Save action button
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundSand),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalDivider(thickness = 0.5.rdp(), color = CloverDarker.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(16.rdp()))

                val isFormComplete = viewModel.currentPassword.isNotEmpty() && 
                                   viewModel.newPassword.isNotEmpty() && 
                                   viewModel.confirmPassword.isNotEmpty()

                Button(
                    onClick = {
                        viewModel.updatePassword {
                            // On successful update, return to the previous screen
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.rdp())
                        .height(44.rdp()),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormComplete) CobaltDarker else SkyDark
                    ),
                    enabled = true,
                    contentPadding = PaddingValues(0.rdp())
                ) {
                    if (viewModel.isLoading) {
                        // Show a loading spinner during the network request
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.rdp()),
                            color = Color.White,
                            strokeWidth = 2.rdp()
                        )
                    } else {
                        Text(
                            text = "Save",
                            fontFamily = BigShouldersDisplay,
                            fontSize = 24.rsp(),
                            letterSpacing = 0.rsp(),
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.rdp()))
            }
        }
    ) { innerPadding ->
        // Main content area: Using a Box to absolute-center the inputs across the screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .padding(horizontal = 16.rdp())
        ) {
            // 1. Top Area (Back Arrow & Title)
            Column(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(top = 26.rdp())
                            .size(20.rdp())
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_left),
                            contentDescription = "Back",
                            tint = CloverDarker,
                            modifier = Modifier.size(20.rdp())
                        )
                    }
                }

                Text(
                    text = "Change Password",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 32.rsp(),
                    fontFamily = BigShouldersDisplay,
                    color = CloverDarker,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.rdp()), 
                    textAlign = TextAlign.Center
                )
            }

            // 2. Main Input Group - Perfectly centered relative to the whole content area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 12.rdp()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(64.rdp())
            ) {
                // block 1: Old Password
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    if (viewModel.currentPassword.isNotEmpty()) {
                        Text(
                            text = "Old Password",
                            fontFamily = Poppins,
                            fontSize = 12.rsp(),
                            color = Color.Black,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(bottom = 8.rdp()) // Shoves input down
                        )
                    }
                    
                    // Custom Input for Old Password (12dp bottom padding, left-aligned)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.rdp(), bottom = 12.rdp())
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                                if (viewModel.currentPassword.isEmpty()) {
                                    Text(
                                        text = "Old Password",
                                        fontFamily = Poppins,
                                        fontSize = 16.rsp(),
                                        color = CloverDarker.copy(alpha = 0.3f),
                                        textAlign = TextAlign.Start
                                    )
                                }
                                androidx.compose.foundation.text.BasicTextField(
                                    value = viewModel.currentPassword,
                                    onValueChange = {
                                        viewModel.currentPassword = it
                                        viewModel.clearMessage()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    visualTransformation = if (viewModel.isCurrentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        textAlign = TextAlign.Start,
                                        fontFamily = Poppins,
                                        color = CloverDarker,
                                        fontSize = 16.rsp()
                                    ),
                                    singleLine = true,
                                    cursorBrush = androidx.compose.ui.graphics.SolidColor(CloverDarker)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.isCurrentPasswordVisible = !viewModel.isCurrentPasswordVisible },
                                modifier = Modifier.size(24.rdp())
                            ) {
                                Icon(
                                    painter = painterResource(id = if (viewModel.isCurrentPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.rdp()),
                                    tint = CloverDarker
                                )
                            }
                        }
                    }
                    HorizontalDivider(
                        thickness = 0.5.rdp(),
                        color = CloverDarker.copy(alpha = 0.4f)
                    )
                }

                // block 2: New Passwords Group
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.rdp())
                ) {
                    // New Password field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (viewModel.newPassword.isNotEmpty()) {
                            Text(
                                text = "New Password",
                                fontFamily = Poppins,
                                fontSize = 12.rsp(),
                                color = Color.Black,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(bottom = 8.rdp())
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 0.rdp(), bottom = 12.rdp())
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                                    if (viewModel.newPassword.isEmpty()) {
                                        Text(
                                            text = "New Password",
                                            fontFamily = Poppins,
                                            fontSize = 16.rsp(),
                                            color = CloverDarker.copy(alpha = 0.3f),
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                    androidx.compose.foundation.text.BasicTextField(
                                        value = viewModel.newPassword,
                                        onValueChange = {
                                            viewModel.newPassword = it
                                            viewModel.clearMessage()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        visualTransformation = if (viewModel.isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                                            textAlign = TextAlign.Start,
                                            fontFamily = Poppins,
                                            color = CloverDarker,
                                            fontSize = 16.rsp()
                                        ),
                                        singleLine = true,
                                        cursorBrush = androidx.compose.ui.graphics.SolidColor(CloverDarker)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.isNewPasswordVisible = !viewModel.isNewPasswordVisible },
                                    modifier = Modifier.size(24.rdp())
                                ) {
                                    Icon(
                                        painter = painterResource(id = if (viewModel.isNewPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.rdp()),
                                        tint = CloverDarker
                                    )
                                }
                            }
                        }
                        HorizontalDivider(
                            thickness = 0.5.rdp(),
                            color = CloverDarker.copy(alpha = 0.4f)
                        )
                    }

                    // Confirm Password field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (viewModel.confirmPassword.isNotEmpty()) {
                            Text(
                                text = "Confirm New Password",
                                fontFamily = Poppins,
                                fontSize = 12.rsp(),
                                color = Color.Black,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(bottom = 8.rdp())
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 0.rdp(), bottom = 12.rdp())
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                                    if (viewModel.confirmPassword.isEmpty()) {
                                        Text(
                                            text = "Confirm New Password",
                                            fontFamily = Poppins,
                                            fontSize = 16.rsp(),
                                            color = CloverDarker.copy(alpha = 0.3f),
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                    androidx.compose.foundation.text.BasicTextField(
                                        value = viewModel.confirmPassword,
                                        onValueChange = {
                                            viewModel.confirmPassword = it
                                            viewModel.clearMessage()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        visualTransformation = if (viewModel.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                                            textAlign = TextAlign.Start,
                                            fontFamily = Poppins,
                                            color = CloverDarker,
                                            fontSize = 16.rsp()
                                        ),
                                        singleLine = true,
                                        cursorBrush = androidx.compose.ui.graphics.SolidColor(CloverDarker)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.isConfirmPasswordVisible = !viewModel.isConfirmPasswordVisible },
                                    modifier = Modifier.size(24.rdp())
                                ) {
                                    Icon(
                                        painter = painterResource(id = if (viewModel.isConfirmPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.rdp()),
                                        tint = CloverDarker
                                    )
                                }
                            }
                        }
                        HorizontalDivider(
                            thickness = 0.5.rdp(),
                            color = if (viewModel.statusMessage != null && !viewModel.isSuccess) Color(0xFFEA4335) else CloverDarker.copy(alpha = 0.4f)
                        )

                        // Error message area - Hides on success
                        val displayMessage = viewModel.statusMessage
                        if (displayMessage != null && !viewModel.isSuccess) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.rdp()),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_alert),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.rdp()).padding(top = 1.rdp()),
                                    tint = Color.Unspecified
                                )
                                Spacer(modifier = Modifier.width(6.rdp()))
                                Text(
                                    text = displayMessage,
                                    color = Color(0xFFEA4335),
                                    fontFamily = Poppins,
                                    fontSize = 12.rsp(),
                                    lineHeight = 18.rsp(),
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

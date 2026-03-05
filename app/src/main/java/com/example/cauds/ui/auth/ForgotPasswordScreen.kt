package com.example.cauds.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar with Back Button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Gray
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // App Icon Placeholder
            Image(
                painter = painterResource(id = com.example.cauds.R.drawable.ic_launcher_foreground),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // A Column that wraps the core content, keeps title, input, and button aligned
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Forgot Password",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                // Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        emailError = "" // Clear error on typing
                    },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = emailError.isNotEmpty(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray,
                        errorBorderColor = Color.Red,
                        errorLabelColor = Color.Red
                    )
                )

                // Email error message under field
                if (emailError.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = emailError,
                            color = Color.Red,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subtitle Instruction
                Text(
                    text = "We'll send a password reset link to this email if it matches with an existing account.",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // SEND Button & Validation
                // Validates that the email is not empty and is properly formatted.
                // If valid, triggers `sendPasswordResetEmail`. On success, pops a dialog
                // telling the user to check their email, avoiding the creation of an insecure 
                // in-app numeric OTP verification screen.
                Button(
                    onClick = {
                        emailError = ""
                        
                        if (email.isBlank()) {
                            emailError = "Please enter an email address"
                            return@Button
                        } else if (!viewModel.isValidEmail(email)) {
                            emailError = "Please enter a valid email address"
                            return@Button
                        }

                        isLoading = true
                        viewModel.sendPasswordResetEmail(
                            email = email,
                            onSuccess = {
                                isLoading = false
                                showSuccessDialog = true
                            },
                            onError = { errorMsg ->
                                isLoading = false
                                emailError = errorMsg
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("SEND", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }

    // Success Dialog & Navigation
    // we show a confirmation native dialog and then bounce the user
    // back to the Login Screen where they can try entering their new password later.
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                navController.popBackStack(Screen.Login.route, inclusive = false)
            },
            title = { Text("Link Sent") },
            text = { Text("A password reset link has been sent to your email. Please check your inbox (and spam folder) to reset your password.") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showSuccessDialog = false
                        navController.popBackStack(Screen.Login.route, inclusive = false)
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

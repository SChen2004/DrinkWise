package com.example.cauds.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cauds.R
import com.example.cauds.ui.navigation.Screen
import com.example.cauds.ui.theme.BackgroundSand
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.CobaltDarker
import com.example.cauds.ui.theme.Poppins
import com.example.cauds.ui.theme.rdp
import com.example.cauds.ui.theme.rsp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isShortScreen = configuration.screenHeightDp < 750

    val topPadding = if (isShortScreen) 64.rdp() else 116.rdp()
    val logoSize = if (isShortScreen) 80.rdp() else 90.rdp()
    val logoToTitleGap = if (isShortScreen) 28.rdp() else 48.rdp()
    val titleToFieldsGap = if (isShortScreen) 28.rdp() else 48.rdp()

    val canSend = email.isNotBlank() && viewModel.isValidEmail(email)
    val buttonColor = if (canSend) CobaltDarker else CobaltDarker.copy(alpha = 0.5f)

    Scaffold(
        containerColor = BackgroundSand,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.rdp()),
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.rdp()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(topPadding))

                // Circular Logo
                Surface(
                    modifier = Modifier.size(logoSize),
                    shape = RoundedCornerShape(1000.dp),
                    color = Color(0xFF4A9D5B)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo_for_sign_in),
                            contentDescription = "App Logo"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(logoToTitleGap))

                // Title
                Text(
                    text = "Forgot Password",
                    style = TextStyle(
                        fontFamily = BigShouldersDisplay,
                        fontWeight = FontWeight.Medium,
                        fontSize = 32.rsp(),
                        color = Color(0xFF1A3720),
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    )
                )

                Spacer(modifier = Modifier.height(titleToFieldsGap))

                // Email Field
                AuthTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = ""
                    },
                    placeholder = "Email",
                    error = emailError,
                    isPassword = false
                )

                Spacer(modifier = Modifier.height(16.rdp()))

                // Wording preserved
                Text(
                    text = "We'll send a password reset link to this email if it matches with an existing account.",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.rsp(),
                        color = Color(0xFF8397A5),
                        lineHeight = 16.rsp(),
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.rdp()))
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // SEND Button
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
                        .height(44.rdp()),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.rdp()), color = Color.White, strokeWidth = 2.rdp())
                    } else {
                        Text(
                            text = "Send",
                            style = TextStyle(
                                fontFamily = BigShouldersDisplay,
                                fontWeight = FontWeight.Normal,
                                fontSize = 24.rsp(),
                                color = Color.White,
                                platformStyle = PlatformTextStyle(includeFontPadding = false)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(64.rdp()))
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                navController.popBackStack(Screen.Login.route, inclusive = false)
            },
            title = {
                Text(
                    "Link Sent",
                    fontFamily = BigShouldersDisplay,
                    fontSize = 24.rsp()
                )
            },
            text = {
                Text(
                    "A password reset link has been sent to your email. Please check your inbox (and spam folder) to reset your password.",
                    fontFamily = Poppins,
                    fontSize = 14.rsp()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        navController.popBackStack(Screen.Login.route, inclusive = false)
                    }
                ) {
                    Text("OK", fontFamily = Poppins, fontWeight = FontWeight.Bold, color = Color(0xFF111D2D))
                }
            },
            shape = RoundedCornerShape(8.dp),
            containerColor = BackgroundSand
        )
    }
}

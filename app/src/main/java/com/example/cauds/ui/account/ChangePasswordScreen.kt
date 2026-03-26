package com.example.cauds.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavController
import com.example.cauds.R
import com.example.cauds.ui.navigation.Screen
import com.example.cauds.ui.theme.BackgroundSand
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.CloverDarker
import com.example.cauds.ui.theme.CobaltDarker
import com.example.cauds.ui.theme.Poppins
import com.example.cauds.ui.theme.rdp
import com.example.cauds.ui.theme.rsp
import com.example.cauds.ui.theme.SkyDark


/**
 * ChangePasswordScreen - Allows users to update their account password.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = viewModel()
) {
    Scaffold(
        containerColor = BackgroundSand,
        // TopBar with a back button to return to the Account screen
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CloverDarker,
                            modifier = Modifier.size(24.rdp())
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
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

                // Triggers the password update logic in the ViewModel
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
                        containerColor = SkyDark
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
        // Main content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.rdp())
        ) {
            // pt-[56px] as seen in Figma Body node
            Spacer(modifier = Modifier.height(56.rdp()))

            // Page Title
            Text(
                text = "Change Password",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 32.rsp(),
                fontFamily = BigShouldersDisplay,
                color = CloverDarker,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Large spacer to push the input group to roughly top-[317px]
            // We'll use a fixed height calculated from Figma: 317 - (56 + ~40 height of title)
            Spacer(modifier = Modifier.height((317 - 56 - 40).rdp()))

            // Input Column for Current, New and Confirm password fields
            // Adding Current Password field to allow re-authentication and solve the logic issue.
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(24.rdp())
            ) {
                // Field 0: Current Password (Required for Firebase security)
                Column {
                    TextField(
                        value = viewModel.currentPassword,
                        onValueChange = { 
                            viewModel.currentPassword = it 
                            viewModel.clearMessage() 
                        },
                        placeholder = { 
                            Text("Current Password", 
                                fontFamily = Poppins, 
                                fontSize = 16.rsp(),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start,
                                color = CloverDarker.copy(alpha = 0.3f)
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = CloverDarker
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Start,
                            fontFamily = Poppins,
                            color = CloverDarker
                        ),
                        singleLine = true
                    )
                    HorizontalDivider(thickness = 0.5.rdp(), color = CloverDarker.copy(alpha = 0.4f))
                }

                // Field 1: Input for the new password
                Column {
                    TextField(
                        value = viewModel.newPassword,
                        onValueChange = { 
                            viewModel.newPassword = it 
                            viewModel.clearMessage() 
                        },
                        placeholder = { 
                            Text("Password", 
                                fontFamily = Poppins, 
                                fontSize = 16.rsp(),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start,
                                color = CloverDarker.copy(alpha = 0.3f)
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = CloverDarker
                        ),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.isNewPasswordVisible = !viewModel.isNewPasswordVisible }) {
                                Icon(
                                    painter = painterResource(id = if (viewModel.isNewPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
                                    contentDescription = if (viewModel.isNewPasswordVisible) "Hide password" else "Show password",
                                    modifier = Modifier.size(20.rdp()),
                                    tint = CloverDarker
                                )
                            }
                        },
                        visualTransformation = if (viewModel.isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Start,
                            fontFamily = Poppins,
                            color = CloverDarker
                        ),
                        singleLine = true
                    )
                    HorizontalDivider(thickness = 0.5.rdp(), color = CloverDarker.copy(alpha = 0.4f))
                }

                // Field 2: Confirmation of the new password
                Column {
                    TextField(
                        value = viewModel.confirmPassword,
                        onValueChange = { 
                            viewModel.confirmPassword = it 
                            viewModel.clearMessage()
                        },
                        placeholder = { 
                            Text("Confirm New Password", 
                                fontFamily = Poppins, 
                                fontSize = 16.rsp(),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start,
                                color = CloverDarker.copy(alpha = 0.3f)
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = CloverDarker
                        ),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.isConfirmPasswordVisible = !viewModel.isConfirmPasswordVisible }) {
                                Icon(
                                    painter = painterResource(id = if (viewModel.isConfirmPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
                                    contentDescription = if (viewModel.isConfirmPasswordVisible) "Hide password" else "Show password",
                                    modifier = Modifier.size(20.rdp()),
                                    tint = CloverDarker
                                )
                            }
                        },
                        visualTransformation = if (viewModel.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Start,
                            fontFamily = Poppins,
                            color = CloverDarker
                        ),
                        singleLine = true
                    )
                    HorizontalDivider(thickness = 0.5.rdp(), color = CloverDarker.copy(alpha = 0.4f))

                    // Error message placed exactly as per Figma (below the second field)
                    val displayMessage = viewModel.statusMessage
                    if (displayMessage != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.rdp()),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_alert),
                                contentDescription = "Alert",
                                modifier = Modifier.size(16.rdp()).padding(top = 1.rdp()),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(6.rdp()))
                            Text(
                                text = displayMessage,
                                color = if (viewModel.isSuccess) Color.DarkGray else Color(0xFFEA4335),
                                fontFamily = Poppins,
                                fontSize = 12.rsp(),
                                textAlign = TextAlign.Start,
                                lineHeight = 18.rsp()
                            )
                        }
                    }
                }
            }
            // Bottom weight to maintain layout proportions
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

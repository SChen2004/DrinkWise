package com.example.cauds.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    // State
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    // Configure Google Sign In
    val gso = remember {
        com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.example.cauds.R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso) }

    // waits for the user to come back after they pick their Google account
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // If the user successfully picked an account
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // get the data back from the Google popup window
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                account.idToken?.let { idToken ->
                    isLoading = true
                    // send idToken to ViewModel to log into Firebase
                    viewModel.signInWithGoogle(idToken,
                        onSuccess = {
                            isLoading = false
                            navController.navigate(Screen.Dashboard.route) {
                                // clear the login page from history, no return after login
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onError = {
                            isLoading = false
                            errorMessage = it
                        }
                    )
                }
            } catch (e: com.google.android.gms.common.api.ApiException) {
                errorMessage = "Google Sign-In failed: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // email input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // password input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // error message
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // login button
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Please fill in all fields."
                    return@Button
                }

                isLoading = true
                errorMessage = ""

                // login with viewmodel
                viewModel.performLogin(
                    email = email,
                    pass = password,
                    onSuccess = {
                        isLoading = false
                        // login success go to dashboard
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onError = { msg ->
                        isLoading = false
                        errorMessage = msg
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("LOGIN")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google sign in button
        Button(
            onClick = {
                launcher.launch(googleSignInClient.signInIntent)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text("Login with Google")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Go to Sign Up
        TextButton(onClick = { navController.navigate(Screen.SignUp.route) }) {
            Text("Don't have an account? Sign Up")
        }
    }
}
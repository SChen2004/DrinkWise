package com.example.cauds.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // --- Google Auth Setup ---
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.example.cauds.R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { idToken ->
                    isLoading = true
                    viewModel.signInWithGoogle(idToken,
                        onSuccess = {
                            isLoading = false
                            navController.navigate(Screen.AudTest.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                        },
                        onError = { error ->
                            isLoading = false
                            if (error == "Email already exists") {
                                emailError = error
                                passwordError = ""
                            } else {
                                emailError = error
                                passwordError = ""
                            }
                        }
                    )
                }
            } catch (e: ApiException) {
                emailError = "Google Sign-In failed: ${e.message}"
            }
        }
    }

    // --- Facebook Auth Setup ---
    val callbackManager = remember { CallbackManager.Factory.create() }
    val loginManager = LoginManager.getInstance()
    
    DisposableEffect(Unit) {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                isLoading = true
                viewModel.signInWithFacebook(result.accessToken,
                    onSuccess = {
                        isLoading = false
                        navController.navigate(Screen.AudTest.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                    },
                    onError = {
                        isLoading = false
                        emailError = it
                    }
                )
            }
            override fun onCancel() {
                // Do nothing
            }
            override fun onError(error: FacebookException) {
                emailError = "Facebook Sign-In failed: ${error.message}"
            }
        })
        onDispose {
            loginManager.unregisterCallback(callbackManager)
        }
    }

    val facebookLauncher = rememberLauncherForActivityResult(
        loginManager.createLogInActivityResultContract(callbackManager, null)
    ) {
        // Result handled by CallbackManager
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // App Icon
        Image(
            painter = painterResource(id = com.example.cauds.R.drawable.ic_launcher_foreground),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(72.dp)
                .background(Color.Transparent)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Text(
            text = "Sign Up",
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

        // Email error message
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

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                passwordError = "" // Clear error on typing
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = passwordError.isNotEmpty(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red
            )
        )

        // Password error message mapped exactly like Figma
        if (passwordError.isNotEmpty()) {
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
                    text = passwordError,
                    color = Color.Red,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // SIGN UP Button
        Button(
            onClick = {
                var hasError = false
                emailError = ""
                passwordError = ""
                
                if (email.isBlank()) {
                    emailError = "Please enter an email address"
                    hasError = true
                } else if (!viewModel.isValidEmail(email)) {
                    emailError = "Please enter a valid email address"
                    hasError = true
                }

                if (password.isBlank()) {
                    passwordError = "Please enter a password"
                    hasError = true
                } else if (!viewModel.isValidPassword(password)) {
                    passwordError = "Must be at least 8 characters and contain 1 number, 1 uppercase, 1 lowercase, 1 special character"
                    hasError = true
                }

                if (hasError) return@Button

                isLoading = true

                viewModel.performSignUp(
                    email = email,
                    pass = password,
                    onSuccess = {
                        isLoading = false
                        navController.navigate(Screen.AudTest.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                    },
                    onError = { errorMsg ->
                        isLoading = false
                        if (errorMsg == "Email already exists") {
                            emailError = errorMsg
                        } else {
                            emailError = errorMsg
                        }
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
                Text("SIGN UP", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Divider with text
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text(
                text = "Or sign up with",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Other sign up options
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Google Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                    .clickable { googleLauncher.launch(googleSignInClient.signInIntent) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.example.cauds.R.drawable.ic_google),
                    contentDescription = "Sign up with Google",
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Facebook Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                    .clickable { facebookLauncher.launch(listOf("email", "public_profile")) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.example.cauds.R.drawable.ic_facebook),
                    contentDescription = "Sign up with Facebook",
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Login Link
        Row(
            modifier = Modifier.padding(bottom = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Already have an account? ", color = Color.Black, fontSize = 12.sp)
            Text(
                text = "Log In",
                color = Color(0xFF1976D2),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.clickable { navController.popBackStack() }
            )
        }
    }
}
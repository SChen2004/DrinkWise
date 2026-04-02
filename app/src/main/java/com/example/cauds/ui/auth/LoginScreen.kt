package com.example.cauds.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cauds.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cauds.R
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import com.example.cauds.ui.navigation.Screen
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
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
                            viewModel.checkOnboardingStatus { completed ->
                                isLoading = false
                                if (completed) {
                                    navController.navigate(Screen.Dashboard.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                                } else {
                                    navController.navigate(Screen.OnboardingName.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                                }
                            }
                        },
                        onError = { error ->
                            isLoading = false
                            if (error == "Email does not exist") {
                                emailError = error
                                passwordError = ""
                            } else if (error == "Invalid password") {
                                passwordError = error
                                emailError = ""
                            } else {
                                // Default broad error
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
                        viewModel.checkOnboardingStatus { completed ->
                            isLoading = false
                            if (completed) {
                                navController.navigate(Screen.Dashboard.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                            } else {
                                navController.navigate(Screen.OnboardingName.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                            }
                        }
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

    val isFormValid = email.isNotBlank() && password.isNotBlank()
    val configuration = LocalConfiguration.current
    val isShortScreen = configuration.screenHeightDp < 750

    val verticalPadding = if (isShortScreen) 32.rdp() else 64.rdp()
    val logoSize = if (isShortScreen) 80.rdp() else 90.rdp()
    val logoToTitleGap = if (isShortScreen) 28.rdp() else 48.rdp()
    val titleToFieldsGap = if (isShortScreen) 28.rdp() else 48.rdp()
    val fieldGap = if (isShortScreen) 16.rdp() else 24.rdp()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSand)
            .padding(horizontal = 16.rdp(), vertical = verticalPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Logo Section
        Box(
            modifier = Modifier
                .size(logoSize)
                .background(CloverNormal, shape = CircleShape),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_for_sign_in),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(logoSize)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(logoToTitleGap))

        // Form Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.rdp()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(titleToFieldsGap)
        ) {
            Text(
                text = "Sign In",
                fontFamily = BigShouldersDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 32.rsp(),
                color = CloverDarker,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
        
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(fieldGap)
            ) {
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

                // Password Field
                AuthTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        passwordError = "" 
                    },
                    placeholder = "Password",
                    error = passwordError,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible }
                )

                Text(
                    text = "Forgot Password?",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.rsp(),
                    color = CloverNormal,
                    modifier = Modifier.align(Alignment.Start).clickable { 
                        if (navController.currentDestination?.route == Screen.Login.route) {
                            navController.navigate(Screen.ForgotPassword.route) {
                                launchSingleTop = true
                            }
                        }
                    },
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
            }

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
                    }

                    if (hasError) return@Button

                    isLoading = true
                    viewModel.performLogin(
                        email = email,
                        pass = password,
                        onSuccess = {
                            viewModel.checkOnboardingStatus { completed ->
                                isLoading = false
                                if (completed) {
                                    navController.navigate(Screen.Dashboard.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                                } else {
                                    navController.navigate(Screen.OnboardingName.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                                }
                            }
                        },
                        onError = { errorMsg ->
                            isLoading = false
                            if (errorMsg == "Email does not exist") {
                                emailError = errorMsg
                            } else if (errorMsg == "Invalid password") {
                                passwordError = errorMsg
                            } else {
                                emailError = errorMsg
                            }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.rdp()),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) CobaltDarker else SkyDark
                ),
                shape = RoundedCornerShape(0.rdp()),
                contentPadding = PaddingValues(0.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.rdp()), color = Color.White)
                } else {
                    Text(
                        text = "Sign In",
                        fontFamily = BigShouldersDisplay,
                        fontSize = 24.rsp(),
                        color = Color.White,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // OAuth Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.rdp()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.rdp())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.rdp()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.rdp())
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = SkyDark, thickness = 0.5.dp)
                Text(
                    text = "Or sign in with",
                    fontFamily = Poppins,
                    fontSize = 10.rsp(),
                    color = SkyDark
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = SkyDark, thickness = 0.5.dp)
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OAuthButton(
                    iconResId = R.drawable.ic_google,
                    onClick = { googleLauncher.launch(googleSignInClient.signInIntent) }
                )
                Spacer(modifier = Modifier.width(12.rdp()))
                OAuthButton(
                    iconResId = R.drawable.ic_facebook,
                    onClick = { facebookLauncher.launch(listOf("email", "public_profile")) }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.rdp())
            ) {
                Text(
                    text = "Don’t have an account?",
                    fontFamily = Poppins,
                    fontSize = 12.rsp(),
                    color = CloverDarker
                )
                Text(
                    text = "Sign Up",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.rsp(),
                    color = CloverNormal,
                    modifier = Modifier.clickable { 
                        if (navController.currentDestination?.route == Screen.Login.route) {
                            navController.navigate(Screen.SignUp.route) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}


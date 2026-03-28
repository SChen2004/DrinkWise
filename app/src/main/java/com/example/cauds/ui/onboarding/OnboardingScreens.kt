package com.example.cauds.ui.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cauds.ui.navigation.Screen
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cauds.data.model.Sex
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.Poppins
import androidx.compose.foundation.text.BasicTextField
import com.example.cauds.ui.components.WheelPicker
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.platform.LocalConfiguration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingNameScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    var name by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    val nameRegex = Regex("^[\\p{L} '-]*$")
    val creamBackground = Color(0xFFFEF5DC)

    Scaffold(
        containerColor = creamBackground,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("CANCEL", color = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = "What should we call you?",
                    fontSize = 30.sp,
                    fontFamily = BigShouldersDisplay,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.weight(1f))

                val interactionSource = remember { MutableInteractionSource() }
                val isFocused by interactionSource.collectIsFocusedAsState()

                BasicTextField(
                    value = name,
                    onValueChange = { newValue ->
                        name = newValue
                        hasError = !nameRegex.matches(newValue)
                    },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontFamily = Poppins
                    ),
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .drawBehind {
                            val strokeWidth = 0.5.dp.toPx()
                            val y = size.height
                            drawLine(
                                color = if (isFocused) Color.Black else Color.Black.copy(alpha = 0.4f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokeWidth
                            )
                        },
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (name.isEmpty()) {
                                Text(
                                    text = "Name",
                                    fontFamily = Poppins,
                                    textAlign = TextAlign.Center,
                                    color = Color(0x4D000000),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (hasError) "Must not contain numbers or special characters" else "",
                    color = Color(0xFFEA4335),
                    fontFamily = Poppins,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = Color.Gray.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.updateName(name.trim())
                    navController.navigate(Screen.OnboardingSex.route)
                },
                enabled = name.isNotBlank() && !hasError,
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF121E30),
                    disabledContainerColor = Color(0x80121E30),
                    contentColor = Color.White,
                    disabledContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(44.dp)
            ) {
                Text(
                    text = "Next",
                    fontFamily = BigShouldersDisplay,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingSexScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    var selectedOption by remember { mutableStateOf<Sex?>(null) }
    val creamBackground = Color(0xFFFEF5DC)

    Scaffold(
        containerColor = creamBackground,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Gray
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
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = "What is your biological sex?",
                    fontSize = 30.sp,
                    fontFamily = BigShouldersDisplay,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.weight(1f))

                val wheelItems = listOf("— Select Sex —") + Sex.entries.map { it.displayName }

                WheelPicker(
                    items = wheelItems,
                    onItemSelected = { index ->
                        selectedOption = if (index == 0) null else Sex.entries[index - 1]
                    }
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = Color.Gray.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.updateBiologicalSex(selectedOption!!)
                    navController.navigate(Screen.OnboardingPurpose.route)
                },
                enabled = selectedOption != null,
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF121E30),
                    disabledContainerColor = Color(0x80121E30),
                    contentColor = Color.White,
                    disabledContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(44.dp)
            ) {
                Text(
                    text = "Next",
                    fontFamily = BigShouldersDisplay,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingPurposeScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    val options = listOf(
        "Cut back on drinking",
        "Understand my habits",
        "Just exploring",
        "Supporting someone else"
    )

    var selectedOption by remember { mutableStateOf<String?>(null) }
    val creamBackground = Color(0xFFFEF5DC)
    val buttonText = if (selectedOption == "Supporting someone else") "Continue" else "Start Assessment"

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val topSpacing = if (screenHeight < 700) 16.dp else 60.dp
    val optionFontSize = if (screenHeight < 700) 18.sp else 24.sp
    val optionVerticalPadding = if (screenHeight < 700) 10.dp else 14.dp
    val optionVerticalSpacing = if (screenHeight < 700) 6.dp else 8.dp
    val labelFontSize = if (screenHeight < 700) 16.sp else 20.sp
    val bottomSpacing = if (screenHeight < 700) 24.dp else 48.dp

    Scaffold(
        containerColor = creamBackground,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Gray
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
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(topSpacing))

                Text(
                    text = "What brings you here today?",
                    fontSize = 30.sp,
                    fontFamily = BigShouldersDisplay,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "I want to...",
                    fontSize = labelFontSize,
                    fontFamily = BigShouldersDisplay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                options.forEach { option ->
                    val isSelected = selectedOption == option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = optionVerticalSpacing)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.Black else Color(0x4D000000),
                                shape = RectangleShape
                            )
                            .clickable { selectedOption = option }
                            .padding(horizontal = 12.dp, vertical = optionVerticalPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .border(1.dp, if (isSelected) Color.Black else Color(0x4D000000), CircleShape)
                                .background(
                                    color = if (isSelected) Color(0x5D000000) else Color.Transparent,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option,
                            fontFamily = BigShouldersDisplay,
                            fontSize = optionFontSize,
                            color = Color(0xFF1A3720)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = Color.Gray.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedOption == "Supporting someone else") {
                        viewModel.updateSupportingFriend(true)
                        viewModel.saveOnboardingData()
                        viewModel.completeOnboarding()
                        navController.navigate(Screen.Dashboard.route)
                    } else {
                        viewModel.updateSupportingFriend(false)
                        viewModel.saveOnboardingData()
                        navController.navigate(Screen.QuizIntro.route)
                    }
                },
                enabled = selectedOption != null,
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF121E30),
                    disabledContainerColor = Color(0xFF8397A5),
                    contentColor = Color.White,
                    disabledContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(44.dp)
            ) {
                Text(
                    text = buttonText,
                    fontFamily = BigShouldersDisplay,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(bottomSpacing))
        }
    }
}
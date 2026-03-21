package com.example.cauds.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.cauds.ui.theme.BigShouldersDisplay
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import com.example.cauds.R
import com.example.cauds.data.model.AudRisk
import com.example.cauds.ui.theme.BowlbyOne
import com.example.cauds.ui.theme.Poppins
import com.example.cauds.ui.util.buildStyledDescription
import kotlinx.coroutines.delay


@Composable
fun QuizIntroScreen(navController: NavController) {

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val isSmall = screenHeight < 700

    val imageHeight = if (isSmall) 200.dp else 240.dp
    val imageTitleSpacing = if (isSmall) 32.dp else 50.dp
    val cardTopPadding = if (isSmall) 90.dp else 128.dp
    val titleFontSize = if (isSmall) 26.sp else 36.sp
    val yourFontSize = if (isSmall) 22.sp else 32.sp
    val cardTextFontSize = if (isSmall) 14.sp else 16.sp
    val buttonHeight = if (isSmall) 40.dp else 44.dp
    val buttonFontSize = if (isSmall) 20.sp else 24.sp
    val bottomSpacing = if (isSmall) 12.dp else 24.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4A9D5B))
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 32.dp, start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(id = R.drawable.drink_cluster),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                )

                Spacer(modifier = Modifier.height(imageTitleSpacing))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = cardTopPadding),
                        shape = RectangleShape,
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF5EF))
                    ) {
                        Text(
                            text = "We'll ask a few quick questions about your drinking to help personalize your experience.",
                            fontSize = cardTextFontSize,
                            fontFamily = Poppins,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Understanding",
                            fontFamily = BowlbyOne,
                            fontSize = titleFontSize,
                            color = Color(0xFF1A3720),
                            modifier = Modifier
                                .background(Color(0xFFAFC9DC))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your",
                                fontFamily = BigShouldersDisplay,
                                fontWeight = FontWeight.Light,
                                fontSize = yourFontSize,
                                color = Color(0xFF1A3720),
                                modifier = Modifier
                                    .graphicsLayer {
                                        rotationZ = -6f
                                        shadowElevation = 10f
                                        shape = RectangleShape
                                        clip = false
                                    }
                                    .background(Color(0xFFAFC9DC))
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "Habits",
                                fontFamily = BowlbyOne,
                                fontSize = titleFontSize,
                                color = Color(0xFF1A3720),
                                modifier = Modifier
                                    .background(Color(0xFFAFC9DC))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = Color.Black.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(Screen.AudQuiz.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(buttonHeight),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF121E30),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Begin",
                    fontFamily = BigShouldersDisplay,
                    fontWeight = FontWeight.Light,
                    fontSize = buttonFontSize
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    navController.navigate(Screen.NotificationPreferences.route) {
                        popUpTo(Screen.QuizIntro.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(buttonHeight),
                shape = RectangleShape
            ) {
                Text(
                    text = "Skip",
                    fontFamily = BigShouldersDisplay,
                    fontSize = buttonFontSize,
                    fontWeight = FontWeight.Light,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(bottomSpacing))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudQuizScreen(navController: NavController, viewModel: OnboardingViewModel = viewModel()) {
    val darkBlue = Color(0xFF264168)
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val imageHeight = if (screenHeight < 700) 150.dp else 200.dp

    LaunchedEffect(Unit) {
        viewModel.setAudTestInProgress(true)
        if (viewModel.quizAnswers.isEmpty() || viewModel.quizAnswers.size != questions.size) {
            viewModel.quizAnswers = List(questions.size) { null }
        }
    }

    val currentIndex = viewModel.currentQuizIndex
    val answers = viewModel.quizAnswers

    if (answers.size != questions.size) return

    val handleNext = {
        if (currentIndex < questions.size - 1) {
            viewModel.currentQuizIndex++
        } else {
            val totalScore = answers.filterNotNull().sumOf { it.score }
            val audRisk = viewModel.scoreToAudRisk(totalScore)
            viewModel.updateAudRisk(audRisk)
            viewModel.saveQuizResult(audRisk)
            viewModel.resetQuizState()
            navController.navigate(Screen.QuizLoading.route) {
                popUpTo("aud_quiz") { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = darkBlue,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        if (viewModel.currentQuizIndex > 0) viewModel.currentQuizIndex--
                        else navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            AnimatedContent(
                targetState = currentIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "quiz_fade",
                modifier = Modifier.fillMaxSize()
            ) { index ->
                val question = questions[index]
                val selectedOption = answers[index]

                QuizContent(
                    question = question,
                    selectedOption = selectedOption,
                    onOptionSelected = { option ->
                        viewModel.quizAnswers = answers.toMutableList().also { it[index] = option }
                        kotlinx.coroutines.MainScope().launch {
                            delay(350)
                            handleNext()
                        }
                    }
                )
            }

            Image(
                painter = painterResource(id = R.drawable.quiz_logo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .align(Alignment.BottomCenter)
                    .alpha(0.25f),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}


@Composable
fun QuizContent(
    question: Question,
    selectedOption: Option?,
    onOptionSelected: (Option) -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp

    val topSpacing = if (screenHeight < 700) 16.dp else 88.dp
    val questionBottomSpacing = if (screenHeight < 700) 36.dp else 88.dp
    val optionVerticalPadding = if (screenHeight < 700) 8.dp else 12.dp
    val questionFontSize = if (screenHeight < 700) 26.sp else 32.sp
    val questionLineHeight = if (screenHeight < 700) 32.sp else 38.sp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(topSpacing))

        Text(
            text = question.text,
            fontSize = questionFontSize,
            lineHeight = questionLineHeight,
            fontFamily = BigShouldersDisplay,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(questionBottomSpacing))

        question.options.forEach { option ->
            val isSelected = selectedOption == option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 6.dp)
                    .border(
                        width = 0.5.dp,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f),
                        shape = RectangleShape
                    )
                    .clickable { onOptionSelected(option) }
                    .padding(horizontal = 12.dp, vertical = optionVerticalPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .border(
                            0.5.dp,
                            if (isSelected) Color.White else Color.White.copy(alpha = 0.3f),
                            CircleShape
                        )
                        .background(
                            color = if (isSelected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = option.text,
                    fontFamily = BigShouldersDisplay,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun QuizLoadingScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(4000)
        navController.navigate(Screen.QuizResult.route) {
            popUpTo(Screen.QuizLoading.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF33578A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.drink_cluster_loading),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(300.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Preparing your results...",
                fontFamily = BigShouldersDisplay,
                fontSize = 36.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun QuizResultScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadUserState()
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val riskFontSize = if (screenHeight < 700) 36.sp else 52.sp
    val beerOffsetY = if (screenHeight < 700) 155.dp else 255.dp

    val backgroundColor = when (viewModel.audRisk) {
        AudRisk.DEFAULT_RISK -> Color(0xFFFFFFFF)
        AudRisk.NO_RISK -> Color(0xFF4A9D5B)
        AudRisk.LOW_RISK -> Color(0xFFFEF5DC)
        AudRisk.MODERATE_RISK -> Color(0xFFFFCB46)
        AudRisk.HIGH_RISK -> Color(0xFFFAAAA5)
    }

    val cocktailColor = when (viewModel.audRisk) {
        AudRisk.LOW_RISK -> Color(0x662E4E7C)
        else -> Color(0xFF33578A)
    }

    val beerColor = when (viewModel.audRisk) {
        AudRisk.MODERATE_RISK -> Color(0xFFFAAAA5)
        else -> Color(0xFFFFCB46)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clipToBounds()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_beer),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(beerColor),
                    modifier = Modifier
                        .size(250.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 115.dp, y = beerOffsetY)
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_cocktail_mixed),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(cocktailColor),
                    modifier = Modifier
                        .size(265.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = (-63).dp, y = 12.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(2f))

                    Text(
                        text = "Results",
                        fontFamily = BigShouldersDisplay,
                        fontSize = 32.sp,
                        color = Color(0xFF1A3720)
                    )

                    Spacer(modifier = Modifier.weight(1.5f))

                    Text(
                        text = "DRINKING PATTERN",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Color(0xFF1A3720)
                    )

                    Spacer(modifier = Modifier.weight(0.35f))

                    Text(
                        text = viewModel.audRisk.displayName,
                        fontFamily = BowlbyOne,
                        fontSize = riskFontSize,
                        color = Color(0xFF1A3720),
                        modifier = Modifier
                            .background(Color(0xFFAFC9DC))
                            .padding(horizontal = 12.dp, vertical = 1.dp)
                    )

                    Spacer(modifier = Modifier.weight(2f))

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 28.dp),
                            shape = RectangleShape,
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF5EF))
                        ) {
                            Text(
                                text = buildStyledDescription(viewModel.audRisk.description),
                                fontFamily = Poppins,
                                fontSize = 15.sp,
                                color = Color(0xFF1A3720),
                                modifier = Modifier.padding(horizontal = 36.dp, vertical = 36.dp)
                            )
                        }

                        Text(
                            text = "What it means",
                            fontFamily = BowlbyOne,
                            fontSize = 17.sp,
                            color = Color(0xFF1A3720),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(x = 20.dp, y = 12.dp)
                                .background(Color(0xFFAFC9DC))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1.25f))
                }
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = Color.Black.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.weight(0.0225f))

            Button(
                onClick = {
                    viewModel.getCompletedOnboarding { completed ->
                        if (completed) {
                            navController.navigate(Screen.Account.route)
                        } else {
                            navController.navigate(Screen.NotificationPreferences.route)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(44.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF121E30),
                    contentColor = Color(0xFFEDF5EF)
                )
            ) {
                Text(
                    text = "Continue",
                    fontFamily = BigShouldersDisplay,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.weight(0.05f))
        }
    }
}
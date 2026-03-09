package com.example.cauds.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun AudQuizScreen(navController: NavController, viewModel: OnboardingViewModel = viewModel()) {

    // TODO: Drinking assessment intro page (see figma design file)

    var currentIndex by remember { mutableIntStateOf(0) }
    var answers by remember { mutableStateOf(List<Option?>(questions.size) { null }) }

    val handleNext = {
        if (currentIndex < questions.size - 1) {
            currentIndex++
        } else {
            val totalScore = answers.filterNotNull().sumOf { it.score }
            val audRisk = viewModel.scoreToAudRisk(totalScore)
            viewModel.updateAudRisk(audRisk)
            viewModel.saveQuizResult(audRisk)
            navController.navigate(Screen.QuizResult.route) { popUpTo("aud_quiz") { inclusive = true } }
        }
    }

    AnimatedContent(
        targetState = currentIndex,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "quiz_fade"
    ) { index ->
        val question = questions[index]
        val selectedOption = answers[index]

        QuizQuestion(
            question = question,
            selectedOption = selectedOption,
            onOptionSelected = { option ->
                answers = answers.toMutableList().also { it[index] = option }
                kotlinx.coroutines.MainScope().launch {
                    kotlinx.coroutines.delay(350)
                    handleNext()
                }
            },
            onBack = {
                if (currentIndex > 0) currentIndex-- else navController.popBackStack()
            }
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizQuestion(
    question: Question,
    selectedOption: Option?,
    onOptionSelected: (Option) -> Unit,
    onBack: (() -> Unit),
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
        Box(
            modifier = Modifier.fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
        ) {

            // Title pinned to top
            Text(
                question.text,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp)
            )

            // Options pinned to true center
            Column(
                modifier = Modifier.align(Alignment.Center)
            ) {
                question.options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedOption == option, onClick = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option.text)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    Scaffold(
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
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Results",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "DRINKING PATTERN",
            style = MaterialTheme.typography.labelMedium
        )

        Text(
            text = viewModel.audRisk.displayName,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        // -- What It Means card --
        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "WHAT IT MEANS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = viewModel.audRisk.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CONTINUE")
        }
    }
    }
}
package com.example.cauds.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen

@Composable
fun AudQuizScreen(navController: NavController, viewModel: OnboardingViewModel = viewModel()) {

    LaunchedEffect(Unit) {
        viewModel.setAudTestInProgress(true)
    }

    // TODO: Drinking assessment intro page (see figma design file)

    // Initialize answers list in ViewModel if it's empty
    LaunchedEffect(Unit) {
        if (viewModel.quizAnswers.isEmpty() || viewModel.quizAnswers.size != questions.size) {
            viewModel.quizAnswers = List(questions.size) { null }
        }
    }

    val currentIndex = viewModel.currentQuizIndex
    val answers = viewModel.quizAnswers

    // Safety check while loading
    if (answers.size != questions.size) return 

    val question = questions[currentIndex]
    val selectedOption = answers[currentIndex]

    QuizQuestion(
        question = question,
        selectedOption = selectedOption,
        onOptionSelected = { option ->
            viewModel.quizAnswers = answers.toMutableList().also { it[currentIndex] = option }
        },
        onNext = {
            if (currentIndex < questions.size - 1) {
                viewModel.currentQuizIndex++
            } else {
                val totalScore = answers.filterNotNull().sumOf { it.score }
                val audRisk = viewModel.scoreToAudRisk(totalScore)
                viewModel.updateAudRisk(audRisk)
                viewModel.saveQuizResult(audRisk)

                navController.navigate(Screen.QuizResult.route) { popUpTo("aud_quiz") { inclusive = true } } // show results

            }
        },
        onBack = if (currentIndex > 0) ({ viewModel.currentQuizIndex-- }) else null
    )


}

@Composable
fun QuizQuestion(
    question: Question,
    selectedOption: Option?,
    onOptionSelected: (Option) -> Unit,
    onNext: () -> Unit,
    onBack: (() -> Unit)?,  // null if first question
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(question.text, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        question.options.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) }
                )
                Text(option.text)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            if (onBack != null) {
                Button(onClick = onBack) { Text("Back") }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Button(onClick = onNext, enabled = selectedOption != null) {
                Text("Next")
            }
        }
    }
}

@Composable
fun QuizResultScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

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
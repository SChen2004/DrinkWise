package com.example.cauds.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun AudQuizScreen(navController: NavController, vm: OnboardingViewModel = viewModel()) {

    var currentIndex by remember { mutableIntStateOf(0) }
    var answers by remember { mutableStateOf(List<Option?>(questions.size) { null }) }

    val question = questions[currentIndex]
    val selectedOption = answers[currentIndex]

    QuizQuestion(
        question = question,
        selectedOption = selectedOption,
        onOptionSelected = { option ->
            answers = answers.toMutableList().also { it[currentIndex] = option }
        },
        onNext = {
            if (currentIndex < questions.size - 1) {
                currentIndex++
            } else {
                val totalScore = answers.filterNotNull().sumOf { it.score }
                val audRisk = vm.scoreToAudRisk(totalScore)
                vm.saveQuizResult(audRisk)

                navController.popBackStack()
            }
        },
        onBack = if (currentIndex > 0) ({ currentIndex-- }) else null
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
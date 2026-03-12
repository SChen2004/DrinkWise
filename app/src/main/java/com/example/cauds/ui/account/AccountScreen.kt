package com.example.cauds.ui.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
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
fun AccountScreen(
    navController: NavController,
    viewModel: AccountViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    val user = viewModel.user
    val userEmail = viewModel.userEmail
    val audTestState = viewModel.audTestState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("You", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp)) // To balance the back button
        }

        // Personal Section
        Text(
            "PERSONAL",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HorizontalDivider()
        
        AccountInfoRow(label = "Name", value = user.name)
        HorizontalDivider()
        AccountInfoRow(label = "Email", value = userEmail)
        HorizontalDivider()
        AccountInfoRow(label = "Sex", value = user.sex.name.lowercase().replaceFirstChar { it.uppercase() }.replace("_", " "))
        HorizontalDivider()

        Spacer(modifier = Modifier.height(24.dp))

        // Notifications Section
        Text(
            "NOTIFICATIONS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HorizontalDivider()
        AccountActionRow(label = "Notifications") {
            navController.navigate(Screen.NotificationPreferences.route)
        }
        HorizontalDivider()

        Spacer(modifier = Modifier.height(24.dp))

        // AUD Test Section
        Text(
            "AUD TEST",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HorizontalDivider()
        
        when (audTestState) {
            AudTestState.UNTAKEN -> {
                AccountActionRow(label = "Take Test") {
                    navController.navigate(Screen.AudQuiz.route)
                }
                HorizontalDivider()
            }
            AudTestState.IN_PROGRESS -> {
                AccountActionRow(label = "Continue Test") {
                    navController.navigate(Screen.AudQuiz.route)
                }
                HorizontalDivider()
            }
            AudTestState.COMPLETED -> {
                AccountActionRow(label = "Retake Test") {
                    navController.navigate(Screen.AudQuiz.route)
                }
                HorizontalDivider()
                AccountActionRow(label = "View My Results") {
                    navController.navigate(Screen.QuizResult.route)
                }
                HorizontalDivider()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Other Actions
        AccountActionRow(label = "Change Password") {
            // Placeholder for now
        }
        HorizontalDivider()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Log out",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.performLogout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
                .padding(vertical = 12.dp)
        )
        HorizontalDivider()
        
    }
}

@Composable
fun AccountInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AccountActionRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

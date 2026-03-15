package com.example.cauds.ui.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.filled.ArrowBack
import com.example.cauds.data.model.Sex
import com.example.cauds.ui.navigation.Screen
import kotlinx.coroutines.launch


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
    
    var showSexPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Notification Banner
        if (viewModel.areNotificationsOff && userEmail.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifications are off. Would you\nlike to receive notifications?",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { navController.navigate(Screen.NotificationPreferences.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Enable", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        
        Text(
            text = "You",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

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
        val sexDisplay = user.sex.name.lowercase().replaceFirstChar { it.uppercase() }.replace("_", " ")
        AccountInfoRow(label = "Sex", value = sexDisplay, onClick = { showSexPicker = !showSexPicker })
        
        AnimatedVisibility(
            visible = showSexPicker,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            val sexOptions = listOf("Select Sex") + Sex.entries.map { it.displayName }
            val initialPage = remember(sexOptions, user.sex) {
                sexOptions.indexOf(user.sex.displayName).coerceAtLeast(0)
            }
            val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { sexOptions.size })
            val coroutineScope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(vertical = 40.dp)
                ) { page ->
                    val optionText = sexOptions[page]
                    val isSelected = pagerState.currentPage == page
                    val isPlaceholder = optionText == "Select Sex"

                    Text(
                        text = if (isPlaceholder) "Select Sex" else optionText,
                        style = if (isSelected) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected && !isPlaceholder) MaterialTheme.colorScheme.onSurface else Color.LightGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isPlaceholder) { // Disable clicking on Select Sex
                                coroutineScope.launch {
                                    // 1. Animate wheel snap
                                    pagerState.animateScrollToPage(page)
                                    // 2. Map string back to Enum
                                    val matchedEnum = Sex.entries.find { it.displayName == optionText }
                                    if (matchedEnum != null) {
                                        viewModel.updateUserSex(matchedEnum)
                                        // 3. Immediately collapse accordion
                                        showSexPicker = false
                                    }
                                }
                            }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
        HorizontalDivider()

        Spacer(modifier = Modifier.height(48.dp))

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

        Spacer(modifier = Modifier.height(48.dp))

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

        Spacer(modifier = Modifier.height(48.dp))

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
fun AccountInfoRow(label: String, value: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
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

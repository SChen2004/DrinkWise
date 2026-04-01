package com.example.cauds.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cauds.data.model.NotificationPreferences
import com.example.cauds.ui.navigation.Screen
import com.example.cauds.ui.theme.BackgroundSand
import com.example.cauds.ui.theme.BigShouldersDisplay
import com.example.cauds.ui.theme.CloverDarker
import com.example.cauds.ui.theme.CobaltDarker
import com.example.cauds.ui.theme.Poppins
import com.example.cauds.ui.theme.SandActive
import com.example.cauds.ui.theme.SkyDark
import com.example.cauds.ui.theme.rdp
import com.example.cauds.ui.theme.rsp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPreferencesScreen(
    navController: NavController,
    viewModel: OnboardingViewModel,
    fromAccount: Boolean = false
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val bottomSpacing = if (screenHeight < 700) 36.dp else 48.dp

    val creamBackground = BackgroundSand
    val darkGreen = CloverDarker

    var completedOnboarding by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.loadUserState()
        viewModel.getCompletedOnboarding { completed ->
            completedOnboarding = completed
        }
    }

    var dailyCheckin by remember { mutableStateOf(viewModel.notificationPreferences.dailyCheckin) }
    var dailyEncouragement by remember { mutableStateOf(viewModel.notificationPreferences.dailyEncouragement) }
    var weeklyReflection by remember { mutableStateOf(viewModel.notificationPreferences.weeklyReflection) }
    var monthlyProgress by remember { mutableStateOf(viewModel.notificationPreferences.monthlyProgress) }

    LaunchedEffect(viewModel.notificationPreferences) {
        dailyCheckin = viewModel.notificationPreferences.dailyCheckin
        dailyEncouragement = viewModel.notificationPreferences.dailyEncouragement
        weeklyReflection = viewModel.notificationPreferences.weeklyReflection
        monthlyProgress = viewModel.notificationPreferences.monthlyProgress
    }

    val options = listOf(
        Triple("Daily check-ins", "A gentle nudge to log or reflect on your day.", dailyCheckin) to { v: Boolean -> dailyCheckin = v },
        Triple("Daily encouragement", "A short message to keep you motivated.", dailyEncouragement) to { v: Boolean -> dailyEncouragement = v },
        Triple("Weekly reflections", "A thoughtful prompt to look back on your week.", weeklyReflection) to { v: Boolean -> weeklyReflection = v },
        Triple("Monthly progress", "A summary of patterns and changes over time.", monthlyProgress) to { v: Boolean -> monthlyProgress = v }
    )

    if (fromAccount) {
        AccountNotificationLayout(
            navController = navController,
            viewModel = viewModel,
            dailyCheckin = dailyCheckin,
            setDailyCheckin = { dailyCheckin = it },
            dailyEncouragement = dailyEncouragement,
            setDailyEncouragement = { dailyEncouragement = it },
            weeklyReflection = weeklyReflection,
            setWeeklyReflection = { weeklyReflection = it },
            monthlyProgress = monthlyProgress,
            setMonthlyProgress = { monthlyProgress = it }
        )
    } else {
        Scaffold(
            containerColor = BackgroundSand,
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.padding(top = 36.rdp())
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(24.rdp()),
                                tint = darkGreen
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
                        .padding(horizontal = 24.rdp()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(0.5f))

                    Text(
                        text = "How would you like to stay on track?",
                        fontSize = 30.rsp(),
                        fontFamily = BigShouldersDisplay,
                        lineHeight = 32.rsp(),
                        textAlign = TextAlign.Center,
                        color = darkGreen,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.weight(0.75f))

                    options.forEachIndexed { i, (triple, onToggle) ->
                        val (title, description, checked) = triple
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (checked) 1.5.dp else 0.75.dp,
                                    color = if (checked) Color(0xFF1A3720) else Color(0x4D000000),
                                    shape = RectangleShape
                                )
                                .clickable { onToggle(!checked) }
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .size(8.dp)
                                    .border(
                                        0.65f.dp,
                                        if (checked) darkGreen else Color(0x4D000000),
                                        RectangleShape
                                    )
                                    .background(
                                        color = if (checked) Color(0xFFAFC9DC) else Color.Transparent
                                    )
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = title,
                                    fontFamily = BigShouldersDisplay,
                                    fontSize = 24.sp,
                                    color = darkGreen
                                )
                                Text(
                                    text = description,
                                    fontFamily = Poppins,
                                    fontSize = 12.sp,
                                    color = Color(0x66000000).copy(alpha = 0.6f)
                                )
                            }
                        }
                        if (i < options.size - 1) {
                            Spacer(modifier = Modifier.weight(0.175f))
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.75f))

                    Text(
                        text = "We can send gentle reminders and updates. You're always in control of how often you hear from us.",
                        fontFamily = Poppins,
                        fontSize = 12.rsp(),
                        lineHeight = 20.rsp(),
                        textAlign = TextAlign.Center,
                        color = SkyDark
                    )
                    Spacer(modifier = Modifier.height(16.rdp()))
                }

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = Color.Gray.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val prefs = NotificationPreferences(
                            dailyCheckin = dailyCheckin,
                            dailyEncouragement = dailyEncouragement,
                            weeklyReflection = weeklyReflection,
                            monthlyProgress = monthlyProgress
                        )
                        viewModel.saveNotificationPreferences(prefs)
                        val isFromAccount = navController.previousBackStackEntry?.destination?.route == Screen.Account.route
                        if (isFromAccount || completedOnboarding) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(Screen.FavouriteDrinks.route)
                        }
                    },
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CobaltDarker,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(44.dp)
                ) {
                    Text(
                        text = "Continue",
                        fontFamily = BigShouldersDisplay,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(bottomSpacing))
            }
        }
    }
}

@Composable
fun AccountNotificationLayout(
    navController: NavController,
    viewModel: OnboardingViewModel,
    dailyCheckin: Boolean,
    setDailyCheckin: (Boolean) -> Unit,
    dailyEncouragement: Boolean,
    setDailyEncouragement: (Boolean) -> Unit,
    weeklyReflection: Boolean,
    setWeeklyReflection: (Boolean) -> Unit,
    monthlyProgress: Boolean,
    setMonthlyProgress: (Boolean) -> Unit
) {
    Scaffold(
        containerColor = BackgroundSand,
        bottomBar = {
            // The bottom of the screen Save button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundSand),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalDivider(thickness = 0.5.rdp(), color = CloverDarker.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(16.rdp()))

                Button(
                    onClick = {
                        val prefs = NotificationPreferences(
                            dailyCheckin = dailyCheckin,
                            dailyEncouragement = dailyEncouragement,
                            weeklyReflection = weeklyReflection,
                            monthlyProgress = monthlyProgress
                        )
                        viewModel.saveNotificationPreferences(prefs)
                        navController.popBackStack()
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.rdp())
                        .height(44.rdp()),
                    shape = RoundedCornerShape(0.rdp()),
                    colors = ButtonDefaults.buttonColors(containerColor = CobaltDarker),
                    contentPadding = PaddingValues(0.rdp())
                ) {
                    Text(
                        text = "Save",
                        fontFamily = BigShouldersDisplay,
                        fontSize = 24.rsp(),
                        letterSpacing = 0.rsp(),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(24.rdp()))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .padding(horizontal = 16.rdp()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Standardized Back Button
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(top = 26.rdp())
                        .size(20.rdp())
                ) {
                    Icon(
                        painter = painterResource(id = com.example.cauds.R.drawable.ic_arrow_left),
                        contentDescription = "Back",
                        tint = CloverDarker,
                        modifier = Modifier.size(20.rdp())
                    )
                }
            }

            // 2. Standardized Title (Synced with Account/ChangePassword)
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 32.rsp(),
                fontFamily = BigShouldersDisplay,
                color = CloverDarker,
                modifier = Modifier.fillMaxWidth().padding(top = 10.rdp()),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.rdp()))

            HorizontalDivider(thickness = 0.5.rdp(), color = CloverDarker.copy(alpha = 0.4f))

            NotificationToggleRow(
                title = "Daily check-ins",
                description = "A gentle nudge to log or reflect on your day.",
                checked = dailyCheckin,
                onCheckedChange = setDailyCheckin
            )
            HorizontalDivider(thickness = 0.5.rdp(), color = CloverDarker.copy(alpha = 0.4f))

            NotificationToggleRow(
                title = "Daily encouragement",
                description = "A short message to keep you motivated.",
                checked = dailyEncouragement,
                onCheckedChange = setDailyEncouragement
            )
            HorizontalDivider(thickness = 0.5.rdp(), color = CloverDarker.copy(alpha = 0.4f))

            NotificationToggleRow(
                title = "Weekly reflections",
                description = "A thoughtful prompt to look back on your week.",
                checked = weeklyReflection,
                onCheckedChange = setWeeklyReflection
            )
            HorizontalDivider(thickness = 0.5.rdp(), color = CloverDarker.copy(alpha = 0.4f))

            NotificationToggleRow(
                title = "Monthly progress",
                description = "A summary of patterns and changes over time.",
                checked = monthlyProgress,
                onCheckedChange = setMonthlyProgress
            )
            HorizontalDivider(thickness = 0.5.rdp(), color = CloverDarker.copy(alpha = 0.4f))

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(thickness = 0.5.rdp(), color = CloverDarker.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun NotificationToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.rdp()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = BigShouldersDisplay,
                fontSize = 24.rsp(),
                color = CloverDarker
            )
            Text(
                text = description,
                fontFamily = Poppins,
                fontSize = 12.rsp(),
                color = Color.Black.copy(alpha = 0.4f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = CloverDarker,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = SandActive,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

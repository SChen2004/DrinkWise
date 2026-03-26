package com.example.cauds.ui.account

import com.example.cauds.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.cauds.data.model.Sex
import com.example.cauds.ui.navigation.Screen
import com.example.cauds.ui.theme.*
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
            .background(BackgroundSand)
            .verticalScroll(rememberScrollState())
    ) {
        // Notification Banner
        if (viewModel.areNotificationsOff && userEmail.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CobaltDarker)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Notifications are off. Would you like to receive notifications?",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(32.dp))
                Button(
                    onClick = { navController.navigate(Screen.NotificationPreferences.createRoute(fromAccount = true)) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFAFC9DC),
                        contentColor = CobaltDarker
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Enable",
                        style = TextStyle(
                            fontFamily = BigShouldersDisplay,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(26.dp))

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack, 
                    contentDescription = "Back",
                    tint = CloverDarker
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        
        Text(
            text = "You",
            style = TextStyle(
                fontFamily = BigShouldersDisplay,
                fontSize = 32.sp,
                fontWeight = FontWeight.Normal
            ),
            color = CloverDarker,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp)
        )

        // Sections
        
        // Personal Section
        AccountSectionHeader("Personal")
        HorizontalDivider(thickness = 0.5.dp, color = CloverDarker.copy(alpha = 0.4f))
        
        AccountInfoRow(label = "NAME", value = user.name)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = CloverDarker.copy(alpha = 0.4f))
        AccountInfoRow(label = "EMAIL", value = userEmail)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = CloverDarker.copy(alpha = 0.4f))
        val sexDisplay = user.sex.displayName
        AccountInfoRow(label = "SEX", value = sexDisplay, onClick = { showSexPicker = !showSexPicker })
        
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
                    .background(BackgroundSand),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = CloverDarker.copy(alpha = 0.4f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Selection Highlight Box
                    Box(
                        modifier = Modifier
                            .width(300.dp)
                            .height(40.dp)
                            .background(Color(0xFF3583A4).copy(alpha = 0.1f))
                    )
                    
                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(vertical = 30.dp),
                        pageSpacing = 10.dp
                    ) { page ->
                        val optionText = sexOptions[page]
                        val isSelected = pagerState.currentPage == page
                        val isPlaceholder = optionText == "Select Sex"
                        val alpha = if (isSelected) 1f else 0.2f

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPlaceholder) {
                                // Placeholder item with dividers 
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    HorizontalDivider(modifier = Modifier.width(32.dp), thickness = 0.5.dp, color = CloverDarker.copy(alpha = if (isSelected) 0.6f else 0.2f))
                                    Text(
                                        text = optionText,
                                        style = TextStyle(
                                            fontFamily = BigShouldersDisplay,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Normal
                                        ),
                                        color = CloverDarker.copy(alpha = if (isSelected) 0.6f else 0.2f),
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    HorizontalDivider(modifier = Modifier.width(32.dp), thickness = 0.5.dp, color = CloverDarker.copy(alpha = if (isSelected) 0.6f else 0.2f))
                                }
                            } else {
                                Text(
                                    text = optionText,
                                    style = TextStyle(
                                        fontFamily = BigShouldersDisplay,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    color = CloverDarker.copy(alpha = alpha),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .clickable {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(page)
                                                val matchedEnum = Sex.entries.find { it.displayName == optionText }
                                                if (matchedEnum != null) {
                                                    viewModel.updateUserSex(matchedEnum)
                                                    showSexPicker = false
                                                }
                                            }
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(thickness = 0.5.dp, color = CloverDarker.copy(alpha = 0.4f))

        Spacer(modifier = Modifier.height(48.dp))

        // Notifications Section
        AccountSectionHeader("Notifications")
        HorizontalDivider(thickness = 0.5.dp, color = CloverDarker.copy(alpha = 0.4f))
        AccountActionRow(label = "Notifications") {
            navController.navigate(Screen.NotificationPreferences.createRoute(fromAccount = true))
        }
        HorizontalDivider(thickness = 0.5.dp, color = CloverDarker.copy(alpha = 0.4f))

        Spacer(modifier = Modifier.height(48.dp))

        // Drink Assessment Section
        AccountSectionHeader("Drink Assessment")
        HorizontalDivider(thickness = 0.5.dp, color = CloverDarker.copy(alpha = 0.4f))
        
        when (audTestState) {
            AudTestState.UNTAKEN -> {
                AccountActionRow(label = "Take Assessment") {
                    navController.navigate(Screen.AudQuiz.route)
                }
            }
            AudTestState.IN_PROGRESS -> {
                AccountActionRow(label = "Continue Assessment") {
                    navController.navigate(Screen.AudQuiz.route)
                }
            }
            AudTestState.COMPLETED -> {
                AccountActionRow(label = "Retake Assessment") {
                    navController.navigate(Screen.AudQuiz.route)
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = CloverDarker.copy(alpha = 0.4f))
                AccountActionRow(label = "View My Results") {
                    navController.navigate(Screen.QuizResult.route)
                }
            }
        }
        HorizontalDivider(thickness = 0.5.dp, color = CloverDarker.copy(alpha = 0.4f))

        Spacer(modifier = Modifier.height(48.dp))

        HorizontalDivider(thickness = 0.5.dp, color = CloverDarker.copy(alpha = 0.4f))
        
        AccountActionRow(label = "Change Password") {
            navController.navigate(Screen.ChangePassword.route)
        }
        
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = CloverDarker.copy(alpha = 0.4f))
        
        Text(
            text = "Log out",
            style = TextStyle(
                fontFamily = Poppins,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            ),
            color = CloverDarker,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.performLogout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
        HorizontalDivider(thickness = 0.5.dp, color = CloverDarker.copy(alpha = 0.4f))
        
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun AccountSectionHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 10.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = Poppins,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            ),
            color = CloverDarker,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun AccountInfoRow(label: String, value: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, 
            style = TextStyle(
                fontFamily = Poppins,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            ), 
            color = CloverDarker.copy(alpha = 0.4f)
        )
        Text(
            text = value, 
            style = TextStyle(
                fontFamily = Poppins,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            ),
            color = CloverDarker
        )
    }
}

@Composable
fun AccountActionRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, 
            style = TextStyle(
                fontFamily = Poppins,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            ),
            color = CloverDarker
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right), 
            contentDescription = null, 
            tint = CloverDarker,
            modifier = Modifier.size(16.dp)
        )
    }
}

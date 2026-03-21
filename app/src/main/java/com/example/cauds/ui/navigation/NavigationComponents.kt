package com.example.cauds.ui.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cauds.R
import com.example.cauds.ui.theme.BackgroundSand
import com.example.cauds.ui.theme.rdp

sealed class BottomNavItem(val route: String, @DrawableRes val iconRes: Int, @DrawableRes val selectedIconRes: Int) {
    object Dashboard : BottomNavItem(Screen.Dashboard.route, R.drawable.ic_dashboard, R.drawable.ic_dashboard_filled)
    object Journal : BottomNavItem(Screen.Journal.route, R.drawable.ic_journal, R.drawable.ic_journal_filled)
    object LearningPage : BottomNavItem(Screen.LearningPage.route, R.drawable.ic_learning_page, R.drawable.ic_learning_page_filled)
    object Account : BottomNavItem(Screen.Account.route, R.drawable.ic_account, R.drawable.ic_account_filled)
}

@Composable
fun AppBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Journal,
        BottomNavItem.LearningPage,
        BottomNavItem.Account
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Surface(
        color = BackgroundSand,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Top border (0.5dp)
            HorizontalDivider(
                thickness = 0.5.dp,
                color = Color(0x33000000) // Subtle dark border
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.rdp(),
                        bottom = 24.rdp(),
                        start = 32.rdp(),
                        end = 32.rdp()
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val selected = currentRoute == item.route
                    val tint = if (selected) Color(0xFF1A3720) else Color(0x661A3720)
                    
                    Icon(
                        painter = painterResource(id = if (selected) item.selectedIconRes else item.iconRes),
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier
                            .size(24.rdp())
                            .clickable {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        navController.graph.startDestinationRoute?.let { route ->
                                            popUpTo(route) {
                                                saveState = true
                                            }
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}

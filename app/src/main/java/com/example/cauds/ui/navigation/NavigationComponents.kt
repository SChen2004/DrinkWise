package com.example.cauds.ui.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

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
                    // Dashboard is logically the parent of Calendar and tracking sub-screens
                    val isDashboardSubScreen = currentDestination?.route == Screen.Calendar.route || 
                                             currentDestination?.route == Screen.Tracking.route ||
                                             currentDestination?.route == Screen.ManageDrinks.route ||
                                             currentDestination?.route == Screen.DaySummary.route ||
                                             currentDestination?.route?.startsWith("day_summary") == true

                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true || 
                                 (item == BottomNavItem.Dashboard && isDashboardSubScreen)

                    Icon(
                        painter = painterResource(id = if (selected) item.selectedIconRes else item.iconRes),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(24.rdp())
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (item.route == Screen.Dashboard.route) {
                                    // SPECIAL CASE: For the Dashboard (Home) button, 
                                    // we want to ensure we pop back to the root if we're on a sub-page.
                                    if (currentDestination?.route != Screen.Dashboard.route) {
                                        navController.popBackStack(Screen.Dashboard.route, inclusive = false)
                                    }
                                } else if (currentDestination?.route != item.route) {
                                    // Standard tab switching logic for other items
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
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

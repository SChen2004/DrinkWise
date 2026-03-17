package com.example.cauds.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner


@Composable
fun DashboardScreen(
    navController: NavController,
    dashboardViewModel: DashboardViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                dashboardViewModel.loadTodaySummary()
                dashboardViewModel.loadTodayJournal()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        DrinkLogSection(
            todayDrinkCount = dashboardViewModel.todayDrinkCount,
            todayTotalSpent = dashboardViewModel.todayTotalSpent,
            didntDrinkToggled = dashboardViewModel.didntDrinkToggled,
            onLogClick = {
                navController.navigate(Screen.Tracking.route)
            },
            onDidntDrinkToggle = {
                dashboardViewModel.toggleDidntDrink()
            }
        )

        JournalSection(
            todayEntryPreview = dashboardViewModel.todayEntryPreview,
            onNewEntryClick = {
                navController.navigate(Screen.CreateEntry.route)
            },
            onViewJournalClick = {
                navController.navigate(Screen.Journal.route)
            }
        )

        DailyMessageSection()

        // ── Bottom row: Mini Calendar + Week Summary side by side ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),  // Forces both children to match the taller one's height
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MiniCalendarSection(
                drinkCountByDay = dashboardViewModel.drinkCountByDay,
                onClick = { navController.navigate(Screen.Calendar.route) },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )

            WeekSummarySection(
                weekDrinkCount = dashboardViewModel.weekDrinkCount,
                weekTotalSpent = dashboardViewModel.weekTotalSpent,
                onClick = {
                    val todayIso = java.time.LocalDate.now()
                        .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
                    navController.navigate(Screen.DaySummary.createRoute(todayIso))
                },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
    }
}
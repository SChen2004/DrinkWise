package com.example.cauds.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen

@Composable
fun DashboardScreen(navController: NavController) {
    Column {
        Text("Dashboard")

        Button(onClick = { navController.navigate(Screen.Tracking.route) }) { Text("Go Tracking") }
        Button(onClick = { navController.navigate(Screen.Insight.route) }) { Text("Go Insight") }
        Button(onClick = { navController.navigate(Screen.Account.route) }) { Text("Go Account") }
        Button(onClick = { navController.navigate(Screen.Support.route) }) { Text("Go Support") }
    }
}
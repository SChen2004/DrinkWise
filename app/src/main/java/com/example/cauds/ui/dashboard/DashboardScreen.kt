package com.example.cauds.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen

@Composable
fun DashboardScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Four Main Features
        Button(onClick = { navController.navigate(Screen.Tracking.route) }, modifier = Modifier.fillMaxWidth()) {
            Text("Drink Tracking")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Screen.Journal.route) }, modifier = Modifier.fillMaxWidth()) {
            Text("Journal")
        }


        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Screen.Calendar.route) }, modifier = Modifier.fillMaxWidth()) {
            Text("Calendar")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Screen.Support.route) }, modifier = Modifier.fillMaxWidth()) {
            Text("Support")
        }
    }
}
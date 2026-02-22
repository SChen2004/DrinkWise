package com.example.cauds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cauds.ui.navigation.AppBottomNavigation
import com.example.cauds.ui.navigation.NavGraph
import com.example.cauds.ui.navigation.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Show bottom bar for all "main" screens
                val bottomBarScreens = listOf(
                    Screen.Dashboard.route,
                    Screen.Journal.route,
                    Screen.Calendar.route,
                    Screen.Support.route,
                    Screen.Account.route
                )
                val showBottomBar = currentRoute in bottomBarScreens

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            AppBottomNavigation(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
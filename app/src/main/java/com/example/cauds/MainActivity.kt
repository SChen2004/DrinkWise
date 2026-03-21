package com.example.cauds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cauds.ui.auth.AuthViewModel
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
                    Screen.LearningPage.route,
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
                        val authViewModel: AuthViewModel = viewModel()

                        var startDestination by remember { mutableStateOf<String?>(null) }

                        LaunchedEffect(Unit) {
                            if (authViewModel.isLoggedIn()) {
                                authViewModel.checkOnboardingStatus { completed ->
                                    if (completed) {
                                        startDestination = Screen.Dashboard.route
                                    } else {
                                        startDestination = Screen.OnboardingName.route
                                    }
                                }
                            } else {
                                startDestination = Screen.Login.route
                            }
                        }

                        if (startDestination == null) {
                            // Empty loading state while waiting for network check
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                            return@Surface
                        }

                        NavGraph(
    navController = navController,
    startDestination = startDestination ?: "login"
)
                    }
                }
            }
        }
    }
}
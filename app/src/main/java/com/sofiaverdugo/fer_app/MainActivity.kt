package com.sofiaverdugo.fer_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sofiaverdugo.fer_app.ui.theme.FER_appTheme
import com.sofiaverdugo.fer_app.views.AppBackground
import com.sofiaverdugo.fer_app.views.AppNavigation
import com.sofiaverdugo.fer_app.views.BottomNavBar
import com.sofiaverdugo.fer_app.views.FerTopBar
import com.sofiaverdugo.fer_app.views.bottomNavItems

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FER_appTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route
                val isTab = bottomNavItems.any { it.route == currentRoute }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = AppBackground,
                    topBar = {
                        if (isTab) FerTopBar()
                    },
                    bottomBar = {
                        if (isTab) {
                            BottomNavBar(
                                currentRoute = currentRoute,
                                onItemClick = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        modifier = if (isTab) {
                            Modifier.padding(top = innerPadding.calculateTopPadding())
                        } else {
                            Modifier
                        }
                    )
                }
            }
        }
    }
}
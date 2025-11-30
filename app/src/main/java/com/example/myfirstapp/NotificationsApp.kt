package com.example.myfirstapp

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfirstapp.model.AppState
import com.example.myfirstapp.navScreens.notifications.NotificationSettingsScreen
import com.example.myfirstapp.navScreens.notifications.EditNotificationScreen
import com.example.myfirstapp.navScreens.notifications.UserMessagesScreen
import com.example.myfirstapp.navigation.NavigationRoutes
import com.example.myfirstapp.ui.components.BottomNavigationBar
import com.example.myfirstapp.utils.NotificationHandler
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.padding
import com.example.myfirstapp.ui.theme.AppTheme

@Composable
fun NotificationsApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val notificationHelper = remember { NotificationHandler(context) }

    var appState by remember { mutableStateOf(AppState()) }

    AppTheme(appTheme = appState.selectedTheme) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = NavigationRoutes.NOTIFICATION_SETTINGS,
                modifier = androidx.compose.ui.Modifier.padding(innerPadding)
            ) {
                composable(NavigationRoutes.NOTIFICATION_SETTINGS) {
                    NotificationSettingsScreen(
                        onSendNotification = { notificationData ->
                            notificationHelper.showNotification(notificationData)
                        }
                    )
                }

                composable(NavigationRoutes.EDIT_NOTIFICATION) {
                    EditNotificationScreen()
                }

                composable(NavigationRoutes.USER_MESSAGES) {
                    UserMessagesScreen()
                }
            }
        }
    }
}
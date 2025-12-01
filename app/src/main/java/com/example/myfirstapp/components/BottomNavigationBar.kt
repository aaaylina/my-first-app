package com.example.myfirstapp.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myfirstapp.R
import com.example.myfirstapp.navigation.NavigationRoutes

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            route = NavigationRoutes.NOTIFICATION_SETTINGS,
            iconRes = R.drawable.ic_settings,
            labelRes = R.string.notification_settings_tab
        ),
        BottomNavItem(
            route = NavigationRoutes.EDIT_NOTIFICATION,
            iconRes = R.drawable.ic_edit,
            labelRes = R.string.edit_notification_tab
        ),
        BottomNavItem(
            route = NavigationRoutes.USER_MESSAGES,
            iconRes = R.drawable.ic_message,
            labelRes = R.string.user_messages_tab
        ),
        BottomNavItem(
            route = NavigationRoutes.COROUTINES_MANAGER,
            iconRes = R.drawable.ic_back_hand_24,
            labelRes = R.string.coroutines_tab
        )
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = stringResource(item.labelRes)
                    )
                },
                label = { Text(stringResource(item.labelRes)) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val iconRes: Int,
    val labelRes: Int
)
package com.example.myfirstapp.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.myfirstapp.presentation.screens.WeatherDetailsScreen
import com.example.myfirstapp.presentation.screens.WeatherListScreen
import kotlinx.serialization.Serializable

@Serializable
object WeatherListRoute : NavKey

@Serializable
data class WeatherDetailsRoute(val cityName: String) : NavKey

@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf<NavKey>(WeatherListRoute) }

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        },
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(300, easing = FastOutLinearInEasing)
                    )
        },
        popTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(300, easing = FastOutLinearInEasing)
                    )
        },
        entryProvider = entryProvider {
            entry<WeatherListRoute> {
                WeatherListScreen(
                    onCityClick = { cityName ->
                        backStack.add(WeatherDetailsRoute(cityName))
                    }
                )
            }

            entry<WeatherDetailsRoute> { route ->
                WeatherDetailsScreen(
                    cityName = route.cityName,
                    onBack = {
                        if (backStack.size > 1) {
                            backStack.removeLastOrNull()
                        }
                    }
                )
            }
        }
    )
}
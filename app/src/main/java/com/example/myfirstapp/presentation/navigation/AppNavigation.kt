package com.example.myfirstapp.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.myfirstapp.analytics.AnalyticsTracker
import com.example.myfirstapp.di.navigation.WeatherNavigationArgs
import com.example.myfirstapp.presentation.screens.AboutScreen
import com.example.myfirstapp.presentation.screens.ChartScreen
import com.example.myfirstapp.presentation.screens.WeatherDetailsScreen
import com.example.myfirstapp.presentation.screens.WeatherListScreen
import kotlinx.serialization.Serializable

@Serializable
object WeatherListRoute : NavKey

@Serializable
object WeatherDetailsRoute : NavKey

@Serializable
object AboutRoute : NavKey

@Serializable
object ChartRoute : NavKey

@Composable
fun AppNavigation(
    analyticsTracker: AnalyticsTracker,
    navigationArgs: WeatherNavigationArgs,
) {
    val backStack = remember { mutableStateListOf<NavKey>(ChartRoute) }

    LaunchedEffect(backStack.lastOrNull()) {
        val screenName = when (backStack.lastOrNull()) {
            is WeatherListRoute -> SCREEN_WEATHER_LIST
            is WeatherDetailsRoute -> SCREEN_WEATHER_DETAILS
            is AboutRoute -> SCREEN_ABOUT
            else -> return@LaunchedEffect
        }
        analyticsTracker.logScreenView(screenName)
    }

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
                        navigationArgs.setSelectedCity(cityName)
                        backStack.add(WeatherDetailsRoute)
                    },
                    onAboutClick = { backStack.add(AboutRoute) },
                )
            }

            entry<AboutRoute> {
                AboutScreen(
                    onBack = {
                        if (backStack.size > 1) {
                            backStack.removeLastOrNull()
                        }
                    },
                )
            }

            entry<WeatherDetailsRoute> {
                WeatherDetailsScreen(
                    onBack = {
                        navigationArgs.clear()
                        if (backStack.size > 1) {
                            backStack.removeLastOrNull()
                        }
                    }
                )
            }

            entry<ChartRoute> {
                ChartScreen(
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

const val SCREEN_WEATHER_LIST = "weather_list"
const val SCREEN_WEATHER_DETAILS = "weather_details"
const val SCREEN_ABOUT = "about"
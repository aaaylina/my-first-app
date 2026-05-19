package com.example.myfirstapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.myfirstapp.analytics.AnalyticsTracker
import com.example.myfirstapp.data.preferences.OnboardingPreferences
import com.example.myfirstapp.di.navigation.WeatherNavigationArgs
import com.example.myfirstapp.presentation.navigation.AppNavigation
import com.example.myfirstapp.presentation.screens.AboutBottomSheet
import com.example.myfirstapp.presentation.theme.MyFirstAppTheme
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    @Inject
    lateinit var navigationArgs: WeatherNavigationArgs

    @Inject
    lateinit var onboardingPreferences: OnboardingPreferences

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNeeded()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { }

        setContent {
            val aboutDismissed by onboardingPreferences.isAboutDismissed
                .collectAsStateWithLifecycle(initialValue = null)
            var aboutEventLogged by remember { mutableStateOf(false) }

            LaunchedEffect(aboutDismissed) {
                if (aboutDismissed == false && !aboutEventLogged) {
                    analyticsTracker.logAboutScreenShown()
                    aboutEventLogged = true
                }
            }

            MyFirstAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavigation(
                        analyticsTracker = analyticsTracker,
                        navigationArgs = navigationArgs,
                    )

                    if (aboutDismissed == false) {
                        AboutBottomSheet(
                            onDismissConfirmed = {
                                lifecycleScope.launch {
                                    onboardingPreferences.setAboutDismissed()
                                    analyticsTracker.logAboutScreenDismissed()
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

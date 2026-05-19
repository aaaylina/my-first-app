package com.example.myfirstapp.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsTracker @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val crashlytics: FirebaseCrashlytics,
) {

    fun logScreenView(screenName: String) {
        crashlytics.log("screen_view: $screenName")
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logAboutScreenShown() {
        firebaseAnalytics.logEvent(EVENT_ABOUT_SCREEN_SHOWN, null)
        crashlytics.log(EVENT_ABOUT_SCREEN_SHOWN)
    }

    fun logAboutScreenDismissed() {
        firebaseAnalytics.logEvent(EVENT_ABOUT_SCREEN_DISMISSED, null)
        crashlytics.log(EVENT_ABOUT_SCREEN_DISMISSED)
    }

    companion object {
        const val EVENT_ABOUT_SCREEN_SHOWN = "about_screen_shown"
        const val EVENT_ABOUT_SCREEN_DISMISSED = "about_screen_dismissed"
    }
}

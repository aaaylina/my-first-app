package com.example.myfirstapp

import android.app.Application
import com.example.myfirstapp.di.session.AppSessionInfo
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WeatherApplication : Application() {

    @Inject
    lateinit var appSessionInfo: AppSessionInfo

    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().apply {
            setUserId(appSessionInfo.userId)
            setCustomKey("session_id", appSessionInfo.sessionId)
        }
    }
}

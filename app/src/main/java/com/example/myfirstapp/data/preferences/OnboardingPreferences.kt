package com.example.myfirstapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "onboarding_preferences"
)

@Singleton
class OnboardingPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    val isAboutDismissed: Flow<Boolean> = context.onboardingDataStore.data.map { preferences ->
        preferences[KEY_ABOUT_DISMISSED] ?: false
    }

    suspend fun setAboutDismissed() {
        context.onboardingDataStore.edit { preferences ->
            preferences[KEY_ABOUT_DISMISSED] = true
        }
    }

    companion object {
        private val KEY_ABOUT_DISMISSED = booleanPreferencesKey("about_dismissed")
    }
}

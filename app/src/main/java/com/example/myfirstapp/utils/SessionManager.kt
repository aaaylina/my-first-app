package com.example.myfirstapp.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("FitnessAppPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
    }

    fun saveLoginSession(userId: Long, email: String) {
        with(sharedPreferences.edit()) {
            putLong(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }

    fun getUserId(): Long = sharedPreferences.getLong(KEY_USER_ID, -1L)

    fun getUserEmail(): String = sharedPreferences.getString(KEY_USER_EMAIL, "") ?: ""

    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)

    fun clearSession() {
        with(sharedPreferences.edit()) {
            remove(KEY_USER_ID)
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_USER_EMAIL)
            apply()
        }
    }
}
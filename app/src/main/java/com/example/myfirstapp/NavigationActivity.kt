package com.example.myfirstapp

import android.content.Context
import android.content.Intent

object NavigationActivity {

    fun navigateActivity(context: Context, text: String, targetClass: Class<*>) {
        val intent = Intent(context, targetClass)
        if (text.isNotEmpty()) {
            intent.putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(intent)
    }

}
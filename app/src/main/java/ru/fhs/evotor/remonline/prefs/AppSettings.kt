package ru.fhs.evotor.remonline.prefs

import android.content.Context
import android.content.SharedPreferences

class AppSettings(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE)

    var apiKey by PreferencesDelegate(preferences, "")

    fun clearAll() {
        apiKey = ""
    }

    companion object {
        const val APP_PREFS_NAME = "app settings prefs"
    }
}
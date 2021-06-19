package com.example.ttk_20.data.storage

import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Prefs @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private fun getSharedPreferences(prefsName: String) =
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    //app settings
    private val SETTINGS_DATA = "settings_data"
    private val SETTINGS_KEY = "settings key"
    private val settingsPrefs by lazy { getSharedPreferences(SETTINGS_DATA) }

    fun getAutoOpenEnabled(): Boolean = settingsPrefs.getBoolean(SETTINGS_KEY, true)

    fun setAutoOpenEnabled(enabled: Boolean) {
        settingsPrefs.edit().putBoolean(SETTINGS_KEY, enabled).apply()
    }

}
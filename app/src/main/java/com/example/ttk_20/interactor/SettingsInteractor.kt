package com.example.ttk_20.interactor

import com.example.ttk_20.data.storage.Prefs
import javax.inject.Inject

class SettingsInteractor @Inject constructor(
    private val prefs: Prefs
) {

    fun setAutoOpenEnable(isEnable: Boolean) {
        prefs.setAutoOpenEnabled(isEnable)
    }

    fun getAutoOpenEnable(): Boolean = prefs.getAutoOpenEnabled()

}
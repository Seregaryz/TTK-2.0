package com.example.ttk_20.ui.settings

import com.example.ttk_20.data.storage.Prefs
import com.example.ttk_20.ui._base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: Prefs
): BaseViewModel() {

    fun setAutoOpenEnable(isEnable: Boolean) {
        prefs.setAutoOpenEnabled(isEnable)
    }

    fun getAutoOpenEnable(): Boolean = prefs.getAutoOpenEnabled()
}
package com.example.ttk_20.ui.settings

import com.example.ttk_20.data.storage.Prefs
import com.example.ttk_20.interactor.SettingsInteractor
import com.example.ttk_20.ui._base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val interactor: SettingsInteractor
): BaseViewModel() {

    fun setAutoOpenEnable(isEnable: Boolean) {
        interactor.setAutoOpenEnable(isEnable)
    }

    fun getAutoOpenEnable(): Boolean = interactor.getAutoOpenEnable()
}
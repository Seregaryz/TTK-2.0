package com.example.ttk_20

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.plugins.RxJavaPlugins
import org.altbeacon.beacon.powersave.BackgroundPowerSaver
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    private lateinit var backgroundPowerSaver: BackgroundPowerSaver

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        backgroundPowerSaver = BackgroundPowerSaver(this)
        RxJavaPlugins.setErrorHandler {
        }
        initLogger()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
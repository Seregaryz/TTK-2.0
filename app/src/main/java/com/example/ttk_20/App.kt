package com.example.ttk_20

import android.app.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import com.example.ttk_20.data.storage.Prefs
import com.example.ttk_20.ui.keyless_access.KeylessAccessViewModel
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.plugins.RxJavaPlugins
import org.altbeacon.beacon.*
import org.altbeacon.beacon.powersave.BackgroundPowerSaver
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), BeaconConsumer {

    private lateinit var backgroundPowerSaver: BackgroundPowerSaver

    @Inject
    lateinit var beaconManager: BeaconManager

    @Inject
    lateinit var prefs: Prefs

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        backgroundPowerSaver = BackgroundPowerSaver(this)
        RxJavaPlugins.setErrorHandler {
        }
        initLogger()
        if (prefs.getAutoOpenEnabled()) {
            startTransmit()
        }
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun startTransmit() {
        val builder: Notification.Builder = Notification.Builder(this)
        builder.setSmallIcon(R.drawable.ic_app_icon)
        builder.setContentTitle("Scanning for Beacons")
        val intent = Intent(this, KeylessAccessViewModel::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "My Notification Channel ID",
                "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "My Notification Channel Description"
            val notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(channel.id)
        }
        beaconManager.enableForegroundServiceScanning(builder.build(), 456)
        beaconManager.setEnableScheduledScanJobs(false)
        beaconManager.bind(this)
        val beacon = Beacon.Builder()
            .setId1("D35B76E2-E01C-9FAC-BA8D-7CE20BDBA0C6")
            .setId2("2021")
            .setId3("100")
            .setManufacturer(0x004c)
            .setTxPower(-59)
            .build()
        val beaconParser = BeaconParser()
            .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        val beaconTransmitter = BeaconTransmitter(this, beaconParser)
        beaconTransmitter.advertiseMode = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY
        beaconTransmitter.startAdvertising(beacon, object : AdvertiseCallback() {
            override fun onStartFailure(errorCode: Int) {
                Timber.e("Advertisement start failed with code: $errorCode")
            }

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                Timber.i("Advertisement start succeeded.")
            }
        })
    }

    override fun onBeaconServiceConnect() {

    }

}
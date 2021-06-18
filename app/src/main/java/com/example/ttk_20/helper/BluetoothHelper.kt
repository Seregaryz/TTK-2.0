package com.example.ttk_20.helper

import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import com.example.ttk_20.helper.BluetoothListener.OnBluetoothEnabledCheckListener
import com.example.ttk_20.helper.BluetoothListener.OnBluetoothSupportedCheckListener
import javax.inject.Inject


class BluetoothHelper @Inject constructor(
    val context: Context
): BluetoothListener {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val btHandler = Handler()

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun initializeBluetooth(listener: OnBluetoothSupportedCheckListener?) {

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            listener!!.onBLENotSupported()
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        //val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            listener!!.onBluetoothNotSupported()
        }
    }


    override fun enableBluetooth(listener: OnBluetoothEnabledCheckListener?) {
        if (mBluetoothAdapter != null) {
            val enabled = mBluetoothAdapter!!.isEnabled
            listener!!.onBluetoothEnabled(enabled)
        }
    }
}
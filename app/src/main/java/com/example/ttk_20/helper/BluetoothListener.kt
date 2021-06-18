package com.example.ttk_20.helper

interface BluetoothListener {

    fun initializeBluetooth(listener: OnBluetoothSupportedCheckListener?)

    fun enableBluetooth(listener: OnBluetoothEnabledCheckListener?)

    interface OnBluetoothSupportedCheckListener {
        fun onBLENotSupported()
        fun onBluetoothNotSupported()
    }

    interface OnBluetoothEnabledCheckListener {
        fun onBluetoothEnabled(enable: Boolean)
    }

    interface BluetoothTrigger {
        fun initBluetooth()
        fun enableBluetooth()
    }
}
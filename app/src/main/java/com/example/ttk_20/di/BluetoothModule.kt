package com.example.ttk_20.di

import android.content.Context
import com.example.ttk_20.helper.BluetoothHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.altbeacon.beacon.BeaconManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BluetoothModule {

    @Provides
    @Singleton
    fun provideBeaconManager(@ApplicationContext context: Context): BeaconManager =
        BeaconManager.getInstanceForApplication(context)

    @Provides
    @Singleton
    fun provideBluetoothHelper(@ApplicationContext context: Context): BluetoothHelper =
        BluetoothHelper(context)

}
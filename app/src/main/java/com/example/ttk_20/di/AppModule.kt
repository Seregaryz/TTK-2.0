package com.example.ttk_20.di

import android.content.Context
import com.example.ttk_20.model.schedulers.AppSchedulers
import com.example.ttk_20.model.schedulers.SchedulersProvider
import com.example.ttk_20.system.ErrorHandler
import com.example.ttk_20.system.ResourceManager
import com.example.ttk_20.system.message.SystemMessageNotifier
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    companion object {

        @Provides
        @Singleton
        fun provideSchedulers(): SchedulersProvider = AppSchedulers()

        @Provides
        @Singleton
        fun provideGson(): Gson = with(GsonBuilder()) {
            serializeNulls()
            create()
        }

        @Provides
        @Singleton
        fun provideSystemMessageNotifier(): SystemMessageNotifier = SystemMessageNotifier()

        @Provides
        @Singleton
        fun providedErrorHandler(
            systemMessageNotifier: SystemMessageNotifier,
            schedulers: SchedulersProvider,
            resourceManager: ResourceManager
        ): ErrorHandler =
            ErrorHandler(
                systemMessageNotifier,
                schedulers,
                resourceManager
            )

    }
}
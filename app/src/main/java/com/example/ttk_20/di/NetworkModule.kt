package com.example.ttk_20.di

import android.content.Context
import android.os.Build
import com.example.ttk_20.BuildConfig
import com.example.ttk_20.data.network.Api
import com.example.ttk_20.system.Tls12SocketFactory.Companion.enableTls12
import com.example.ttk_20.utils.setSslContext
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideOkHttpClientBuilder(
        @ApplicationContext context: Context
    ): OkHttpClient.Builder =
        OkHttpClient.Builder().apply {
            connectTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            enableTls12()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
                setSslContext(context.resources)
            }
            val httpLogger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            addNetworkInterceptor(httpLogger)
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        okHttpClientBuilder: OkHttpClient.Builder,
        gson: Gson
    ): OkHttpClient =
        with(okHttpClientBuilder) {
            val httpLogger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            addInterceptor(httpLogger)
            build()
        }


    @Provides
    @Singleton
    fun provideApi(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Api {
        return with(Retrofit.Builder()) {
            addConverterFactory(GsonConverterFactory.create(gson))
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            client(okHttpClient)
            baseUrl(BuildConfig.BASE_URL)
            build()
        }.create(Api::class.java)
    }

}
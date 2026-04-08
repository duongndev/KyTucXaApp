package com.duongnd.kytucxa

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class KyTucXaApplication: Application() {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    override fun onCreate() {
        super.onCreate()

        // Khởi tạo Timber để log trong quá trình phát triển
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
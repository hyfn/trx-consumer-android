package com.trx.consumer.core

import android.app.Application
import com.trx.consumer.BuildConfig
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.ConfigManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var configManager: ConfigManager

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        if (instance == null) instance = this
        configManager.configure(this)
    }

    companion object {
        private var instance: MainApplication? = null
        fun getInstance(): MainApplication? {
            return instance
        }
    }
}

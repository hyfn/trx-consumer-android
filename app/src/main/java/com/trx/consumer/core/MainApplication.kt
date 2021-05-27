package com.trx.consumer.core

import android.app.Application
import com.trx.consumer.BuildConfig
import com.trx.consumer.managers.ConfigManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var configManager: ConfigManager

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        configManager.configure(this)
    }

}

package com.trx.consumer.core

import android.app.Application
import com.trx.consumer.managers.ConfigManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var configManager: ConfigManager

    override fun onCreate() {
        super.onCreate()
        configManager.configure(this)
    }
}

package com.trx.consumer.managers

import android.app.Application
import android.content.Context
import com.revenuecat.purchases.Purchases
import com.trx.consumer.BuildConfig

class ConfigManager {

    fun configure(application: Application) {
        configureRevenueCat(application)
    }

    private fun configureRevenueCat(context: Context) {
        Purchases.debugLogsEnabled = BuildConfig.DEVELOPMENT
        Purchases.configure(context, BuildConfig.kRevenueCatSdkKey)
    }
}

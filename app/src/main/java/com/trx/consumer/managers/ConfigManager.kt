package com.trx.consumer.managers

import android.app.Application
import android.content.Context
import com.amplitude.api.Amplitude
import com.amplitude.api.AmplitudeClient
import com.revenuecat.purchases.Purchases
import com.trx.consumer.BuildConfig

class ConfigManager {

    lateinit var amplitudeClient: AmplitudeClient
        private set

    fun configure(application: Application) {
        configureRevenueCat(application)
        configureAmplitude(application)
    }

    private fun configureRevenueCat(context: Context) {
        Purchases.debugLogsEnabled = BuildConfig.DEBUG
        Purchases.configure(context, BuildConfig.kRevenueCatSdkKey)
    }

    private fun configureAmplitude(application: Application) {
        with(application) {
            amplitudeClient = Amplitude
                .getInstance()
                .initialize(this, BuildConfig.kAmplitudeApiKey)
            amplitudeClient.enableForegroundTracking(this)
        }
    }
}

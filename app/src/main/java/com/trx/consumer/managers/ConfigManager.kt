package com.trx.consumer.managers

import android.app.Application
import com.amplitude.api.Amplitude
import com.amplitude.api.AmplitudeClient
import com.trx.consumer.BuildConfig

class ConfigManager(val cacheManager: CacheManager) {

    lateinit var amplitudeClient: AmplitudeClient
        private set

    fun configure(application: Application) {
        configureAmplitude(application)
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

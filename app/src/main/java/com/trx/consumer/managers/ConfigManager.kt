package com.trx.consumer.managers

import android.app.Application
import android.content.Context
import com.amplitude.api.Amplitude
import com.amplitude.api.AmplitudeClient
import com.trx.consumer.BuildConfig
import com.trx.consumer.R
import io.branch.referral.Branch

class ConfigManager(val cacheManager: CacheManager) {

    lateinit var amplitudeClient: AmplitudeClient
        private set

    fun configure(application: Application) {
        configureBranch(application)
        configureAmplitude(application)
    }

    private fun configureBranch(context: Context) {
        Branch.enableLogging()
        if (BuildConfig.DEVELOPMENT) Branch.enableTestMode()
        with(context) { Branch.getAutoInstance(this, getString(R.string.branch_key)) }
    }

    private fun configureAmplitude(application: Application) {
        with(application) {
            amplitudeClient = Amplitude
                .getInstance()
                .initialize(this, getString(R.string.amplitude_key))
            amplitudeClient.enableForegroundTracking(this)
        }
    }
}
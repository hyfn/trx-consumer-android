package com.trx.consumer.managers

import android.app.Application
import android.content.Context
import com.amplitude.api.Amplitude
import com.amplitude.api.AmplitudeClient
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.revenuecat.purchases.Purchases
import com.trx.consumer.BuildConfig
import com.trx.consumer.extensions.format
import com.trx.consumer.extensions.minutesAgo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

class ConfigManager(val cacheManager: CacheManager) {

    lateinit var remoteConfig: FirebaseRemoteConfig
        private set
    lateinit var amplitudeClient: AmplitudeClient
        private set

    fun configure(application: Application) {
        configureFirebase(application)
        configureRevenueCat(application)
        configureAmplitude(application)
    }

    private fun configureFirebase(context: Context) {
        FirebaseApp.initializeApp(context)

        val config = FirebaseRemoteConfig.getInstance()
        val fetchInterval = BuildConfig.kFirebaseFetchInterval.toLong()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(fetchInterval)
            .build()
        config.setConfigSettingsAsync(configSettings)

        GlobalScope.launch(Dispatchers.IO) {
            val latestDate = cacheManager.lastFirebaseFetchDate()
            if (latestDate == null || latestDate.minutesAgo() >= fetchInterval) {
                val task = config.fetchAndActivate()
                task.addOnFailureListener {
                    LogManager.log(it)
                    loadRemoteConfig()
                }
                task.addOnCompleteListener {
                    if (task.isSuccessful) {
                        val now = Calendar.getInstance().time
                        GlobalScope.launch(Dispatchers.IO) {
                            cacheManager.lastFirebaseFetchDate(now)
                        }
                        LogManager.log("fetchAndActivate ${now.format("h:mm")}")
                    }
                    loadRemoteConfig()
                }
            }
        }
        remoteConfig = config
    }

    private fun loadRemoteConfig() {
        val mode = remoteConfig.getBoolean(BuildConfig.isMaintenanceMode)
        LogManager.log("mode: $mode")
        val build = remoteConfig.getLong(BuildConfig.minimumBuildNumber)
        LogManager.log("build: $build")
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

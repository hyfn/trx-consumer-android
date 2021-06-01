package com.trx.consumer.managers

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.trx.consumer.BuildConfig
import timber.log.Timber

object LogManager {

    private val crashlyticsInstance = FirebaseCrashlytics.getInstance()

    fun log(message: String) {
        if (BuildConfig.DEBUG) Timber.d(message)
        else crashlyticsInstance.log(message)
    }

    fun log(exception: Exception) {
        if (BuildConfig.DEBUG) Timber.d(exception)
        else crashlyticsInstance.recordException(exception)
    }

}

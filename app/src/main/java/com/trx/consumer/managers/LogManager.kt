package com.trx.consumer.managers

import com.trx.consumer.BuildConfig
import timber.log.Timber

class LogManager {

    companion object {

        fun log(message: String) {
            if (BuildConfig.DEBUG) Timber.d(message)
        }

        fun log(exception: Exception) {
            if (BuildConfig.DEBUG) Timber.d(exception)
        }
    }
}

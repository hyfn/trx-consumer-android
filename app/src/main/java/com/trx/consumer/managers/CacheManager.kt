package com.trx.consumer.managers

import javax.inject.Inject

class CacheManager @Inject constructor() {

    fun isUserLoggedIn(): Boolean {
        return false
    }
}

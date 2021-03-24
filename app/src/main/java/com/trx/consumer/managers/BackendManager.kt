package com.trx.consumer.managers

import com.trx.consumer.base.BaseApi

class BackendManager(
    private val api: BaseApi,
    private val cacheManager: CacheManager
)

package com.trx.consumer.models.common

import com.android.billingclient.api.Purchase
import com.revenuecat.purchases.PurchaserInfo
import com.revenuecat.purchases.PurchasesError

class IAPModel(
    val purchaserInfo: PurchaserInfo? = null,
    val error: PurchasesError? = null,
    val purchase: Purchase? = null,
    val userCancelled: Boolean = false
)

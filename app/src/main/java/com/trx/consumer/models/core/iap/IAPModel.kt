package com.trx.consumer.models.core.iap

import com.android.billingclient.api.Purchase
import com.revenuecat.purchases.PurchaserInfo
import com.revenuecat.purchases.PurchasesError
import com.trx.consumer.BuildConfig

class IAPModel(
    val purchaserInfo: PurchaserInfo? = null,
    val error: PurchasesError? = null,
    val purchase: Purchase? = null,
    val userCancelled: Boolean = false
) {

    private fun hasEntitlement(value: String = ENTITLEMENT): Boolean {
        return purchaserInfo?.let { info -> info.entitlements[value]?.isActive } ?: false
    }

    val hasOnDemandSubscription: Boolean
        get() = hasEntitlement()

    companion object {
        const val ENTITLEMENT: String = BuildConfig.kRevenueCatEntitlementOnDemand
    }
}

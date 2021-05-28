package com.trx.consumer.models.common.iap

import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.PurchasesError
import com.trx.consumer.models.common.SubscriptionModel

class IAPOfferingsModel(
    private val offerings: Offerings? = null,
    var error: PurchasesError? = null
) {

    val lstPackages: List<SubscriptionModel>
        get() {
            val packages = offerings?.current?.availablePackages ?: emptyList()
            return packages.map { SubscriptionModel(iapPackage = it) }
        }
}

package com.trx.consumer.models.common

import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchasesError

class IAPOfferingsModel(
    private val offerings: Offerings? = null,
    var error: PurchasesError? = null
) {

    val lstPackages: List<SubscriptionModel>
        get() {
            val packages = offerings?.current?.availablePackages ?: emptyList()
            return packages.map { SubscriptionModel(iapPackage = it) }
        }

    val packages: List<Package>
        get() = offerings?.current?.availablePackages ?: emptyList()
}

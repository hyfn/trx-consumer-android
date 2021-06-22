package com.trx.consumer.models.common

import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchasesError

class IAPOfferingsModel(
    private val offerings: Offerings? = null,
    var error: PurchasesError? = null
) {

    val packages: List<Package>
        get() = offerings?.current?.availablePackages ?: emptyList()
}

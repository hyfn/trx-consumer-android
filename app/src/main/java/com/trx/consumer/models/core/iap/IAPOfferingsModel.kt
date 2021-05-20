package com.trx.consumer.models.core.iap

import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.PurchasesError

class IAPOfferingsModel(val offerings: Offerings? = null, var error: PurchasesError? = null) {

    val lstPackages: List<IAPPackageModel>
        get() {
            val packages = offerings?.current?.availablePackages ?: emptyList()
            return packages.map { IAPPackageModel(it) }
        }
}

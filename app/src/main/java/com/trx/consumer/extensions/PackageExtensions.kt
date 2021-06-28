package com.trx.consumer.extensions

import com.revenuecat.purchases.Package

fun Package.params(entitlement: String): HashMap<String, Any> {
    return hashMapOf(
        "subscriptionType" to entitlement,
        "currency" to product.priceCurrencyCode,
        "subscriptionId" to product.sku,
        "paymentProcessor" to "REVCAT",
        "priceInCents" to (product.priceAmountMicros / 10000)
    )
}

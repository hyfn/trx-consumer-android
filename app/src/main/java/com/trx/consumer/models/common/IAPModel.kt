package com.trx.consumer.models.common

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

    fun paramsPurchase(model: SubscriptionModel): HashMap<String, Any> {
        return params(isRestore = false, model = model)
    }

    fun paramsRestore(): HashMap<String, Any> {
        return params(isRestore = true)
    }

    private fun params(isRestore: Boolean, model: SubscriptionModel? = null): HashMap<String, Any> {
        val purchaseToken = purchase?.purchaseToken
        if (purchaserInfo == null || purchaseToken == null) return hashMapOf()

        val userId = purchaserInfo.originalAppUserId.replace("\$RCAnonymousID:", "")
        val params = hashMapOf<String, Any>(
            "app_user_id" to userId,
            "is_restore" to isRestore,
            "fetch_token" to purchaseToken
        )

        model?.let {
            val product = model.iapPackage.product
            params["product_id"] = product.sku
            params["price"] = product.price.filter { it.isDigit() || it == '.' }.toDouble()
            val currency = product.priceCurrencyCode
            if (currency.isNotEmpty()) params["currency"] = currency
        }

        return params
    }

    companion object {
        const val ENTITLEMENT: String = BuildConfig.kRevenueCatEntitlementOnDemand
    }
}

package com.trx.consumer.models.common

import android.os.Parcelable
import com.revenuecat.purchases.Package
import com.trx.consumer.screens.subscriptions.list.SubscriptionsViewState
import kotlinx.parcelize.Parcelize

@Parcelize
class SubscriptionModel(
    val iapPackage: Package,
    var primaryState: SubscriptionsViewState = SubscriptionsViewState.OTHER
) : Parcelable {

    val cost: String
        get() {
            val price = price
            if (price < 0) return ""

            var cost = String.format("%.2f", price)
            interval?.let { interval ->
                cost += " per $interval"
            }

            return "\$$cost"
        }

    val description: String
        get() = iapPackage.product.description

    private val price: Double
        get() {
            return iapPackage.product.price.filter { it.isDigit() || it == '.' }.toDouble()
        }

    val title: String
        get() = iapPackage.product.title

    private val interval: String?
        get() {
            return when (iapPackage.identifier) {
                "\$rc_monthly" -> "month"
                "\$rc_annual" -> "year"
                else -> null
            }
        }
}

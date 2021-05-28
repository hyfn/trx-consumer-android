package com.trx.consumer.models.common

import android.os.Parcelable
import com.android.billingclient.api.SkuDetails
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.trx.consumer.screens.plans.list.PlansViewState
import kotlinx.parcelize.Parcelize

@Parcelize
class SubscriptionModel(
    var key: String = "",
    var title: String = "",
    var description: String = "",
    var cost: String = "",
    var startsAt: Int = 0,
    var isActive: Boolean = false,
    val iapPackage: Package,
    var primaryState: PlansViewState = PlansViewState.OTHER
) : Parcelable {

    companion object {

        fun test(): SubscriptionModel {
            return SubscriptionModel(
                key = "",
                title = "TRX On demand (monthly)",
                description = "TRX On demand yearly",
                cost = "$19.99 per month",
                startsAt = 1,
                iapPackage = Package(
                    identifier = "",
                    packageType = PackageType.MONTHLY,
                    product = SkuDetails(""),
                    offering = ""
                )
            )
        }

        fun testList(count: Int): List<SubscriptionModel> {
            return mutableListOf<SubscriptionModel>().apply {
                for (i in 0 until count) {
                    val test = test()
                    add(test)
                }
            }
        }
    }
}

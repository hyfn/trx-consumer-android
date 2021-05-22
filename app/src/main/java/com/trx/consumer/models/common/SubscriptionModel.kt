package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.screens.subscriptions.list.SubscriptionsViewState
import kotlinx.parcelize.Parcelize

@Parcelize
class SubscriptionModel(
    var key: String?,
    var title: String?,
    var description: String?,
    var cost: String?,
    var startsAt: Int = 0,
    var primaryState: SubscriptionsViewState = SubscriptionsViewState.OTHER
) : Parcelable {

    companion object {

        fun test(): SubscriptionModel {
            return SubscriptionModel(
                key = "",
                title = "UNlimited live classes",
                description = "TRX On demand yearly",
                cost = "$19.99 per month",
                startsAt = 1
            )
        }

        fun testList(count: Int): List<SubscriptionModel> {
            return mutableListOf<SubscriptionModel>().apply {
                val firstItem = SubscriptionModel(
                    key = "",
                    title = "TRX On demand (monthly)",
                    description = "TRX On demand monthly\n\$9.99 per Month",
                    cost = "",
                    startsAt = 1,
                    primaryState = SubscriptionsViewState.CURRENT
                )
                add(firstItem)

                for (i in 0 until count) {
                    val test = test()
                    add(test)
                }
            }
        }
    }
}

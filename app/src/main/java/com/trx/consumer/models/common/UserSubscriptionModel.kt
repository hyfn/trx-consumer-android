package com.trx.consumer.models.common

import org.json.JSONObject

class UserSubscriptionModel(
    var cancelAtPeriodEnd: Boolean = false,
    var currentPeriodEnd: Double = 0.0
) {

    companion object {

        fun parse(jsonObject: JSONObject): UserSubscriptionModel =
            UserSubscriptionModel(
                cancelAtPeriodEnd = jsonObject.optBoolean("subscription.cancel_at_period_end", false),
                currentPeriodEnd = jsonObject.optDouble("subscription.current_period_end", 0.0)
            )
    }
}

package com.trx.consumer.models.common

import org.json.JSONObject

class UserPlanModel(
    var cancelAtPeriodEnd: Boolean = false,
    var currentPeriodEnd: Double = 0.0
) {

    companion object {

        fun parse(jsonObject: JSONObject): UserPlanModel =
            UserPlanModel(
                cancelAtPeriodEnd = jsonObject.optBoolean("subscription.cancel_at_period_end"),
                currentPeriodEnd = jsonObject.optDouble("subscription.current_period_end")
            )
    }
}

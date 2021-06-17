package com.trx.consumer.models.common

import org.json.JSONObject

class UserPlanModel(
    val cancelAtPeriodEnd: Boolean = false,
    val currentPeriodEnd: Long = 0,
    val currentPeriodStart: Long = 0
) {

    companion object {

        fun parse(jsonObject: JSONObject): UserPlanModel {
            return UserPlanModel(
                cancelAtPeriodEnd = jsonObject.optBoolean("cancel_at_period_end"),
                currentPeriodEnd = jsonObject.optLong("current_period_end"),
                currentPeriodStart = jsonObject.optLong("current_period_start")
            )
        }
    }
}
